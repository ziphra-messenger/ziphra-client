package com.privacity.cliente.activity.common;

import android.app.Activity;
import android.view.View;
import android.widget.Button;

import com.google.android.material.button.MaterialButton;
import com.privacity.cliente.R;
import com.privacity.cliente.model.Grupo;
import com.privacity.cliente.singleton.SingletonValues;import com.privacity.cliente.singleton.Singletons;
import com.privacity.cliente.singleton.interfaces.ViewCallbackActionInterface;
import com.privacity.cliente.singleton.localconfiguration.SingletonButtonSetup;

public class GetButtonReady {

    public static Button get(Activity activity, int idButton) {
        return get(activity, idButton, null,null);
    }
    public static Button get(Activity activity, int idButton, ViewCallbackActionInterface onClickInternetRequieredCallback) {
        return get(activity, idButton, null,onClickInternetRequieredCallback);
    }
    public static Button get(Activity activity, int idButton, int text) {
        return get(activity, idButton, activity.getResources().getText(text).toString(),null);
    }
    public static Button get(Activity activity, int idButton, String text) {
        return get(activity, idButton, text,null);
    }

    public static Button get(Activity activity, int idButton, String text, ViewCallbackActionInterface onClickInternetRequieredCallback) {

        Button b = activity.findViewById(idButton);

        if (text != null){
            b.setText(text);
        }else{
            if (b.getText() != null && !b.getText().toString().trim().equals("")){
                text=b.getText().toString().trim();
            }
        }

        if (b.getMarqueeRepeatLimit() > 0) {
            ButtonLongClickMaquee.setListener(b);
        }

        if ( onClickInternetRequieredCallback != null){
            ButtonLongClickMaquee.setListener(b,onClickInternetRequieredCallback
                    );
        }

        if (SingletonButtonSetup.getInstance().isHideIcon(activity)){

                ((MaterialButton)  activity.findViewById(idButton)).setIcon(null);


        }
        if (((MaterialButton)  activity.findViewById(idButton)).getIcon() != null) {
            if (SingletonButtonSetup.getInstance().isHideText(activity)) {
                b.setText("");
            }
        }



        if (b.getTag() != null) {
            b.setVisibility(View.GONE);
            Grupo grupo = SingletonValues.getInstance().getGrupoSeleccionado();

            if (grupo.iAmAdmin() && b.getTag().equals(activity.getString(R.string.tag_admin_gone))) {
                b.setVisibility(View.VISIBLE);
            }

            if ((grupo.iAmAdmin() || grupo.iAmModerator()) && b.getTag().equals(activity.getString(R.string.not_translate_general__roles__moderator))) {
                b.setVisibility(View.VISIBLE);
            }

            if ((grupo.iAmAdmin() || grupo.iAmModerator() || grupo.iAmMember()) && b.getTag().equals(activity.getString(R.string.not_translate_general__roles__member))) {
                b.setVisibility(View.VISIBLE);
            }
            if ((grupo.iAmAdmin() || grupo.iAmModerator() || grupo.iAmMember() || grupo.iAmReadOnly()) && b.getTag().equals(activity.getString(R.string.not_translate_general__roles__readonly))) {
                b.setVisibility(View.VISIBLE);
            }

        } else {
            b.setVisibility(View.VISIBLE);
        }

return b;
    }
    }
