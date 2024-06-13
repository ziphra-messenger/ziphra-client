package com.privacity.cliente.activity.grupoinfo;

import android.annotation.SuppressLint;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.privacity.common.enumeration.ConfigurationStateEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MultipleOption {
    private TextView titulo;
    private RadioGroup grupo;
    private RadioButton permitir;
    private RadioButton bloquear;
    private RadioButton obligatorio;

    @SuppressLint("ResourceType")
    public boolean isSelected(){
        return grupo.getCheckedRadioButtonId() >= 0;
    }

    public ConfigurationStateEnum getSelectedValue(){
        if (permitir.isChecked()) return ConfigurationStateEnum.ALLOW;
        else if (bloquear.isChecked()) return ConfigurationStateEnum.BLOCK;
        else if ( obligatorio !=null) {
            if (obligatorio.isChecked()) return ConfigurationStateEnum.MANDATORY;
        }
        return null;
    }

    public void setValue(ConfigurationStateEnum resend) {
        permitir.setChecked(false);
        bloquear.setChecked(false);
        if (obligatorio != null) obligatorio.setChecked(false);

        if (resend.equals(ConfigurationStateEnum.ALLOW)) permitir.setChecked(true);
        if (resend.equals(ConfigurationStateEnum.BLOCK)) bloquear.setChecked(true);
        if (resend.equals(ConfigurationStateEnum.MANDATORY)) obligatorio.setChecked(true);
    }
}
