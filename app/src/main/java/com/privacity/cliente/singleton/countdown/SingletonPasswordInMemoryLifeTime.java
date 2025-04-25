package com.privacity.cliente.singleton.countdown;

import android.os.CountDownTimer;

import com.privacity.common.SingletonReset;

import lombok.Getter;


public class SingletonPasswordInMemoryLifeTime implements SingletonReset {



    private static SingletonPasswordInMemoryLifeTime instance;

    public static SingletonPasswordInMemoryLifeTime getInstance() {

        if (instance == null){
            instance = new SingletonPasswordInMemoryLifeTime();
        }
        return instance;
    }

    private SingletonPasswordInMemoryLifeTime() {

        initCounter();
    }

    private void initCounter() {
        countDownTimer = new CountDownTimer(
                segundosPasswordEnMemoria *1000
                , segundosPasswordEnMemoria * 10

        ) {

            public void onTick(long millisUntilFinished) {
           //     System.out.println("Vencimiento de password en memoria " + millisUntilFinished);
            }

            public void onFinish() {
                System.out.println("Vencimiento de password en memoria onFinish");
                running=false;
            }
        };
    }


    @Override
    public void reset() {
        System.out.println("Vencimiento de password en memoria RESET");
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
    int segundosPasswordEnMemoria = 300;

    public synchronized void restart(){
        //mirar la memoria
        System.out.println("Vencimiento de password en memoria RESTART");
        if ( countDownTimer != null){
            countDownTimer.cancel();
            running=false;
        }{
            initCounter();
        }



        running=true;
        countDownTimer.start();

    }
}
