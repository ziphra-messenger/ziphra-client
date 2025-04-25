package com.privacity.cliente.model.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.singleton.Singletons;
import com.privacity.cliente.model.dto.MessageDetail;
import com.privacity.cliente.singleton.observers.ObserverMessage;
import com.privacity.common.annotations.PrivacityId;
import com.privacity.common.annotations.PrivacityIdExclude;
import com.privacity.common.annotations.PrivacityIdOrder;
import com.privacity.common.dto.MessageDetailDTO;
import com.privacity.common.dto.UsuarioDTO;
import com.privacity.common.enumeration.MessageState;

import java.lang.reflect.Field;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import static com.privacity.common.enumeration.MessageState.DESTINY_DELIVERED;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@JsonIgnoreProperties(value = { "isOwnMessageCreatorAndIsMyMessageDetail" })
public class MessageDetail  {

    private boolean sendChangeState;

    @JsonIgnoreProperties(value = { "ownMessage" })
    private boolean ownMessage;
    @PrivacityIdOrder
    private String idMessage;
    @PrivacityId
    @PrivacityIdOrder
    private String idGrupo;
    private UsuarioDTO usuarioDestino;
    @PrivacityIdExclude
    private MessageState estado;

    public void setDeleted(boolean deleted) {
        if  (    MessageState.DESTINY_DELIVERED.equals(estado) &&isOwnMessageDetail()) {
            Singletons.unread().removeMe(this);
        }
        this.deleted = deleted;
    }

    @PrivacityIdExclude
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private boolean deleted;
    private boolean hideRead;
    private String error;

    public synchronized MessageDetail setEstado(MessageState estado) {

        MessageState actual = this.estado;

        if ( actual != null && estado.ordinal() < this.estado.ordinal() ) return this;

        if  (    MessageState.DESTINY_DELIVERED.equals(estado) &&isOwnMessageDetail()) {
           Singletons.unread().addMe(this);
        }else if  (    MessageState.DESTINY_READ.equals(estado) &&isOwnMessageDetail()){
            Singletons.unread().removeMe(this);
        }


        this.estado = estado;

        return this;
    }

    public String buildIdMessageDetailToMap() {
        String usuarioid=null;
        if (usuarioDestino != null) {
            usuarioid= usuarioDestino.getIdUsuario();
        }
        return idGrupo + "{-}" + idMessage + "{-}" + usuarioid;
    }

    public String buildIdMessageToMap() {
        return idGrupo + "{-}" + idMessage;
    }
    public MessageDetail(MessageDetailDTO dto) {
        copyFields(dto, this);

    }

    public boolean isOwnMessageDetail() {

        if (Singletons.usuario().getUsuario().getIdUsuario().equals(getIdUsuario(getUsuarioDestino()))) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isThisMessageDetailDestinyToUsuarioId(String usuarioId) {

        if (usuarioId.equals(getIdUsuario(getUsuarioDestino()))) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isOwnMessageCreatorAndIsMyMessageDetail() {

        if (ObserverMessage.getInstance().getMensajesPorId(this.buildIdMessageToMap()) == null) return false;

        if (Singletons.usuario().getUsuario().getIdUsuario().equals(getIdUsuario(getUsuarioDestino())) &&
                Singletons.usuario().getUsuario().getIdUsuario().equals(

                        getIdUsuario(ObserverMessage.getInstance().getMensajesPorId(this.buildIdMessageToMap()).getUsuarioCreacion()))
                                ) {
            return true;
        } else {
            return false;
        }


    }
    public String getIdUsuario(UsuarioDTO u) {
        if (u == null) return null;

        return u.getIdUsuario();
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
}
