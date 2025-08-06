package com.privacity.cliente.singleton.serverconfiguration;

import com.privacity.cliente.util.ToolsUtil;
import com.privacity.common.SingletonReset;
import com.privacity.common.dto.servergralconf.SystemGralConf;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import lombok.Getter;

public class SingletonServerTime implements SingletonReset  {

    private LocalDateTime serverTime;
    private long serverTimeDifference;

    static private SingletonServerTime instance;

    private SingletonServerTime(){

    }

    public static SingletonServerTime getInstance() {
        if (instance == null){
            instance= new SingletonServerTime();
        }
        return instance;
    }

    public LocalDateTime getServerTime() {
        return serverTime;
    }


    public void setServerTime(LocalDateTime serverTime) {
        this.serverTime = serverTime;

        LocalDateTime my = LocalDateTime.now();

        Duration dur = Duration.between(my, serverTime);
        serverTimeDifference = dur.toMillis();
    }

    public LocalDateTime calculateServerTime() {
        LocalDateTime my = LocalDateTime.now();

        LocalDateTime r = my.plus(serverTimeDifference, ChronoUnit.MILLIS);
//        Log.d(this.getClass().getSimpleName() + "." + getMethodName(3),"serverTimeDifference > " + serverTimeDifference);
//        Log.d(this.getClass().getSimpleName() + "." + getMethodName(3),"localtime > " + my.toString());
//        Log.d(this.getClass().getSimpleName() + "." + getMethodName(3),"calculateServerTime > " +r.toString());
        return r;
    }

    @Override
    public void reset() {
        ToolsUtil.forceGarbageCollector(serverTime);
        ToolsUtil.forceGarbageCollector(serverTimeDifference);
        ToolsUtil.forceGarbageCollector(instance);
    }
}
