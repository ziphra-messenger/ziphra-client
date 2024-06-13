package com.privacity.cliente.activity.message;

import android.os.CountDownTimer;

import com.privacity.cliente.model.Message;
import com.privacity.common.dto.MessageDetailDTO;

import java.io.Serializable;

import lombok.Data;

@Data
public class ItemListMessage implements Serializable {
    public RecyclerHolderGeneric getRch() {
        return rch;
    }

    public void setRch(RecyclerHolderGeneric rch) {
        this.rch = rch;
    }

    MessageDetailDTO messageDetailDTO;
    Message message;
    RecyclerHolderGeneric rch;
    String nombreMostrado;

    boolean mostrado;
    boolean isPlaying=false;
    public CountDownTimer getCounter() {
        return counter;
    }

    public void setCounter(CountDownTimer counter) {
        this.counter = counter;
    }

    private CountDownTimer counter=null;
    private boolean running;
    private boolean ocularDetails;
    private boolean messageBlackEyeShowOn;
    public void startTimer(){
        running = true;
        this.counter.start();
    }
    public boolean isRunning(){
        return running;
    }

    public ItemListMessage(Message message, MessageDetailDTO messageDetailDTO) {
        this.message = message;
        this.messageDetailDTO = messageDetailDTO;
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public MessageDetailDTO getMessageDetailDTO() {
        return messageDetailDTO;
    }

    public void setMessageDetailDTO(MessageDetailDTO messageDetailDTO) {
        this.messageDetailDTO = messageDetailDTO;
    }




}
