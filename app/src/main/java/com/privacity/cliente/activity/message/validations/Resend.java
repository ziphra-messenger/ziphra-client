package com.privacity.cliente.activity.message.validations;

import android.view.View;

import com.privacity.cliente.activity.message.ItemListMessage;
import com.privacity.cliente.activity.message.MessageActivity;
import com.privacity.cliente.activity.message.RecyclerHolderGeneric;

public class Resend {
    public static void processResend(ItemListMessage item, RecyclerHolderGeneric rch, boolean isReply, MessageActivity messageActivity) {
        if (item.getMessage().getParentResend() != null){
            rch.getStateIcons().isResend().setVisibility(View.VISIBLE);
        }else{
            rch.getStateIcons().isResend().setVisibility(View.GONE);
        }

        if (item.getMessage().isBlockResend()){
            rch.getStateIcons().isBlockResend().setVisibility(View.VISIBLE);
        }else{
            rch.getStateIcons().isBlockResend().setVisibility(View.GONE);
        }

    }
}