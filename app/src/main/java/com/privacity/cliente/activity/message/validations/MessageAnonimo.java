package com.privacity.cliente.activity.message.validations;

import android.view.View;

import com.privacity.cliente.activity.message.ItemListMessage;
import com.privacity.cliente.activity.message.MessageActivity;
import com.privacity.cliente.activity.message.RecyclerHolderGeneric;
import com.privacity.cliente.model.Message;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.singleton.observers.ObserverMessage;

public class MessageAnonimo {
    public static void processAnonimo(ItemListMessage item, RecyclerHolderGeneric rch, boolean isReply, MessageActivity messageActivity) {

        if (item.getMessage().getShowingAnonimo()){
            rch.getStateIcons().isAnonimo().setVisibility(View.VISIBLE);
            rch.getTvRemitente().setVisibility(View.GONE);
        }else{
            rch.getStateIcons().isAnonimo().setVisibility(View.GONE);

            Message mensanje2 = ObserverMessage.getInstance().getMensajesPorId(item.getMessage().buildIdMessageToMap());
            if (mensanje2 != null && mensanje2.getMessagesDetail() != null && mensanje2.getMessagesDetail().length > 2){
                rch.getTvRemitente().setVisibility(View.VISIBLE);
            }else{
                rch.getTvRemitente().setVisibility(View.GONE);
            }

            rch.getTvRemitente().setText(item.getMessage().getUsuarioCreacion().getNickname());
        }

        }


    }