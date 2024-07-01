package com.privacity.cliente.rest.restcalls.invitation;

import android.app.Activity;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.privacity.cliente.activity.grupo.ItemListGrupo;
import com.privacity.cliente.encrypt.EncryptKeysToUse;
import com.privacity.cliente.encrypt.EncryptUtil;
import com.privacity.cliente.encrypt.RSA;
import com.privacity.cliente.includes.ProgressBarUtil;
import com.privacity.cliente.model.Grupo;
import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.cliente.singleton.Observers;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.util.GsonFormated;
import com.privacity.common.dto.AESDTO;
import com.privacity.common.dto.EncryptKeysDTO;
import com.privacity.common.dto.ProtocoloDTO;
import com.privacity.common.dto.request.GrupoInvitationAcceptRequestDTO;
import com.privacity.common.enumeration.ProtocoloActionsEnum;
import com.privacity.common.enumeration.ProtocoloComponentsEnum;

import org.springframework.http.ResponseEntity;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

public class AcceptInvitationCallRest {
    public static void call(Activity activity, ProgressBar progressBar , ItemListGrupo item) throws Exception {
        Grupo grupo = item.getGrupo();

        EncryptKeysDTO encryptKeysDTO = new EncryptKeysDTO();

        byte[] desencriptado = SingletonValues.getInstance().getPersonalAEStoUse().getAESDecrypt(
                GsonFormated.get().fromJson(grupo.getGrupoInvitationDTO().getPrivateKey(), byte[].class));



        EncryptKeysToUse encryptKeysToUseGrupo = new EncryptKeysToUse();

        RSA t = new RSA();
        encryptKeysToUseGrupo.setRsa(t);
        KeyFactory kf = KeyFactory.getInstance("RSA");

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec( desencriptado);

        PrivateKey privateKey = kf.generatePrivate(spec);
        encryptKeysToUseGrupo.setPrivateKey(privateKey);

        String secretKeyAESDescr = EncryptUtil.encryptKeyDesencrypt(encryptKeysToUseGrupo,grupo.getGrupoInvitationDTO().getAesDTO().getSecretKeyAES());
        String saltAESDescr = EncryptUtil.encryptKeyDesencrypt(encryptKeysToUseGrupo,grupo.getGrupoInvitationDTO().getAesDTO().getSaltAES());
        String iteratorAESDescr = EncryptUtil.encryptKeyDesencrypt(encryptKeysToUseGrupo,grupo.getGrupoInvitationDTO().getAesDTO().getIteration());

        AESDTO aesGrupoDTO = EncryptUtil.encriptarAES(new AESDTO(secretKeyAESDescr, saltAESDescr, iteratorAESDescr,
                        SingletonValues.getInstance().getSystemGralConf().getMessagingAES().getBits()+""
                ),
                SingletonValues.getInstance().getEncryptKeysToUse(),
                SingletonValues.getInstance().getEncryptKeysToUse().getPublicKey()
        );
        //encriptar los aes y enviar al servidor;

        ProtocoloDTO p = new ProtocoloDTO();
        p.setComponent(ProtocoloComponentsEnum.PROTOCOLO_COMPONENT_GRUPO);
        p.setAction(ProtocoloActionsEnum.GRUPO_ACCEPT_INVITATION);

        GrupoInvitationAcceptRequestDTO o = new GrupoInvitationAcceptRequestDTO();
        o.setIdGrupo(grupo.getIdGrupo());
        o.setAesDTO(aesGrupoDTO);
        o.setIdUsuarioInvitado(SingletonValues.getInstance().getUsuario().getIdUsuario());
        o.setIdUsuarioInvitante(item.getGrupo().getGrupoInvitationDTO().getUsuarioInvitante().getIdUsuario());
        p.setObjectDTO(GsonFormated.get().toJson(o));

        //ObservatorGrupos.getInstance().avisarGrupoRemove(grupoDTO);
        RestExecute.doit(activity, p,
                new CallbackRest() {

                    @Override
                    public void response(ResponseEntity<ProtocoloDTO> response) {

                        Grupo l = GsonFormated.get().fromJson(response.getBody().getObjectDTO(), Grupo.class);

                        Observers.grupo().updateGrupoAcceptInvitation(l);
                        ProgressBarUtil.hide(activity, progressBar);
                        Toast toast=Toast. makeText(activity,"Invitacion Aceptada",Toast. LENGTH_SHORT);
                        toast.show();
                    }

                    @Override
                    public void onError(ResponseEntity<ProtocoloDTO> response) {

                    }

                    @Override
                    public void beforeShowErrorMessage(String msg) {
                        ProgressBarUtil.hide(activity, progressBar);
                    }
                });

    }

}
