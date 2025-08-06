package com.privacity.cliente.activity.message;

import android.app.Activity;

import com.privacity.cliente.activity.messageresend.MessageUtil;
import com.privacity.cliente.encrypt.AESEncrypt;
import com.privacity.cliente.encrypt.PGPKeyBuilder;
import com.privacity.cliente.enumeration.DeleteForEnum;
import com.privacity.cliente.model.Grupo;
import com.privacity.cliente.model.Message;
import com.privacity.cliente.model.dto.MessageDetail;
import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.cliente.singleton.Observers;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.singleton.UtilsStringSingleton;
import com.privacity.common.dto.AESDTO;
import com.privacity.common.dto.EncryptKeysDTO;
import com.privacity.common.dto.GrupoDTO;
import com.privacity.common.dto.IdMessageDTO;
import com.privacity.common.dto.MessageDTO;
import com.privacity.cliente.model.dto.Protocolo;
import com.privacity.common.enumeration.ProtocoloActionsEnum;
import com.privacity.common.enumeration.ProtocoloComponentsEnum;

import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.List;
import java.util.Set;

public class RestCalls {

    public static void loadMessagesContador(Activity context) {

        Protocolo p = new Protocolo();
        p.setComponent(ProtocoloComponentsEnum.MESSAGE);
        p.setAction(ProtocoloActionsEnum.MESSAGE_GET_ALL_ID_MESSAGE_UNREAD
        );
        RestExecute.doit(context, p,
                new CallbackRest(){

                    @Override
                    public void response(ResponseEntity<Protocolo> response) {
                        try {
                            String d = response.getBody().getObjectDTO();// SingletonValues.getInstance().getSessionAEStoUseServerEncrypt().getAESDecrypt(response.getBody().getObjectDTO());


                            Message[] l = UtilsStringSingleton.getInstance().gson().fromJson(d, Message[].class);

                            //tvLoadingGetNewMessagesCount.setText("Obteniendo" + contadorMensajes + " de " + l.length);

                            loadMessages(context, l);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(ResponseEntity<Protocolo> response) {

                    }

                    @Override
                    public void beforeShowErrorMessage(String msg) {

                    }

                });



    }

    public static void loadMessages(Activity context, Message[] list) {

        if (list == null) return;
        for (Message messageDTO : list) {
            Protocolo p = new Protocolo();
            p.setComponent(ProtocoloComponentsEnum.MESSAGE);
            p.setAction(ProtocoloActionsEnum.MESSAGE_GET_MESSAGE);

            MessageDTO o = new MessageDTO();
            o.setIdGrupo(messageDTO.getIdGrupo());
            o.setIdMessage(messageDTO.getIdMessage());

            p.setObjectDTO(UtilsStringSingleton.getInstance().gson().toJson(o));
            RestExecute.doit(context, p,
                    new CallbackRest() {
                        @Override
                        public void response(ResponseEntity<Protocolo> response) {

                            Observers.message().mensajeNuevoWS(response.getBody(), false, context);
                        }

                        @Override
                        public void onError(ResponseEntity<Protocolo> response) {
                        }

                        @Override
                        public void beforeShowErrorMessage(String msg) {
                        }
                    });
        }
    }

    public static void retry(MessageActivity messageActivity) throws Exception {
        MessageDetail detail = SingletonValues.getInstance().getMessageDetailSeleccionado().getMessageDetail();
        Message message = Observers.message().getMensajesPorId(detail.buildIdMessageToMap());

        (new MessageUtil()).sendMessage(messageActivity,
                message.getParentReply(), null,
                message.getIdGrupo(),
                message.getText(),
                message.getMedia(),
                message.isBlackMessage(), message.amITimeMessage(),
                message.isAnonimo(), message.isSecretKeyPersonal(),message.isBlockResend(),
                true, message.getTimeMessage(),null);

        Observers.message().removeMessage(detail.getIdGrupo(), detail.buildIdMessageToMap());
        if (SingletonValues.getInstance().getMessageDetailSeleccionado().getMessageDetail().buildIdMessageDetailToMap().equals(detail.buildIdMessageDetailToMap())) {
            SingletonValues.getInstance().setMessageDetailSeleccionado(null);
        }
    }

    public static void deleteForRest(MessageActivity messageActivity, DeleteForEnum deleteFor) {
        Set<ItemListMessage> messages = Observers.message().getMessageSelected();
        //List<IdMessageDTO> ids = new ArrayList<IdMessageDTO>();
        for (ItemListMessage m: messages){
            //ids.add(new IdMessageDTO (m.getIdGrupo(), m.getIdMessage()));

            deleteForRestElemento(messageActivity, new IdMessageDTO (m.getMessage().getIdGrupo(), m.getMessage().getIdMessage()), deleteFor);
        }

    }

    public static void deleteForRestElemento(MessageActivity messageActivity, IdMessageDTO idMessageDTO, DeleteForEnum deleteFor) {
        //MessageDetail detail = SingletonValues.getInstance().getMessageDetailSeleccionado().getMessageDetail();

        Protocolo p = new Protocolo();
        p.setComponent(ProtocoloComponentsEnum.MESSAGE);
        if (deleteFor.equals( DeleteForEnum.FOR_EVERYONE)){
            p.setAction(ProtocoloActionsEnum.MESSAGE_DELETE_FOR_EVERYONE);
        }else {
            p.setAction(ProtocoloActionsEnum.MESSAGE_DELETE_FOR_ME);
        }


        //m.setIdMessageDetail(detail.getIdMessageDetail());

        p.setObjectDTO(UtilsStringSingleton.getInstance().gson().toJson(idMessageDTO));

        RestExecute.doit(messageActivity, p,
                new CallbackRest() {

                    @Override
                    public void response(ResponseEntity<Protocolo> response) {

                        Observers.message().removeMessage(idMessageDTO.getIdGrupo(), idMessageDTO.buildIdMessageToMap());

                        //Message message = ;


                        /*
                        if (SingletonValues.getInstance().getMessageDetailSeleccionado().getMessageDetail().buildIdMessageDetailToMap().equals(detail.buildIdMessageDetailToMap())) {
                            SingletonValues.getInstance().setMessageDetailSeleccionado(null);
                        }*/
                    }

                    @Override
                    public void onError(ResponseEntity<Protocolo> response) {

                    }

                    @Override
                    public void beforeShowErrorMessage(String msg) {

                    }
                });

    }

    public static PublicKey getPublicKey(EncryptKeysDTO encryptKeysDTO) throws GeneralSecurityException, IOException {
        PublicKey publicKey = PGPKeyBuilder.publicKey(UtilsStringSingleton.getInstance().gson().fromJson(encryptKeysDTO.getPublicKeyNoEncrypt(),byte[].class));
        return publicKey;
    }
    public static AESDTO encriptarAES(Grupo grupo, EncryptKeysDTO encryptKeysDTO) throws GeneralSecurityException, IOException {
        PublicKey publicKey = getPublicKey(encryptKeysDTO);
        return encriptarAES(publicKey, grupo);
    }

    public static AESDTO encriptarAES(PublicKey publicKey, Grupo grupo) throws GeneralSecurityException, IOException {
        return AESEncrypt.encrypt(grupo.getAESDTOdesencrypt(), publicKey);
    }



    public static void emptyList(MessageActivity messageActivity,String grupoSeleccionado) {
        Protocolo p = new Protocolo();
        p.setComponent(ProtocoloComponentsEnum.MESSAGE);
        p.setAction(ProtocoloActionsEnum.MESSAGE_EMPTY_LIST);

        GrupoDTO o = new GrupoDTO();
        o.setIdGrupo(grupoSeleccionado);
        p.setObjectDTO(UtilsStringSingleton.getInstance().gson().toJson(o));
        RestExecute.doit(messageActivity, p,
                new CallbackRest(){

                    @Override
                    public void response(ResponseEntity<Protocolo> response) {
                        Observers.message().emptyList(grupoSeleccionado);

                    }

                    @Override
                    public void onError(ResponseEntity<Protocolo> response) {

                    }

                    @Override
                    public void beforeShowErrorMessage(String msg) {

                    }
                });

    }

    public static void loadOldMessages(MessageActivity messageActivity,String grupoSeleccionado, List<ItemListMessage> items) {
        Protocolo p = new Protocolo();
        p.setComponent(ProtocoloComponentsEnum.MESSAGE);
        p.setAction(ProtocoloActionsEnum.MESSAGE_GET_LOAD_MESSAGES);

        MessageDTO m = new MessageDTO();
        m.setIdGrupo(grupoSeleccionado);
        m.setIdMessage(items.get(0).getMessage().getIdMessage());
        p.setObjectDTO(UtilsStringSingleton.getInstance().gson().toJson(m));

        RestExecute.doit(messageActivity, p,
                new CallbackRest(){

                    @Override
                    public void response(ResponseEntity<Protocolo> response) {
                        //ObservatorMensajes.getInstance().emptyList(SingletonValues.getInstance().getGrupoSeleccionado().getIdGrupo());

                    }

                    @Override
                    public void onError(ResponseEntity<Protocolo> response) {

                    }

                    @Override
                    public void beforeShowErrorMessage(String msg) {

                    }
                });

    }
}
