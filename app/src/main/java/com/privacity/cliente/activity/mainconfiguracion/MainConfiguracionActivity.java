package com.privacity.cliente.activity.mainconfiguracion;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.mainconfiguracion.actions.MainConfigurationActionsView;
import com.privacity.cliente.activity.mainconfiguracion.check.MainConfigurationCheckView;
import com.privacity.cliente.activity.mainconfiguracion.seturl.MainConfigurationSetURLView;
import com.privacity.cliente.singleton.activity.SingletonCurrentActivity;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainConfiguracionActivity extends AppCompatActivity {

    @Getter
    private MainConfigurationActionsView setActions;
    @Getter
    private MainConfigurationSetURLView setUrl;
    @Getter
    private MainConfigurationCheckView checkView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_configuracion);

        SingletonCurrentActivity.getInstance().set(this);
        initActionBar();
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SingletonCurrentActivity.getInstance().set(this);
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

    private void initView() {
        setActions = new MainConfigurationActionsView(this);
        setUrl = new MainConfigurationSetURLView(this);
        checkView = new MainConfigurationCheckView(this);
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) return;
        actionBar.setTitle(getString(R.string.general__title));
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private String getServerCheck() {
        return getString(R.string.main_configuration_url_port__help);
    }
}
