package com.privacity.cliente.util.notificacion;

import android.os.CountDownTimer;

import com.privacity.cliente.singleton.Observers;
import com.privacity.cliente.model.Message;

import lombok.Data;

@Data
public class MessageCountDownTimer {

    private CountDownTimer timeMessageCountDownTimer;
    private boolean timeMessageCountDownTimerRunning =false;
    private Message message;
    private long seconds;
    private boolean deleted=false;
    public MessageCountDownTimer(Message m){
        message=m;
        seconds = m.getTimeMessage();
    }
    public void restart(){
        //mirar la memoria
        if (! message.isTimeMessage()){
            return;
        }

        if ( timeMessageCountDownTimer != null ){
            return;
        }

        timeMessageCountDownTimer = new CountDownTimer(
                message.getTimeMessage()*1000
                , 1000) {

            public void onTick(long millisUntilFinished) {
                seconds = millisUntilFinished/1000;
                System.out.println("counting message > " + millisUntilFinished);
            }

            public void onFinish() {
                deleted=true;
                Observers.message().removeMessage(message.getIdGrupo(), message.buildIdMessageToMap());
            }
        };
        timeMessageCountDownTimerRunning =true;
        timeMessageCountDownTimer.start();
    }

}
