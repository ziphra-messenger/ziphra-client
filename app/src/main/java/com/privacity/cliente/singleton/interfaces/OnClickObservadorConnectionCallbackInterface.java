package com.privacity.cliente.singleton.interfaces;

import android.view.View;

public interface OnClickObservadorConnectionCallbackInterface extends View.OnClickListener, ObservadoresConnection {
    void action(View v);

}
