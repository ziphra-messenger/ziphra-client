package com.privacity.cliente.activity.userhelper;

import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.common.CustomAppCompatActivity;
import com.privacity.cliente.singleton.impl.SingletonServer;

import java.util.Locale;

public class UserHelperActivity extends CustomAppCompatActivity {





    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_helper);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Documentacion");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        String url_doc = bundle.getString(UserHelperPagesContant.URL_DOC);

        String url_language = "/" + Locale.getDefault().getLanguage();

        WebView myWebView = (WebView) findViewById(R.id.userhelper_webview);

        String URL_DOC= SingletonServer.getInstance().getHelpServer()
        + UserHelperPagesContant.URL_ROOT;

        myWebView.loadUrl(URL_DOC + url_language + url_doc);


    }

    @Override
    protected boolean isOnlyAdmin() {
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        this.finish();
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem itemMenu) {

        finish();


        return true;
    }
}