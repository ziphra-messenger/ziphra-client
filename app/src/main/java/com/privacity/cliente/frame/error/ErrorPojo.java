package com.privacity.cliente.frame.error;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@NoArgsConstructor
@Accessors(chain = true)
@Data
public class ErrorPojo {
    private String errorDescription;
    private String errorCode;
    private String recomendacion;
    private String stackTrace;
    private String TAG;
    private HttpStatus httpStatus;
    private boolean informar=true;
    private String url;


    public void setStackTrace(Exception e) {
        stackTrace=ExceptionUtils.getStackTrace(e);
    }
}
