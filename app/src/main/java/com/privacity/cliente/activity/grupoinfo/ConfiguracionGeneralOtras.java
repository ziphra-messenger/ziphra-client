package com.privacity.cliente.activity.grupoinfo;

import android.app.Activity;
import android.view.View;
import android.widget.Spinner;

import com.privacity.cliente.activity.myaccount.SwitchTxt;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.util.DialogsToShow;

import lombok.Data;

@Data

public class ConfiguracionGeneralOtras {
    private View content;
    private SwitchTxt confGenRandomNickname;
    private SwitchTxt confGenHideMessageDetails;
    private SwitchTxt confGenHideMessageReadState;
    private SwitchTxt confGenBlockAudioMessages;
    private SwitchTxt confGenHideMemberList;
    private SwitchTxt confGenBlackMessageAttachMandatory;
    private SwitchTxt confGenBlockResend;
    private SwitchTxt confGenBlockMediaDownload;

    private Spinner confAudiochatMaxTime;



    public ConfiguracionGeneralOtras(Activity activity, View content, SwitchTxt confGenRandomNickname, SwitchTxt confGenHideMessageDetails, SwitchTxt confGenHideMessageReadState, SwitchTxt confGenBlockAudioMessages, SwitchTxt confGenHideMemberList, SwitchTxt confGenBlackMessageAttachMandatory, SwitchTxt confGenBlockResend, SwitchTxt confGenBlockMediaDownload, Spinner confAudiochatMaxTime) {
        this.content = content;
        this.confGenRandomNickname = confGenRandomNickname;
        this.confGenHideMessageDetails = confGenHideMessageDetails;
        this.confGenHideMessageReadState = confGenHideMessageReadState;
        this.confGenBlockAudioMessages = confGenBlockAudioMessages;
        this.confAudiochatMaxTime = confAudiochatMaxTime;
        this.confGenHideMemberList = confGenHideMemberList;
        this.confGenBlackMessageAttachMandatory = confGenBlackMessageAttachMandatory;
        this.confGenBlockMediaDownload = confGenBlockMediaDownload;
        this.confGenBlockResend = confGenBlockResend;

        DialogsToShow.noAdminDialog(activity, SingletonValues.getInstance().getGrupoSeleccionado().getIdGrupo(),content);
    }
}
