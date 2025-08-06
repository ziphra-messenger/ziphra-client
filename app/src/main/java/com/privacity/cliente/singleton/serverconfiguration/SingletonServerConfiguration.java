package com.privacity.cliente.singleton.serverconfiguration;

import com.privacity.cliente.R;
import com.privacity.cliente.frame.error.ErrorPojo;
import com.privacity.cliente.frame.error.ErrorView;
import com.privacity.cliente.singleton.impl.SingletonServer;
import com.privacity.cliente.util.ToolsUtil;
import com.privacity.common.SingletonReset;
import com.privacity.common.dto.servergralconf.SystemGralConf;

import lombok.Getter;

public class SingletonServerConfiguration implements SingletonReset {
    public SystemGralConf getSystemGralConf() {

        return systemGralConf;
    }

    private SystemGralConf systemGralConf;
    static private SingletonServerConfiguration instance;

    private SingletonServerConfiguration(){

    }

    public static SingletonServerConfiguration getInstance() {
        if (instance == null){
            instance= new SingletonServerConfiguration();
        }
        return instance;
    }

    public void setSystemGralConf(SystemGralConf systemGralConf) {
        if (this.systemGralConf!= null) return;
        this.systemGralConf = systemGralConf;
    }

    @Override
    public void reset() {
       //ToolsUtil.forceGarbageCollector(systemGralConf);
        //ToolsUtil.forceGarbageCollector(instance);
    }
}
