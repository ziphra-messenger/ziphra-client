package com.privacity.cliente.activity.grupo;


import com.privacity.cliente.model.Grupo;

import java.io.Serializable;

import lombok.Data;

@Data
public class ItemListGrupo implements Serializable {

    private Grupo grupo;
    private int unread;

}
