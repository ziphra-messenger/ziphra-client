package com.privacity.cliente.activity.grupoinfo;

import android.widget.CheckBox;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConfiguracionGeneralOtras {
    private CheckBox confGenCambiarNicknameToNumber;
    private CheckBox confGenOcultarDetalles;
    private CheckBox confGenOcultarEstado;
    private CheckBox confGenOcultarListaIntegrantes;
}
