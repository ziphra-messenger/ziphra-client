package com.privacity.cliente.common.validations;

import android.util.Log;
import android.widget.EditText;

import com.privacity.common.enumeration.ExceptionReturnCode;

public class FieldsValidations {
    private static final String TAG = "FieldsValidations";

    public static boolean notEmptySetError(EditText editText, String errorMessage){
        if (editText == null){
            Log.e(TAG, ExceptionReturnCode.CLIENT_VALIDATION__FIELD_IS_NULL.toShow(errorMessage));
            return false;
        }
        if (editText.getText().toString().equals("")) {
            editText.setError(errorMessage);
            return false;
        }else{
            editText.setError(null);
        }
        return true;
    }
}
