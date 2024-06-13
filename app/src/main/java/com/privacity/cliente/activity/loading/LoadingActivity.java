package com.privacity.cliente.activity.loading;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.grupo.GrupoActivity;
import com.privacity.cliente.model.Grupo;
import com.privacity.cliente.singleton.Observers;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.singleton.impl.SingletonServer;
import com.privacity.cliente.singleton.interfaces.ObservadoresGrupos;
import com.privacity.cliente.singleton.sharedpreferences.SharedPreferencesUtil;
import com.privacity.cliente.ws.WebSocket;
import com.privacity.common.dto.GrupoDTO;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.google.GoogleEmojiProvider;

import lombok.Getter;
import lombok.Setter;
import ua.naiksoftware.stomp.ActionI;
import ua.naiksoftware.stomp.StateProcess;

public class LoadingActivity extends AppCompatActivity implements ObservadoresGrupos {

    public static final String TAG = "LoadingActivity";

    private final LoadingActivityGrupoRestDelegate loadingActivityGrupoRestDelegate = new LoadingActivityGrupoRestDelegate(this);
    private final LoadingActivityCrearKeysDelegate loadingActivityCrearKeysDelegate = new LoadingActivityCrearKeysDelegate(this);

    private TextView loadingConsole;
    private String loadingConsoleString="";

    @Getter @Setter
    private StateProcess crearKeys = StateProcess.WORKING;
    @Getter @Setter
    private StateProcess showAppServer = StateProcess.WORKING;
    @Getter @Setter
    private StateProcess connectWS = StateProcess.WORKING;
    @Getter @Setter
    private StateProcess getGrupos = StateProcess.NOT_INIT;
    @Getter @Setter
    private StateProcess emoji = StateProcess.WORKING;

    @Getter @Setter
    int countGruposOK = 0;
    @Getter @Setter
    int countGruposTOTAL = 0;
    @Getter @Setter
    int countGruposPROCESSED = 0;

    boolean ingresandoGrupos=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        SingletonValues.getInstance().getPasswordShortLiveCountDownTimer().restart();
        loadingConsole = (TextView) findViewById(R.id.loading_console);
        loadingConsole.setMovementMethod(new ScrollingMovementMethod());

        loadingConsole.setText("");

        setListeners();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("PrivaCity");

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent intent) {
                String action = intent.getAction();
                if (action.equals("finish_activity_loading")) {
                    finish();
                } else if (action.equals("activity_loading_refresh_console_log")) {
                    refeshTextConsole();
                }
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter("finish_activity_loading"));
        registerReceiver(broadcastReceiver, new IntentFilter("activity_loading_refresh_console_log"));

    }

    private void setListeners() {
        loadingConsole.setKeyListener(null);

        ((Button)findViewById(R.id.loading_console_error_goon)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoadingActivity.this, GrupoActivity.class);
                startActivity(i);
            }
        });

        ((Button)findViewById(R.id.loading_console_error_retry)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ingresandoGrupos=false;
                loadingConsole.setText("");
                onResume();
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        Observers.grupo().dessuscribirse(this);
    }
    @Override
    protected void onResume() {
        super.onResume();

        LoadingAsyncTask t = new LoadingAsyncTask(() -> comenzar());
        t.execute();
    }

    protected void comenzar() {

        addTextConsole("Start");
        try {

            loadingActivityCrearKeysDelegate.crearKeys();
        } catch (Exception e) {
            e.printStackTrace();
            addTextConsole("Fail crearKeys");
            addTextConsole(e.getMessage());
            crearKeys=StateProcess.FAIL;
            endProcess();
        }
        try {
            showAppServer();
        } catch (Exception e) {
            addTextConsole("Fail showAppServer");
            addTextConsole(e.getMessage());
            showAppServer=StateProcess.FAIL;
            endProcess();
        }

        try {
            loadingActivityGrupoRestDelegate.getIdsMyGrupos();
        } catch (Exception e) {
            e.printStackTrace();
            addTextConsole("Fail Grupo");
            addTextConsole(e.getMessage());
            getGrupos=StateProcess.FAIL;
            endProcess();
        }

        try {
            connectWS();
        } catch (Exception e) {
            e.printStackTrace();
            addTextConsole("Fail connectWS");
            addTextConsole(e.getMessage());
            connectWS=StateProcess.FAIL;
            endProcess();
        }

        try {
            addTextConsole("Loading Messaging Tools");
            EmojiManager.install(new GoogleEmojiProvider());
            emoji=StateProcess.SUCESS;
            endProcess();
        } catch (Exception e) {
            e.printStackTrace();
            addTextConsole("Fail Messaging Tools");
            addTextConsole(e.getMessage());
            emoji=StateProcess.FAIL;
            endProcess();
        }

        if (SingletonValues.getInstance().getMyAccountConfDTO().isLoginSkip()){
            SharedPreferencesUtil.saveSharedPreferencesUserPass(this,
                    SingletonValues.getInstance().getUsernameNoHash(),
                    SingletonValues.getInstance().getPasswordNoHash());
        }else{
            SharedPreferencesUtil.deleteSharedPreferencesUserPass(this);
        }
    }

    public void endProcess(){

        if (ingresandoGrupos) return;
        if (countGruposPROCESSED == countGruposTOTAL && !getGrupos.equals(StateProcess.NOT_INIT)) {
            if (countGruposPROCESSED == countGruposOK) {
                getGrupos =StateProcess.SUCESS;
            }else{
                getGrupos = StateProcess.FAIL;
            }
        }
        if(
                !emoji.equals(StateProcess.WORKING) &&
                        !getGrupos.equals(StateProcess.WORKING) &&
                        !getGrupos.equals(StateProcess.NOT_INIT) &&
                        !connectWS.equals(StateProcess.WORKING) &&
                        !showAppServer.equals(StateProcess.WORKING) &&
                        !crearKeys.equals(StateProcess.WORKING)
        ){

            if(
                    emoji.equals(StateProcess.SUCESS) &&
                            getGrupos.equals(StateProcess.SUCESS) &&
                            connectWS.equals(StateProcess.SUCESS) &&
                            showAppServer.equals(StateProcess.SUCESS) &&
                            crearKeys.equals(StateProcess.SUCESS) &&
                            connectWS.equals(StateProcess.SUCESS)

            ) {
                ingresandoGrupos=true;
                SingletonValues.getInstance().passwordCountDownTimerRestart();

                addTextConsole("Ingresando a Privacity");

                Intent i = new Intent(LoadingActivity.this, GrupoActivity.class);
                startActivity(i);
            }else{
                addTextConsole("Error en la carga inicial");

                addTextConsole("Messaging Tools : " + emoji.name());
                addTextConsole("getGrupos : " + getGrupos.name());
                addTextConsole("showAppServer : " + showAppServer.name());
                addTextConsole("crearKeys : " + crearKeys.name());
                addTextConsole("connectWS : " + connectWS.name());
                findViewById(R.id.loading_console_error).setVisibility(View.VISIBLE);
            }
        }

    }

    private void showAppServer(){
        addTextConsole("Connect to");
        String appServer = SingletonServer.getInstance().getAppServer();
        addTextConsole(appServer);
        showAppServer=StateProcess.SUCESS;
        endProcess();
    }
    private void connectWS() {

        addTextConsole("Preparation WebSocket Connection");
        String wsServer = SingletonServer.getInstance().getWsServer();
        addTextConsole(wsServer);
        SingletonValues.getInstance().setWebSocket(new WebSocket(this));

        ActionI action =
                new ActionI(){

                    @Override
                    public void actionSucess(String msg) {
                        addTextConsole(msg);
                        if (!connectWS.equals(StateProcess.FAIL)){
                            connectWS=StateProcess.SUCESS;
                            endProcess();
                        }

                    }
                    @Override
                    public void actionFail(String msg) {
                        connectWS=StateProcess.FAIL;
                        addTextConsole(msg);
                        endProcess();

                    }

                    @Override
                    public void sendInfoMessage(String msg) {
                        addTextConsole(msg);
                    }

                    @Override
                    public void isNotOnline() {
                        connectWS=StateProcess.FAIL;
                        addTextConsole("No hay acceso a Internet. Revise su dispositivo");
                        endProcess();
                    }

                };
        SingletonValues.getInstance().getWebSocket().connectStomp(action,this);

    }

    public synchronized void addTextConsole(String text){
        Log.e(TAG, text);
        loadingConsoleString = loadingConsoleString+text + "\n";

        Intent intent = new Intent("activity_loading_refresh_console_log");
        this.sendBroadcast(intent);

    }

    private synchronized void refeshTextConsole(){
        loadingConsole.setText(loadingConsoleString);
    }

    @Override
    public void actualizarLista() {}

    @Override
    public synchronized void nuevoGrupo(Grupo g) {
        countGruposOK++;
        countGruposPROCESSED++;
        addTextConsole("Grupo Agregado = " + g.getName());

        addTextConsole("Grupo Procesados : " + countGruposPROCESSED +"/" + countGruposTOTAL);

        endProcess();
    }

    @Override
    public void cambioUnread(String idGrupo) {

    }

    @Override
    public void removeGrupo(String idGrupo) {

    }

    @Override
    public void avisarLock(GrupoDTO g) {

    }
}