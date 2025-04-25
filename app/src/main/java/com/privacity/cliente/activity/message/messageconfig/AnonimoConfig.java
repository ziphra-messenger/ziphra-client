package com.privacity.cliente.activity.message.messageconfig;

import com.privacity.cliente.common.component.selectview.SelectView;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.singleton.Singletons;
import com.privacity.common.enumeration.RulesConfEnum;

public class AnonimoConfig {

    public static boolean alwaysSendAnonino(String idGrupo) {

        RulesConfEnum e = SelectView.applyRules(RulesConfEnum.OFF,
                Singletons.observerGrupo().getGrupoById(idGrupo).getGralConfDTO().getAnonimo(),
                Singletons.observerGrupo().getGrupoById(idGrupo).getUserConfDTO().getAnonimoAlways());

        if (e.equals(RulesConfEnum.MANDATORY) && e.equals(RulesConfEnum.ON)) {
            return true;

        }
        return false;
    }
}
