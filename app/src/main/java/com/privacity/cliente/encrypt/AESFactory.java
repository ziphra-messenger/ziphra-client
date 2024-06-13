package com.privacity.cliente.encrypt;

import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.common.dto.AESDTO;
import com.privacity.common.dto.servergralconf.SGCAESDTO;
import com.privacity.common.util.RandomGenerator;

import org.jetbrains.annotations.NotNull;

public class AESFactory {
    @NotNull
    private static AESDTO build(SGCAESDTO a) {
        AESDTO r = new AESDTO();
        r.setSecretKeyAES(RandomGenerator.AESKey(a));
        r.setSaltAES(RandomGenerator.AESSalt(a));
        r.setIteration(RandomGenerator.AESIteration(a)+"");
        return r;
    }
    public static AESDTO getAESPublic(){
        SGCAESDTO a = SingletonValues.getInstance().getSystemGralConf().getPublicAES();
        return build(a);
    }

    public static AESDTO getAESMessaging(){
        SGCAESDTO a = SingletonValues.getInstance().getSystemGralConf().getMessagingAES();
        return build(a);
    }

    public static AESDTO getAESInvitation(){
        SGCAESDTO a = SingletonValues.getInstance().getSystemGralConf().getInvitationAES();
        return build(a);
    }

    public static AESDTO getAESSession(){
        SGCAESDTO a = SingletonValues.getInstance().getSystemGralConf().getSessionAES();
        return build(a);
    }
}
