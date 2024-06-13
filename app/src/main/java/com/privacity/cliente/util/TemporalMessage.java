package com.privacity.cliente.util;

import java.util.HashMap;
import java.util.Map;

public class TemporalMessage {

    public static int getIndexBySeconds(long seconds){
        Map<Long,Integer> tiempos = new HashMap<Long,Integer>();
        tiempos.put(15L,1);
        tiempos.put(30L,2);
        tiempos.put(60L,3);
        tiempos.put(300L,4);
        tiempos.put(600L,5);
        tiempos.put(1800L,6);
        tiempos.put(3600L,7);

        return tiempos.get(seconds);
    }

    public static long getSecondsByIndex(int index){
        Map<Integer,Long> tiempos = new HashMap<Integer,Long>();
        tiempos.put(1,15L);
        tiempos.put(2,30L);
        tiempos.put(3,60L);
        tiempos.put(4,300L);
        tiempos.put(5,600L);
        tiempos.put(6,1800L);
        tiempos.put(7,3600L);

        return tiempos.get(index);
    }
}
