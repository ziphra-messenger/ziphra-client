package com.privacity.cliente.activity.message.validations;

import com.privacity.cliente.R;
import com.privacity.cliente.common.component.selectview.SelectView;
import com.privacity.cliente.singleton.SingletonValues;import com.privacity.cliente.singleton.Singletons;
import com.privacity.cliente.singleton.activity.SingletonCurrentActivity;
import com.privacity.cliente.singleton.toast.SingletonToastManager;

public class MessageValidations {

    public static boolean blockAudioMessageValidation() {
        if (SingletonValues.getInstance().getGrupoSeleccionado().getGralConfDTO().isBlockAudioMessages()){
            SingletonToastManager.getInstance().showToadShort(SingletonCurrentActivity.getInstance().get(),
                    SingletonCurrentActivity.getInstance().get().getString(R.string.message_activity__allow_audio__not_allow));
            return false;
        }
        return true;
    }

    public static boolean isBlockResend(String idGrupo){

        return  SelectView.isFull(SingletonValues.getInstance().getMyAccountConfDTO().isBlockResend(),
        Singletons.observerGrupo().getGrupoById(idGrupo).getGralConfDTO().isBlockResend(),
                Singletons.observerGrupo().getGrupoById(idGrupo).getUserConfDTO().getBlockResend());

    }

    public static boolean isBlackMessageAttachMandatoryReceivedActivated(String idGrupo){

        return  SelectView.isMyAccount(SingletonValues.getInstance().getMyAccountConfDTO().isBlackMessageAttachMandatoryReceived(),

                Singletons.observerGrupo().getGrupoById(idGrupo).getUserConfDTO().getBlackMessageAttachMandatoryReceived());

    }
}
