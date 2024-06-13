package com.privacity.cliente.singleton.countdown;

import android.app.Activity;
import android.os.CountDownTimer;

import com.privacity.cliente.model.Grupo;
import com.privacity.cliente.rest.restcalls.grupo.StopWrittingCallRest;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.common.dto.WrittingDTO;

import lombok.Data;

@Data
public class WrittingCountDownTimer {

    private CountDownTimer countDownTimer;
    private boolean countDownTimerRunning=false;
    public Grupo grupo;
    private Activity activity;

    public WrittingCountDownTimer(Activity activity, Grupo g){
        this.activity =activity;
        grupo=g;
    }
    public void restart(){

        if ( countDownTimer != null){
            countDownTimer.cancel();
        }
        countDownTimerRunning=true;
        countDownTimer = new CountDownTimer(
                3000
                , 1000) {

            public void onTick(long millisUntilFinished) {
                System.out.println("escribiendo tic " + millisUntilFinished);
            }

            public void onFinish() {
                WrittingDTO dto = new WrittingDTO();
                dto.setNickname(SingletonValues.getInstance().getUsuario().getNickname());
                dto.setIdGrupo(grupo.getIdGrupo());
                grupo.setIamWritting(false);
                countDownTimerRunning=false;
                try {
                    StopWrittingCallRest.call(activity,dto);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        countDownTimer.start();

    }

    public void stop() {
        if ( countDownTimer != null){
            countDownTimer.cancel();

            WrittingDTO dto = new WrittingDTO();
            dto.setNickname(SingletonValues.getInstance().getUsuario().getNickname());
            dto.setIdGrupo(grupo.getIdGrupo());
            try {
                StopWrittingCallRest.call(activity,dto);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        grupo.setIamWritting(false);
        countDownTimerRunning=false;
    }
}