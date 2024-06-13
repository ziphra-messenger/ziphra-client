package com.privacity.cliente.activity.grupoinfo;

import android.widget.Spinner;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConfiguracionGeneralAudioChat {

    private Spinner confAudioChatMaximoTiempo;
    private MultipleOption audioChat=new MultipleOption();
}
