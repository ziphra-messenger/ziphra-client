package com.privacity.cliente.encrypt;

import com.privacity.cliente.singleton.serverconfiguration.SingletonServerConfiguration;
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
        SGCAESDTO a = SingletonServerConfiguration.getInstance().getSystemGralConf().getPublicAES();
        return build(a);
    }

    public static AESDTO getAESMessaging(){
        SGCAESDTO a = SingletonServerConfiguration.getInstance().getSystemGralConf().getMessagingAES();
        return build(a);
    }

    public static AESDTO getAESInvitation(){
        SGCAESDTO a = SingletonServerConfiguration.getInstance().getSystemGralConf().getInvitationAES();
        return build(a);
    }

    public static AESDTO getAESSession(){
        SGCAESDTO a = SingletonServerConfiguration.getInstance().getSystemGralConf().getSessionAES();
        return build(a);
    }
}
