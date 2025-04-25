package com.privacity.cliente.singleton.interfaces;

import android.net.Network;

import androidx.annotation.NonNull;

public interface ObservadoresConnection{
    public void onAvailable(@NonNull Network network);
     public void onLost(@NonNull Network network);
    public void onLine();
     public void offLine();
}
