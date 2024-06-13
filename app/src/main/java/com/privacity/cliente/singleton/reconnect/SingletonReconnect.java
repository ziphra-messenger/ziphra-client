package com.privacity.cliente.singleton.reconnect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.CountDownTimer;

import com.privacity.cliente.activity.common.CustomAppCompatActivity;
import com.privacity.cliente.enumeration.WsStatusEnum;
import com.privacity.cliente.rest.restcalls.message.GetMessageById;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.singleton.interfaces.SingletonReset;

import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import ua.naiksoftware.stomp.ActionI;

@Getter
@Setter
public class SingletonReconnect implements SingletonReset {

    private long secondsValueInicial=5;
    private long secondsValue=5;


    private static SingletonReconnect instance;

    private int disconnetedCount=1;
    private int reintentosCount=0;
    private int reintentosTotalCount=0;
    //private String log="";
    private SingletonReconnect() {
    }

    public static SingletonReconnect getInstance() {
        if (instance == null){
            instance = new SingletonReconnect();
        }
        return instance;
    }

    @Getter
    private CountDownTimer countDownTimer;
    private boolean running =false;

    public boolean showingLock;

    public WsStatusEnum status= WsStatusEnum.CONNECTED;
    public static String getCurrentTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate + "\n";
    }
    public void reset(){
        if (countDownTimer != null )  countDownTimer.cancel();
        instance=null;
    }
    private void connected(CustomAppCompatActivity activity){
        reintentosCount=0;

        if (!status.equals(WsStatusEnum.CONNECTED)) disconnetedCount++;

        if (status.equals(WsStatusEnum.CONNECTED)) return;

        status = WsStatusEnum.CONNECTED;

        if (countDownTimer != null){
            countDownTimer.cancel();

        }
        //log = log + "Connected >> " + getCurrentTimeStamp();
        if (activity != null){
            Intent intent = new Intent("connection_open");
            activity.sendBroadcast(intent);

            GetMessageById.loadMessagesContador(activity);

        }else {
            System.out.println("activity null connect");
        }

    }

    public synchronized void waiting(CustomAppCompatActivity activity){
        //mirar la memoria

        if (status.equals(WsStatusEnum.TRYING)) return;
        if (running) return;
        running =true;



//        if (activity != null){
//            Intent intent = new Intent("connection_closed");
//            activity.sendBroadcast(intent);
//        }

        if (countDownTimer != null )  countDownTimer.cancel();

        //if (status.equals(WsStatusEnum.CONNECTED)) return;

        status = WsStatusEnum.WAITING;

        //Notificacion.getInstance().notificacion2("descontect > " + getCurrentTimeStamp());
        //log = log + "Desconnect >> " + getCurrentTimeStamp();

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

                status = WsStatusEnum.TRYING;
                reintentosCount++;
                reintentosTotalCount++;
                if (activity != null){
                    Intent intent = new Intent("connection_trying");
                    activity.sendBroadcast(intent);
                }
                SingletonValues.getInstance().getWebSocket().connectStomp(new ActionI(){
                    @Override
                    public void actionSucess(String msg) {
                        running =false;
                        System.out.println("actionSucess  >> " +  msg);

                            connected(activity);


                    }

                    @Override
                    public void actionFail(String msg) {
                        status = WsStatusEnum.WAITING;
                        running =false;
                        System.out.println("actionFail  >> " +  msg);

                        //Intent intent = new Intent("connection_closed");
                        //activity.sendBroadcast(intent);
                    }

                    @Override
                    public void sendInfoMessage(String msg) {
                        System.out.println("sendInfoMessage  >> " +  msg);
                    }

                    @Override
                    public void isNotOnline() {
                        if (activity != null){
                            Intent intent = new Intent("connection_off_line");
                            activity.sendBroadcast(intent);
                        }

                    }
                }, activity );
            }
        };

        countDownTimer.start();
        status = WsStatusEnum.WAITING;

    }

    public static boolean isOnline(Activity activity) {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

}
