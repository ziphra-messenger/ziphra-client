package com.privacity.cliente.singleton;

import android.content.Intent;
import android.os.CountDownTimer;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.privacity.cliente.singleton.interfaces.SingletonReset;

import lombok.Getter;

@Getter
public class SingletonSessionFinish implements SingletonReset {

    private CountDownTimer sessionTime;
    private AppCompatActivity context;

    private static SingletonSessionFinish instance;

    public static SingletonSessionFinish getInstance() {

        if (instance == null){
            instance = new SingletonSessionFinish();

        }
        return instance;
    }

    private SingletonSessionFinish() { }
    public void setup(AppCompatActivity context){
        this.context = context;
    }
    public void startClock(){

        instance.sessionTime = new CountDownTimer(216000000, 30000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.println(Log.INFO, "tick", "tock " + millisUntilFinished);
            }

            @Override
            public void onFinish() {


                Intent intent = new Intent("finish_all_activities");
                context.sendBroadcast(intent);

//                Intent i = new Intent(context, MainActivity.class);
//                context.startActivity(i);


            }
        };

        instance.sessionTime.start();
    }
    public void cancel(){
        if (instance.sessionTime != null ) instance.sessionTime.cancel();
    }

    public void restart(){
        if (instance.sessionTime != null ) instance.sessionTime.cancel();
        instance.startClock();
    }

    @Override
    public void reset() {
        if (instance.sessionTime != null ) instance.sessionTime.cancel();
        instance=null;
    }
}
