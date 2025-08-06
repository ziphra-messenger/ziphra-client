package com.privacity.cliente.activity.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.grupo.GrupoActivity;
import com.privacity.cliente.activity.grupo.GrupoUtil;
import com.privacity.cliente.activity.lock.LockActivity;
import com.privacity.cliente.activity.reconnect.ReconnectFrame;
import com.privacity.cliente.enumeration.WsStatusEnum;
import com.privacity.cliente.frame.help.HelpFrame;
import com.privacity.cliente.model.Grupo;
import com.privacity.cliente.singleton.Observers;
import com.privacity.cliente.singleton.SingletonSessionFinish;
import com.privacity.cliente.singleton.SingletonValues;import com.privacity.cliente.singleton.Singletons;
import com.privacity.cliente.singleton.countdown.SingletonMyAccountConfLockDownTimer;
import com.privacity.cliente.singleton.interfaces.ObservadoresPassword;
import com.privacity.cliente.singleton.usuario.SingletonSessionClosing;
import com.privacity.common.BroadcastConstant;
import com.privacity.common.SingletonReconnectionLog;
import com.privacity.common.enumeration.GrupoRolesEnum;

public abstract class CustomAppCompatActivity extends RootCommonAppCompatActivity implements ObservadoresPassword {

    protected abstract boolean isOnlyAdmin();
    public ReconnectFrame reconnectFrame;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

//        if(!SingletonServerConfiguration.getInstance().getSystemGralConf().getExtras().isScreenshotEnabled()){
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
//        }

        super.onCreate(savedInstanceState);



        if (isOnlyAdmin()){
            GrupoRolesEnum myRole = Observers.grupo().whichIsMyRole(SingletonValues.getInstance().getGrupoSeleccionado().getIdGrupo());

            if (!myRole.equals(GrupoRolesEnum.ADMIN)){
                String roleString = GrupoUtil.transformGrupoRoleEnumToCompleteString(this,myRole);

                Toast toast=Toast.makeText(getApplicationContext(),getString(R.string.general__alert__validation__role__only_admin,roleString) ,Toast. LENGTH_LONG);
                toast.show();
                finish();
                return;
            }
        }

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent intent) {
                String action = intent.getAction();
                switch (action) {
                    case BroadcastConstant.BROADCAST__FINISH_ALL_ACTIVITIES:
                    case BroadcastConstant.BROADCAST__FINISH_APPLICATION:
                        finish();
                        break;
                    case BroadcastConstant.BROADCAST__MESSAGING__CONNECTION_CLOSED:
                        reconnection();
                        break;
                    case "connection_trying":
                        trying();
                        break;
                    case "connection_open":
                        connected();
                        break;
                    case "connection_time":
                        reconnectTime();
                        break;
                    case "connection_off_line":
                        reconnectOffline();
                        break;
                }


            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter(BroadcastConstant.BROADCAST__FINISH_ALL_ACTIVITIES));
        registerReceiver(broadcastReceiver, new IntentFilter(BroadcastConstant.BROADCAST__FINISH_APPLICATION));

        registerReceiver(broadcastReceiver, new IntentFilter(BroadcastConstant.BROADCAST__MESSAGING__CONNECTION_CLOSED));
        registerReceiver(broadcastReceiver, new IntentFilter("connection_open"));
        registerReceiver(broadcastReceiver, new IntentFilter("connection_trying"));
        registerReceiver(broadcastReceiver, new IntentFilter("connection_time"));
        registerReceiver(broadcastReceiver, new IntentFilter("connection_off_line"));

        Observers.password().suscribirse(this);



    }

    protected void reconnectOffline(){
        reconnectFrame.getContentAll().setVisibility(View.VISIBLE);
    }


    protected void trying(){
        reconnectFrame.getContentAll().setVisibility(View.VISIBLE);

        if ( reconnectFrame.getSeconds() == null) return;

        reconnectFrame.getWaitingView().setVisibility(View.GONE);
        reconnectFrame.getTryingView().setVisibility(View.VISIBLE);

        reconnectFrame.getCountDisconnect().setText(Singletons.reconnect().getDisconnetedCount()+"");
        reconnectFrame.getCountTrying().setText(Singletons.reconnect().getReintentosCount()+"");
        reconnectFrame.getCountTryingTotal().setText(Singletons.reconnect().getReintentosTotalCount()+"");

       reconnectFrame.addLogLines(SingletonReconnectionLog.getInstance().getLog());
    }

    protected void connected(){

        reconnectFrame.getContentAll().setVisibility(View.GONE);
        reconnectFrame.addLogLines(SingletonReconnectionLog.getInstance().getLog());
        SingletonReconnectionLog.getInstance().reset();

    }

    protected void reconnection(){

        if ( this instanceof GrupoActivity){
            Observers.grupo().resetOnLineMember();
            Observers.grupo().resetWriting();
        }


        Singletons.reconnect().waiting(this);
        reconnectFrame.getContentAll().setVisibility(View.VISIBLE);

        if ( reconnectFrame.getSeconds() != null) {

            reconnectFrame.getWaitingView().setVisibility(View.VISIBLE);
            reconnectFrame.getTryingView().setVisibility(View.GONE);

            reconnectFrame.getCountDisconnect().setText(Singletons.reconnect().getDisconnetedCount() + "");
            reconnectFrame.getCountTrying().setText(Singletons.reconnect().getReintentosCount() + "");
            reconnectFrame.getCountTryingTotal().setText(Singletons.reconnect().getReintentosTotalCount() + "");
            reconnectFrame.addLogLines(SingletonReconnectionLog.getInstance().getLog());
        }


    }

    protected void reconnectTime(){
        if ( reconnectFrame.getSeconds() == null) return;
        reconnectFrame.getSeconds().setText(Singletons.reconnect().getSecondsValue()+"");

        reconnectFrame.addLogLines(SingletonReconnectionLog.getInstance().getLog());


    }


    @Override
    protected void onResume() {

        super.onResume();
        if (SingletonSessionClosing.getInstance().isClosing())return;

        if (reconnectFrame == null) {
            reconnectFrame = new ReconnectFrame(this);
        }else{
            reconnectFrame.loadValues();
        }

        if (Singletons.reconnect().status.equals(WsStatusEnum.CONNECTED)){
            this.connected();
        }else if (Singletons.reconnect().status.equals(WsStatusEnum.WAITING)){
            this.reconnection();
        }else if (Singletons.reconnect().status.equals(WsStatusEnum.TRYING)){
            this.trying();
        }
        reconnectTime();

        SingletonSessionFinish.getInstance().restart();
        Observers.password().suscribirse(this);

        try {


        if ( !SingletonValues.getInstance().getWebSocket().isConnected()) {
            reconnection();
        }else {
            SingletonReconnectionLog.getInstance().reset();
        }
        }catch (Exception e){
            //e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Observers.password().remove(this);
        //SingletonValues.getInstance().passwordCountDownTimerRestart();

    }

    @Override
    protected void onStart() {
        super.onStart();
        Observers.password().suscribirse(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Observers.password().remove(this);
        //SingletonValues.getInstance().passwordCountDownTimerRestart();
    }

    @Override
    public void passwordExpired() {

        if (SingletonValues.getInstance().getMyAccountConfDTO().getLock().isEnabled()){
            if (!SingletonValues.getInstance().isShowingLock()){
                Intent i = new Intent(this, LockActivity.class);
                startActivity(i);
            }
        }
    }

    @Override
    public void passwordSet() {

    }


    public void goToHelp(View v){
        HelpFrame g = new HelpFrame();


        try {
            g.show(((View)v.getParent().getParent()).getTag().toString());
        } catch (Exception e) {
            g.show("no implementado");
        }
        //String url = ((View)v.getParent().getParent()).getTag().toString();
        //UserHelperUtil.open(this, url);
    }

    public boolean getGrupoSeleccionadoIsNull(){
        return SingletonValues.getInstance().getGrupoSeleccionado() != null
                && SingletonValues.getInstance().getGrupoSeleccionado().getIdGrupo() != null;
    }

    public Grupo getGrupoSeleccionado(){
        return SingletonValues.getInstance().getGrupoSeleccionado();
    }

    private final long lastSingletonMyAccountConfLockDownTimerRestart=new java.util.Date().getTime();
    @Override
    public void onUserInteraction() {
        super.onUserInteraction();

        try {
            if(SingletonMyAccountConfLockDownTimer.getInstance().isRunning() ) {

                if (
                        new java.util.Date().getTime() - lastSingletonMyAccountConfLockDownTimerRestart
                                > 2000) {
                    SingletonMyAccountConfLockDownTimer.getInstance().restart();
                }
                {
                    System.out.println("Tiempo para lock la app IGNORANDO EVENTO");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
