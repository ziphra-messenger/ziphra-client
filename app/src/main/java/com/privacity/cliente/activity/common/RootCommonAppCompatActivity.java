package com.privacity.cliente.activity.common;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.privacity.cliente.activity.reconnect.ReconnectFrame;
import com.privacity.cliente.singleton.activity.SingletonCurrentActivity;
import com.privacity.cliente.singleton.usuario.SingletonSessionClosing;

public abstract class RootCommonAppCompatActivity extends AppCompatActivity  {


    public ReconnectFrame reconnectFrame;

    @Override
    protected void onResume() {
        super.onResume();
        if (SingletonSessionClosing.getInstance().isClosing())return;
        SingletonCurrentActivity.getInstance().set(this);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SingletonSessionClosing.getInstance().isClosing())return;
        SingletonCurrentActivity.getInstance().set(this);
    }


}
