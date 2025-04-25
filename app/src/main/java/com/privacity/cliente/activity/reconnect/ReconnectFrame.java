package com.privacity.cliente.activity.reconnect;

import android.graphics.ImageDecoder;
import android.graphics.drawable.AnimatedImageDrawable;
import android.net.Network;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.common.GetButtonReady;
import com.privacity.cliente.common.constants.ReconnectConstant;
import com.privacity.cliente.singleton.impl.SingletonServer;
import com.privacity.cliente.singleton.interfaces.ObservadoresConnection;
import com.privacity.cliente.singleton.localconfiguration.SingletonReconnectState;
import com.privacity.cliente.singleton.observers.ObserverConnection;
import com.privacity.cliente.singleton.reconnect.SingletonReconnect;
import com.privacity.cliente.util.CopyPasteUtil;
import com.privacity.common.SingletonReconnectionLog;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

import lombok.AccessLevel;
import lombok.Getter;

@Getter
public class ReconnectFrame implements ObservadoresConnection {

    private AppCompatActivity activity;

    private LinearLayout contentInfo;
    private LinearLayout waitingView;
    @Getter(AccessLevel.NONE)
    private TextView log;

    private TextView title;

    private TextView waiting1;
    private TextView waiting2;
    private TextView seconds;
    private TextView trying;

    private ProgressBar progressBar;
    private LinearLayout tryingView;
    private TextView countTrying;
    private TextView countTryingTotal;
    private TextView countDisconnect;
    private View showMore;
    private ImageView showMoreIma;
    private ImageView showLess;
    private View contentAll;
    private LinearLayout contentToShowAndHide;
    private View copy;

    private Button showLog;
    private View contentLog;
    private View offline;

    public ReconnectFrame(AppCompatActivity activity) {
        this.activity = activity;
        initView();
        setListener();
        loadValues();
    }

    public void addLogLines(String l) {

        log.setText(StringUtils.right((l), ReconnectConstant.LOG__LENGTH_MAX));
        ScrollView scrollView = (ScrollView) activity.findViewById(log.getScrollY());
        if (scrollView != null) {
            scrollView.scrollTo(0, log.getLineCount() - 1);
            scrollView.fullScroll(View.FOCUS_DOWN);
        }

    }

    public void loadValues() {


        if (SingletonReconnectState.getInstance().get(activity)) {
            open();

        } else {
            close();
        }
        if (SingletonReconnectState.getInstance().getLog(activity)) {
            contentLog.setVisibility(View.VISIBLE);

        } else {
            contentLog.setVisibility(View.GONE);
        }


        addLogLines(SingletonReconnectionLog.getInstance().getLog());
    }

    private void initView() {
        title = activity.findViewById(R.id.common__messaging__reconnect__tittle);
        waiting1 = activity.findViewById(R.id.common__messaging__reconnect__waiting_1);
        waiting2 = activity.findViewById(R.id.common__messaging__reconnect__waiting_2);
        seconds = activity.findViewById(R.id.common__messaging__reconnect__seconds);
        trying = activity.findViewById(R.id.common__messaging__reconnect__trying);

                showLog = GetButtonReady.get(activity, R.id.common__messaging__reconnect__log__button, activity.getString(R.string.general__log));
        countTrying = activity.findViewById(R.id.reconnect_count_trying);
        countDisconnect = activity.findViewById(R.id.common__messaging__reconnect__counter_disconnect);
        countTryingTotal = activity.findViewById(R.id.common__messaging__reconnect__counter_retry_total);

        showLess = activity.findViewById(R.id.common__messaging__reconnect__show__less);
        //    showMore = (View) activity.findViewById(R.id.common__messaging__reconnect__show__more);
        progressBar = activity.findViewById(R.id.common__messaging__reconnect__progressbar);

        copy = GetButtonReady.get(activity, R.id.common__messaging__reconnect__copy, activity.getString(R.string.copy_paste_util__copy));

        contentLog = activity.findViewById(R.id.common__messaging__reconnect__content__log);

        contentAll = activity.findViewById(R.id.common__messaging__reconnect__content__all);
        contentToShowAndHide = activity.findViewById(R.id.common__messaging__reconnect__content__to_show_and_hide);
        contentInfo = activity.findViewById(R.id.common__messaging__reconnect__content__info);
        offline = activity.findViewById(R.id.common__messaging__reconnect__offline);

        waitingView = activity.findViewById(R.id.common__messaging__reconnect__content__waiting);

        tryingView = activity.findViewById(R.id.common__messaging__reconnect__content__trying);

        log = activity.findViewById(R.id.common__messaging__reconnect__log);

        log.setMovementMethod(ScrollingMovementMethod.getInstance());
        //txt.setScrollBarStyle(0x03000000);
        log.setHorizontallyScrolling(true);
        log.setNestedScrollingEnabled(true);
        log.setVerticalScrollBarEnabled(true);
        log.setHorizontalScrollBarEnabled(true);
        log.setTextColor(0xFF000000);

        try {


            ImageDecoder.Source source = ImageDecoder.createSource(activity.getResources(), R.drawable.ezgif_4_877f5310ef);


            AnimatedImageDrawable drawable = (AnimatedImageDrawable) ImageDecoder.decodeDrawable(source);


            showMoreIma = activity.findViewById(R.id.common__messaging__reconnect__show__more_image);
            AnimatedImageDrawable finalDrawable = drawable;
            showMoreIma.post(() -> {
                showMoreIma.setImageDrawable(finalDrawable);

                finalDrawable.start();

            });


        } catch (IOException e) {
            e.printStackTrace();
        }





    }


    public void setListener() {
        ObserverConnection.getInstance().suscribirse(this);
        showLess.setOnClickListener(view -> close());
        showMoreIma.setOnClickListener(view -> open());
        copy.setOnClickListener(view -> CopyPasteUtil.setClipboard(activity, log.getText().toString()));



        showLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    if (contentLog.getVisibility() == View.GONE) {
                        SingletonReconnectState.getInstance().saveLog(activity,true);
                        contentLog.setVisibility(View.VISIBLE);
                    } else {
                        SingletonReconnectState.getInstance().saveLog(activity,false);
                        contentLog.setVisibility(View.GONE);
                    }

            }
        });
    }
    private void close() {
        contentToShowAndHide.setVisibility(View.GONE);
        showMoreIma.setVisibility(View.VISIBLE);
        showLess.setVisibility(View.GONE);


        SingletonReconnectState.getInstance().save(activity, false);
    }

    private void open() {
        contentToShowAndHide.setVisibility(View.VISIBLE);
        showLess.setVisibility(View.VISIBLE);
        showMoreIma.setVisibility(View.GONE);


        SingletonReconnectState.getInstance().save(activity, true);
    }

    @Override
    public void onAvailable(@NonNull Network network) {
        try {
            offline.setVisibility(View.GONE);
        } catch (Exception e) {
            ObserverConnection.getInstance().dessuscribirse(this);
        }
    }

    @Override
    public void onLost(@NonNull Network network) {
        try {
            offline.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            ObserverConnection.getInstance().dessuscribirse(this);
        }
    }

    @Override
    public void onLine() {
        try {
            offline.setVisibility(View.GONE);
        } catch (Exception e) {
            ObserverConnection.getInstance().dessuscribirse(this);
        }
    }

    @Override
    public void offLine() {
        try {
            offline.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            ObserverConnection.getInstance().dessuscribirse(this);
        }
    }

//    public void loadValues(){
//        mainView.setVisibility(Singletons.reconnect().getMainViewVisibility());
//        waitingView.setVisibility(Singletons.reconnect().getWaitingViewVisibility());
//        seconds.setText(Singletons.reconnect().getSecondsValue()+"");
//        tryingView.setVisibility(Singletons.reconnect().getTryingViewVisibility());
//   }


}

