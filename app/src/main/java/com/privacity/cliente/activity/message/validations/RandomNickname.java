package com.privacity.cliente.activity.message.validations;

import android.view.View;

import com.privacity.cliente.activity.message.ItemListMessage;
import com.privacity.cliente.activity.message.MessageActivity;
import com.privacity.cliente.activity.message.RecyclerHolderGeneric;

public class RandomNickname {
    public static void processRandomNickname(ItemListMessage item, RecyclerHolderGeneric rch, boolean isReply, MessageActivity messageActivity) {
        if (item.getMessage().isChangeNicknameToRandom()){
            rch.getStateIcons().isRandomNickname().setVisibility(View.VISIBLE);
        }else{
            rch.getStateIcons().isRandomNickname().setVisibility(View.GONE);
        }



    }
}
