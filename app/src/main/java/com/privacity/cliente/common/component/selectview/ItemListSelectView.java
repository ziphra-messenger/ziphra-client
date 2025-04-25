package com.privacity.cliente.common.component.selectview;

import com.privacity.cliente.model.dto.MessageDetail;

import java.io.Serializable;


public class ItemListSelectView implements Serializable {

    Object object;

    public ItemListSelectView(Object object) {
        this.object = object;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }


}
