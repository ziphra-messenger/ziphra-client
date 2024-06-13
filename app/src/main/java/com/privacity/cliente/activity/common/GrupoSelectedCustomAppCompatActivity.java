package com.privacity.cliente.activity.common;

import android.os.Bundle;

import com.privacity.cliente.model.Grupo;
import com.privacity.cliente.singleton.Observers;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.singleton.interfaces.ObservadoresPasswordGrupo;

public abstract class GrupoSelectedCustomAppCompatActivity
        extends CustomAppCompatActivity implements ObservadoresPasswordGrupo {

    protected abstract boolean isOnlyAdmin();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Observers.passwordGrupo().suscribirse(this);



    }



    @Override
    protected void onResume() {

        super.onResume();
        Observers.passwordGrupo().suscribirse(this);




    }

    @Override
    protected void onStop() {
        super.onStop();
        //ObservatorPasswordGrupo.getInstance().remove(this);


    }

    @Override
    protected void onStart() {
        super.onStart();
        Observers.passwordGrupo().suscribirse(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Observers.passwordGrupo().remove(this);
        //grupoRestartTimer();
    }
}
