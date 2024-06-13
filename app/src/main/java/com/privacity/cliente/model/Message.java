package com.privacity.cliente.model;

import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.util.notificacion.MessageCountDownTimer;
import com.privacity.common.dto.MessageDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class Message extends MessageDTO {

    private boolean downloadingMedia=false;
    private boolean reply;

    public Message (MessageDTO dto){
        super();
        this.idMessage=dto.idMessage;
        this.idGrupo=dto.idGrupo;
        this.usuarioCreacion=dto.usuarioCreacion;
        this.text=dto.text;
        this.blackMessage=dto.blackMessage;
        this.timeMessage=dto.timeMessage;
        this.anonimo=dto.anonimo;
        this.systemMessage=dto.systemMessage;
        this.secretKeyPersonal=dto.secretKeyPersonal;
        this.permitirReenvio=dto.permitirReenvio;
        //this.idMessageParentResend=dto.idMessageParentResend;
        this.MessagesDetailDTO=dto.MessagesDetailDTO;
        this.MediaDTO=dto.MediaDTO;
        this.changeNicknameToRandom=dto.changeNicknameToRandom;
        this.hideMessageDetails=dto.hideMessageDetails;
        this.hideMessageState=dto.hideMessageState;
        if (dto.getParentReply() != null){
            this.setParentReply(dto.getParentReply());
        }

      }

    private MessageCountDownTimer countDownTimer;

    public MessageCountDownTimer getCountDownTimer() {
        if (countDownTimer == null){
            if (this.isTimeMessage()){
                countDownTimer = new MessageCountDownTimer(this);
            }
        }
        return countDownTimer;
    }

    public void setCountDownTimer(MessageCountDownTimer countDownTimer) {
        this.countDownTimer = countDownTimer;
    }

    public boolean amIMessageCreator(){

        if ( this.getUsuarioCreacion() == null) return false;
        if ( this.getUsuarioCreacion().getIdUsuario() == null) return false;
        if (this.getUsuarioCreacion().getIdUsuario().equals(SingletonValues.getInstance().getUsuario().getIdUsuario())){
            return true;
        }
        return false;
    }

}
