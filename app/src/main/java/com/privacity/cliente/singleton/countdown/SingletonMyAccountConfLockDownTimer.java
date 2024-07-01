package com.privacity.cliente.singleton.countdown;

import android.app.Activity;
import android.content.Intent;
import android.os.CountDownTimer;

import com.privacity.cliente.activity.lock.LockActivity;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.singleton.interfaces.SingletonReset;
import com.privacity.common.enumeration.ProtocoloComponentsEnum;import com.privacity.common.enumeration.ProtocoloActionsEnum;


import lombok.Getter;
import lombok.Setter;


public class SingletonMyAccountConfLockDownTimer implements SingletonReset {



    private static SingletonMyAccountConfLockDownTimer instance;
    private Activity activity;

    public static SingletonMyAccountConfLockDownTimer getInstance() {

        if (instance == null){
            instance = new SingletonMyAccountConfLockDownTimer();
        }
        return instance;
    }

    public void setup(Activity activity) {
        this.activity = activity;
    }
    private SingletonMyAccountConfLockDownTimer() {

        initCounter();
    }
//    protected long lastTimeRestart=0;
@Getter
@Setter
    boolean isLocked;
    private void initCounter() {
        countDownTimer = new CountDownTimer(
                SingletonValues.getInstance().getMyAccountConfDTO().getLock().getSeconds() *1000
                ,1000

        ) {

            public void onTick(long millisUntilFinished) {
                System.out.println("Tiempo para lock la app " + millisUntilFinished);
  //              lastTimeRestart=millisUntilFinished;
            }

            public void onFinish() {
                System.out.println("Tiempo para lock la app onFinish");
                running=false;

                if ( SingletonValues.getInstance().getMyAccountConfDTO().getLock().isEnabled()) {
                    if (!SingletonMyAccountConfLockDownTimer.getInstance().isLocked()) {
                        Intent i = new Intent(activity, LockActivity.class);
                        activity.startActivity(i);
                    }

                }
            }
        };
    }


    @Override
    public void reset() {
        System.out.println("Tiempo para lock la app RESET");
        if ( countDownTimer != null){
            countDownTimer.cancel();
        }
        countDownTimer=null;
        boolean running =false;
        instance = null;
    }

    private CountDownTimer countDownTimer;
    @Getter
    private boolean running =false;


    public synchronized void restart(){
        restart(false);
    }

    public synchronized void restart(boolean reset){

        if ( countDownTimer != null){
            countDownTimer.cancel();
            running=false;
        }

        if (reset){

            initCounter();
        }

        System.out.println("Tiempo para lock la app RESTART");
        if ( countDownTimer != null){
            countDownTimer.cancel();
            running=false;
        }


        if ( SingletonValues.getInstance().getMyAccountConfDTO().getLock().isEnabled()) {

            running = true;
            countDownTimer.start();
        }
    }
}
