package com.privacity.cliente.activity.myaccount;

import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.privacity.cliente.singleton.SingletonValues;import com.privacity.cliente.singleton.Singletons;
import com.privacity.cliente.singleton.activity.SingletonCurrentActivity;
import com.privacity.cliente.singleton.localconfiguration.SingletonButtonSetup;
import com.privacity.cliente.util.DialogsToShow;

import lombok.Data;

@Data

public class SwitchTxt {
    private final Switch switchView;
    private final TextView textView;
    private Button icon;

    public SwitchTxt(Switch switchView, TextView textView) {
        this.switchView = switchView;
        this.textView = textView;

        initTextView();
    }

    public SwitchTxt(Switch switchView, TextView textView, Button icon) {
        this.switchView = switchView;
        this.textView = textView;
        this.icon=icon;
        initTextView();
    }

    public void setChecked(boolean checked) {
        switchView.setChecked(checked);
    }

    public boolean isChecked() {
        return switchView.isChecked();
    }

    private void initTextView() {
        textView.setClickable(true);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchView.setChecked(!isChecked());
            }
        });

        textView.setLongClickable(true);

        if (icon != null) {
            textView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    setLongListener();
                    return true;
                }
            });
        }
        if (SingletonButtonSetup.getInstance().isHideText(SingletonCurrentActivity.getInstance().get())){

            textView.setVisibility(View.GONE);
        }else{
            textView.setVisibility(View.VISIBLE);
        }
        if (icon != null){
            icon.setClickable(true);
            icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switchView.setChecked(!isChecked());
                }
            });

            icon.setLongClickable(true);
            icon.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    setLongListener();
                    return true;
                }
            });
            if (SingletonButtonSetup.getInstance().isHideIcon(SingletonCurrentActivity.getInstance().get())){

               icon.setVisibility(View.GONE);


            }else {
                icon.setVisibility(View.VISIBLE);
            }
        }
        DialogsToShow.noAdminDialog(SingletonCurrentActivity.getInstance().get(), SingletonValues.getInstance().getGrupoSeleccionado(), (View) textView.getParent());

    }

    public void setLongListener() {

            if ( icon != null && (icon.getVisibility() == View.GONE) ||  (textView.getVisibility() == View.GONE)){
                icon.setVisibility(View.VISIBLE);
                textView.setVisibility(View.VISIBLE);
            } else {
                initTextView();
            }



    }
}
