package com.privacity.cliente.activity.message;

import android.os.CountDownTimer;

import com.privacity.cliente.model.Message;
import com.privacity.cliente.model.dto.MessageDetail;


import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class ItemListMessage implements Serializable {
    public RecyclerHolderGeneric getRch() {
        return rch;
    }

    public void setRch(RecyclerHolderGeneric rch) {
        this.rch = rch;
    }

    MessageDetail messageDetail;
    Message message;
    RecyclerHolderGeneric rch;
    String nombreMostrado;
    List<String> url;
    boolean mostrado;
    boolean isPlaying = false;
    RecyclerHolder holder;

    public CountDownTimer getCounter() {
        return counter;
    }

    public void setCounter(CountDownTimer counter) {
        this.counter = counter;
    }


    private CountDownTimer counter = null;
    private boolean running;
    private boolean ocularDetails;

    public void startTimer() {
        running = true;
        this.counter.start();
    }

    public boolean isRunning() {
        return running;
    }

    public ItemListMessage(Message message, MessageDetail messageDetail) {
        this.message = message;
        this.messageDetail = messageDetail;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public MessageDetail getMessageDetail() {
        return messageDetail;
    }

    public void setMessageDetail(MessageDetail messageDetail) {
        this.messageDetail = messageDetail;
    }

}
