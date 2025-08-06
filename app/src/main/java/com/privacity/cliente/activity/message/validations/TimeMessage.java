package com.privacity.cliente.activity.message.validations;

import android.content.Intent;
import android.os.CountDownTimer;
import android.view.View;

import com.privacity.cliente.activity.message.ItemListMessage;
import com.privacity.cliente.activity.message.MessageActivity;
import com.privacity.cliente.activity.message.RecyclerHolderGeneric;
import com.privacity.cliente.activity.message.RecyclerMessageAdapter;
import com.privacity.cliente.activity.messageresend.MessageUtil;
import com.privacity.cliente.model.Message;
import com.privacity.cliente.model.dto.MessageDetail;
import com.privacity.cliente.singleton.Observers;
import com.privacity.common.BroadcastConstant;
import com.privacity.common.enumeration.MessageState;

import java.util.List;

public class TimeMessage {
    private static final String TAG = "TimeMessage";

    public static void processTimeMessage(RecyclerMessageAdapter recyclerMessageAdapter, ItemListMessage item, RecyclerHolderGeneric rch, MessageActivity messageActivity, List<ItemListMessage> items) {

        if (item.getMessage().amITimeMessage()){
            rch.getStateIcons().isTime().setVisibility(View.VISIBLE);
        }else{
            rch.getStateIcons().isTime().setVisibility(View.GONE);
        }

        if (item.getMessage().amITimeMessage()){

            Message ms = (Message) item.getMessage();
            if (!item.isRunning() || ms.getCountDownTimer().isTimeMessageCountDownTimerRunning()) {


                rch.getBtActivateMessageTime().setText(MessageUtil.CalcularTiempoFormaterSinHora(ms.getCountDownTimer().getSeconds()));
                item.setCounter(new CountDownTimer(
                        ms.getCountDownTimer().getSeconds() * 1000, 1000) {

                    public void onTick(long millisUntilFinished) {

                        rch.getLayoutMessageContentData().setVisibility(View.VISIBLE);

                        rch.getBtActivateMessageTime().setText((MessageUtil.CalcularTiempoFormaterSinHora(ms.getCountDownTimer().getSeconds())) + "");

                        if (!rch.isHasMediaAudioChat() &&
                                (!item.getMessageDetail().getEstado().equals(MessageState.MY_MESSAGE_SENT) &&
                                        !item.getMessageDetail().getEstado().equals(MessageState.DESTINY_READ)
                                )
                        ) {
                            Observers.message().cambiarEstadoUso(item.getMessageDetail(), true, messageActivity);
                        }


                    }

                    public void onFinish() {
                        item.setPlaying( false);
                        rch.getBtActivateMessageTime().setText("End");

                        if (item.getMessage() != null) {
                            item.getMessage().setDeleted(true);
                            for (MessageDetail mdd : item.getMessage().getMessagesDetail()) {
                                mdd.setDeleted(true);
                            }
                        }
                        if (item.getMessageDetail().isOwnMessageCreatorAndIsMyMessageDetail()) {
                            rch.getLayoutMessageContentData().setVisibility(View.GONE);
                           // items.remove(item);

                        } else {
                            rch.getLayoutAllMessage().setVisibility(View.GONE);
                        }
                        rch.getLayoutAllMessage().setVisibility(View.GONE);

                        try {
                            recyclerMessageAdapter.notifyDataSetChanged();
                        } catch (Exception e) {

                        }
                /*        Intent intent = new Intent(BroadcastConstant.BROADCAST__FINISH_ACTIVITY);
                        messageActivity.sendBroadcast(intent);*/

                    }
                });
                if (ms.getCountDownTimer().isTimeMessageCountDownTimerRunning()) {
                    item.startTimer();
                } else {
                    rch.getBtActivateMessageTime().setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (!rch.isHasMediaAudioChat()) {
                                        item.startTimer();
                                        ms.getCountDownTimer().restart();
                                    } else {
                                        rch.getLayoutMessageContentData().setVisibility(View.VISIBLE);
                                    }

                                }
                            });
                }
            }


        }
    }

}
