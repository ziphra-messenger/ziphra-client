package com.privacity.cliente.rest;

import com.privacity.common.dto.ProtocoloDTO;

import org.springframework.http.ResponseEntity;

abstract public class CallbackRest {

    public abstract void response(ResponseEntity<ProtocoloDTO> response);
    public abstract void onError(ResponseEntity<ProtocoloDTO> response);

    public abstract void beforeShowErrorMessage(String msg);
}
