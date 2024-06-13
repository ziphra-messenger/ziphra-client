package com.privacity.cliente.activity.myaccount;

import android.widget.CheckBox;
import android.widget.Spinner;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConfiguracionGeneralTemporal {
    private CheckBox confTemporalSiempre;
    private Spinner confTemporalTiempoDefault;
}
