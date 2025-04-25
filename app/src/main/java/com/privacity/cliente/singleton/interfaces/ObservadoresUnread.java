package com.privacity.cliente.singleton.interfaces;

public interface ObservadoresUnread {

    void avisar(String idGrupo, int unread);

    void suscribeByGrupo(String idGrupo);

}
