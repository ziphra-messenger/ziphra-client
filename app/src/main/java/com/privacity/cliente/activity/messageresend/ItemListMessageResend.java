package com.privacity.cliente.activity.messageresend;

import com.privacity.cliente.model.Grupo;

import java.io.Serializable;

public class ItemListMessageResend implements Serializable {

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }

    Grupo grupo;

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    boolean checked;
}