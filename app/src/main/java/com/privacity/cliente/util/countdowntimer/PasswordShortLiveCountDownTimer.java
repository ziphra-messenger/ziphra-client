package com.privacity.cliente.util.countdowntimer;

import android.os.CountDownTimer;

import com.privacity.cliente.singleton.Observers;

import lombok.Data;

@Data
public class PasswordShortLiveCountDownTimer {

    private CountDownTimer countDownTimer;
    private boolean running =false;

    public void restart(){
        //mirar la memoria
        if ( countDownTimer != null){
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(
                300*1000
                , 300*1000

        ) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                Observers.password().passwordExpired();
                running=false;
            }
        };
        running=true;
        countDownTimer.start();

    }
}
