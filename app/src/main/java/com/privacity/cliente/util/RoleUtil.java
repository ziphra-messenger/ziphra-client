package com.privacity.cliente.util;

import android.widget.Spinner;

import com.privacity.common.enumeration.GrupoRolesEnum;

public class RoleUtil {

    public static GrupoRolesEnum transformRole(Spinner roles) throws Exception {
        int rol = roles.getSelectedItemPosition();
        if (rol == 0) {
            return GrupoRolesEnum.READONLY;
        } else if (rol == 1) {
            return GrupoRolesEnum.MEMBER;
        } else if (rol == 2) {
            return GrupoRolesEnum.MODERATOR;
        } else if (rol == 3) {
            return GrupoRolesEnum.ADMIN;
        } else {
            throw new Exception("NO EXISTE EL ROL");
        }
    }
}