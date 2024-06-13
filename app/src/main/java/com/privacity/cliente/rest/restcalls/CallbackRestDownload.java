package com.privacity.cliente.rest.restcalls;

import org.springframework.http.ResponseEntity;

abstract public class CallbackRestDownload {

    public abstract void response(ResponseEntity<byte[]> response);
    public abstract void onError(ResponseEntity<byte[]> response);

    public abstract void beforeShowErrorMessage(String msg);
}
