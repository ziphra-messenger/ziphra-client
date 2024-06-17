package com.privacity.cliente.activity.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.privacity.cliente.activity.grupo.GrupoActivity;
import com.privacity.cliente.activity.grupo.GrupoUtil;
import com.privacity.cliente.activity.lock.LockActivity;
import com.privacity.cliente.activity.reconnect.ReconnectFrame;
import com.privacity.cliente.activity.userhelper.UserHelperUtil;
import com.privacity.cliente.enumeration.WsStatusEnum;
import com.privacity.cliente.model.Grupo;
import com.privacity.cliente.singleton.Observers;
import com.privacity.cliente.singleton.SingletonSessionFinish;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.singleton.Singletons;
import com.privacity.cliente.singleton.countdown.SingletonMyAccountConfLockDownTimer;
import com.privacity.cliente.singleton.interfaces.ObservadoresPassword;
import com.privacity.common.enumeration.GrupoRolesEnum;

public abstract class CustomAppCompatActivity extends AppCompatActivity implements ObservadoresPassword {

    protected abstract boolean isOnlyAdmin();
    public ReconnectFrame reconnectFrame;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

//        if(!SingletonValues.getInstance().getSystemGralConf().getExtras().isScreenshotEnabled()){
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
//        }

        super.onCreate(savedInstanceState);



        if (isOnlyAdmin()){
            GrupoRolesEnum myRole = Observers.grupo().whichIsMyRole(SingletonValues.getInstance().getGrupoSeleccionado().getIdGrupo());

            if (!myRole.equals(GrupoRolesEnum.ADMIN)){
                String roleString = GrupoUtil.transformGrupoRoleEnumToCompleteString(myRole);

                Toast toast=Toast.makeText(getApplicationContext(),"Esta pantalla es solo para Administradores. Su rol es " + roleString,Toast. LENGTH_LONG);
                toast.show();
                finish();
                return;
            };
        }

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent intent) {
                String action = intent.getAction();
                if (action.equals("finish_all_activities")) {
                    finish();
                } else if (action.equals("finish_application")) {
                        finish();
                }else if (action.equals("connection_closed")) {
                    reconnection();
                }else if (action.equals("connection_trying")) {
                    trying();
                }else if (action.equals("connection_open")) {
                    connected();
                }else if (action.equals("connection_time")) {
                    reconnectTime();
                }else if (action.equals("connection_off_line")) {
                    reconnectOffline();
                }


            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter("finish_all_activities"));
        registerReceiver(broadcastReceiver, new IntentFilter("finish_application"));

        registerReceiver(broadcastReceiver, new IntentFilter("connection_closed"));
        registerReceiver(broadcastReceiver, new IntentFilter("connection_open"));
        registerReceiver(broadcastReceiver, new IntentFilter("connection_trying"));
        registerReceiver(broadcastReceiver, new IntentFilter("connection_time"));
        registerReceiver(broadcastReceiver, new IntentFilter("connection_off_line"));

        Observers.password().suscribirse(this);

    }

    protected void reconnectOffline(){

    };


    protected void trying(){
        if ( reconnectFrame.getSeconds() == null) return;
        reconnectFrame.getMainView().setVisibility(View.VISIBLE);
        reconnectFrame.getWaitingView().setVisibility(View.GONE);
        reconnectFrame.getTryingView().setVisibility(View.VISIBLE);

        reconnectFrame.getCountDisconnect().setText(Singletons.reconnect().getDisconnetedCount()+"");
        reconnectFrame.getCountTrying().setText(Singletons.reconnect().getReintentosCount()+"");
        reconnectFrame.getCountTryingTotal().setText(Singletons.reconnect().getReintentosTotalCount()+"");

       //reconnectFrame.getLog().setText(Singletons.reconnect().getLog());
    };

    protected void connected(){

        if ( reconnectFrame.getSeconds() == null) return;
        reconnectFrame.getMainView().setVisibility(View.GONE);
        //reconnectFrame.getLog().setText(Singletons.reconnect().getLog());


    };
    protected void reconnection(){

        if ( this instanceof GrupoActivity){
            Observers.grupo().resetOnLineMember();
            Observers.grupo().resetWriting();
        }


        Singletons.reconnect().waiting(this);

        if ( reconnectFrame.getSeconds() != null) {
            reconnectFrame.getMainView().setVisibility(View.VISIBLE);
            reconnectFrame.getWaitingView().setVisibility(View.VISIBLE);
            reconnectFrame.getTryingView().setVisibility(View.GONE);

            reconnectFrame.getCountDisconnect().setText(Singletons.reconnect().getDisconnetedCount() + "");
            reconnectFrame.getCountTrying().setText(Singletons.reconnect().getReintentosCount() + "");
            reconnectFrame.getCountTryingTotal().setText(Singletons.reconnect().getReintentosTotalCount() + "");
           // reconnectFrame.getLog().setText(Singletons.reconnect().getLog());
        }


    };

    protected void reconnectTime(){
        if ( reconnectFrame.getSeconds() == null) return;
        reconnectFrame.getSeconds().setText(Singletons.reconnect().getSecondsValue()+"");

        //reconnectFrame.getLog().setText(Singletons.reconnect().getLog());


    };


    @Override
    protected void onResume() {

        super.onResume();

        reconnectFrame = new ReconnectFrame(this);

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
        String url = ((View)v.getParent().getParent()).getTag().toString();
        UserHelperUtil.open(this, url);
    }

    public boolean getGrupoSeleccionadoIsNull(){
        if ( SingletonValues.getInstance().getGrupoSeleccionado() != null
        && SingletonValues.getInstance().getGrupoSeleccionado().getIdGrupo() != null
        ) {
           return false;
        }
        return true;
    }

    public Grupo getGrupoSeleccionado(){
        return SingletonValues.getInstance().getGrupoSeleccionado();
    }

    private long lastSingletonMyAccountConfLockDownTimerRestart=new java.util.Date().getTime();
    @Override
    public void onUserInteraction() {
        super.onUserInteraction();

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

    }
}
