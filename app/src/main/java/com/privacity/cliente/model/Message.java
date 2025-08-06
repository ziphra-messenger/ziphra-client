package com.privacity.cliente.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.privacity.cliente.activity.message.validations.MessageValidations;
import com.privacity.cliente.model.dto.MessageDetail;
import com.privacity.cliente.singleton.Singletons;
import com.privacity.cliente.util.notificacion.MessageCountDownTimer;
import com.privacity.common.annotations.PrivacityId;
import com.privacity.common.annotations.PrivacityIdExclude;
import com.privacity.common.annotations.PrivacityIdOrder;
import com.privacity.common.dto.IdMessageDTO;
import com.privacity.common.dto.MediaDTO;
import com.privacity.common.dto.MessageDTO;
import com.privacity.common.dto.UsuarioDTO;
import com.privacity.common.enumeration.MediaTypeEnum;

import java.lang.reflect.Field;
import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data

public class Message {

    private boolean historial;
    private boolean downloadingMedia = false;
    private boolean reply;
    private boolean deleted;
    private MessageDetail[] messagesDetail;
    @PrivacityIdOrder
    private String idMessage;
    @PrivacityId
    @PrivacityIdOrder
    private String idGrupo;
    @PrivacityIdOrder
    private IdMessageDTO parentReply;
    @PrivacityIdOrder
    private IdMessageDTO parentResend;
    private UsuarioDTO usuarioCreacion;
    @PrivacityIdExclude
    private String text;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    @PrivacityIdExclude
    private boolean blackMessage;

    @PrivacityIdExclude
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int timeMessage;
    @PrivacityIdExclude
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private boolean anonimo;
    @PrivacityIdExclude
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private boolean systemMessage;
    @PrivacityIdExclude
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private boolean secretKeyPersonal;
    @PrivacityIdExclude
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private boolean blockResend;
    private MediaDTO media;
    @PrivacityIdExclude
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private boolean changeNicknameToRandom;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    @PrivacityIdExclude
    private boolean hideMessageDetails;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    @PrivacityIdExclude

    private boolean hideMessageReadState;

    private MessageCountDownTimer countDownTimer;

    public Message() {
        orden = new Date().getTime();
    }

    private final Long orden;

    private boolean showingBlackMessage;
    private Boolean showingAnonimo;
    public boolean getShowingAnonimo() {
        if (showingAnonimo == null){
            showingAnonimo=false;
            if ( isAnonimo() ){
                showingAnonimo=true;
            }

            if (
                    !amIUsuarioCreacion() &&
                    Singletons.observerGrupo().getGrupoById(idGrupo).getUserConfDTO().isAnonimoRecived()
            ){
                showingAnonimo=true;
            }

            if (
                    amIUsuarioCreacion() &&
                            Singletons.observerGrupo().getGrupoById(idGrupo).getUserConfDTO().isAnonimoRecivedMyMessage()
            ){
                showingAnonimo=true;
            }
        }
        return showingAnonimo;
    }

    public boolean amIUsuarioCreacion(){
        if (getUsuarioCreacion() == null) return false;
        if(Singletons.usuario().getUsuario().getIdUsuario().equals(getUsuarioCreacion().getIdUsuario())){
            return true;
        }
           return false;

    }

    public Message (MessageDTO dto){
        super();


        copyFields(dto, this);

        orden = new Date().getTime();
        if (amIUsuarioCreacion()){
        if ( getMedia()!= null && getMedia().getMediaType() != null){
           if (MediaTypeEnum.IMAGE.equals(getMedia().getMediaType()) ||
                   MediaTypeEnum.VIDEO.equals(getMedia().getMediaType())){
        }{

            if (!amITimeMessage()){
                if (MessageValidations.isBlackMessageAttachMandatoryReceivedActivated(getIdGrupo())){
                                        setBlackMessage(true);

            }
        }
           }

        }


        }
    }

    public boolean amIReplyMessage() {
        return getParentReply() != null;

    }

    public Boolean canDownloadMyMedia() {
        if (this.getMedia() != null) {
            return this.getMedia().isDownloadable();
        }
        return false;
    }

    public Boolean hasMedia() {
        return (this.getMedia() != null);
    }

    private <T extends Object, Y extends Object> void copyFields(T from, Y too) {

        Class<? extends Object> fromClass = from.getClass();
        Field[] fromFields = fromClass.getDeclaredFields();

        Class<? extends Object> tooClass = too.getClass();
        Field[] tooFields = tooClass.getDeclaredFields();

        if (fromFields != null && tooFields != null) {
            for (Field tooF : tooFields) {
                try {
                    // Check if that fields exists in the other method
                    Field fromF = fromClass.getDeclaredField(tooF.getName());
                    if (fromF.getType().equals(tooF.getType())) {
                        tooF.set(tooF, fromF);
                    }
                } catch (SecurityException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (NoSuchFieldException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }
    }

    public IdMessageDTO buildIdMessageDTO(){
        return new IdMessageDTO(this.getIdGrupo(),this.getIdMessage());
    }

    public String buildIdMessageToMap() {
        return idGrupo + "{-}" + idMessage;
    }

    public MessageCountDownTimer getCountDownTimer() {
        if (countDownTimer == null){
            if (amITimeMessage()){
                countDownTimer = new MessageCountDownTimer(this);
            }
        }
        return countDownTimer;
    }

    public void setCountDownTimer(MessageCountDownTimer countDownTimer) {
        this.countDownTimer = countDownTimer;
    }

    @JsonIgnore
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public boolean amITimeMessage() {
        return timeMessage > 0;
    }

    public boolean amIMessageCreator(){
        return isThisIdUsuarioMessageCreator(Singletons.usuario().getUsuario().getIdUsuario());
    }

    public boolean isThisIdUsuarioMessageCreator(String idUsuario){
        if ( this.getUsuarioCreacion() != null){
            if (this.getUsuarioCreacion().getIdUsuario() != null){
                if (this.getUsuarioCreacion().getIdUsuario().equals(idUsuario)) return true;
            }
        }
        return false;
    }

}
