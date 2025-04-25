package com.privacity.cliente.ws;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.privacity.cliente.activity.lock.LockActivity;
import com.privacity.cliente.singleton.Observers;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.singleton.UtilsStringSingleton;
import com.privacity.cliente.singleton.countdown.SingletonMyAccountConfLockDownTimer;
import com.privacity.cliente.singleton.impl.SingletonServer;
import com.privacity.common.dto.GrupoGralConfDTO;
import com.privacity.common.dto.IdGrupoDTO;
import com.privacity.cliente.model.dto.Protocolo;
import com.privacity.common.dto.request.GrupoChangeUserRoleDTO;
import com.privacity.common.dto.response.SaveGrupoGralConfLockResponseDTO;
import com.privacity.common.enumeration.ProtocoloActionsEnum;
import com.privacity.common.enumeration.ProtocoloComponentsEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.CompletableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import lombok.Getter;
import ua.naiksoftware.stomp.ActionI;
import ua.naiksoftware.stomp.LineStatus;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompHeader;

public class WebSocket {


    private static final String TAG = "WebSocket";
    private CompositeDisposable compositeDisposable;

    private final Gson mGson = new GsonBuilder().create();

    private StompClient mStompClient;
    private Disposable mRestPingDisposable;
    private final AppCompatActivity context;

    @Getter
    private boolean connected=false;


    public WebSocket(AppCompatActivity context){
        this.context = context;
    }
    public void disconnectStomp() {
        mStompClient.disconnect();
        if (mRestPingDisposable != null) mRestPingDisposable.dispose();
        if (compositeDisposable != null) compositeDisposable.dispose();
    }

    public void connectStomp(ActionI connectWS, Activity activity) {


        Log.e(TAG, "SingletonValues.getInstance().getToken() " + SingletonValues.getInstance().getToken());
        if (SingletonValues.getInstance().getToken() == null){
            connectWS.actionFail("Stomp connection error");
            LineStatus.statusOffLine(activity);
            return;
        }
        Map<String,String> h = new HashMap<String,String>();
        h.put(org.apache.hc.core5.http.HttpHeaders.AUTHORIZATION, SingletonValues.getInstance().getToken());

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
                                connected=true;
                                connectWS.actionSucess("Stomp connection opened");
                            }
                            break;
                        case ERROR:
                            //Log.e(TAG, "Stomp connection error", lifecycleEvent.getException());
                            connected=false;
                            toast("Stomp connection error");
                            if ( connectWS != null ){
                                connectWS.actionFail("Stomp connection error");
                                LineStatus.statusOffLine(activity);
                            }
                            break;
                        case CLOSED:
                            //toast("Stomp connection closed");
                            connected=false;
                            if ( connectWS != null ){

                                connectWS.actionFail("Stomp connection closed");
                            }
                            LineStatus.statusOffLine(activity);
                            //resetSubscriptions();
                            break;
                        case FAILED_SERVER_HEARTBEAT:
                            connected=false;
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

                    try {
                        Log.d(TAG, "MENSAJE RECIbIDO CRUDO" + topicMessage.getPayload());


                        Log.d(TAG, "entrada :" + topicMessage.getPayload());
/*                        String uncompressB64 = UtilsStringSingleton.getInstance().uncompressB64(topicMessage.getPayload());
                        Log.d(TAG, "uncompressB64 :" + uncompressB64);

                        String protocoloJson = SingletonValues.getInstance().getSessionAEStoUseWS().getAESDecrypt(uncompressB64);*/

                        Protocolo p = Protocolo.convert(UtilsStringSingleton.getInstance().protocoloToSendDecrypt(SingletonValues.getInstance().getSessionAEStoUseWS(), topicMessage.getPayload()));

                        Log.d(TAG, "MENSAJE RECIbIDO GSON " + UtilsStringSingleton.getInstance().gsonToSend(p));


                        if (p.getComponent().equals(ProtocoloComponentsEnum.MESSAGE)) {



                            if (p.getAction().equals(ProtocoloActionsEnum.MESSAGE_RECIVIED)) {

                                //MessageDTO id = UtilsStringSingleton.getInstance().gson().fromJson(p.getObjectDTO(), MessageDTO.class);
                                Observers.message().mensajeNuevoWS(p, true, activity);
                                //GetMessageById.get(activity, id.getIdGrupo(), id.getIdMessage());


                            } else if (p.getAction().equals(ProtocoloActionsEnum.MESSAGE_CHANGE_STATE)) {
                                changeStateDetail(p);
                            } else if (p.getAction().equals(ProtocoloActionsEnum.MESSAGE_DELETE_FOR_EVERYONE )) {

                                Observers.message().removeMessage(p);
                            }
                        } else if (p.getComponent().equals(ProtocoloComponentsEnum.GRUPO)) {
                            if (p.getAction().equals(ProtocoloActionsEnum.GRUPO_ADDUSER_ADDME )) {


                            } else if (p.getAction().equals(ProtocoloActionsEnum.GRUPO_INVITATION_RECIVED)) {
                                Observers.grupo().addGrupo(p);
                            } else if (p.getAction().equals(ProtocoloActionsEnum.GRUPO_REMOVE_OTHER)) {
                                //ObservatorGrupos.getInstance().removeUserFromGrupo(p);
                                Observers.message().removeAllMessageFromUser(p);
                            } else if (p.getAction().equals(ProtocoloActionsEnum.GRUPO_REMOVE_ME)) {
                                //ObservatorGrupos.getInstance().removeUserFromGrupo(p);
                                IdGrupoDTO g = UtilsStringSingleton.getInstance().gson().fromJson(p.getObjectDTO(), IdGrupoDTO.class);
                                Observers.grupo().avisarGrupoRemove(g.getIdGrupo());
                            } else if (p.getAction().equals(ProtocoloActionsEnum.GRUPO_WRITTING)) {
                                //ObservatorGrupos.getInstance().removeUserFromGrupo(p);
                                Observers.message().writting(p);
                            } else if (p.getAction().equals(ProtocoloActionsEnum.GRUPO_STOP_WRITTING)) {
                                //ObservatorGrupos.getInstance().removeUserFromGrupo(p);
                                Observers.message().writtingStop(p);
                            } else if (p.getAction().equals(ProtocoloActionsEnum.GRUPO_HOW_MANY_MEMBERS_ONLINE)) {
                                //ObservatorGrupos.getInstance().removeUserFromGrupo(p);
                                Observers.grupo().updateOnline(p);
                            } else if (p.getAction().equals(ProtocoloActionsEnum.GRUPO_SAVE_GENERAL_CONFIGURATION_LOCK)) {
                                System.out.println(UtilsStringSingleton.getInstance().gsonToSend(p));
                                SaveGrupoGralConfLockResponseDTO sgglr = mGson.fromJson(p.getObjectDTO(), SaveGrupoGralConfLockResponseDTO.class);
                                Observers.grupo().updateGrupoLock(sgglr);

/*                            if (
                                    SingletonValues.getInstance().getGrupoSeleccionado() != null &&
                                    p.getSaveGrupoGralConfLockResponseDTO().getIdGrupo().equals(
                                    SingletonValues.getInstance().getGrupoSeleccionado().getIdGrupo()
                            )) {
                                if (SingletonValues.getInstance().getGrupoSeleccionado().getPassword().isEnabled()
                                && !oldPasswordIsEnabled) {
                                    {
                                        Intent intent = new Intent(BroadcastConstant.BROADCAST__FINISH_MESSAGE_ACTIVITY);
                                        activity.sendBroadcast(intent);
                                    }

                                    {
                                        Intent intent = new Intent(BroadcastConstant.BROADCAST__FINISH_ACTIVITY);
                                        activity.sendBroadcast(intent);
                                    }
                                }
                            }*/
                            } else if (p.getAction().equals(ProtocoloActionsEnum.GRUPO_CHANGE_USER_ROLE_OUTPUT)) {
                                System.out.println(UtilsStringSingleton.getInstance().gsonToSend(p));
                                GrupoChangeUserRoleDTO sgglr = mGson.fromJson(p.getObjectDTO(), GrupoChangeUserRoleDTO.class);
                                Observers.grupo().updateGrupoUserRole(sgglr);
                            } else if (p.getAction().equals(ProtocoloActionsEnum.GRUPO_SAVE_GENERAL_CONFIGURATION)) {
                                System.out.println(UtilsStringSingleton.getInstance().gsonToSend(p));
                                GrupoGralConfDTO sgglr = mGson.fromJson(p.getObjectDTO(), GrupoGralConfDTO.class);
                                Observers.grupo().updateGrupoGralConf(sgglr);
                            } else if (p.getAction().equals(ProtocoloActionsEnum.GRUPO_BLOCK_REMOTO )) {
                                System.out.println("BLOQUEO REMOTO " + UtilsStringSingleton.getInstance().gsonToSend(p));

                                if (!SingletonMyAccountConfLockDownTimer.getInstance().isLocked()) {
                                    Intent i = new Intent(activity, LockActivity.class);

                                    i.putExtra(ProtocoloActionsEnum.GRUPO_BLOCK_REMOTO.toString(), true);


                                    activity.startActivity(i);

                                }
                            }
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }, throwable -> {
                    if ( connectWS != null ){
                        connectWS.actionFail("Error on Subscribe Personal Channel");
                    }
                    Log.e(TAG, "Error on subscribe topic: " + throwable.getMessage());
                });


        compositeDisposable.add(dispTopicUser);

        mStompClient.connect(headers, connectWS, activity);
    }

    private void addItem(Protocolo protocolo) {
        Observers.message().mensaje(protocolo,context, null);
    }
    /*
    private void changeState(MessageDetail messageDetailDTO) {
        Observers.message().mensajeChangeState(messageDetailDTO,context);
    }*/
    private void changeStateDetail(Protocolo protocolo) {
        Observers.message().mensajeDetailChangeState(protocolo);
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
