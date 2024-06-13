package com.privacity.cliente.includes;

import android.app.Activity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

public class ProgressBarUtil {

    public static void changeState(Activity context, ProgressBar progressBar){
        if (progressBar == null) return;
        if ( progressBar.getVisibility() == View.VISIBLE ){
            hide(context,progressBar);
        }else{
            show(context,progressBar);
        }

    }

    public static void hide(Activity context, ProgressBar progressBar){
        if (progressBar == null) return;
            progressBar.setVisibility(View.GONE);
            context.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);



    }

    public static void show(Activity context, ProgressBar progressBar){
        if (progressBar == null) return;
        progressBar.setVisibility(View.VISIBLE);
        context.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

    }

}


