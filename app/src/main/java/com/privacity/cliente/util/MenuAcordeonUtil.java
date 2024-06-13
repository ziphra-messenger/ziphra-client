package com.privacity.cliente.util;

import android.graphics.PorterDuff;
import android.view.View;
import android.widget.Button;

import com.privacity.cliente.R;

public class MenuAcordeonUtil {
    public static void setActionMenu(MenuAcordeonObject menu){
        setActionMenu(menu.getTitle(),menu.getContent());
    }
    public static void setActionMenu(Button button, final View view){
        iniciarMenu(button,view);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PorterDuff.Mode t = button.getCompoundDrawableTintMode();
                if (view.getVisibility() == View.VISIBLE){
                    view.setVisibility(View.GONE);
                    button.setCompoundDrawablesWithIntrinsicBounds( 0, 0, R.drawable.ic_arrow_down,0);
                    button.setCompoundDrawableTintMode(t);
                }else{
                    view.setVisibility(View.VISIBLE);
                    button.setCompoundDrawablesWithIntrinsicBounds( 0, 0,R.drawable.ic_arrow_up, 0);
                    button.setCompoundDrawableTintMode(t);
                }
            }
        });
    }

    private static void iniciarMenu(Button button, final View view){
        PorterDuff.Mode t = button.getCompoundDrawableTintMode();
        if (view.getVisibility() != View.VISIBLE){
            button.setCompoundDrawablesWithIntrinsicBounds( 0, 0, R.drawable.ic_arrow_down,0);
            button.setCompoundDrawableTintMode(t);
        }else{
            button.setCompoundDrawablesWithIntrinsicBounds( 0, 0,R.drawable.ic_arrow_up, 0);
            button.setCompoundDrawableTintMode(t);
        }
    }


}
