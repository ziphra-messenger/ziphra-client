package com.privacity.cliente.activity.messagedetail;

import com.privacity.common.dto.MessageDetailDTO;

import java.io.Serializable;


public class ItemListMessageDetail implements Serializable {

    MessageDetailDTO messageDetailDTO;

    public ItemListMessageDetail(MessageDetailDTO messageDetailDTO) {
        this.messageDetailDTO = messageDetailDTO;
    }

    public MessageDetailDTO getMessageDetailDTO() {
        return messageDetailDTO;
    }

    public void setMessageDetailDTO(MessageDetailDTO messageDetailDTO) {
        this.messageDetailDTO = messageDetailDTO;
    }


}
