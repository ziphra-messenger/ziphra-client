package com.privacity.cliente.common.component.selecttime;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.messageresend.MessageUtil;
import com.privacity.cliente.common.component.picktime.PickTimeView;

import lombok.Getter;

public class SelectTime {

    @Getter
    private PickTimeView pickTimeView;

    private Button close;
    private Activity activity;
    private View viewFrame;
    private Button accept;

    CallbackSelectTime cb;
    public SelectTime(Activity activity, CallbackSelectTime cb) {
        this.activity = activity;
    this.cb=cb;
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

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cb.action(MessageUtil.CalcularTiempoFormater (getPickTimeView().getValue()), getPickTimeView().getValue());
                close();
            }
        });
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
        close = (Button) activity.findViewById(R.id.common__select__view__close);
        accept = (Button) activity.findViewById(R.id.common__select__view__accept);

        this.viewFrame = activity.findViewById(R.id.common__select__view__content__frame);


    }
}


