package com.privacity.cliente.activity.mainconfiguracion;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.mainconfiguracion.check.MainConfigurationCheckView;
import com.privacity.cliente.frame.help.HelpFrame;
import com.privacity.cliente.singleton.activity.SingletonCurrentActivity;
import com.privacity.cliente.singleton.sharedpreferences.SharedPreferencesUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainConfiguracionActivity extends AppCompatActivity {
    public static final String CONSTANT__APP = "\nAPP: ";
    public static final String CONSTANT__WS = "\nWS: ";
    public static final String CONSTANT__SLASH = "/";
    private EditText confSelected;
    private Spinner protConf;
    private Spinner portConf;
    private Spinner serverList;
    private boolean flag = false;

    private MainConfigurationCheckView checkView;
    private ImageButton serverHelp;
    private ImageButton serverCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_configuracion);




        SingletonCurrentActivity.getInstance().set(this);
        initActionBar();
        initView();

        setListeners();

    }

    @Override
    protected void onResume() {
        super.onResume();
        SingletonCurrentActivity.getInstance().set(this);
    }

    private void setListeners() {
        serverHelp.setOnClickListener(view -> new HelpFrame().show(getServerHelp()));
        serverCheck.setOnClickListener(view -> new HelpFrame().show(getServerCheck()));

        serverList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (flag) {


                    String appServer = serverList.getSelectedItem().toString();

                    confSelected.setText(appServer);
                } else {
                    flag = true;
                }
                //SharedPreferencesUtil.saveAppServer(MainConfiguracionActivity.this, confSelected.getText().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // when we add text in the edit text
        // it will check for the pattern of text
        confSelected.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            // whenever text size changes it will check
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // if text written matches the pattern then
                // it will show a toast of pattern matches
                validateUrl(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        findViewById(R.id.main_conf_app_server_select).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validateUrl(true);
            }
        });
        /*
        ((Switch) findViewById(R.id.main_conf_developer_developer_switch)).setChecked(
                SharedPreferencesUtil.getDeveloperMode(MainConfiguracionActivity.this));


        findViewById(R.id.main_conf_developer_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Switch s = (Switch) findViewById(R.id.main_conf_developer_developer_switch);
                SharedPreferencesUtil.saveDeveloperMode(MainConfiguracionActivity.this, s.isChecked());
            }
        });*/
    }

    private void initView() {
        confSelected = (EditText) findViewById(R.id.main_conf_app_server_conf);
        serverCheck = (ImageButton) findViewById(R.id.main_conf_app_server__check);
        serverHelp = (ImageButton) findViewById(R.id.main_conf_app_server__help);
        serverList = (Spinner) findViewById(R.id.main_conf_app_server_list);
        protConf = (Spinner) findViewById(R.id.main_conf_app_server_conf_prot);
        portConf = (Spinner) findViewById(R.id.main_conf_app_server_conf_port);
        checkView = new MainConfigurationCheckView();
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) return;
        actionBar.setTitle(getString(R.string.general__title));
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public void validateUrl(boolean save) {
        if (Patterns.WEB_URL.matcher(confSelected.getText().toString()).matches()) {

            String prot;
            String protws;
            if (protConf.getSelectedItemPosition() == 0) {
                prot = "http";
                protws = "ws";
            } else {
                prot = "https";
                protws = "wss";
            }

            String port = null;
            String portws = null;
            if (portConf.getSelectedItemPosition() == 0) {
                port = "80";
                portws = "80";
            } else if (portConf.getSelectedItemPosition() == 1) {
                port = "80";
                portws = "90";
            } else if (portConf.getSelectedItemPosition() == 2) {
                port = "8080";
                portws = "8080";
            } else if (portConf.getSelectedItemPosition() == 3) {
                port = "8080";
                portws = "8090";
            } else if (portConf.getSelectedItemPosition() == 4) {
                port = "80";
                portws = "3000";
            } else if (portConf.getSelectedItemPosition() == 5) {
                port = "443";
                portws = "8443";
            }

            SharedPreferencesUtil.saveWsServerProtocol(MainConfiguracionActivity.this, protws);
            SharedPreferencesUtil.saveAppServerProtocol(MainConfiguracionActivity.this, prot);

            SharedPreferencesUtil.saveWsServerPort(MainConfiguracionActivity.this, portws);
            SharedPreferencesUtil.saveAppServerPort(MainConfiguracionActivity.this, port);

            SharedPreferencesUtil.saveWsServerUrl(MainConfiguracionActivity.this, confSelected.getText().toString());
            SharedPreferencesUtil.saveAppServerUrl(MainConfiguracionActivity.this, confSelected.getText().toString());


            if (save) {

                Toast.makeText(MainConfiguracionActivity.this, getString(R.string.general__saved) +
                                CONSTANT__APP + SharedPreferencesUtil.getAppServerToUse(this) +
                                CONSTANT__WS + SharedPreferencesUtil.getWsServerToUse(this)
                        , Toast.LENGTH_SHORT).show();
               // onBackPressed();
            }
        } else {
            // otherwise show error of invalid url
            confSelected.setError(getString(R.string.mainconfiguracion_activity__validation__error__url));
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem itemMenu) {
        this.finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        validateUrl(true);
        this.finish();
    }


    @Override
    protected void onStart() {
        super.onStart();


        String url = SharedPreferencesUtil.getAppServerUrl(MainConfiguracionActivity.this);

        String wsPort = SharedPreferencesUtil.getWsServerPort(MainConfiguracionActivity.this);
        String appPort = SharedPreferencesUtil.getAppServerPort(MainConfiguracionActivity.this);

        String wsProto = SharedPreferencesUtil.getWsServerPort(MainConfiguracionActivity.this);
        String appProto = SharedPreferencesUtil.getAppServerPort(MainConfiguracionActivity.this);

        confSelected.setText(url);
        if ((appPort + CONSTANT__SLASH + wsPort).equals("80/80")) portConf.setSelection(0);
        if ((appPort + CONSTANT__SLASH + wsPort).equals("80/90")) portConf.setSelection(1);
        if ((appPort + CONSTANT__SLASH + wsPort).equals("8080/8080")) portConf.setSelection(2);
        if ((appPort + CONSTANT__SLASH + wsPort).equals("8080/8090")) portConf.setSelection(3);
        if ((appPort + CONSTANT__SLASH + wsPort).equals("80/3000")) portConf.setSelection(4);
        if ((appPort + CONSTANT__SLASH + wsPort).equals("443/8443")) portConf.setSelection(5);

        if (appProto.contains("https")) {
            protConf.setSelection(1);
        } else {
            protConf.setSelection(0);
        }


    }

    private String getServerHelp(){

        return getString( R.string.main_configuration__check_connections__help);
  }

    private String getServerCheck(){
        return getString( R.string.main_configuration_url_port__help);
    };
}