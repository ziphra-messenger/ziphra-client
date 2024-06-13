package com.privacity.cliente.encrypt;

import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.common.dto.AESDTO;
import com.privacity.common.dto.servergralconf.SGCAESSimple;

public class AEStoUseFactory {

    public static AEStoUse getAEStoUseExtra(AESDTO a) throws Exception {
        return getAEStoUseExtra(a.getSecretKeyAES(), a.getSaltAES());
    }
    public static AEStoUse getAEStoUseExtra(String secretKeyAES, String saltAES) throws Exception {
        SGCAESSimple extra = SingletonValues.getInstance().getSystemGralConf().getExtraAES();
        return new AEStoUse(secretKeyAES,saltAES,extra.getIteration(), extra.getBits());
    }

    public static AEStoUse getAEStoUsePersonal(AESDTO a) throws Exception {
        return getAEStoUsePersonal(a.getSecretKeyAES(), a.getSaltAES());
    }
    public static AEStoUse getAEStoUsePersonal(String secretKeyAES, String saltAES) throws Exception {
        SGCAESSimple personal = SingletonValues.getInstance().getSystemGralConf().getPersonalAES();
        return new AEStoUse(secretKeyAES,saltAES,personal.getIteration(), personal.getBits());
    }

    public static AEStoUse getAEStoUsePublic(AESDTO a) throws Exception {
        return getAEStoUsePublic(a.getSecretKeyAES(), a.getSaltAES(), Integer.parseInt(a.getIteration()));
    }

    public static AEStoUse getAEStoUsePublic(String secretKeyAES, String saltAES, int iteration) throws Exception {
            int bits = SingletonValues.getInstance().getSystemGralConf().getPublicAES().getBits();
        	return new AEStoUse(secretKeyAES,saltAES,iteration, bits);
    }

    public static AEStoUse getAEStoUseMessaging(AESDTO a) throws Exception {
        return getAEStoUseMessaging(a.getSecretKeyAES(), a.getSaltAES(), Integer.parseInt(a.getIteration()));
    }

    public static AEStoUse getAEStoUseMessaging(String secretKeyAES, String saltAES, int iteration) throws Exception {
        int bits = SingletonValues.getInstance().getSystemGralConf().getMessagingAES().getBits();
        return new AEStoUse(secretKeyAES,saltAES,iteration, bits);
    }

    public static AEStoUse getAEStoUseInvitation(AESDTO a) throws Exception {
        return getAEStoUseInvitation(a.getSecretKeyAES(), a.getSaltAES(), Integer.parseInt(a.getIteration()));
    }
    public static AEStoUse getAEStoUseInvitation(String secretKeyAES, String saltAES, int iteration) throws Exception {
        int bits = SingletonValues.getInstance().getSystemGralConf().getInvitationAES().getBits();
        return new AEStoUse(secretKeyAES,saltAES,iteration, bits);
    }

    public static AEStoUse getAEStoUseSession(AESDTO a) throws Exception {
        return getAEStoUseSession(a.getSecretKeyAES(), a.getSaltAES(), Integer.parseInt(a.getIteration()));
    }
    public static AEStoUse getAEStoUseSession(String secretKeyAES, String saltAES, int iteration) throws Exception {
        int bits = SingletonValues.getInstance().getSystemGralConf().getSessionAES().getBits();
        return new AEStoUse(secretKeyAES,saltAES,iteration, bits);
    }
}
