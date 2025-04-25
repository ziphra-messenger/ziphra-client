package com.privacity.cliente.activity.common;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Network;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.google.android.material.button.MaterialButton;
import com.privacity.cliente.singleton.activity.SingletonCurrentActivity;
import com.privacity.cliente.singleton.interfaces.ObservadoresConnection;
import com.privacity.cliente.singleton.interfaces.OnClickObservadorConnectionCallbackInterface;
import com.privacity.cliente.singleton.interfaces.ViewCallbackActionInterface;
import com.privacity.cliente.singleton.observers.ObserverConnection;
import com.privacity.cliente.singleton.toast.SingletonToastManager;

public class ButtonLongClickMaquee {



    public static void setListener(View view){

        view.setLongClickable(true);

        view.setOnLongClickListener(new View.OnLongClickListener() {
            String txt= ((Button) view).getText().toString();
            Drawable icon = ((MaterialButton) view).getIcon();
            @Override
            public boolean onLongClick(View view) {
                view.setSelected(false);
                view.setSelected(true);
                ((Button) view).setText(txt);
                ((MaterialButton) view).setIcon(icon);
                return true;            }
        });

    }

    public static void setListener(View view, ViewCallbackActionInterface cb){

        OnClickObservadorConnectionCallbackInterface s = new OnClickObservadorConnectionCallbackInterface() {
            View v=view;

            @Override
            public void onAvailable(@NonNull Network network) {
                v.setEnabled(true);
                ((MaterialButton)v).setStrokeWidth(0);

            }

            @Override
            public void onLost(@NonNull Network network) {
               // v.setEnabled(false);
                ((MaterialButton)v).setStrokeWidth(7);


                ((MaterialButton)v).setStrokeColor(ColorStateList.valueOf(Color.parseColor("#8FFF0000")));

            }

            @Override
            public void onLine() {
                v.setEnabled(true);
                ((MaterialButton)v).setStrokeWidth(0);
            }

            @Override
            public void offLine() {
                ((MaterialButton)v).setStrokeWidth(7);
                ((MaterialButton)v).setStrokeColor(ColorStateList.valueOf(Color.parseColor("#8FFF0000")));
            }

            @Override
            public void onClick(View view) {
              //  ((MaterialButton)v).setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                if (ObserverConnection.getInstance().isOnline()){
                    action(v);
                }else{
                    actionOffLine(v);
                }

            }

            @Override
            public void action(View v) {
                cb.action(v);

            }

            public void actionOffLine(View v) {
                SingletonToastManager.getInstance().showToadShort(SingletonCurrentActivity.getInstance().get(), "El disposivo no tiene acceso a Internet");
            }
        };

        view.setOnClickListener(s);
        ObserverConnection.getInstance().suscribirse(s);


    }
}
