package com.privacity.cliente.singleton;

import android.content.Intent;
import android.os.CountDownTimer;

import androidx.appcompat.app.AppCompatActivity;

import com.privacity.cliente.util.ToolsUtil;
import com.privacity.common.BroadcastConstant;
import com.privacity.common.SingletonReset;

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
                //Log.println(Log.INFO, "tick", "tock " + millisUntilFinished);
            }

            @Override
            public void onFinish() {


                Intent intent = new Intent(BroadcastConstant.BROADCAST__FINISH_ALL_ACTIVITIES);
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
        ToolsUtil.forceGarbageCollector(instance);
    }
}
