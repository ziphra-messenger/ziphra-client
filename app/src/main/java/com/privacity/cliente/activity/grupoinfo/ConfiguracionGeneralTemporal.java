package com.privacity.cliente.activity.grupoinfo;

import android.widget.CheckBox;
import android.widget.Spinner;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConfiguracionGeneralTemporal {
    private CheckBox confTemporalObligatorio;
    private Spinner confTemporalMaximoTiempoPermitido;
}
