package ua.naiksoftware.stomp;

import android.content.Context;
import android.content.Intent;

import com.privacity.common.BroadcastConstant;

public class LineStatus {

    public static void statusOffLine(Context context){


        Intent intent = new Intent(BroadcastConstant.BROADCAST__MESSAGING__CONNECTION_CLOSED);
        context.sendBroadcast(intent);
    }
    public static void statusOnLine(Context context){
        Intent intent = new Intent("connection_open");
        context.sendBroadcast(intent);
    }

}
