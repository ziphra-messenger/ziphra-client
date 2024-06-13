package com.privacity.cliente.activity.main;


import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.privacity.cliente.R;
import com.privacity.cliente.singleton.SingletonValues;

public class MainActivityNoPass {

    private final MainActivity activity;

    private final LinearLayout cEntry;
    private final LinearLayout cError;

    private final Button cBack;
    private final Button cRetry;

    private final ProgressBar progressBar;

    public MainActivityNoPass(MainActivity activity) {
        this.activity=activity;

        progressBar = (ProgressBar) activity.findViewById(R.id.main_nopass_entry_progressbar);

        cEntry = (LinearLayout) activity.findViewById(R.id.main_nopass_entry_contenedor);
        cError = (LinearLayout) activity.findViewById(R.id.main_nopass_error_contenedor);

        cBack = (Button) activity.findViewById(R.id.main_nopass_error_back);
        cRetry = (Button) activity.findViewById(R.id.main_nopass_error_retry);

        setListener();
    }

    public void setVisibilityError() {
        cEntry.setVisibility(View.GONE);
        cError.setVisibility(View.VISIBLE);
    }

    public void setVisibilityEntry() {
        cEntry.setVisibility(View.VISIBLE);
        cError.setVisibility(View.GONE);
    }

     public void setListener() {


         cBack.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

                 SingletonValues.getInstance().setLogout(true);
                 activity.chooseView();

             }
         });

         cRetry.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 SingletonValues.getInstance().setLogout(false);
                 activity.chooseView();
             }
         });

     }

}


