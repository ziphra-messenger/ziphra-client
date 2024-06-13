package com.privacity.cliente.util.notificacion;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.loading.LoadingActivity;

public class NotificationReceiver  extends Activity {
@Override
protected void onCreate(Bundle savedInstanceState) {


    if (savedInstanceState == null) {
        Bundle extras = getIntent().getExtras();
        if(extras == null)
        {
            Log.d("Test4", "Test4");
        }
        else if (extras.getBoolean("NotiClick"))
        {
            Log.d("Test5", "Test5");
            stopService(new Intent(getApplicationContext(), LoadingActivity.class));
        }

    }



    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_loading);
}}