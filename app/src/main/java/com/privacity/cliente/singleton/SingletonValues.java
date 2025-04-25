package com.privacity.cliente.singleton;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.CountDownTimer;
import android.widget.Toast;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.main.MainActivi2ty;
import com.privacity.cliente.activity.message.ItemListMessage;
import com.privacity.cliente.activity.myaccount.MyAccountLoginSkipFrame;
import com.privacity.cliente.encrypt.AEStoUse;
import com.privacity.cliente.encrypt.AEStoUseFactory;
import com.privacity.cliente.encrypt.EncryptKeysToUse;
import com.privacity.cliente.encrypt.RSA;
import com.privacity.cliente.model.Grupo;
import com.privacity.cliente.singleton.countdown.SingletonMyAccountConfLockDownTimer;
import com.privacity.cliente.singleton.countdown.SingletonPasswordInMemoryLifeTime;
import com.privacity.cliente.singleton.observers.ObserverGrupo;
import com.privacity.cliente.ws.WebSocket;
import com.privacity.common.BroadcastConstant;
import com.privacity.common.SingletonReset;
import com.privacity.common.dto.AESDTO;
import com.privacity.common.dto.MyAccountConfDTO;

import java.security.PrivateKey;
import java.security.PublicKey;

import lombok.Getter;
import lombok.Setter;

public class SingletonValues implements SingletonReset{

    private ItemListMessage messageDetailSeleccionado;
    private String token;


    private String usernameHash;
    private String passwordHash;
    private String usernameNoHash;
    private String passwordNoHash;



    private int counter=0;
    private Bitmap imagenFull;
    private String idGrupoSeleccionado;



    private AEStoUse personalAEStoUse;
    private AEStoUse sessionAEStoUse;
    @Getter
    @Setter
    private AEStoUse sessionAEStoUseWS;
    @Getter
    @Setter
    private AEStoUse sessionAEStoUseServerEncrypt;

    private EncryptKeysToUse encryptKeysToUse;
    private RSA rsa;


    public PublicKey pkRegistro;
    public PrivateKey privateRegistro;

    private WebSocket webSocket;

    private String password;

    private CountDownTimer passwordCountDownTimer;


    public boolean showingLock;
    private final boolean passwordCountDownTimerRunning=false;


    private static SingletonValues instance = new SingletonValues();

    boolean logout = false;

    private MyAccountConfDTO myAccountConfDTO = new MyAccountConfDTO();




    public String getUsernameNoHash() {
        return usernameNoHash;
    }

    public void setUsernameNoHash(String usernameNoHash) {
        this.usernameNoHash = usernameNoHash;
    }

    public String getPasswordNoHash() {
        return passwordNoHash;
    }

    public void setPasswordNoHash(String passwordNoHash) {
        this.passwordNoHash = passwordNoHash;
    }



    public String getUsernameHash() {
        return usernameHash;
    }

    public void setUsernameHash(String usernameHash) {
        this.usernameHash = usernameHash;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }




    public RSA getRsa() {
        return rsa;
    }

    public void setRsa(RSA rsa) {
        this.rsa = rsa;
    }



    public EncryptKeysToUse getEncryptKeysToUse() {
        return encryptKeysToUse;
    }

    public void setEncryptKeysToUse(EncryptKeysToUse encryptKeysToUse) {
        this.encryptKeysToUse = encryptKeysToUse;
    }



    public AEStoUse getPersonalAEStoUse() {
        return personalAEStoUse;
    }

    public void setPersonalAEStoUse(AEStoUse personalAEStoUse) {
        this.personalAEStoUse = personalAEStoUse;
    }

    public void setPersonalAESToUse(AESDTO param) {
        try {
            personalAEStoUse = AEStoUseFactory.getAEStoUsePersonal
                    (param);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public AEStoUse getSessionAEStoUse() {
        return sessionAEStoUse;
    }

    public void setSessionAEStoUse(AEStoUse sessionAEStoUse) {
        this.sessionAEStoUse = sessionAEStoUse;
    }

    public void setSessionAESToUse(AESDTO param) {
        try {
            sessionAEStoUse = AEStoUseFactory.getAEStoUseSession
                    (param.getSecretKeyAES(), param.getSaltAES(), Integer.parseInt(param.getIteration()));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    public ItemListMessage getMessageDetailSeleccionado() {

        return messageDetailSeleccionado;
    }

    public void setMessageDetailSeleccionado(ItemListMessage messageDetailSeleccionado) {
        this.messageDetailSeleccionado = messageDetailSeleccionado;
    }



    public Bitmap getImagenFull() {
        return imagenFull;
    }

    public void setImagenFull(Bitmap imagenFull) {
        this.imagenFull = imagenFull;
    }

    public WebSocket getWebSocket() {
        return webSocket;
    }

    public void setWebSocket(WebSocket webSocket) {
        this.webSocket = webSocket;
    }



    public boolean isPasswordCountDownTimerRunning() {
        return passwordCountDownTimerRunning;
    }
    public CountDownTimer getPasswordCountDownTimer() {
        return passwordCountDownTimer;
    }


    public boolean isShowingLock() {
        return showingLock;
    }

    public void setShowingLock(boolean showingLock) {
        this.showingLock = showingLock;
    }
/*
    private PasswordShortLiveCountDownTimer passwordShortLiveCountDownTimer = new PasswordShortLiveCountDownTimer();

    public PasswordShortLiveCountDownTimer getPasswordShortLiveCountDownTimer() {
        return passwordShortLiveCountDownTimer;
    }

    public void passwordCountDownTimerRestart(){
        //mirar la memoria
        System.out.println("Password down -> Reset  ");
        if ( passwordCountDownTimer != null){
            passwordCountDownTimer.cancel();
        }
        if (myAccountConfDTO == null) return;

        if (myAccountConfDTO.getLock() == null) return;
        if (!myAccountConfDTO.getLock().isEnabled()){
            passwordCountDownTimerRunning=false;
            System.out.println("Password down -> No enabled  ");
            return;
        }

        passwordCountDownTimer = new CountDownTimer(
                SingletonValues.getInstance().getMyAccountConfDTO().getLock().getSeconds()*1000
                , SingletonValues.getInstance().getMyAccountConfDTO().getLock().getSeconds()*100

                 ) {

            public void onTick(long millisUntilFinished) {
                System.out.println("Password down -> " + millisUntilFinished );
            }

            public void onFinish() {
                Observers.password().passwordExpired();
                passwordCountDownTimerRunning=false;
            }
        };
        passwordCountDownTimerRunning=true;
        passwordCountDownTimer.start();
        Observers.password().passwordSet();
    }
*/

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public synchronized String getCounterNextValue() {
        counter++;
        return counter+"";
    }



        public static SingletonValues getInstance() {
            if (instance == null){
                instance = new SingletonValues();
            }
            return instance;
        }

        private SingletonValues() { }

    public boolean isLogout() {
        return logout;
    }

    public void setLogout(boolean logout) {
        this.logout = logout;
    }


    public Grupo getGrupoSeleccionado() {


        if ( idGrupoSeleccionado == null) return null;
        if ( ObserverGrupo.getInstance().getGrupoById(idGrupoSeleccionado) == null){
            idGrupoSeleccionado=null;
            return null;
        }

        ObserverGrupo.getInstance().getGrupoById(idGrupoSeleccionado).getCountDownTimer().restart();
        return ObserverGrupo.getInstance().getGrupoById(idGrupoSeleccionado);
    }

    public void setGrupoSeleccionado(Grupo grupoSeleccionado) {

        if  (grupoSeleccionado == null || grupoSeleccionado.getIdGrupo() == null){
            idGrupoSeleccionado=null;
            return;
        }
        idGrupoSeleccionado = grupoSeleccionado.getIdGrupo();

        ObserverGrupo.getInstance().getGrupoById(idGrupoSeleccionado).getCountDownTimer().restart();

        // ObserverGrupo.getInstance().getGrupoById(idGrupoSeleccionado);



    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public MyAccountConfDTO getMyAccountConfDTO() {
        return myAccountConfDTO;
    }

    public void setMyAccountConfDTO(MyAccountConfDTO myAccountConfDTO) {
        this.myAccountConfDTO = myAccountConfDTO;
    }







    /**
     * Get the method name for a depth in call stack. <br />
     * Utility function
     * @param depth depth in the call stack (0 means current method, 1 means call method, ...)
     * @return method name
     */
    public static String getMethodName(final int depth)
    {
        final StackTraceElement[] ste = Thread.currentThread().getStackTrace();

        //System. out.println(ste[ste.length-depth].getClassName()+"#"+ste[ste.length-depth].getMethodName());
        // return ste[ste.length - depth].getMethodName();  //Wrong, fails for depth = 0
        return ste[depth].getMethodName(); //Thank you Tom Tresansky
    }

    @Override
    public void reset() {
        instance = null;
    }

    public static final int CONSTANT__MAX__REINTENTOS = 3;
    int reintentos=0;

    public void getPasswordReintentosAdd(Activity activity) {
        reintentos++;


        //currentPassword.getField().setError("Password Incorrecto");
        Toast.makeText(activity,
                activity.getString(R.string.lock_activity__validation__password__fail,reintentos+"", SingletonValues.CONSTANT__MAX__REINTENTOS+"")
                ,Toast. LENGTH_SHORT).show();

        if (reintentos >= SingletonValues.CONSTANT__MAX__REINTENTOS){

            try {
                if (SingletonValues.getInstance().getMyAccountConfDTO().isLoginSkip()){
                    MyAccountLoginSkipFrame.saveDisabled(activity);
                }else{
                    Intent intent = new Intent(BroadcastConstant.BROADCAST__FINISH_ALL_ACTIVITIES);
                    activity.sendBroadcast(intent);
                    activity.finish();
                    Intent i = new Intent(activity, MainActivi2ty.class);
                    activity.startActivity(i);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
    public void getPasswordReintentosReset() {
        SingletonPasswordInMemoryLifeTime.getInstance().restart();
        SingletonMyAccountConfLockDownTimer.getInstance().restart();

        reintentos=0;
    }


}
