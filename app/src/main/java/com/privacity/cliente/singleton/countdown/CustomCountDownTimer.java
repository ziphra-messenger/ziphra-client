package com.privacity.cliente.singleton.countdown;

import android.os.CountDownTimer;

import lombok.Getter;

public abstract class CustomCountDownTimer extends CountDownTimer {
    @Getter
    protected int lastTimeRestart;

    public CustomCountDownTimer(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
    }
}
