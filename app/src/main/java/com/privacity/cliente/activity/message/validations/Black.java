package com.privacity.cliente.activity.message.validations;

import android.app.Activity;
import android.view.View;

import com.privacity.cliente.activity.message.ItemListMessage;
import com.privacity.cliente.activity.message.MessageActivity;
import com.privacity.cliente.activity.message.RecyclerHolderGeneric;
import com.privacity.cliente.singleton.Observers;

import lombok.Data;


@Data
public class Black {
    RecyclerHolderGeneric rch;
    ItemListMessage item;
    MessageActivity activity;

    public Black(RecyclerHolderGeneric rch, ItemListMessage item, MessageActivity activity) {
        this.rch = rch;
        this.item = item;
        this.activity = activity;

        initItem();
    }



    synchronized public void  initItem (){
        if ( item.getMessage().isBlackMessage() && !item.getMessage().amIReplyMessage()){
            rch.getLayoutBlackmessageLocks().setVisibility(View.VISIBLE);
            rch.getLayoutAllMessageSinPersonalEncrypt().setVisibility(View.VISIBLE);
            rch.getBtMessageBlackEyeHide().setVisibility(View.VISIBLE);
            rch.getBtMessageBlackEyeShow().setVisibility(View.VISIBLE);
            setListenerMessageBlack(item, rch,activity);
            rch.getStateIcons().isBlack().setVisibility(View.VISIBLE);

         //   hide();
            if ( item.getMessage().isShowingBlackMessage()){
                show();

            }else{
               hide();
            }
        }else {
            rch.getBtMessageBlackEyeHide().setVisibility(View.GONE);
            rch.getBtMessageBlackEyeShow().setVisibility(View.GONE);
            rch.getLayoutBlackmessageLocks().setVisibility(View.GONE);
            rch.getStateIcons().isBlack().setVisibility(View.GONE);

        }
    }

    public void hide(){
        item.getMessage().setShowingBlackMessage(false);
        rch.getLayoutBlackmessageLocks().setVisibility(View.VISIBLE);
        rch.getBtMessageBlackEyeShow().setVisibility(View.VISIBLE);
        rch.getBtMessageBlackEyeHide().setVisibility(View.GONE);
        rch.getLayoutMessageFrame().setVisibility(View.GONE);


    }



    public void show(){
        item.getMessage().setShowingBlackMessage(true);
        rch.getLayoutBlackmessageLocks().setVisibility(View.VISIBLE);
        rch.getBtMessageBlackEyeShow().setVisibility(View.GONE);
        rch.getBtMessageBlackEyeHide().setVisibility(View.VISIBLE);
        rch.getLayoutMessageFrame().setVisibility(View.VISIBLE);

    }

    public void setListenerMessageBlack(ItemListMessage item, RecyclerHolderGeneric rch, Activity context) {
        rch.getBtMessageBlackEyeShow().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
show();


                new Runnable(){

                    @Override
                    public void run() {

                            Observers.message().cambiarEstadoUso(item.getMessageDetail(), true,context);
                    }
                }.run();


            }
        });

        rch.getBtMessageBlackEyeHide().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();



            }
        });
    }
}
