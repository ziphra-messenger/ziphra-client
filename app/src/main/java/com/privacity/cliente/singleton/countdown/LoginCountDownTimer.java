package com.privacity.cliente.singleton.countdown;

import android.app.Activity;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.CountDownTimer;

import androidx.appcompat.app.AlertDialog;

import com.privacity.cliente.singleton.impl.SingletonServer;

import lombok.Data;

@Data
public class LoginCountDownTimer {


    private static long TIME_SECONDS = 5;
    private CountDownTimer countDownTimer;
    private boolean countDownTimerRunning=false;
    private Activity activity;
    private AlertDialog dialog;

    public LoginCountDownTimer(Activity activity){
        this.activity =activity;
    }
    public void restart(){

        if ( countDownTimer != null){
            countDownTimer.cancel();
        }
        countDownTimerRunning=true;
        countDownTimer = new CountDownTimer(
                TIME_SECONDS*1000
                , TIME_SECONDS * 1000) {

            public void onTick(long millisUntilFinished) {
                //System.out.println("escribiendo tic " + millisUntilFinished);
            }

            public void onFinish() {
                countDownTimerRunning=false;
                alertClose();
            }
        };

        countDownTimer.start();

    }

    public void stop() {
        if ( countDownTimer != null){
            countDownTimer.cancel();
        }
        if (dialog != null){
            dialog.dismiss();
        }

        countDownTimerRunning=false;
    }

    private void alertClose(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setPositiveButton("Cerrar App", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                Runtime.getRuntime().exit(0);

            }
        });

        builder.setNegativeButton("Reiniciar App", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                PackageManager packageManager = activity.getPackageManager();
                Intent intent = packageManager.getLaunchIntentForPackage(activity.getPackageName());
                ComponentName componentName = intent.getComponent();
                Intent mainIntent = Intent.makeRestartActivityTask(componentName);
                activity.startActivity(mainIntent);
                Runtime.getRuntime().exit(0);

            }
        });

        builder.setNeutralButton("Esperar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                restart();
            }
        });

        builder.setTitle("Aviso");

        builder.setMessage("La llamada al servidor esta tardando mas tiempo de lo esperado. " + SingletonServer.getInstance().getAppServer());
        this.dialog = builder.create();

        dialog.show();

    }
}