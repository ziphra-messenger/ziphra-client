package com.privacity.cliente.singleton.interfaces;

import com.privacity.cliente.model.Grupo;

public interface ObservadoresPasswordGrupo {
    public void passwordExpired(Grupo g);

    public void passwordSet(Grupo g);

    public void deleteExtraEncrypt(Grupo g);

    public void lock(Grupo g);

}
