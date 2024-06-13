package com.privacity.cliente.ws;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.privacity.cliente.rest.restcalls.grupo.GrupoById;
import com.privacity.cliente.singleton.Observers;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.singleton.impl.SingletonServer;
import com.privacity.cliente.util.GsonFormated;
import com.privacity.common.config.ConstantProtocolo;
import com.privacity.common.dto.MessageDetailDTO;
import com.privacity.common.dto.ProtocoloDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.CompletableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.ActionI;
import ua.naiksoftware.stomp.LineStatus;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompHeader;

public class WebSocket {


    private static final String TAG = "WebSockcet";
    private CompositeDisposable compositeDisposable;

    private Gson mGson = new GsonBuilder().create();

    private StompClient mStompClient;
    private Disposable mRestPingDisposable;
    private AppCompatActivity context;

    public WebSocket(AppCompatActivity context){
        this.context = context;
    }
    public void disconnectStomp() {
        mStompClient.disconnect();
        if (mRestPingDisposable != null) mRestPingDisposable.dispose();
        if (compositeDisposable != null) compositeDisposable.dispose();
    }

    public void connectStomp(ActionI connectWS, Activity activity) {



        Map<String,String> h = new HashMap<String,String>();
        h.put("Authorization", SingletonValues.getInstance().getToken());

        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP,  SingletonServer.getInstance().getWsServer()+ "/gs-guide-websocket/websocket",h);


        List<StompHeader> headers = new ArrayList<>();



        mStompClient.withClientHeartbeat(20000).withServerHeartbeat(0);

        resetSubscriptions();

        Disposable dispLifecycle = mStompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            toast("Stomp connection opened");
                            if ( connectWS != null ){
                                connectWS.actionSucess("Stomp connection opened");
                            }
                            break;
                        case ERROR:
                            //Log.e(TAG, "Stomp connection error", lifecycleEvent.getException());
                            toast("Stomp connection error");
                            if ( connectWS != null ){
                                connectWS.actionFail("Stomp connection error");
                                LineStatus.statusOffLine(activity);
                            }
                            break;
                        case CLOSED:
                            //toast("Stomp connection closed");

                            if ( connectWS != null ){

                                connectWS.actionFail("Stomp connection closed");
                            }
                            LineStatus.statusOffLine(activity);
                            //resetSubscriptions();
                            break;
                        case FAILED_SERVER_HEARTBEAT:
                            //toast("Stomp failed server heartbeat");
                            System.out.println(" ******* FALLO HEARTBEAT !!! ");
                            if ( connectWS != null ){
                                connectWS.actionFail("Stomp failed server heartbeat");

                            }
                            LineStatus.statusOffLine(activity);
                            break;
                    }
                });

        compositeDisposable.add(dispLifecycle);
        if ( connectWS != null ){
            connectWS.sendInfoMessage("Setting General Channel Suscription");
        }

//        // Receive greetings
//        Disposable dispTopic = mStompClient.topic("/topic/greetings")
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(topicMessage -> {
//                    Log.d(TAG, "Received " + topicMessage.getPayload());
//
//                }, throwable -> {
//                    if ( connectWS != null ){
//                        connectWS.actionFail("Error on Subscribe To General Channel");
//                    }
//                    Log.e(TAG, "Error on subscribe topic", throwable);
//
//                });
//
//
//        compositeDisposable.add(dispTopic);

        if ( connectWS != null ){
            connectWS.sendInfoMessage("Setting Personal Channel Suscription");
        }

        Disposable dispTopicUser = mStompClient.topic("/user/topic/reply")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    //Log.d(TAG, "Received " + topicMessage.getPayload());

                    String protocoloJson = SingletonValues.getInstance().getSessionAEStoUse().getAESDecrypt(topicMessage.getPayload());

                    ProtocoloDTO p = GsonFormated.get().fromJson(protocoloJson, ProtocoloDTO.class);
                    Log.d(TAG, "MENSAJE RECIbIDO " + GsonFormated.get().toJson(p));


                    if ( p.getComponent().equals(ConstantProtocolo.PROTOCOLO_COMPONENT_MESSAGE)){

                        if (p.getSaveGrupoGralConfLockResponseDTO() != null){
                            Observers.grupo().updateGrupoLock(p.getSaveGrupoGralConfLockResponseDTO());
                        }

                        if (p.getAction().equals(ConstantProtocolo.PROTOCOLO_ACTION_MESSAGE_RECIVIED)){

                            //MessageDTO id = GsonFormated.get().fromJson(p.getObjectDTO(), MessageDTO.class);
                            Observers.message().mensajeNuevoWS(p, true, activity);
                            //GetMessageById.get(activity, id.getIdGrupo(), id.getIdMessage());


                        }else if (p.getAction().equals(ConstantProtocolo.PROTOCOLO_ACTION_MESSAGE_CHANGE_STATE)){
                            changeStateDetail(p);
                        }else if (p.getAction().equals("/message/deleteForEveryone")){

                            Observers.message().removeMessage(p);
                        }
                    }else if ( p.getComponent().equals(ConstantProtocolo.PROTOCOLO_COMPONENT_GRUPO)){
                        if (p.getAction().equals("/grupo/addUser/addMe")){




                        } else if (p.getAction().equals(ConstantProtocolo.PROTOCOLO_ACTION_GRUPO_INVITATION_RECIVED)){
                            Observers.grupo().addGrupo(p);
                        } else if (p.getAction().equals(ConstantProtocolo.PROTOCOLO_ACTION_GRUPO_REMOVE_USER)){
                            //ObservatorGrupos.getInstance().removeUserFromGrupo(p);
                            Observers.message().removeAllMessageFromUser(p);
                        } else if (p.getAction().equals(ConstantProtocolo.PROTOCOLO_ACTION_GRUPO_WRITTING)){
                            //ObservatorGrupos.getInstance().removeUserFromGrupo(p);
                            Observers.message().writting(p);
                        } else if (p.getAction().equals(ConstantProtocolo.PROTOCOLO_ACTION_GRUPO_STOP_WRITTING)){
                            //ObservatorGrupos.getInstance().removeUserFromGrupo(p);
                            Observers.message().writtingStop(p);
                        } else if (p.getAction().equals(ConstantProtocolo.PROTOCOLO_ACTION_GRUPO_HOW_MANY_MEMBERS_ONLINE)){
                            //ObservatorGrupos.getInstance().removeUserFromGrupo(p);
                            Observers.grupo().updateOnline(p);
                        } else if (p.getAction().equals(ConstantProtocolo.PROTOCOLO_ACTION_GRUPO_SAVE_GENERAL_CONFIGURATION_LOCK)){

                            Observers.grupo().updateGrupoLock(p.getSaveGrupoGralConfLockResponseDTO());


/*                            if (
                                    SingletonValues.getInstance().getGrupoSeleccionado() != null &&
                                    p.getSaveGrupoGralConfLockResponseDTO().getIdGrupo().equals(
                                    SingletonValues.getInstance().getGrupoSeleccionado().getIdGrupo()
                            )) {
                                if (SingletonValues.getInstance().getGrupoSeleccionado().getPassword().isEnabled()
                                && !oldPasswordIsEnabled) {
                                    {
                                        Intent intent = new Intent("finish_message_activity");
                                        activity.sendBroadcast(intent);
                                    }

                                    {
                                        Intent intent = new Intent("finish_activity");
                                        activity.sendBroadcast(intent);
                                    }
                                }
                            }*/
                        }



                    }
                }, throwable -> {
                    if ( connectWS != null ){
                        connectWS.actionFail("Error on Subscribe Personal Channel");
                    }
                    Log.e(TAG, "Error on subscribe topic", throwable);
                });


        compositeDisposable.add(dispTopicUser);

        mStompClient.connect(headers, connectWS, activity);
    }

    private void addItem(ProtocoloDTO protocoloDTO) {
        Observers.message().mensaje(protocoloDTO,context, null);
    }
    /*
    private void changeState(MessageDetailDTO messageDetailDTODTO) {
        Observers.message().mensajeChangeState(messageDetailDTODTO,context);
    }*/
    private void changeStateDetail(ProtocoloDTO protocoloDTO) {
        Observers.message().mensajeDetailChangeState(protocoloDTO);
    }

    private void toast(String text) {
        Log.i(TAG, text);
        //Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    protected CompletableTransformer applySchedulers() {
        return upstream -> upstream
                .unsubscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private void resetSubscriptions() {
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
        compositeDisposable = new CompositeDisposable();
    }


    protected void onDestroy() {
        mStompClient.disconnect();

        if (mRestPingDisposable != null) mRestPingDisposable.dispose();
        if (compositeDisposable != null) compositeDisposable.dispose();

    }
}
