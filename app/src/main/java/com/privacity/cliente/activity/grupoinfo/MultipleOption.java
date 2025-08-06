package com.privacity.cliente.activity.grupoinfo;

import android.annotation.SuppressLint;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.privacity.common.enumeration.ConfigurationStateEnum;
import com.privacity.common.enumeration.RulesConfEnum;

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

    public RulesConfEnum getSelectedValue(){
        if (permitir != null && permitir.isChecked()) return RulesConfEnum.NULL;
        else if (bloquear != null && bloquear.isChecked()) return RulesConfEnum.BLOCK;
        else if ( obligatorio !=null) {
            if (obligatorio.isChecked()) return RulesConfEnum.MANDATORY;
        }
        return RulesConfEnum.NULL;
    }

    public void setValue(RulesConfEnum value) {
        if (permitir != null) permitir.setChecked(false);
        if (bloquear != null) bloquear.setChecked(false);
        if (obligatorio != null) obligatorio.setChecked(false);

        if (value.equals(RulesConfEnum.NULL) && permitir != null ) permitir.setChecked(true);
        if (value.equals(RulesConfEnum.BLOCK) && bloquear != null ) bloquear.setChecked(true);
        if (value.equals(RulesConfEnum.MANDATORY) && obligatorio != null ) obligatorio.setChecked(true);
    }
}
