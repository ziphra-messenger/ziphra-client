package com.privacity.cliente.singleton.interfaces;

import com.privacity.cliente.model.Grupo;

public interface ObservadoresGrupos {
    void actualizarLista();
    void nuevoGrupo(Grupo g);
    void cambioUnread(String idGrupo);

    void removeGrupo(String idGrupo);

    void avisarLock(Grupo g);
    void avisarRoleChange(Grupo g);
    void avisarCambioGrupoGralConf(Grupo g);
}
