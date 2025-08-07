package com.privacity.cliente.activity.mainconfiguracion.seturl;

import com.privacity.cliente.singleton.sharedpreferences.SharedPreferencesUtil;

import lombok.Data;

@Data
public class ServerConfigurationPOJO {
    private String wsProtocolo;
    private String appProtocolo;

    private String wsServerURL;
    private String appServerURL;

    private String wsPort;
    private String appPort;

    private String timeout;

    public String getAppServerToUse(){
        return appProtocolo + "://" + appServerURL + ":" + appPort;
    }
    public String getWsServerToCheck(){
        return appProtocolo + "://" + wsServerURL + ":" + wsPort;
    }
}
