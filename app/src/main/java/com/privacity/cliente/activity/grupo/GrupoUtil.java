package com.privacity.cliente.activity.grupo;

import com.privacity.common.enumeration.GrupoRolesEnum;

public class GrupoUtil {

    public static boolean isGrupoDeDos(String idGrupo){
        return false; //ObservatorGrupos.getInstance().getGrupoById((idGrupo)).getUsersForGrupoDTO().length < 3;
    }

    public static String transformGrupoRoleEnumToCompleteString(GrupoRolesEnum e){
        if (e.equals(GrupoRolesEnum.ADMIN)) return "Administrador";
        else if (e.equals(GrupoRolesEnum.MEMBER)) return "Miembro";
        else if (e.equals(GrupoRolesEnum.READONLY)) return "Solo Lectura";
        else return "Rol no definido";

    }
}
