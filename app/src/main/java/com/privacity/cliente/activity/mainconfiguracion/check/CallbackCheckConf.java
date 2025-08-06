package com.privacity.cliente.activity.mainconfiguracion.check;

import com.privacity.cliente.frame.error.ErrorPojo;

public interface CallbackCheckConf {
    public abstract void response(ErrorPojo p);
    public abstract void onError(ErrorPojo p);

}
