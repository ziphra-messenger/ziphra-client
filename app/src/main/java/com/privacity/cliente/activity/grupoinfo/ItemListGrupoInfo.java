package com.privacity.cliente.activity.grupoinfo;

import com.privacity.common.dto.UserForGrupoDTO;

import java.io.Serializable;

import lombok.Data;

@Data
public class ItemListGrupoInfo implements Serializable {

    private UserForGrupoDTO usersForGrupoDTO;
}
