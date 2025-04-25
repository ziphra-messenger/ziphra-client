package com.privacity.cliente.singleton.reconnect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.CountDownTimer;

import com.privacity.cliente.activity.common.CustomAppCompatActivity;
import com.privacity.cliente.enumeration.WsStatusEnum;
import com.privacity.cliente.model.dto.MessageDetail;
import com.privacity.cliente.rest.restcalls.message.GetMessageById;
import com.privacity.cliente.rest.restcalls.message.MessageChangeState;
import com.privacity.cliente.singleton.SingletonValues;import com.privacity.cliente.singleton.Singletons;
import com.privacity.cliente.singleton.observers.ObserverConnection;
import com.privacity.cliente.singleton.observers.ObserverMessage;
import com.privacity.cliente.singleton.usuario.SingletonSessionClosing;
import com.privacity.cliente.util.ToolsUtil;
import com.privacity.common.BroadcastConstant;
import com.privacity.common.SingletonReconnectionLog;
import com.privacity.common.SingletonReset;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import ua.naiksoftware.stomp.ActionI;

@Getter
@Setter
public class SingletonReconnect implements SingletonReset {
    private static final String TAG = "SingletonReconnect";
    private long secondsValueInicial=5;
    private long secondsValue=5;


    private static SingletonReconnect instance;

    private int disconnetedCount=1;
    private int reintentosCount=0;
    private int reintentosTotalCount=0;

    private SingletonReconnect() {
    }

    public static SingletonReconnect getInstance() {
        if (instance == null){
            instance = new SingletonReconnect();
        }
        return instance;
    }

    public void addLog(String txt){
       // if (ObserverConnection.getInstance().isOnline()) {
            SingletonReconnectionLog.getInstance().addLog(txt);
        //}
    }


    @Getter
    private CountDownTimer countDownTimer;
    private boolean running =false;

    public boolean showingLock;

    public WsStatusEnum status= WsStatusEnum.CONNECTED;

    public void reset(){
        if (countDownTimer != null )  countDownTimer.cancel();
        SingletonReconnectionLog.getInstance().reset();
        ToolsUtil.forceGarbageCollector(countDownTimer);
        ToolsUtil.forceGarbageCollector(instance);


    }
    private void connected(CustomAppCompatActivity activity){
        reintentosCount=0;

        if (!status.equals(WsStatusEnum.CONNECTED)) disconnetedCount++;

        if (status.equals(WsStatusEnum.CONNECTED)) return;

        status = WsStatusEnum.CONNECTED;

        if (countDownTimer != null){
            countDownTimer.cancel();

        }
        addLog("Connected");
        if (activity != null){
            Intent intent = new Intent("connection_open");
            activity.sendBroadcast(intent);

            List<MessageDetail> dl = ObserverMessage.getInstance().getAllMyMensajesDetailsToChangeStateOnReconnect();

            for (MessageDetail d : dl){
                MessageChangeState.mensajeChangeState(d, activity);
            }
            GetMessageById.loadMessagesContador(activity);

        }else {
            addLog("activity null connect");
            System.out.println("activity null connect");
        }

    }

    public synchronized void waiting(CustomAppCompatActivity activity){
        //mirar la memoria

        if (status.equals(WsStatusEnum.TRYING)) return;
        if (running) return;
        running =true;



//        if (activity != null){
//            Intent intent = new Intent(BroadcastConstant.BROADCAST__MESSAGING__CONNECTION_CLOSED);
//            activity.sendBroadcast(intent);
//        }

        if (countDownTimer != null )  countDownTimer.cancel();

        //if (status.equals(WsStatusEnum.CONNECTED)) return;

        status = WsStatusEnum.WAITING;

        //Notificacion.getInstance().notificacion2("descontect > " + getCurrentTimeStamp());
        addLog("Desconnect");

        countDownTimer = new CountDownTimer(
                secondsValueInicial*1000
                , 1*1000

        ) {

            public void onTick(long millisUntilFinished) {
                running =true;
                status = WsStatusEnum.WAITING;
                secondsValue=millisUntilFinished/1000;
                if (activity != null){
                    Intent intent = new Intent("connection_time");
                    activity.sendBroadcast(intent);
                }

            }

            public void onFinish() {
                if (SingletonSessionClosing.getInstance().isClosing())return;
                status = WsStatusEnum.TRYING;
                reintentosCount++;
                reintentosTotalCount++;

                if (activity != null){
                    Intent intent = new Intent("connection_trying");
                    activity.sendBroadcast(intent);
                }
                try {
                    SingletonValues.getInstance().getWebSocket().connectStomp(new ActionI(){
                        @Override
                        public void actionSucess(String msg) {
                            running =false;
                            addLog("actionSucess  >> " +  msg);

                                connected(activity);


                        }

                        @Override
                        public void actionFail(String msg) {
                            status = WsStatusEnum.WAITING;
                            running =false;
                            addLog("actionFail  >> " +  msg);

                            //Intent intent = new Intent(BroadcastConstant.BROADCAST__MESSAGING__CONNECTION_CLOSED);
                            //activity.sendBroadcast(intent);
                        }

                        @Override
                        public void sendInfoMessage(String msg) {
                            addLog("sendInfoMessage  >> " +  msg);
                        }

                        @Override
                        public void isNotOnline() {
                            if (activity != null){
                                Intent intent = new Intent("connection_off_line");
                                activity.sendBroadcast(intent);
                            }

                        }
                    }, activity );
                } catch (Exception e) {
                    e.printStackTrace();
                    status = WsStatusEnum.WAITING;
                    running =false;

                    addLog("InesperadpactionFail  >> " +  e.getMessage());
                }
            }
        };

        countDownTimer.start();
        status = WsStatusEnum.WAITING;

    }

    public static boolean isOnline(Activity activity) {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}
