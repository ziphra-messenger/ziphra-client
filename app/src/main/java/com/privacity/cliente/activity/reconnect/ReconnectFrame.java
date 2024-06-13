package com.privacity.cliente.activity.reconnect;

import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.privacity.cliente.activity.common.CustomAppCompatActivity;
import com.privacity.cliente.R;

import lombok.Getter;

@Getter
public class ReconnectFrame {

    private final CustomAppCompatActivity activity;

    private final LinearLayout mainView;
    private final LinearLayout waitingView;

    private final TextView log;

    private final TextView title;

    private final TextView waiting1;
    private final TextView waiting2;
    private final TextView seconds;
    private final TextView trying;

    private final ProgressBar progressBar;
    private final LinearLayout tryingView;
    private final TextView countTrying;
    private final TextView countTryingTotal;
    private final TextView countDisconnect;

    public ReconnectFrame(CustomAppCompatActivity activity) {

        progressBar = (ProgressBar) activity.findViewById(R.id.reconnect_progressbar);

        mainView = (LinearLayout) activity.findViewById(R.id.reconnect_contenedor);
        waitingView = (LinearLayout) activity.findViewById(R.id.reconnect_contenedor_waiting);

        tryingView = (LinearLayout) activity.findViewById(R.id.reconnect_contenedor_trying);

        log = (TextView) activity.findViewById(R.id.reconnect_log);
        title = (TextView) activity.findViewById(R.id.reconnect_title);
        waiting1 = (TextView) activity.findViewById(R.id.reconnect_waiting_1);
        waiting2 = (TextView) activity.findViewById(R.id.reconnect_waiting_2);
        seconds = (TextView) activity.findViewById(R.id.reconnect_seconds);
        trying = (TextView) activity.findViewById(R.id.reconnect_trying);

        countTrying = (TextView) activity.findViewById(R.id.reconnect_count_trying);
        countDisconnect = (TextView) activity.findViewById(R.id.reconnect_count_disconnect);
        countTryingTotal = (TextView) activity.findViewById(R.id.reconnect_count_retry_total);



        this.activity = activity;

    }

//    public void loadValues(){
//        mainView.setVisibility(Singletons.reconnect().getMainViewVisibility());
//        waitingView.setVisibility(Singletons.reconnect().getWaitingViewVisibility());
//        seconds.setText(Singletons.reconnect().getSecondsValue()+"");
//        tryingView.setVisibility(Singletons.reconnect().getTryingViewVisibility());
//   }


}

