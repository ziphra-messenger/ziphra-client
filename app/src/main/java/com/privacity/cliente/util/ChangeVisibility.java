package com.privacity.cliente.util;

import android.view.View;

public class ChangeVisibility {
    public static void changeState(View view){

        if ( view.getVisibility() == View.VISIBLE ){
            hide(view);
        }else{
            show(view);
        }

    }

    public static void hide(View view){

        view.setVisibility(View.GONE);
  


    }

    public static void show(View view){

        view.setVisibility(View.VISIBLE);
 
    }    
}
