package com.privacity.cliente.encrypt;

import com.privacity.cliente.singleton.SingletonValues;import com.privacity.cliente.singleton.Singletons;
import com.privacity.common.dto.AESDTO;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

public class AESEncrypt {

    public static AESDTO messaging(AESDTO aesdto) throws IOException, GeneralSecurityException {
        return encrypt(aesdto, SingletonValues.getInstance().getEncryptKeysToUse().getPublicKey());
    }
    public static AESDTO encrypt(AESDTO aesdto, PublicKey publicKey) throws IOException, GeneralSecurityException {
        AESDTO r = new AESDTO();

        RSA t = new RSA();
        {
            byte[] enc = t.encryptFilePublic(aesdto.getSecretKeyAES().getBytes(), publicKey);
            r.setSecretKeyAES(Base64.getEncoder().encodeToString(enc));
        }

        {
            byte[] enc = t.encryptFilePublic(aesdto.getSaltAES().getBytes(), publicKey);
            r.setSaltAES(Base64.getEncoder().encodeToString(enc));
        }
        {
            byte[] enc = t.encryptFilePublic(aesdto.getIteration().getBytes(), publicKey);
            r.setIteration(Base64.getEncoder().encodeToString(enc));
        }

        return r;
    }

    public static AESDTO decrypt(AESDTO aesdto, PrivateKey privateKey) throws IOException, GeneralSecurityException {
        RSA t = new RSA();
        android.util.Base64.decode(aesdto.getSecretKeyAES(), android.util.Base64.DEFAULT);

        t.decryptFilePrivate(android.util.Base64.decode(aesdto.getSecretKeyAES(), android.util.Base64.DEFAULT), privateKey);

        byte[] decoded64 = android.util.Base64.decode(aesdto.getSecretKeyAES(), android.util.Base64.DEFAULT);
        byte[] des = t.decryptFilePrivate(decoded64, privateKey);

        byte[] saltdecoded64 = android.util.Base64.decode(aesdto.getSaltAES(), android.util.Base64.DEFAULT);
        byte[] saltdes = t.decryptFilePrivate(saltdecoded64, privateKey);

        byte[] iterationdecoded64 = android.util.Base64.decode(aesdto.getIteration(), android.util.Base64.DEFAULT);
        byte[] iterationdes = t.decryptFilePrivate(iterationdecoded64, privateKey);

        AESDTO r  = new AESDTO();
        r.setSecretKeyAES(new String (des, StandardCharsets.UTF_8));
        r.setSaltAES(new String (saltdes, StandardCharsets.UTF_8));
        r.setIteration(new String (iterationdes, StandardCharsets.UTF_8));
        return r;
    }

}
