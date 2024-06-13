package ua.naiksoftware.stomp;

import android.content.Context;
import android.content.Intent;

public class LineStatus {

    public static void statusOffLine(Context context){


        Intent intent = new Intent("connection_closed");
        context.sendBroadcast(intent);
    }
    public static void statusOnLine(Context context){
        Intent intent = new Intent("connection_open");
        context.sendBroadcast(intent);
    }

}
