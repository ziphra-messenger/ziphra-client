package com.privacity.cliente.frame.error;

import android.app.Activity;

import com.privacity.cliente.singleton.UtilsStringSingleton;

import lombok.Data;

@Data
public class ErrorInforme {

    private ErrorPojo pojo;
    private String  version;
    private String TAG;
    private String  activity;
    private String entorno;

    public String buildInfo(){
        return UtilsStringSingleton.getInstance().gsonPretty().toJson(this);
    }

}
