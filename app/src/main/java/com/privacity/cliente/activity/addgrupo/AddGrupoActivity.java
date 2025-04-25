package com.privacity.cliente.activity.addgrupo;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.common.CustomAppCompatActivity;

import org.apache.commons.lang3.RandomStringUtils;

public class AddGrupoActivity extends CustomAppCompatActivity {

    private AddGrupoView addGrupoView;
    @Override
    protected boolean isOnlyAdmin() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_grupo);

        initActionBar();

        addGrupoView=new AddGrupoView(this);

    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) return;
        actionBar.setTitle(getString(R.string.addgrupo_activity__title));
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public String generateNombre() {
        return RandomStringUtils.randomAlphanumeric(8);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem itemMenu) {
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}