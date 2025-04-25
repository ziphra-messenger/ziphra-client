package com.privacity.cliente.singleton.observers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;

import androidx.annotation.NonNull;

import com.privacity.cliente.singleton.activity.SingletonCurrentActivity;
import com.privacity.cliente.singleton.interfaces.ObservadoresConnection;
import com.privacity.common.BroadcastConstant;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import lombok.Getter;

public class ObserverConnection {

    @Getter
    private boolean online;

    private List<ObservadoresConnection>  suscriptores=new CopyOnWriteArrayList<ObservadoresConnection>();

    static private ObserverConnection instance;
    private ObserverConnection(){
        online=isOnlinePrivate();
        startCallback();
    }
    public void clean() {
        suscriptores.clear();
    }
    public void dessuscribirse( ObservadoresConnection n) {
        suscriptores.remove(n);
    }
    public void suscribirse( ObservadoresConnection n) {
        if(online) n.onLine();
        else n.offLine();

        suscriptores.add(0,n);
    }


    public static ObserverConnection getInstance() {
        if (instance == null){
            instance= new ObserverConnection();
        }
        return instance;
    }

    private static boolean isOnlinePrivate() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private static Activity getActivity(){
        return SingletonCurrentActivity.getInstance().get();
    }

    private void startCallback(){
        ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                online=isOnlinePrivate();
                avisarOnAvailable(network);

                if(!isOnlinePrivate()){
                    Intent intent = new Intent(BroadcastConstant.BROADCAST__MESSAGING__CONNECTION_CLOSED);
                    getActivity().sendBroadcast(intent);
                }
            }

            @Override
            public void onLost(@NonNull Network network) {
                super.onLost(network);
                online=isOnlinePrivate();
                avisarOnLost(network);
                if(!isOnlinePrivate()){
                    Intent intent = new Intent(BroadcastConstant.BROADCAST__MESSAGING__CONNECTION_CLOSED);
                    getActivity().sendBroadcast(intent);
                }
            }};
        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .build();

        ConnectivityManager connectivityManager =
                (ConnectivityManager) getActivity().getSystemService(ConnectivityManager.class);
        connectivityManager.requestNetwork(networkRequest, networkCallback);
    }

    private void avisarOnAvailable(Network network) {
        for( ObservadoresConnection e : suscriptores) {
            e.onAvailable(network);
        }
    }

    private void avisarOnLost(Network network) {
        for( ObservadoresConnection e : suscriptores) {
            e.onLost(network);
        }
    }


}
