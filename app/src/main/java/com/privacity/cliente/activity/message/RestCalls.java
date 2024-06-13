package com.privacity.cliente.activity.message;

import android.app.Activity;

import com.privacity.cliente.activity.messageresend.MessageUtil;
import com.privacity.cliente.encrypt.AESEncrypt;
import com.privacity.cliente.encrypt.PGPKeyBuilder;
import com.privacity.cliente.enumeration.DeleteForEnum;
import com.privacity.cliente.model.Grupo;
import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.cliente.singleton.Observers;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.util.GsonFormated;
import com.privacity.common.config.ConstantProtocolo;
import com.privacity.common.dto.AESDTO;
import com.privacity.common.dto.EncryptKeysDTO;
import com.privacity.common.dto.GrupoDTO;
import com.privacity.common.dto.IdMessageDTO;
import com.privacity.common.dto.MessageDTO;
import com.privacity.common.dto.MessageDetailDTO;
import com.privacity.common.dto.ProtocoloDTO;

import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.List;
import java.util.Set;

public class RestCalls {

    public static void loadMessagesContador(Activity context) {

        ProtocoloDTO p = new ProtocoloDTO();
        p.setComponent(ConstantProtocolo.PROTOCOLO_COMPONENT_MESSAGE);
        p.setAction(ConstantProtocolo.PROTOCOLO_ACTION_MESSAGE_GET_ALL_ID_MESSAGE_UNREAD
        );
        RestExecute.doit(context, p,
                new CallbackRest(){

                    @Override
                    public void response(ResponseEntity<ProtocoloDTO> response) {
                        MessageDTO[] l = GsonFormated.get().fromJson(response.getBody().getObjectDTO(), MessageDTO[].class);

                        //tvLoadingGetNewMessagesCount.setText("Obteniendo" + contadorMensajes + " de " + l.length);

                        loadMessages(context, l);
                    }

                    @Override
                    public void onError(ResponseEntity<ProtocoloDTO> response) {

                    }

                    @Override
                    public void beforeShowErrorMessage(String msg) {

                    }

                });



    }

    public static void loadMessages(Activity context, MessageDTO[] list) {
        // init valores

        for ( int i = 0 ; i < list.length ; i++){
            ProtocoloDTO p = new ProtocoloDTO();
            p.setComponent(ConstantProtocolo.PROTOCOLO_COMPONENT_MESSAGE);
            p.setAction(ConstantProtocolo.PROTOCOLO_ACTION_MESSAGE_GET_MESSAGE);

            MessageDTO o = new MessageDTO();
            o.setIdGrupo(list[i].getIdGrupo());
            o.setIdMessage(list[i].getIdMessage());

            p.setObjectDTO(GsonFormated.get().toJson(o));

            RestExecute.doit(context, p,
                    new CallbackRest(){

                        @Override
                        public void response(ResponseEntity<ProtocoloDTO> response) {
                            //MessageDTO m = GsonFormated.get().fromJson(response.getBody().getObjectDTO(), MessageDTO.class);

                            Observers.message().mensajeNuevoWS(response.getBody(),false, context);



                        }

                        @Override
                    public void onError(ResponseEntity<ProtocoloDTO> response) {

                    }

                    @Override
                    public void beforeShowErrorMessage(String msg) {

                    }

                    });

        }
    }

    public static void retry(MessageActivity messageActivity) throws Exception {
        MessageDetailDTO detail = SingletonValues.getInstance().getMessageDetailSeleccionado().getMessageDetailDTO();
        MessageDTO message = Observers.message().getMensajesPorId(detail.getIdMessageToMap());

        (new MessageUtil()).sendMessage(messageActivity,
                message.getParentReply(), null,
                message.getIdGrupo(),
                message.getText(),
                message.getMediaDTO(),
                message.isBlackMessage(), message.isTimeMessage(),
                message.isAnonimo(), message.isSecretKeyPersonal(),message.isPermitirReenvio(),
                true, message.getTimeMessage());

        Observers.message().removeMessage(detail.getIdGrupo(), detail.getIdMessageToMap());
        if (SingletonValues.getInstance().getMessageDetailSeleccionado().getMessageDetailDTO().getIdMessageDetailToMap().equals(detail.getIdMessageDetailToMap())) {
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
        //MessageDetailDTO detail = SingletonValues.getInstance().getMessageDetailSeleccionado().getMessageDetailDTO();

        ProtocoloDTO p = new ProtocoloDTO();
        p.setComponent("/message");
        if (deleteFor.equals( DeleteForEnum.FOR_EVERYONE)){
            p.setAction("/message/deleteForEveryone");
        }else {
            p.setAction("/message/deleteForMe");
        }


        //m.setIdMessageDetail(detail.getIdMessageDetail());

        p.setObjectDTO(GsonFormated.get().toJson(idMessageDTO));

        RestExecute.doit(messageActivity, p,
                new CallbackRest() {

                    @Override
                    public void response(ResponseEntity<ProtocoloDTO> response) {

                        Observers.message().removeMessage(idMessageDTO.getIdGrupo(), idMessageDTO.getIdMessageToMap());

                        //Message message = ;


                        /*
                        if (SingletonValues.getInstance().getMessageDetailSeleccionado().getMessageDetailDTO().getIdMessageDetailToMap().equals(detail.getIdMessageDetailToMap())) {
                            SingletonValues.getInstance().setMessageDetailSeleccionado(null);
                        }*/
                    }

                    @Override
                    public void onError(ResponseEntity<ProtocoloDTO> response) {

                    }

                    @Override
                    public void beforeShowErrorMessage(String msg) {

                    }
                });

    }


    public static AESDTO encriptarAES(Grupo grupo, EncryptKeysDTO encryptKeysDTO) throws GeneralSecurityException, IOException {
        PublicKey publicKey = PGPKeyBuilder.publicKey(GsonFormated.get().fromJson(encryptKeysDTO.getPublicKeyNoEncrypt(),byte[].class));



        return AESEncrypt.encrypt(grupo.getAESDTOdesencrypt(), publicKey);
    }




    public static void emptyList(MessageActivity messageActivity,String grupoSeleccionado) {
        ProtocoloDTO p = new ProtocoloDTO();
        p.setComponent("/message");
        p.setAction("/message/emptyList");

        GrupoDTO o = new GrupoDTO();
        o.setIdGrupo(grupoSeleccionado);
        p.setObjectDTO(GsonFormated.get().toJson(o));
        RestExecute.doit(messageActivity, p,
                new CallbackRest(){

                    @Override
                    public void response(ResponseEntity<ProtocoloDTO> response) {
                        Observers.message().emptyList(grupoSeleccionado);

                    }

                    @Override
                    public void onError(ResponseEntity<ProtocoloDTO> response) {

                    }

                    @Override
                    public void beforeShowErrorMessage(String msg) {

                    }
                });

    }

    public static void loadOldMessages(MessageActivity messageActivity,String grupoSeleccionado, List<ItemListMessage> items) {
        ProtocoloDTO p = new ProtocoloDTO();
        p.setComponent("/message");
        p.setAction("/message/get/loadMessages");

        MessageDTO m = new MessageDTO();
        m.setIdGrupo(grupoSeleccionado);
        m.setIdMessage(items.get(0).getMessage().getIdMessage());
        p.setObjectDTO(GsonFormated.get().toJson(m));

        RestExecute.doit(messageActivity, p,
                new CallbackRest(){

                    @Override
                    public void response(ResponseEntity<ProtocoloDTO> response) {
                        //ObservatorMensajes.getInstance().emptyList(SingletonValues.getInstance().getGrupoSeleccionado().getIdGrupo());

                    }

                    @Override
                    public void onError(ResponseEntity<ProtocoloDTO> response) {

                    }

                    @Override
                    public void beforeShowErrorMessage(String msg) {

                    }
                });

    }
}
