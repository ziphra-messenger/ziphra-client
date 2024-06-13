package com.privacity.cliente.common.component;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SecureFieldAndEye {
    private View content;
    private EditText field;
    private ImageButton eyeShow;
    private ImageButton eyeHide;


    public void changeVisibility(){
        if ( content.getVisibility() == View.VISIBLE){
            changeVisibilityGone();
        }else{
            changeVisibilityVisisble();
        }
    }
    public void changeVisibilityVisisble(){
        content.setVisibility(View.VISIBLE);
    }

    public void changeVisibilityGone(){
        content.setVisibility(View.GONE);
    }

}
