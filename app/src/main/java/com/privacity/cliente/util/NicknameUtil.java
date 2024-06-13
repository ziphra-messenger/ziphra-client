package com.privacity.cliente.util;

import android.app.Activity;
import android.text.InputFilter;
import android.widget.TextView;

import com.privacity.cliente.R;
import com.privacity.cliente.singleton.Observers;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.common.config.ConstantValidation;

import org.apache.commons.lang3.RandomStringUtils;

public class NicknameUtil {

    public static void setNicknameMaxLenght(TextView t){
        t.setFilters(new InputFilter[]{new InputFilter.LengthFilter(ConstantValidation.USER_NICKNAME_MAX_LENGTH)});

    }
    public static String generateRandomNickname() {
        return RandomStringUtils.randomAlphabetic(4).toUpperCase() + "-" +
                RandomStringUtils.randomNumeric(4);
    }
    public static boolean compareCurrentNickname(TextView t) {
        return SingletonValues.getInstance().getUsuario().getNickname().equals(t.getText().toString());
    }

    public static boolean compareCurrentNickname(String idGrupo , TextView t) {
        String nicknameActual = Observers.grupo().getGrupoById(idGrupo).getAlias();
        return nicknameActual.equals(t.getText().toString());
    }
    public static boolean validarNickname(Activity activity, TextView t) {
        return validarNickname(activity,t, false);
    }
    public static boolean validarNickname(Activity activity, TextView t, boolean empty) {

        if (t.getText().toString().equals("") && empty== false){
            t.setError(activity.getResources().getString(R.string.registro_validation_nickname_empty));

            return false;
        } else if (t.getText().toString().length() > ConstantValidation.USER_NICKNAME_MAX_LENGTH){
            t.setError(activity.getResources().getString(R.string.registro_validation_nickname_too_long));
            return false;

        }
        return true;
    }
}
