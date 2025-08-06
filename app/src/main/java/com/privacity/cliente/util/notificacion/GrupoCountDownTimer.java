package com.privacity.cliente.util.notificacion;

import android.os.CountDownTimer;

import com.privacity.cliente.singleton.Observers;
import com.privacity.cliente.singleton.observers.ObserverGrupo;

import lombok.Data;

@Data
public class GrupoCountDownTimer {

    private CountDownTimer passwordCountDownTimer;
    private boolean passwordCountDownTimerRunning=false;
    public boolean showingLock;
    String idGrupo;
    public void cancel(){
        if ( passwordCountDownTimer != null){
            passwordCountDownTimer.cancel();
        }
        passwordCountDownTimerRunning=false;
    }
    public GrupoCountDownTimer(String idGrupo){
        this.idGrupo = idGrupo;


    }
    public void finish(){
        if ( passwordCountDownTimer != null){
            passwordCountDownTimer.onFinish();
            this.passwordCountDownTimerRunning=false;
            passwordCountDownTimer.cancel();
        }
    }
    public void restart(){
        //mirar la memoria

        if ( passwordCountDownTimer != null){
            passwordCountDownTimer.cancel();
        }


        if (  !ObserverGrupo.getInstance().getGrupoById(idGrupo).getLock().isEnabled()){



            return;
        }
        if (ObserverGrupo.getInstance().getGrupoById(idGrupo).isGrupoLocked()){
            return;
        }
        passwordCountDownTimer = new CountDownTimer(
                ObserverGrupo.getInstance().getGrupoById(idGrupo).getLock().getSeconds()*1000
                , 1000) {

            public void onTick(long millisUntilFinished) {
                if (ObserverGrupo.getInstance().getGrupoById(idGrupo).isGrupoLocked()){
                    this.cancel();
                }
                System.out.println("" +
                        "restart pg > " + millisUntilFinished);
            }

            public void onFinish() {

                if (ObserverGrupo.getInstance().getGrupoById(idGrupo).getPassword().isDeleteExtraEncryptEnabled()){
                    Observers.passwordGrupo().deleteExtraEncrypt(ObserverGrupo.getInstance().getGrupoById(idGrupo));
                }
                if (ObserverGrupo.getInstance().getGrupoById(idGrupo).getPassword().isEnabled()){
                    Observers.passwordGrupo().passwordExpired(ObserverGrupo.getInstance().getGrupoById(idGrupo));
                }

                ObserverGrupo.getInstance().avisarLock(ObserverGrupo.getInstance().getGrupoById(idGrupo));
                ObserverGrupo.getInstance().getGrupoById(idGrupo).setGrupoLocked(true);
                passwordCountDownTimerRunning=false;
            }
        };

        passwordCountDownTimerRunning=true;
        passwordCountDownTimer.start();
//        ObservatorPassword.getInstance().passwordSet();
    }

}
