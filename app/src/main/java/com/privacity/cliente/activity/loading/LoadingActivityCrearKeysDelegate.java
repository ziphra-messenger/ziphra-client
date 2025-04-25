package com.privacity.cliente.activity.loading;

import android.content.Intent;
import android.util.Log;

import com.privacity.cliente.common.constants.IntentConstant;
import com.privacity.cliente.encrypt.AEStoUse;
import com.privacity.cliente.encrypt.EncryptKeysToUse;
import com.privacity.cliente.encrypt.EncryptUtil;
import com.privacity.cliente.encrypt.RSA;
import com.privacity.cliente.singleton.SingletonValues;import com.privacity.cliente.singleton.Singletons;
import com.privacity.cliente.singleton.Singletons;
import com.privacity.cliente.singleton.UtilsStringSingleton;
import com.privacity.cliente.singleton.serverconfiguration.SingletonServerConfiguration;
import com.privacity.common.dto.AESDTO;
import com.privacity.common.dto.LoginDataDTO;
import com.privacity.common.dto.UsuarioDTO;
import com.privacity.common.dto.response.LoginDTOResponse;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.inject.Singleton;

import ua.naiksoftware.stomp.StateProcess;

public class LoadingActivityCrearKeysDelegate {
    private static final String TAG = "LoadingActivityCrearKeysDelegate";
    private final LoadingActivity loadingActivity;

    public LoadingActivityCrearKeysDelegate(LoadingActivity loadingActivity) {
        this.loadingActivity = loadingActivity;
    }

    public void crearKeys() throws Exception {
        loadingActivity.addTextConsole("Getting Login Data" );
        Intent intent = loadingActivity.getIntent();
        String protocoloDTO = intent.getStringExtra(IntentConstant.PROTOCOLO_DTO );
        String username = intent.getStringExtra(IntentConstant.USERNAME);
        String password = intent.getStringExtra(IntentConstant.PASSWORD );
        loadingActivity.addTextConsole("Creating Personal Encrypt" );
        createPersonalAES(username);


        Log.d(TAG, "protocoloDTO entrada :" + protocoloDTO);
        //protocoloDTO = UtilsString.uncompressB64(protocoloDTO);
        //Log.d(TAG, "protocoloDTO uncompressB64 :" + protocoloDTO);

        String prettyJsonString = UtilsStringSingleton.getInstance().gsonToSend(protocoloDTO);
        Log.i("ProtocoloDTO  prettyJsonString <<", prettyJsonString);
        LoginDTOResponse l = UtilsStringSingleton.getInstance().gson().fromJson(protocoloDTO, LoginDTOResponse.class);
        loadingActivity.addTextConsole("Deserializing Personal Private Key" );
        createPersonalPrivateKey(l,username);
        createSessionAES(l);

        LoginDataDTO data = l.getLoginDataDTO();
        loadingActivity.addTextConsole("Saving Token" );
        SingletonValues.getInstance().setToken(
                SingletonServerConfiguration.getInstance().getSystemGralConf().getAuth().getTokenType()
                        + " " + data.getToken());
        Singletons.usuario().setInvitationCode(data.getInvitationCode());
        SingletonValues.getInstance().setPassword(password);



        SingletonValues.getInstance().setMyAccountConfDTO(data.getMyAccountGralConfDTO());
        SingletonValues.getInstance().setSessionAEStoUseWS(
        new AEStoUse(data.getSessionAESDTOWS().getSecretKeyAES(),
                data.getSessionAESDTOWS().getSaltAES(),
                Integer.parseInt(data.getSessionAESDTOWS().getIteration()),
                SingletonServerConfiguration.getInstance().getSystemGralConf().getPublicAES().getBits())
        );

        SingletonValues.getInstance().setSessionAEStoUseServerEncrypt(
                new AEStoUse(data.getSessionAESDTOServerEncrypt().getSecretKeyAES(),
                        data.getSessionAESDTOServerEncrypt().getSaltAES(),
                        Integer.parseInt(data.getSessionAESDTOServerEncrypt().getIteration()),
                        SingletonServerConfiguration.getInstance().getSystemGralConf().getPublicAES().getBits())
        );

        UsuarioDTO u = new UsuarioDTO();
        u.setNickname(data.getNickname());
        u.setIdUsuario(data.getId() + "");


        Singletons.usuario().setUsuario(u);
        loadingActivity.addTextConsole("Deserializing Personal Public Key");
        createPersonalPublicKey(data);

        loadingActivity.setCrearKeys(StateProcess.SUCESS);
        loadingActivity.endProcess();
    }

    void createPersonalPrivateKey(LoginDTOResponse l, String username) throws Exception {
        System.out.println(l.toString());
        String privateEncr = l.getPrivateKey();
        /*System.out.println("Entrada l.toString() : ///" + l.toString()  + "///");
        System.out.println("Entrada l.getPrivateKey() : ///" + l.getPrivateKey()  + "///");

        if (SingletonValues.getInstance().getPersonalAEStoUse() == null){
            System.out.println("personal aes == null");
        }else{
        }*/
       // System.out.println("Entrada l.getPrivateKey() : ///" + l.getPrivateKey()  + "///");
       // MixBytesUtil mix = new MixBytesUtil();

        System.out.println("l.getPrivateKey();-> " + l.getPrivateKey());

//        try {
//            byte[] privateDesc2 = SingletonValues.getInstance().getPersonalAEStoUse().getAESDecryptData(
//                    privateEncr.getBytes());
//            System.out.println("OKKKKKKKKK privateEncr.getBytes();-> " + privateDesc2);
//        }catch (Exception e) {
//            System.out.println("privateEncr.getBytes(); ERRORRRRRRRRRR-> ");
//            e.printStackTrace();
//        };
//        try {
//            String privateDesc2 = SingletonValues.getInstance().getPersonalAEStoUse().getAESDecrypt(
//                    privateEncr.getBytes());
//            System.out.println("OKKKKKKKKK privateEncr.String();-> " + privateDesc2);
//        }catch (Exception e) {
//            System.out.println("privateEncr.String(); ERRORRRRRRRRRR-> ");
//            e.printStackTrace();
//        };
//
//        try {
//            String privateDesc2 = SingletonValues.getInstance().getPersonalAEStoUse().getAESDecrypt(
//                    UtilsStringSingleton.getInstance().gson().fromJson(privateEncr, byte[].class));
//            System.out.println("OKKKKKKKKK 22222.String();-> " + privateDesc2);
//        }catch (Exception e) {
//            System.out.println("22222.String(); ERRORRRRRRRRRR-> ");
//            e.printStackTrace();
//        };

     //   SingletonValues.getInstance().getPersonalAEStoUse().toString()

        byte[] privateDesc = SingletonValues.getInstance().getPersonalAEStoUse().getAESDecryptData(
                UtilsStringSingleton.getInstance().gson().fromJson(privateEncr, byte[].class)
        );
        //privateDesc = mix.unmix(privateDesc, username.length());

        KeyFactory kf3 = KeyFactory.getInstance(RSA.CONSTANT_RSA);
        PKCS8EncodedKeySpec spec3 = new PKCS8EncodedKeySpec(
                privateDesc);
        PrivateKey privateKeyPrivacity = kf3.generatePrivate(spec3);
        System.out.println(privateKeyPrivacity.toString());


        SingletonValues.getInstance().setEncryptKeysToUse(new EncryptKeysToUse());
        SingletonValues.getInstance().getEncryptKeysToUse().setPrivateKey(privateKeyPrivacity);
        SingletonValues.getInstance().getEncryptKeysToUse().setRsa(new RSA());


    }

    void createPersonalPublicKey(LoginDataDTO l) throws Exception {

        String publicEncr = l.getPublicKey();
        byte[] publicDesc = SingletonValues.getInstance().getPersonalAEStoUse().getAESDecryptData(UtilsStringSingleton.getInstance().gson().fromJson(publicEncr, byte[].class));
        KeyFactory kf = KeyFactory.getInstance(RSA.CONSTANT_RSA);

        X509EncodedKeySpec spec2 = new X509EncodedKeySpec(
                publicDesc);
        PublicKey publicKey = kf.generatePublic(spec2);

        SingletonValues.getInstance().getEncryptKeysToUse().setPublicKey(publicKey);


    }

    void createPersonalAES(String username) throws Exception {

        AESDTO personalAES = EncryptUtil.createPersonalAES(username);
        SingletonValues.getInstance().setPersonalAESToUse(personalAES);

    }

    void createSessionAES(LoginDTOResponse l) throws Exception {
        PrivateKey privateKey = SingletonValues.getInstance().getEncryptKeysToUse().getPrivateKey();

        byte[] sessionAESKeyEncrypt = Base64.getDecoder().decode(l.getSessionAESDTO().getSecretKeyAES().getBytes(StandardCharsets.UTF_8));
        byte[] sessionAESSaltEncrypt = Base64.getDecoder().decode(l.getSessionAESDTO().getSaltAES().getBytes(StandardCharsets.UTF_8));
        byte[] sessionAESIteratorEncrypt = Base64.getDecoder().decode(l.getSessionAESDTO().getIteration().getBytes(StandardCharsets.UTF_8));

        byte[] auxKey = SingletonValues.getInstance().getEncryptKeysToUse().getRsa().decryptFilePrivate(sessionAESKeyEncrypt, privateKey);
        String sessionAESKey = new String(auxKey, StandardCharsets.UTF_8);

        byte[] auxKey2 = SingletonValues.getInstance().getEncryptKeysToUse().getRsa().decryptFilePrivate(sessionAESSaltEncrypt, privateKey);
        String sessionAESSalt = new String(auxKey2, StandardCharsets.UTF_8);

        byte[] auxKey3 = SingletonValues.getInstance().getEncryptKeysToUse().getRsa().decryptFilePrivate(sessionAESIteratorEncrypt, privateKey);
        String sessionAESIterator = new String(auxKey3, StandardCharsets.UTF_8);


        SingletonValues.getInstance().setSessionAESToUse((new AESDTO())
                .setBitsEncrypt( SingletonServerConfiguration.getInstance().getSystemGralConf().getMessagingAES().getBits()+"")
                .setIteration(sessionAESIterator)
                .setSecretKeyAES(sessionAESKey)
                .setSaltAES(sessionAESSalt));

    }
}