package com.privacity.cliente.singleton.interfaces;

import com.privacity.common.dto.MessageDTO;
import com.privacity.common.dto.MessageDetailDTO;
import com.privacity.common.dto.ProtocoloDTO;
import com.privacity.common.dto.WrittingDTO;

public interface ObservadoresMensajes {

    public void nuevoMensaje(ProtocoloDTO protocoloDTO);
    public void cambioEstado(MessageDetailDTO m);
    public void emptyList();
    public void mensajeAddItem(MessageDTO miMensaje, String asyncId);
    void borrarMensaje(MessageDetailDTO detail);

    void writting(WrittingDTO w);

    void writtingStop(WrittingDTO w);
}
