package com.privacity.cliente.activity.mainconfiguracion;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.privacity.cliente.R;
import com.privacity.cliente.singleton.sharedpreferences.SharedPreferencesUtil;

public class MainConfiguracionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_configuracion);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("PrivaCity");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViewById(R.id.main_conf_app_server_select).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Spinner s = (Spinner) findViewById(R.id.main_conf_app_server_list);
                String appServer = s.getSelectedItem().toString();
                SharedPreferencesUtil.saveAppServer(MainConfiguracionActivity.this, appServer);
            }
        });

        findViewById(R.id.main_conf_ws_server_select).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Spinner s = (Spinner) findViewById(R.id.main_conf_ws_server_list);
                String wsServer = s.getSelectedItem().toString();
                SharedPreferencesUtil.saveWsServer(MainConfiguracionActivity.this, wsServer);
            }
        });
        ((Switch) findViewById(R.id.main_conf_developer_developer_switch)).setChecked(
                SharedPreferencesUtil.getDeveloperMode(MainConfiguracionActivity.this));


        findViewById(R.id.main_conf_developer_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Switch s = (Switch) findViewById(R.id.main_conf_developer_developer_switch);
                SharedPreferencesUtil.saveDeveloperMode(MainConfiguracionActivity.this, s.isChecked());
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem itemMenu) {
        this.finish();
        return true;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        this.finish();
    }


}