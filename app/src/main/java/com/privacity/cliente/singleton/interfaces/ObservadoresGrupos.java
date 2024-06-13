package com.privacity.cliente.singleton.interfaces;

import com.privacity.cliente.model.Grupo;
import com.privacity.common.dto.GrupoDTO;

public interface ObservadoresGrupos {
    public void actualizarLista();
    public void nuevoGrupo(Grupo g);
    public void cambioUnread(String idGrupo);

    void removeGrupo(String idGrupo);

    void avisarLock(GrupoDTO g);
}
