package com.privacity.cliente.activity.mainconfiguracion.actions;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.common.GetButtonReady;
import com.privacity.cliente.activity.mainconfiguracion.MainConfiguracionActivity;
import com.privacity.cliente.activity.mainconfiguracion.seturl.ServerConfigurationPOJO;
import com.privacity.cliente.frame.help.HelpFrame;
import com.privacity.cliente.singleton.sharedpreferences.SharedPreferencesUtil;

import lombok.Getter;

public class MainConfigurationActionsView {


    private static final String CONSTANT__APP = "\nAPP: ";
    private static final String CONSTANT__WS = "\nWS: ";
  //  private static final String CONSTANT__SLASH = "/";

    @Getter
    private Button startTest;
    @Getter
    private Button save;
    private MainConfiguracionActivity activity;

    private ImageButton serverHelp;
    private ImageButton serverCheck;

    public MainConfigurationActionsView(MainConfiguracionActivity activity){
        this.activity=activity;
        initView();
        setListeners();
    }

    private void initView() {

        this.startTest = GetButtonReady.get(activity, R.id.main_conf__check__start, "Probar" );
        this.save = GetButtonReady.get(activity, R.id.main_conf__save, "Guardar" );
        serverCheck = (ImageButton) this.activity.findViewById(R.id.main_conf_app_server__check);
        serverHelp = (ImageButton) this.activity.findViewById(R.id.main_conf_app_server__help);


    }
    public void startTestEnabled(boolean b){

        startTest.setEnabled(b);
    }

    private void setListeners() {
        serverHelp.setOnClickListener(view -> new HelpFrame().show(getServerHelp()));
        startTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.getCheckView().startTest();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ServerConfigurationPOJO pojo = activity.getSetUrl().getServerConfigurationPOJO();

                SharedPreferencesUtil.saveWsServerProtocol(activity, pojo.getWsProtocolo());
                SharedPreferencesUtil.saveAppServerProtocol(activity, pojo.getAppProtocolo());

                SharedPreferencesUtil.saveWsServerPort(activity, pojo.getWsPort());
                SharedPreferencesUtil.saveAppServerPort(activity, pojo.getAppPort());

                SharedPreferencesUtil.saveWsServerUrl(activity, pojo.getWsServerURL());
                SharedPreferencesUtil.saveAppServerUrl(activity, pojo.getAppServerURL());


                Toast.makeText(activity, activity.getString(R.string.general__saved) +
                            CONSTANT__APP + SharedPreferencesUtil.getAppServerToUse(activity) +
                            CONSTANT__WS + SharedPreferencesUtil.getWsServerToUse(activity)
                    , Toast.LENGTH_SHORT).show();
                // onBackPressed();


            }
        });
    }

    private String getServerHelp(){

        return this.activity.getString( R.string.main_configuration__check_connections__help);
    }
}
