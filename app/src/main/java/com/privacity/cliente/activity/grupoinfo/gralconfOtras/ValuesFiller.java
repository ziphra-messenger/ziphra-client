package com.privacity.cliente.activity.grupoinfo.gralconfOtras;

import com.privacity.cliente.activity.grupoinfo.ConfiguracionGeneralOtras;
import com.privacity.cliente.common.component.selectview.SelectView;
import com.privacity.common.dto.GrupoGralConfDTO;

public class ValuesFiller {
    public  static void setDefaultValues(ConfiguracionGeneralOtras otras) {
        otras.getConfGenRandomNickname().setChecked(false);
        otras.getConfGenHideMessageReadState().setChecked(false);
        otras.getConfGenHideMessageDetails().setChecked(false);
        otras.getConfGenHideMemberList().setChecked(false);
        otras.getConfGenBlockAudioMessages().setChecked(true);
        otras.getConfAudiochatMaxTime().setSelection(4);
        otras.getConfGenBlackMessageAttachMandatory().setChecked(false);
        otras.getConfGenBlockResend().setChecked(false);
    }

    public static void initValues(GrupoGralConfDTO c, ConfiguracionGeneralOtras otras) {
        otras.getConfGenRandomNickname().setChecked(c.isRandomNickname());
        otras.getConfGenHideMessageReadState().setChecked(c.isHideMessageReadState());
        otras.getConfGenHideMessageDetails().setChecked(c.isHideMessageDetails());
        otras.getConfGenHideMemberList().setChecked(c.isHideMemberList());
        otras.getConfGenBlockAudioMessages().setChecked(c.isBlockMediaDownload());
        otras.getConfAudiochatMaxTime().setSelection((c.getAudiochatMaxTime()/60)-1);
        otras.getConfGenBlackMessageAttachMandatory().setChecked(c.isBlackMessageAttachMandatory());
        otras.getConfGenBlockResend().setChecked(c.isBlockResend());


    }
}
