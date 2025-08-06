package com.privacity.cliente.activity.mainconfiguracion.seturl;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.mainconfiguracion.MainConfiguracionActivity;
import com.privacity.cliente.singleton.sharedpreferences.SharedPreferencesUtil;
import com.privacity.common.config.ConstantValidation;

public class MainConfigurationSetURLView {


    private Spinner protocoloConfWS;
    private Spinner protocoloConfHTTP;

    private EditText serverURLConfWS;
    private EditText serverURLConfHTTP;

    private EditText portConfWS;
    private EditText portConfHTTP;


    private MainConfiguracionActivity activity;
    private Button reset;

    private Button developerConfiguration;
    private Button oficialConfiguration;

    public MainConfigurationSetURLView(MainConfiguracionActivity activity){
        this.activity=activity;
        initView();
        setListeners();
        loadValues();
    }

    public ServerConfigurationPOJO getServerConfigurationPOJO(){
        ServerConfigurationPOJO r = new ServerConfigurationPOJO();

        r.setAppPort(portConfHTTP.getText().toString());
        r.setWsPort(portConfWS.getText().toString());

        r.setAppServerURL(serverURLConfHTTP.getText().toString());
        r.setWsServerURL(serverURLConfWS.getText().toString());

        r.setAppProtocolo( protocoloConfHTTP.getSelectedItem().toString());
        r.setWsProtocolo( protocoloConfWS.getSelectedItem().toString());
        return r;
    }

    private void loadValues() {


        String wsProtocolo = SharedPreferencesUtil.getWsServerPort(this.activity);
        String appProtocolo = SharedPreferencesUtil.getAppServerPort(this.activity);

        String wsServerURL = SharedPreferencesUtil.getWsServerUrl(this.activity);
        String appServerURL = SharedPreferencesUtil.getAppServerUrl(this.activity);

        String wsPort = SharedPreferencesUtil.getWsServerPort(this.activity);
        String appPort = SharedPreferencesUtil.getAppServerPort(this.activity);

        setValues(wsProtocolo,appProtocolo, wsServerURL, appServerURL, wsPort, appPort);

    }

    private void setValues(String wsProtocol,  String appProtocolo, String wsServerURL, String appServerURL, String wsPort, String appPort) {

        if (wsProtocol.equals("ws")){
            protocoloConfWS.setSelection(0);
        }else{
            protocoloConfWS.setSelection(1);
        }

        if (appProtocolo.equals("http")){
            protocoloConfHTTP.setSelection(0);
        }else{
            protocoloConfHTTP.setSelection(1);
        }

        serverURLConfWS.setText(wsServerURL);
        serverURLConfHTTP.setText(appServerURL);

        portConfWS.setText(wsPort);
        portConfHTTP.setText(appPort);

        serverURLConfWS.setError(null);
        serverURLConfHTTP.setError(null);
    }

    private void setListeners() {

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadValues();
            }
        });

        developerConfiguration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValues("ws", "http", "10.0.2.2", "10.0.2.2", "8090", "8080");
            }
        });

        oficialConfiguration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValues("ws", "http", "192.168.0.14", "192.168.0.14", "8090", "8080");
            }
        });

        setTextChangeListener(serverURLConfHTTP);
        setTextChangeListener(serverURLConfWS);
    }


    private boolean validarURL(String url){
        return Patterns.WEB_URL.matcher(url).matches();
       // confSelected.setError(this.activity.getString(R.string.mainconfiguracion_activity__validation__error__url));
    }
    private void setTextChangeListener(EditText e){

        e.addTextChangedListener(
        new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            // whenever text size changes it will check
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // if text written matches the pattern then
                // it will show a toast of pattern matches


            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!validarURL(e.getText().toString())){
                    e.setError(activity.getString(R.string.mainconfiguracion_activity__validation__error__url));
                    activity.getSetActions().getStartTest().setEnabled(false);
                    activity.getSetActions().getSave().setEnabled(false);
                }else{
                    activity.getSetActions().getStartTest().setEnabled(true);
                    activity.getSetActions().getSave().setEnabled(true);

                }
            }
        });
    }


    private void initView() {

        reset = (Button) this.activity.findViewById(R.id.main_conf__reset);
        developerConfiguration = (Button) this.activity.findViewById(R.id.main_conf__developer_server);
        oficialConfiguration = (Button) this.activity.findViewById(R.id.main_conf__oficial_server);

        protocoloConfWS = (Spinner) this.activity.findViewById(R.id.main_conf__ws__protoloco);
        protocoloConfHTTP = (Spinner) this.activity.findViewById(R.id.main_conf__http__protoloco);

        serverURLConfWS = (EditText) this.activity.findViewById(R.id.main_conf__ws__ip);
        serverURLConfHTTP = (EditText) this.activity.findViewById(R.id.main_conf__http__ip);

        serverURLConfWS.setFilters(new InputFilter[]{new InputFilter.LengthFilter(100)});
        serverURLConfHTTP.setFilters(new InputFilter[]{new InputFilter.LengthFilter(100)});

        portConfWS = (EditText) this.activity.findViewById(R.id.main_conf__ws__port);
        portConfHTTP = (EditText) this.activity.findViewById(R.id.main_conf__http__port);

        portConfWS.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
        portConfHTTP.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});

    }

}
