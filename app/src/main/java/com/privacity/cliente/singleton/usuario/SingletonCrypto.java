package com.privacity.cliente.singleton.usuario;

import com.privacity.cliente.encrypt.AEStoUse;
import com.privacity.cliente.encrypt.EncryptKeysToUse;
import com.privacity.cliente.encrypt.RSA;
import com.privacity.cliente.singleton.serverconfiguration.SingletonServerConfiguration;
import com.privacity.common.dto.AESDTO;
import com.privacity.common.util.PersonalAesGenerator;

public class SingletonCrypto {
    public final static String SERVER_IN="SERVER_IN";
    public final static String SERVER_OUT="SERVER_OUT";
    public final static String SERVER_WS="SERVER_WS";
    public final static String PERSONAL="PERSONAL";


    private AEStoUse sessionAESDTOServerIn;
    private AEStoUse sessionAESDTOWS;
    private AEStoUse sessionAESDTOServerOut;
    private AEStoUse personalAEStoUse;
    private EncryptKeysToUse personalEncryptKeysToUse;

    private RSA rsa;
    private static SingletonCrypto instance;

    public static SingletonCrypto getInstance() {

        if (instance == null){
            instance = new SingletonCrypto();
        }
        return instance;
    }

    private SingletonCrypto() { }

    public void setPersonalAEStoUse(String input) {
        this.personalAEStoUse = personalAEStoUse;
        setAEStoUse(SingletonCrypto.PERSONAL, (new PersonalAesGenerator()).a1(SingletonServerConfiguration.getInstance().getSystemGralConf().getPersonalAES().getBits(), input));
    }

    public void setAEStoUse(String constant, AESDTO param) {
        if ( constant.equals(SingletonCrypto.PERSONAL)){
            buildAes(param,this.personalAEStoUse);
        }else if ( constant.equals(SingletonCrypto.SERVER_IN)){
            buildAes(param,this.sessionAESDTOServerIn);
        }else if ( constant.equals(SingletonCrypto.SERVER_OUT)){
            buildAes(param,this.sessionAESDTOServerOut);
        }else if ( constant.equals(SingletonCrypto.SERVER_WS)){
            buildAes(param,this.sessionAESDTOWS);
        }
    }

    private void buildAes(AESDTO param, AEStoUse destino) {

        try {
            destino = new AEStoUse
                    (param.getSecretKeyAES(), param.getSaltAES(), Integer.parseInt(param.getIteration()),
                            SingletonServerConfiguration.getInstance().getSystemGralConf().getSessionAES().getBits());
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
