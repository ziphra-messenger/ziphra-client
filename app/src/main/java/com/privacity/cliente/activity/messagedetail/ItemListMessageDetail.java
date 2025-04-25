package com.privacity.cliente.activity.messagedetail;

import com.privacity.cliente.model.dto.MessageDetail;

import java.io.Serializable;


public class ItemListMessageDetail implements Serializable {

    MessageDetail messageDetail;
    TestRecyclerMessageDetailAdapter.RecyclerHolder holder;
    public ItemListMessageDetail(MessageDetail messageDetail) {
        this.messageDetail = messageDetail;
    }

    public MessageDetail getMessageDetail() {
        return messageDetail;
    }

    public void setMessageDetail(MessageDetail messageDetail) {
        this.messageDetail = messageDetail;
    }


}
