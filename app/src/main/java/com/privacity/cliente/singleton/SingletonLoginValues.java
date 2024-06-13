package com.privacity.cliente.singleton;

import com.privacity.cliente.encrypt.AEStoUse;
import com.privacity.cliente.singleton.interfaces.SingletonReset;
import com.privacity.common.dto.AESDTO;

import java.security.PublicKey;

import lombok.Data;

@Data
public class SingletonLoginValues implements SingletonReset {

    private PublicKey publicKeyServer;
    private AEStoUse AEStoUse;
    private AESDTO AEStoSend;

    private static SingletonLoginValues instance;

    @Override
    public void reset() {
        instance = null;
    }

    public static SingletonLoginValues getInstance() {
        if (instance == null){
            instance = new SingletonLoginValues();
        }
        return instance;
    }

    private SingletonLoginValues() { }
}
