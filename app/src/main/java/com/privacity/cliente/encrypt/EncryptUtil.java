package com.privacity.cliente.encrypt;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.GsonBuilder;
import com.privacity.cliente.model.Grupo;
import com.privacity.cliente.singleton.SingletonValues;import com.privacity.cliente.singleton.Singletons;
import com.privacity.cliente.singleton.serverconfiguration.SingletonServerConfiguration;
import com.privacity.common.adapters.LocalDateAdapter;
import com.privacity.common.dto.AESDTO;
import com.privacity.common.dto.EncryptKeysDTO;
import com.privacity.common.util.PersonalAesGenerator;

import org.apache.commons.lang3.RandomStringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.KeySpec;
import java.time.LocalDateTime;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class EncryptUtil {
    private static final String TAG = "EncryptUtil";
    private static final String salt= "nqeveveo!#$%/##%#8vr126#$#&$/%&/84ewevRLEER23$\"#=424\"##\"%";

    public static void generateSecretPersonalKeyListener(Button button, TextView view){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.setText( RandomStringUtils.randomAlphanumeric((int)Math.floor(Math.random()*(15-10+1)+10)));
            }
        });
    }
    public static AEStoUse iniciarEncrypt(Grupo g) throws Exception {

        PrivateKey privateKey = SingletonValues.getInstance().getEncryptKeysToUse().getPrivateKey();

        return AEStoUseFactory.getAEStoUseMessaging(AESEncrypt.decrypt(g.getUserForGrupoDTO().getAesDTO(), privateKey));
    }

    public static String toHash(String txt) throws Exception {
        //KeySpec spec = new PBEKeySpec(txt.toCharArray(), salt.getBytes(), 65536, 128);
        KeySpec spec = new PBEKeySpec(txt.toCharArray(), salt.getBytes(),
                SingletonServerConfiguration.getInstance().getSystemGralConf().getPersonalAES().getIteration(),
                SingletonServerConfiguration.getInstance().getSystemGralConf().getPersonalAES().getBits());
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = f.generateSecret(spec).getEncoded();
        Base64.Encoder enc = Base64.getEncoder();
        return enc.encodeToString(hash);
    }

    public static AESDTO createPersonalAES(String e){

        AESDTO ret = (new PersonalAesGenerator()).a1(SingletonServerConfiguration.getInstance().getSystemGralConf().getPersonalAES().getBits(), e);
        Log.d(TAG, "Personal Aes DTO : " + ret.toStringComplete());
        return ret;

    }
    public static String gsonToSend(Object s) {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
                .create().toJson(s).replace("\n", "").replace("\t", "").replace("\r", "").replace("\b", "").replace("\f", "")
                .replace(" ", "");

    }

    public static EncryptKeysDTO invitationCodeEncryptKeysGenerator(AEStoUse personalAEStoUse) throws NoSuchAlgorithmException, NoSuchProviderException {
        RSA t = new RSA();
        KeyPair keyPair = null;

        keyPair = t.generateKeyPair();

        byte[] privateKey = keyPair.getPrivate().getEncoded();
        byte[] publicKey = keyPair.getPublic().getEncoded();

        Log.d(TAG, "invitationCodeEncryptKeysGenerator: " + gsonToSend(privateKey));

        EncryptKeysDTO encryptKeysDTO =  new EncryptKeysDTO();
        encryptKeysDTO.setPrivateKey(gsonToSend(personalAEStoUse.getAES(privateKey)));
        encryptKeysDTO.setPublicKey(null);
        encryptKeysDTO.setPublicKeyNoEncrypt(gsonToSend(publicKey));
        return encryptKeysDTO;
    }


//    public static EncryptKeysToUse generateEncryptKeyFromString(EncryptKeysDTO encryptKey) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeySpecException {
//        EncryptKeysToUse r = new EncryptKeysToUse();
//
//        RSA t = new RSA();
//        KeyFactory kf = KeyFactory.getInstance(RSA.CONSTANT_RSA);
//
//        if (encryptKey.getPrivateKey() != null){
//            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec( encryptKey.getPrivateKey().getBytes());
//
//            PrivateKey privateKey = kf.generatePrivate(spec);
//            r.setPrivateKey(privateKey);
//        }
//
//        if (encryptKey.getPublicKey() != null){
//            X509EncodedKeySpec spec2 = new X509EncodedKeySpec( encryptKey.getPublicKey().getBytes());
//            PublicKey publicKey = kf.generatePublic(spec2);
//
//            r.setPublicKey(publicKey);
//
//        }
//
//        if (encryptKey.getPublicKeyNoEncrypt() != null){
//            X509EncodedKeySpec spec2 = new X509EncodedKeySpec( encryptKey.getPublicKeyNoEncrypt().getBytes());
//            PublicKey publicKey = kf.generatePublic(spec2);
//
//            r.setPublicKeyNoEncrypt(publicKey);
//
//        }
//        r.setRsa(t);
//        return r;
//    }

    public static String EncryptKeyEncrypt(EncryptKeysToUse ekTouse, PublicKey publicKey,  String data) throws IOException, GeneralSecurityException {
        byte[] enc = ekTouse.getRsa().encryptFilePublic(data.getBytes(), publicKey);
        String encode = android.util.Base64.encodeToString(enc, android.util.Base64.DEFAULT);

        return encode;


    }

    public static String encryptKeyDesencrypt(EncryptKeysToUse ekTouse, String data) throws IOException, GeneralSecurityException {
        return encryptKeyDesencrypt(ekTouse.getPrivateKey(), data);
    }

    public static String encryptKeyDesencrypt(PrivateKey privateKey, String data) throws IOException, GeneralSecurityException {
        byte[] decoded64 = android.util.Base64.decode(data, android.util.Base64.DEFAULT);
        byte[] des = new RSA().decryptFilePrivate(decoded64, privateKey);

        return new String(des, StandardCharsets.UTF_8);
    }

    public static AESDTO encriptarAES(AESDTO aesdto, EncryptKeysToUse ekTouse, PublicKey publicKey) throws IOException, GeneralSecurityException {
        AESDTO r = new AESDTO();
        r.setSecretKeyAES(EncryptUtil.EncryptKeyEncrypt(ekTouse, publicKey,aesdto.getSecretKeyAES()));
        r.setSaltAES(EncryptUtil.EncryptKeyEncrypt(ekTouse, publicKey,aesdto.getSaltAES()));
        r.setIteration(EncryptUtil.EncryptKeyEncrypt(ekTouse, publicKey,aesdto.getIteration()));

        return r;
    }



}
