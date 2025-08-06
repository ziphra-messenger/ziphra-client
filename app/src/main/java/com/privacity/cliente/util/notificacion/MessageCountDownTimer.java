package com.privacity.cliente.util.notificacion;

import android.os.CountDownTimer;

import com.privacity.cliente.model.Message;
import com.privacity.cliente.singleton.Observers;

import lombok.Data;

@Data
public class MessageCountDownTimer {

    private final int messeconds;
    private CountDownTimer timeMessageCountDownTimer;
    private boolean timeMessageCountDownTimerRunning =false;

    @Override
    public String toString() {
        return "MessageCountDownTimer{" +
                "timeMessageCountDownTimer=" + timeMessageCountDownTimer +
                ", timeMessageCountDownTimerRunning=" + timeMessageCountDownTimerRunning +
                ", seconds=" + seconds +
                ", deleted=" + deleted +
                '}';
    }


    private long seconds;
    private boolean deleted=false;

    private boolean isTime;
    private String idGrupo;
    private String buildIdMessageToMap;
    public MessageCountDownTimer(Message m){
        isTime=m.amITimeMessage();
        idGrupo=m.getIdGrupo();
        buildIdMessageToMap=m.buildIdMessageToMap();
        seconds=m.getTimeMessage();
        messeconds = m.getTimeMessage();
    }
    public void restart(){
        //mirar la memoria
        if (! isTime){
            return;
        }

        if ( timeMessageCountDownTimer != null ){
            return;
        }

        timeMessageCountDownTimer = new CountDownTimer(
                messeconds*1000
                , 1000) {

            public void onTick(long millisUntilFinished) {
                seconds = millisUntilFinished/1000;
                System.out.println("counting message > " + millisUntilFinished);
            }

            public void onFinish() {
                deleted=true;
                Observers.message().removeMessage(idGrupo, buildIdMessageToMap);
            }
        };
        timeMessageCountDownTimerRunning =true;
        timeMessageCountDownTimer.start();
    }

}
