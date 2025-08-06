package com.privacity.cliente.activity.grupo;

import android.app.Activity;

import com.privacity.cliente.R;
import com.privacity.common.enumeration.GrupoRolesEnum;

public class GrupoUtil {

    public static boolean isGrupoDeDos(String idGrupo){
        return false; //ObservatorGrupos.getInstance().getGrupoById((idGrupo)).getUsersForGrupoDTO().length < 3;
    }

    public static String transformGrupoRoleEnumToCompleteString(Activity context, GrupoRolesEnum e){
        if (e.equals(GrupoRolesEnum.ADMIN)) return context.getString(R.string.general__roles__admin);
        else if (e.equals(GrupoRolesEnum.MODERATOR)) return context.getString(R.string.general__roles__moderator);
        else if (e.equals(GrupoRolesEnum.MEMBER)) return context.getString(R.string.general__roles__member);
        else if (e.equals(GrupoRolesEnum.READONLY)) return context.getString(R.string.general__roles__readonly);

        else return null;

    }
}
