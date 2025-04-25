package com.privacity.cliente.rest.restcalls.invitation;

import android.app.Activity;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.grupo.ItemListGrupo;
import com.privacity.cliente.encrypt.EncryptKeysToUse;
import com.privacity.cliente.encrypt.EncryptUtil;
import com.privacity.cliente.encrypt.RSA;
import com.privacity.cliente.includes.ProgressBarUtil;
import com.privacity.cliente.model.Grupo;
import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.cliente.singleton.Observers;
import com.privacity.cliente.singleton.SingletonValues;import com.privacity.cliente.singleton.Singletons;
import com.privacity.cliente.singleton.UtilsStringSingleton;
import com.privacity.cliente.singleton.serverconfiguration.SingletonServerConfiguration;
import com.privacity.common.dto.AESDTO;
import com.privacity.common.dto.EncryptKeysDTO;
import com.privacity.common.dto.GrupoDTO;
import com.privacity.cliente.model.dto.Protocolo;
import com.privacity.common.dto.request.GrupoInvitationAcceptRequestDTO;
import com.privacity.common.enumeration.ProtocoloActionsEnum;
import com.privacity.common.enumeration.ProtocoloComponentsEnum;

import org.springframework.http.ResponseEntity;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

public class AcceptInvitationCallRest {

    private static final String TAG = "AcceptInvitationCallRest";

    public static void call(Activity activity, ProgressBar progressBar , ItemListGrupo item) throws Exception {
        Grupo grupo = item.getGrupo();

        EncryptKeysDTO encryptKeysDTO = new EncryptKeysDTO();

        byte[] desencriptado = SingletonValues.getInstance().getPersonalAEStoUse().getAESDecryptData(
                UtilsStringSingleton.getInstance().gson().fromJson(grupo.getGrupoInvitationDTO().getPrivateKey(), byte[].class));

        //Log.d(TAG,"call desencriptado: " + desencriptado;

                EncryptKeysToUse encryptKeysToUseGrupo = new EncryptKeysToUse();

        RSA t = new RSA();
        encryptKeysToUseGrupo.setRsa(t);
        KeyFactory kf = KeyFactory.getInstance(RSA.CONSTANT_RSA);

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec( desencriptado);

        PrivateKey privateKey = kf.generatePrivate(spec);
        encryptKeysToUseGrupo.setPrivateKey(privateKey);

        String secretKeyAESDescr = EncryptUtil.encryptKeyDesencrypt(encryptKeysToUseGrupo,grupo.getGrupoInvitationDTO().getAesDTO().getSecretKeyAES());
        String saltAESDescr = EncryptUtil.encryptKeyDesencrypt(encryptKeysToUseGrupo,grupo.getGrupoInvitationDTO().getAesDTO().getSaltAES());
        String iteratorAESDescr = EncryptUtil.encryptKeyDesencrypt(encryptKeysToUseGrupo,grupo.getGrupoInvitationDTO().getAesDTO().getIteration());

        AESDTO aesGrupoDTO = EncryptUtil.encriptarAES((new AESDTO())
                .setBitsEncrypt( SingletonServerConfiguration.getInstance().getSystemGralConf().getMessagingAES().getBits()+"")
                .setIteration(iteratorAESDescr)
                .setSecretKeyAES(secretKeyAESDescr)
                .setSaltAES(saltAESDescr)
                ,
                SingletonValues.getInstance().getEncryptKeysToUse(),
                SingletonValues.getInstance().getEncryptKeysToUse().getPublicKey()
        );
        //encriptar los aes y enviar al servidor;

        Protocolo p = new Protocolo();
        p.setComponent(ProtocoloComponentsEnum.GRUPO);
        p.setAction(ProtocoloActionsEnum.GRUPO_ACCEPT_INVITATION);

        GrupoInvitationAcceptRequestDTO o = new GrupoInvitationAcceptRequestDTO();
        o.setIdGrupo(grupo.getIdGrupo());
        o.setAesDTO(aesGrupoDTO);
        o.setIdUsuarioInvitado(Singletons.usuario().getUsuario().getIdUsuario());
        o.setIdUsuarioInvitante(item.getGrupo().getGrupoInvitationDTO().getUsuarioInvitante().getIdUsuario());
        p.setObjectDTO(UtilsStringSingleton.getInstance().gsonToSend(o));

        //ObservatorGrupos.getInstance().avisarGrupoRemove(grupoDTO);
        RestExecute.doit(activity, p,
                new CallbackRest() {

                    @Override
                    public void response(ResponseEntity<Protocolo> response) {

                        GrupoDTO l = UtilsStringSingleton.getInstance().gson().fromJson(response.getBody().getObjectDTO(), GrupoDTO.class);

                        Observers.grupo().updateGrupoAcceptInvitation(l);
                        ProgressBarUtil.hide(activity, progressBar);
                        Toast toast=Toast. makeText(activity,activity.getString(R.string.accept_invitation_callrest__invitation_accepted),Toast. LENGTH_SHORT);
                        toast.show();
                    }

                    @Override
                    public void onError(ResponseEntity<Protocolo> response) {

                    }

                    @Override
                    public void beforeShowErrorMessage(String msg) {
                        ProgressBarUtil.hide(activity, progressBar);
                    }
                });

    }

}
