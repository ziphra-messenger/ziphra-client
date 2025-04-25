package com.privacity.cliente.activity.message.envioespecial;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.message.MessageActivity;
import com.privacity.cliente.activity.message.validations.MessageValidations;
import com.privacity.cliente.activity.messageresend.MessageUtil;
import com.privacity.cliente.common.component.picktime.PickTimeView;
import com.privacity.cliente.model.Grupo;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.singleton.Singletons;

import lombok.Data;
import lombok.Getter;
import lombok.SneakyThrows;

public class MessageEnvioEspecial {
    @Getter
    private PickTimeView pickTimeView;

    private Button close;
    private MessageActivity activity;
    private View viewFrame;


        @Getter
        private Button anonimo;
        @Getter
        private Button black;
        @Getter
        private Button temporal;
        @Getter
        private Button extraEncrypt;
    @Getter
    private Button temporalDefault;

    public MessageEnvioEspecial(MessageActivity activity) {
        this.activity = activity;

        initView();

        setListeners();
        close();
    }

    private void setListeners() {
       close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                close();
            }
        });

        
        getAnonimo().setOnClickListener(new View.OnClickListener() {
            @SneakyThrows
            @Override
            public void onClick(View view) {
                activity.sendMessageMethod(false, false, 0,true, false, MessageValidations.isBlockResend(getGrupoSeleccionado().getIdGrupo()));
                close();
            }
        });
        getBlack().setOnClickListener(new View.OnClickListener() {
            @SneakyThrows
            @Override
            public void onClick(View view) {
                activity.sendMessageMethod(true, false,0,false, false, MessageValidations.isBlockResend(getGrupoSeleccionado().getIdGrupo()));
                close();
            }
        });

        getTemporal().setOnClickListener(new View.OnClickListener() {
            @SneakyThrows
            @Override
            public void onClick(View view) {
                Singletons.timeMessage().save(activity,getPickTimeView().getValue());
                activity.sendMessageMethod(false, true,
                        getPickTimeView().getValue()
                        , false, false, MessageValidations.isBlockResend(getGrupoSeleccionado().getIdGrupo()));
                close();
            }
        });
        getTemporalDefault().setOnClickListener(new View.OnClickListener() {
            @SneakyThrows
            @Override
            public void onClick(View view) {
                activity.sendMessageMethod(false, true,Integer.parseInt(getTemporalDefault().getTag().toString()), false, false, MessageValidations.isBlockResend(getGrupoSeleccionado().getIdGrupo()));
                close();
            }
        });
    }

    private Grupo getGrupoSeleccionado() {
        return  SingletonValues.getInstance().getGrupoSeleccionado();
    }

    public void open(){
        viewFrame.setVisibility(View.VISIBLE);
    }
    public void close(){
        viewFrame.setVisibility(View.GONE);
    }
    private void initView() {
        pickTimeView = new PickTimeView(
                null,
                (NumberPicker) activity.findViewById(R.id.selecttime__picker_minutos),
                (NumberPicker) activity.findViewById(R.id.selecttime__picker_segundos)
        );
        pickTimeView.initViewShortValues();
        close = (Button) activity.findViewById(R.id.message__envio_especial__close);
        this.viewFrame = activity.findViewById(R.id.message__envio_especial__content__all);

        anonimo = (Button) activity.findViewById(R.id.message__envio_especial__anonimo);
        extraEncrypt = (Button) activity.findViewById(R.id.message__envio_especial__extraencryp);
        black = (Button) activity.findViewById(R.id.message__envio_especial__black);
        temporal = (Button) activity.findViewById(R.id.message__envio_especial__temporal);

        temporalDefault = (Button) activity.findViewById(R.id.message__envio_especial__temporal__default);

        pickTimeView.loadValue(Singletons.timeMessage().get(activity));

        temporalDefault.setTag(SingletonValues.getInstance().getMyAccountConfDTO().getTimeMessageDefaultTime());
        temporalDefault.setText(MessageUtil.CalcularTiempoFormater( SingletonValues.getInstance().getMyAccountConfDTO().getTimeMessageDefaultTime()));


    }
}
