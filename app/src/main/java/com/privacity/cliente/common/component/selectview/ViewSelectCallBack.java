package com.privacity.cliente.common.component.selectview;

import android.widget.Switch;

import com.privacity.common.enumeration.RulesConfEnum;

import java.util.List;

public interface ViewSelectCallBack {
    void action(RulesConfEnum user);
    List<RulesConfEnum> rulesConfNeeded();

    RulesConfEnum getMyAccountValue();
    RulesConfEnum getGrupoValue();
    RulesConfEnum getUserValue();
    Switch getParentView();

    String descripcionText();


}
