package com.privacity.cliente.util.notificacion;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.core.app.NotificationCompat;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.grupo.GrupoActivity;
import com.privacity.cliente.singleton.Observers;

import static android.content.Context.VIBRATOR_SERVICE;

public class Notificacion {

    private static Activity activity;
    private static Notificacion instance = new Notificacion();

    private Notificacion() {
    }
    public void init(Activity grupoActivity2){
        activity = grupoActivity2;
    }
    public static Notificacion getInstance() {
        return instance;
    }


    public static final String CHANNEL_ID="1";
    public static final int ID = 1;

    private void shakeItBaby() {
        if (Build.VERSION.SDK_INT >= 26) {
            ((Vibrator) activity.getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(150,10));
        } else {
            ((Vibrator) activity.getSystemService(VIBRATOR_SERVICE)).vibrate(150);
        }
    }

    public void notificacion() {
        if (activity == null) return;

        if ( Observers.grupo().isGrupoOnTop()){


            shakeItBaby();
            return;
        }





        NotificationManager mNotificationManager = (NotificationManager) activity.getApplicationContext().getSystemService(activity.NOTIFICATION_SERVICE);

        //For API 26+ you need to put some additional code like below:
        NotificationChannel mChannel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID, "Privacity Mensaje Nuevo", NotificationManager.IMPORTANCE_HIGH);
            mChannel.setLightColor(Color.GRAY);
            mChannel.enableLights(true);
            mChannel.setDescription("Recepcion de un nuevo mensaje");

            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel( mChannel );
            }
        }

//        Log.d("mensage", "mensage ");
        Intent intent = new Intent(activity, GrupoActivity.class);
//        intent.putExtra("NotiClick", true);


        PendingIntent pIntent = PendingIntent.getActivity(activity, 0, intent, Intent.FILL_IN_ACTION);
        //General code:
        NotificationCompat.Builder status = new NotificationCompat.Builder(activity.getApplicationContext(),CHANNEL_ID);
        status.setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_baseline_location_city_24)
                .setOnlyAlertOnce(true)
                .setContentTitle("Mensaje Nuevo")
                //.setContentText("privacity")
                .setVibrate(new long[]{0, 500, 1000})
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pIntent);
        //.setContent(views);

        mNotificationManager.notify(ID, status.build());
    }


//    public void notificacion2(String msg) {
//        ID++;
//        if (activity == null) return;
//
//        String CHANNEL_ID= ID +"";
//
//        NotificationManager mNotificationManager = (NotificationManager) activity.getApplicationContext().getSystemService(activity.NOTIFICATION_SERVICE);
//
//        //For API 26+ you need to put some additional code like below:
//        NotificationChannel mChannel;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            mChannel = new NotificationChannel(CHANNEL_ID, "desconect", NotificationManager.IMPORTANCE_HIGH);
//            mChannel.setLightColor(Color.GRAY);
//            mChannel.enableLights(true);
//            mChannel.setDescription(msg);
//
//            if (mNotificationManager != null) {
//                mNotificationManager.createNotificationChannel( mChannel );
//            }
//        }
//
//        Log.d("desconect", "desconect ");
//        Intent intent = new Intent(activity, NotificationReceiver.class);
//        intent.putExtra("NotiClick", true);
//
//
//        PendingIntent pIntent = PendingIntent.getActivity(activity, 0, intent, Intent.FILL_IN_ACTION);
//        //General code:
//        NotificationCompat.Builder status = new NotificationCompat.Builder(activity.getApplicationContext(),CHANNEL_ID);
//        status.setAutoCancel(true)
//                .setWhen(System.currentTimeMillis())
//                .setSmallIcon(R.drawable.ic_baseline_location_city_24)
//                //.setOnlyAlertOnce(true)
//                .setContentTitle("OFF LINE")
//                .setContentText(msg)
//                .setVibrate(new long[]{0, 500, 1000})
//                .setDefaults(Notification.DEFAULT_ALL)
//                .setContentIntent(pIntent);
//        //.setContent(views);
//
//        mNotificationManager.notify(ID, status.build());
//    }
    }
