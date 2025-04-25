package com.privacity.cliente.common.component.picktime;

import android.widget.NumberPicker;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.messageresend.MessageUtil;
import com.privacity.cliente.model.dto.Protocolo;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;

import lombok.Getter;

@Getter
public class PickTimeView {

    protected NumberPicker hora;
    protected NumberPicker minutos;
    protected NumberPicker segundos;

    public static final NumberPicker.Formatter TWO_DIGIT_FORMATTER =
            new NumberPicker.Formatter() {

                @Override
                public String format(int value) {
                    // TODO Auto-generated method stub
                    return String.format("%02d", value);
                }
            };

    public PickTimeView(NumberPicker hora, NumberPicker minutos, NumberPicker segundos) {
        if (hora != null)
        this.hora = hora;
        this.minutos = minutos;
        this.segundos = segundos;


    }

    public void initViewShortValues() {
        if (hora != null)hora.setMinValue(0);
        if (hora != null)hora.setMaxValue(23);
        if (hora != null)hora.setFormatter(TWO_DIGIT_FORMATTER);


/*        ArrayList<String> shortvM = new ArrayList<>();

        for ( int i = 0 ; i < 60 ; i=i+2){
            shortvM.add(i+"");
        }
        minutos.setDisplayedValues(shortvM.toArray(new String[0]));*/


        minutos.setMinValue(0);
        minutos.setMaxValue(59);
        minutos.setFormatter(TWO_DIGIT_FORMATTER);

        ArrayList<String> shortv = new ArrayList<>();

        for ( int i = 0 ; i < 60 ; i=i+5){
                shortv.add(i+"");
        }
        segundos.setDisplayedValues(shortv.toArray(new String[0]));
        segundos.setMinValue(0);
        segundos.setMaxValue(shortv.size()-1);
        segundos.setFormatter(TWO_DIGIT_FORMATTER);

    }
    public void initViewFullValues() {
        if (hora != null) hora.setMinValue(0);
        if (hora != null)hora.setMaxValue(23);
        if (hora != null)hora.setFormatter(TWO_DIGIT_FORMATTER);


        minutos.setMinValue(0);
        minutos.setMaxValue(59);
        minutos.setFormatter(TWO_DIGIT_FORMATTER);

        segundos.setMinValue(0);
        segundos.setMaxValue(59);
        segundos.setFormatter(TWO_DIGIT_FORMATTER);
    }
    public void loadValue(long seconds){
        loadValue(Integer.parseInt(seconds+""));
    }
    public void loadValue(int seconds){
        if (hora != null)hora.setValue(MessageUtil.CalcularTiempoFormaterSoloHora(seconds));
        minutos.setValue(MessageUtil.CalcularTiempoFormaterSoloMinutos(seconds));
        segundos.setValue(MessageUtil.CalcularTiempoFormaterSoloSegundos(seconds/5));
    }

    public int getValue(){
        int horav=0;
        if (hora != null) horav=hora.getValue()*60*60;
        int value = horav+
                minutos.getValue()*60+
                (segundos.getValue() * 5);
        return value;
    }
}
