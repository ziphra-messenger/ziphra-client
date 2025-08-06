package com.privacity.cliente.singleton.interfaces;

import com.privacity.cliente.model.Grupo;

public interface ObservadoresPasswordGrupo {
    void passwordExpired(Grupo g);

    void passwordSet(Grupo g);

    void deleteExtraEncrypt(Grupo g);

    void lock(Grupo g);

}
