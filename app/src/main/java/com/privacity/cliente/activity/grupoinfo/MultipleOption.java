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
        if (grupo != null) {
            return grupo.getCheckedRadioButtonId() >= 0;
        }
        return false;
    }

    public ConfigurationStateEnum getSelectedValue(){
        if (permitir != null && permitir.isChecked()) return ConfigurationStateEnum.ALLOW;
        else if (bloquear != null && bloquear.isChecked()) return ConfigurationStateEnum.BLOCK;
        else if ( obligatorio !=null) {
            if (obligatorio.isChecked()) return ConfigurationStateEnum.MANDATORY;
        }
        return ConfigurationStateEnum.ALLOW;
    }

    public void setValue(ConfigurationStateEnum value) {
        if (permitir != null) permitir.setChecked(false);
        if (bloquear != null) bloquear.setChecked(false);
        if (obligatorio != null) obligatorio.setChecked(false);

        if (value.equals(ConfigurationStateEnum.ALLOW) && permitir != null ) permitir.setChecked(true);
        if (value.equals(ConfigurationStateEnum.BLOCK) && bloquear != null ) bloquear.setChecked(true);
        if (value.equals(ConfigurationStateEnum.MANDATORY) && obligatorio != null ) obligatorio.setChecked(true);
    }
}
