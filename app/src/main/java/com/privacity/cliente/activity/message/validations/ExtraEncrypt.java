package com.privacity.cliente.activity.message.validations;

import android.view.View;

import com.privacity.cliente.activity.message.ItemListMessage;
import com.privacity.cliente.activity.message.MessageActivity;
import com.privacity.cliente.activity.message.RecyclerHolderGeneric;

public class ExtraEncrypt {
    RecyclerHolderGeneric rch;
    ItemListMessage item;
    MessageActivity activity;

    public ExtraEncrypt(RecyclerHolderGeneric rch, ItemListMessage item, MessageActivity activity) {
        this.rch = rch;
        this.item = item;
        this.activity = activity;

        initItem();
    }

    synchronized public void  initItem (){
        rch.getStateIcons().isExtraEncrypt().setVisibility(View.GONE);
    }
}
