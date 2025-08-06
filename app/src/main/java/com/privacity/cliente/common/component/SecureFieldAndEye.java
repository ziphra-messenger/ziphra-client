package com.privacity.cliente.common.component;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SecureFieldAndEye {

    private static final String TAG = "SecureFieldAndEye";

    private View content;
    private EditText field;
    //private EditText fieldValidate;7
    private ImageButton eyeShow;
    private ImageButton eyeHide;

    public void changeVisibility(){
        if (content == null ) {
            Log.e(TAG, "changeVisibility: content is null.");
            return;
        }
            if (content.getVisibility() == View.VISIBLE) {
                changeVisibilityGone();
            } else {
                changeVisibilityVisisble();

        }
    }
    public void changeVisibilityVisisble(){
        changeVisibilityContent(View.VISIBLE);
    }

    public void changeVisibilityGone(){
        changeVisibilityContent(View.GONE);
    }

    private void changeVisibilityContent(int newVisibility){
        Log.e(TAG, "changeVisibilityContent: content is null.");
        if (content != null){
            content.setVisibility(View.GONE);
        }
    }
}
