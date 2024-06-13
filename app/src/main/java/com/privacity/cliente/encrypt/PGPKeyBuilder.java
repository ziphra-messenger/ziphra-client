package com.privacity.cliente.encrypt;

import com.privacity.cliente.singleton.SingletonValues;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class PGPKeyBuilder {
    
    public static PublicKey publicKey(byte[] publicKeyByte) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory kf = KeyFactory.getInstance(
                SingletonValues.getInstance().getSystemGralConf().getAsymEncrypt().getType()
                );
        X509EncodedKeySpec spec2 = new X509EncodedKeySpec(publicKeyByte);
        return kf.generatePublic(spec2);
    }
}
