package com.privacity.cliente.singleton.devtools;

import com.privacity.cliente.singleton.serverconfiguration.SingletonServerTime;
import com.privacity.cliente.util.ToolsUtil;
import com.privacity.common.SingletonReset;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import lombok.Getter;

public class GeneratorMessageTestSingleton implements SingletonReset {



    static private GeneratorMessageTestSingleton instance;


    private int count;

    public int getCount(){
        count++;
        return count;
    }
    private GeneratorMessageTestSingleton(){
        count=0;

    }

    public static GeneratorMessageTestSingleton getInstance() {
        if (instance == null){
            instance= new GeneratorMessageTestSingleton();
        }
        return instance;
    }


    @Override
    public void reset() {

    }
}
