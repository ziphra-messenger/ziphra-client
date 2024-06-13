package com.privacity.cliente.ws;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.privacity.cliente.R;

public class CreateForegroundServiceActivity extends AppCompatActivity { // implements View.OnClickListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_foreground_service);
        setTitle("dev2qa.com - Android Foreground Service Example.");
        Button startServiceButton = (Button)findViewById(R.id.start_foreground_service_button);
        startServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateForegroundServiceActivity.this, MyForeGroundService.class);
                intent.setAction(MyForeGroundService.ACTION_START_FOREGROUND_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startService(intent);
                     startForegroundService(intent);
                } else {
                     startService(intent);
                }
            }
        });
        Button stopServiceButton = (Button)findViewById(R.id.stop_foreground_service_button);
        stopServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateForegroundServiceActivity.this, MyForeGroundService.class);
                intent.setAction(MyForeGroundService.ACTION_STOP_FOREGROUND_SERVICE);
               
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                     startForegroundService(intent);
                } else {
                     startService(intent);
                }     
            }
        });
    }
}