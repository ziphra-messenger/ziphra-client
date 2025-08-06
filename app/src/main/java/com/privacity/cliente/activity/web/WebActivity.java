package com.privacity.cliente.activity.web;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.privacity.cliente.R;
import com.privacity.cliente.common.constants.IntentConstant;

public class WebActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        WebView w = (WebView) findViewById(R.id.web);

        Intent intent = getIntent();
        String protocoloDTO = intent.getStringExtra(IntentConstant.URL );
        w.setWebViewClient(new WebViewClient());

        WebSettings webSettings = w.getSettings();
        webSettings.setJavaScriptEnabled(true);

        w.loadUrl(protocoloDTO);    }
}