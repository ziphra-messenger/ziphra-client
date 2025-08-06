package com.privacity.cliente.rest;

import com.privacity.cliente.model.dto.Protocolo;

import org.springframework.http.ResponseEntity;

abstract public class CallbackRest {

    public abstract void response(ResponseEntity<Protocolo> response);
    public abstract void onError(ResponseEntity<Protocolo> response);

    public abstract void beforeShowErrorMessage(String msg);
}
