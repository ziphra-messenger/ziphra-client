package com.privacity.cliente.singleton.interfaces;

import com.privacity.cliente.model.Message;
import com.privacity.cliente.model.dto.MessageDetail;
import com.privacity.cliente.model.dto.Protocolo;
import com.privacity.common.dto.WrittingDTO;

public interface ObservadoresMensajes {

    void nuevoMensaje(Protocolo protocolo);
    void cambioEstado(MessageDetail m);
    void emptyList(String idGrupo);
    void mensajeAddItem(Message miMensaje, String asyncId);
    void borrarMessageDetail(MessageDetail detail);
    void borrarMessage(String idMessageToMap);

    void writting(WrittingDTO w);

    void writtingStop(WrittingDTO w);
}
