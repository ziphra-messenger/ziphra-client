package com.privacity.cliente.activity.loading;

import android.content.Intent;
import android.util.Log;

import com.privacity.cliente.encrypt.AEStoUse;
import com.privacity.cliente.encrypt.EncryptKeysToUse;
import com.privacity.cliente.encrypt.EncryptUtil;
import com.privacity.cliente.encrypt.RSA;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.util.GsonFormated;
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

import ua.naiksoftware.stomp.StateProcess;

public class LoadingActivityCrearKeysDelegate {
    private final LoadingActivity loadingActivity;

    public LoadingActivityCrearKeysDelegate(LoadingActivity loadingActivity) {
        this.loadingActivity = loadingActivity;
    }

    public void crearKeys() throws Exception {
        loadingActivity.addTextConsole("Getting Login Data" );
        Intent intent = loadingActivity.getIntent();
        String protocoloDTO = intent.getStringExtra("protocoloDTO" );
        String username = intent.getStringExtra("username" );
        String password = intent.getStringExtra("password" );
        loadingActivity.addTextConsole("Creating Personal Encrypt" );
        createPersonalAES(username);

        String prettyJsonString = GsonFormated.get().toJson(protocoloDTO);
        Log.i("ProtocoloDTO  prettyJsonString <<", prettyJsonString);
        LoginDTOResponse l = GsonFormated.get().fromJson(protocoloDTO, LoginDTOResponse.class);
        loadingActivity.addTextConsole("Deserializing Personal Private Key" );
        createPersonalPrivateKey(l);
        createSessionAES(l);

        LoginDataDTO data = l.getLoginDataDTO();
        loadingActivity.addTextConsole("Saving Token" );
        SingletonValues.getInstance().setToken(
                SingletonValues.getInstance().getSystemGralConf().getAuth().getTokenType()
                        + " " + data.getToken());
        SingletonValues.getInstance().setInvitationCode(data.invitationCode);
        SingletonValues.getInstance().setPassword(password);
        SingletonValues.getInstance().setMyAccountConfDTO(data.getMyAccountGralConfDTO());
        SingletonValues.getInstance().setSessionAEStoUseWS(
        new AEStoUse(data.getSessionAESDTOWS().secretKeyAES,
                data.getSessionAESDTOWS().getSaltAES(),
                Integer.parseInt(data.getSessionAESDTOWS().getIteration()),
                SingletonValues.getInstance().getSystemGralConf().getPublicAES().getBits())
        );

        SingletonValues.getInstance().setSessionAEStoUseServerEncrypt(
                new AEStoUse(data.getSessionAESDTOServerEncrypt().getSecretKeyAES(),
                        data.getSessionAESDTOServerEncrypt().getSaltAES(),
                        Integer.parseInt(data.getSessionAESDTOServerEncrypt().getIteration()),
                        SingletonValues.getInstance().getSystemGralConf().getPublicAES().getBits())
        );

        UsuarioDTO u = new UsuarioDTO();
        u.setNickname(data.getNickname());
        u.setIdUsuario(data.getId() + "");


        SingletonValues.getInstance().setUsuario(u);
        loadingActivity.addTextConsole("Deserializing Personal Public Key");
        createPersonalPublicKey(data);

        loadingActivity.setCrearKeys(StateProcess.SUCESS);
        loadingActivity.endProcess();
    }

    void createPersonalPrivateKey(LoginDTOResponse l) throws Exception {

        String privateEncr = l.getPrivateKey();

        byte[] privateDesc = SingletonValues.getInstance().getPersonalAEStoUse().getAESDecrypt(GsonFormated.get().fromJson(privateEncr, byte[].class));

        KeyFactory kf3 = KeyFactory.getInstance("RSA");
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
        byte[] publicDesc = SingletonValues.getInstance().getPersonalAEStoUse().getAESDecrypt(GsonFormated.get().fromJson(publicEncr, byte[].class));
        KeyFactory kf = KeyFactory.getInstance("RSA");

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

        System.out.println("sessionAESKey = " + sessionAESKey);
        System.out.println("sessionAESSalt = " + sessionAESSalt);
        System.out.println("sessionAESIterator = " + sessionAESIterator);
        SingletonValues.getInstance().setSessionAESToUse(new AESDTO(sessionAESKey, sessionAESSalt, sessionAESIterator,
                SingletonValues.getInstance().getSystemGralConf().getMessagingAES().getBits()+""
                ));
    }
}