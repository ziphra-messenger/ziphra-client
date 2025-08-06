package com.privacity.cliente.activity.mainconfiguracion.check;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.common.GetButtonReady;
import com.privacity.cliente.activity.mainconfiguracion.MainConfiguracionActivity;
import com.privacity.cliente.frame.error.ErrorPojo;
import com.privacity.cliente.frame.error.ErrorView;
import com.privacity.cliente.singleton.impl.SingletonServer;
import com.privacity.cliente.singleton.sharedpreferences.SharedPreferencesUtil;

public class MainConfigurationCheckView {

    private TextView wifi;
    private TextView datosMoviles;
    private TextView internet;
    private TextView server;

    private MainConfiguracionActivity activity;

    public MainConfigurationCheckView(MainConfiguracionActivity activity){
        this.activity=activity;
        initView();
    }

    public void startTest() {
        cleanView();

        testWifiOn();
        testDatosMovilesOn();
        testInternetOn();
        testServerOn();
    }

    private void cleanView() {
        wifi.setText("");
        internet.setText("");
        datosMoviles.setText("");
        server.setText("");
    }
    private void testInternetOn() {
        TextView v =  internet;
        String url="http://www.stackoverflow.com";

        checkSites(v, url);


    }
    private void testServerOn() {
        TextView v =  server;

        String url= activity.getSetUrl().getServerConfigurationPOJO().getAppServerToUse();



        checkSites(v, url);


    }

    private void checkSites(TextView v, String url) {
        CheckServer taskinner = new CheckServer(getActivity(), new CallbackCheckConf() {
            @Override
            public void response(ErrorPojo p) {
                setTest(v, true);
            }

            @Override
            public void onError(ErrorPojo p) {
                setTest(v, false, p);

            }


        }, url

        );
        taskinner.execute();
    }

    private void setTest(TextView internet, boolean b) {
        setTest(internet,b,new ErrorPojo().setUrl("wewe").setErrorDescription("eew"));
    }






    private void testDatosMovilesOn() {
        boolean mobileYN = false;

        TelephonyManager tm = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        if (tm.getSimState() == TelephonyManager.SIM_STATE_READY) {
            if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1)
            {
                mobileYN = Settings.Global.getInt(getActivity().getContentResolver(), "mobile_data", 1) == 1;
            }
            else{
                mobileYN = Settings.Secure.getInt(getActivity().getContentResolver(), "mobile_data", 1) == 1;
            }
        }
        setTest(datosMoviles, mobileYN);
    }

    private void testWifiOn() {
        WifiManager wifiTest = (WifiManager)getActivity().getSystemService(Context.WIFI_SERVICE);
            setTest(wifi, wifiTest.isWifiEnabled());
    }

    private void setTest(TextView v, boolean r, ErrorPojo p) {

            Thread thread = new Thread(){
                @Override
                public void run() {
                    try {
                        synchronized (this) {


                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (r){
                                        v.setText("✓");

                                    }else{
                                        v.setText("❌ ver detalles");

                                        v.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                if (p!=null) new ErrorView().show(p);
                                            }
                                        });
                                    }
                                }
                            });

                        }
                    } catch (Exception e) {
                        v.setText("❌ ver detalles");
                        v.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (p!=null) new ErrorView().show(p);
                            }
                        });
                    }

                };
            };
            thread.start();


    }

    private void initView() {
        
        this.wifi= (TextView)getActivity().findViewById(R.id.main_conf__check__wifi);
        this.internet= (TextView)getActivity().findViewById(R.id.main_conf__check__internet);
        this.server= (TextView)getActivity().findViewById(R.id.main_conf__check__server);
        this.datosMoviles= (TextView)getActivity().findViewById(R.id.main_conf__check__datos_moviles);
    }

    private Activity getActivity() {
        return this.activity;
    }


}
