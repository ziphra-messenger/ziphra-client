package com.privacity.cliente.util.activities;

import android.app.Activity;
import android.content.Intent;

import com.privacity.cliente.singleton.activity.SingletonCurrentActivity;

public class IntentUtil {
    public static void sendBroadcast(Activity activ, String broadcastAction) {
        Intent intent = new Intent(broadcastAction);
        activ.sendBroadcast(intent);
    }
    public static void callActivity(Class toOpen) {
        Intent i = new Intent(SingletonCurrentActivity.getInstance().get(), toOpen);
        SingletonCurrentActivity.getInstance().get().startActivity(i);
    }
}
