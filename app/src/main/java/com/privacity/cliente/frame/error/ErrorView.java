package com.privacity.cliente.frame.error;

import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.privacity.cliente.BuildConfig;
import com.privacity.cliente.R;
import com.privacity.cliente.activity.common.GetButtonReady;
import com.privacity.cliente.common.error.SimpleErrorDialog;
import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.singleton.UtilsStringSingleton;
import com.privacity.cliente.singleton.activity.SingletonCurrentActivity;
import com.privacity.cliente.singleton.usuario.SingletonSessionClosing;
import com.privacity.cliente.model.dto.Protocolo;
import com.privacity.cliente.util.CopyPasteUtil;

import org.springframework.http.ResponseEntity;

import lombok.Data;
import lombok.experimental.Accessors;

/* android:id="@+id/common__error__view__close"
         android:id="@+id/common__error__view__content_close"
<TextView
                        android:id="@+id/common__error__view__txt"
<ScrollView
                    android:id="@+id/common__error__view__info__scroll"
<LinearLayout
                android:id="@+id/common__error__view__content_info"
<LinearLayout
            android:id="@+id/common__error__view__content_all"
<ScrollView
        android:id="@+id/"*/
@Accessors(chain = true)
@Data
public class ErrorView {

    private View scrollAll;
    private Button copy;
    private Button close;
    private TextView errorDescription;
    private TextView errorCode;
    private View contentAll;
    private TextView recomendacion;
    private View contentDescripcion;
    private View contentCode;
    private View contentInformar;
    private TextView version;
    private CallbackRest callbackRest;
    private ResponseEntity<Protocolo> response;
    private TextView url;
    private View contentUrl;
    private TextView stacktrace;

    private ErrorInforme informe;

 private SimpleErrorDialog.PasswordValidationI passwordValidationI;
    private Button share;

    public ErrorView(Activity activity) {
        if (SingletonSessionClosing.getInstance().isClosing())return;
        this.activity = activity;
        initView();
        initListeners();
        initData();
    }

    private Activity activity;
    public ErrorView() {
        if (SingletonSessionClosing.getInstance().isClosing())return;
        initView();
        initListeners();
        initData();
    }

    private void initData() {
        if (SingletonSessionClosing.getInstance().isClosing())return;

        version.setText(getActivity().getString(R.string.main_login__version, BuildConfig.VERSION_NAME));
    }

    private void initListeners() {
        if (SingletonSessionClosing.getInstance().isClosing())return;
        // A previously invisible view.

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeVisibility(View.GONE);
                if(callbackRest !=null ) callbackRest.onError(response);
                if(passwordValidationI !=null ) passwordValidationI.action();

            }
        });
        contentAll.setClickable(true);
        contentAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeVisibility(View.GONE);

            }
        });



    }

    public void changeVisibility(int visibility) {
        if (SingletonSessionClosing.getInstance().isClosing())return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Get the center for the clipping circle.
            int cx = scrollAll.getWidth() / 2;
            int cy = scrollAll.getHeight() / 2;

            // Get the final radius for the clipping circle.
            float finalRadius = (float) Math.hypot(cx, cy);

            // Create the animator for this view. The start radius is 0.
            Animator anim = ViewAnimationUtils.createCircularReveal(scrollAll, cx, cy, 0f, finalRadius);


            // Make the view visible and start the animation.
            scrollAll.setVisibility(visibility);
            anim.start();

        }
    }

    private void initView() {
        if (SingletonSessionClosing.getInstance().isClosing())return;
        this.scrollAll = getActivity().findViewById(R.id.common__error__view__scroll);
        this.close = (Button)getActivity().findViewById(R.id.common__error__view__close);
        this.share = GetButtonReady.get(getActivity(),R.id.common__error__view__share_all);
        this.errorCode = (TextView)getActivity().findViewById(R.id.common__error__view__error_code);
        this.copy = GetButtonReady.get(getActivity(),R.id.common__error__view__copy_all);
        this.stacktrace = (TextView)getActivity().findViewById(R.id.common__error__view__stacktrace);
        this.version = (TextView)getActivity().findViewById(R.id.common__error__view__version);
        this.url = (TextView)getActivity().findViewById(R.id.common__error__view__error_url);
        this.errorDescription = (TextView)getActivity().findViewById(R.id.common__error__view__error_description);
        this.recomendacion = (TextView)getActivity().findViewById(R.id.common__error__view__recomendacion);
        this.contentAll = getActivity().findViewById(R.id.common__error__view__content_all);
        this.contentUrl = getActivity().findViewById(R.id.common__error__view__content_url);
        this.contentDescripcion = getActivity().findViewById(R.id.common__error__view__content_description);
        this.contentCode = getActivity().findViewById(R.id.common__error__view__content_code);
        this.contentInformar = getActivity().findViewById(R.id.common__error__view__content_informar);

        stacktrace.setMovementMethod(ScrollingMovementMethod.getInstance());
        //txt.setScrollBarStyle(0x03000000);
        stacktrace.setHorizontallyScrolling(true);
        stacktrace.setNestedScrollingEnabled(true);
        stacktrace.setVerticalScrollBarEnabled(true);
        stacktrace.setHorizontalScrollBarEnabled(true);
        stacktrace.setTextColor(0xFF000000);


    }

    private Activity getActivity() {
        if (activity != null) return activity;
        return SingletonCurrentActivity.getInstance().get();
    }

    public void show(ErrorPojo p) {
        if (SingletonSessionClosing.getInstance().isClosing())return;
        errorCode.setText(p.getErrorCode());
        url.setText(p.getUrl());
        errorDescription.setText(p.getErrorDescription());
        recomendacion.setText(p.getRecomendacion());

/*        try {
            int i=1/0;
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            p.setStackTrace(stacktrace);
        }*/
        stacktrace.setText(p.getStackTrace());
System.out.println(p.getStackTrace());
        checkToShow(p.getStackTrace(),this.stacktrace);
        checkToShow(p.getUrl(),this.contentUrl);
        checkToShow(p.getErrorCode(),this.contentCode);
        checkToShow(p.getErrorDescription(),this.contentDescripcion);
        checkToShow(p.isInformar(),this.contentInformar);
        checkToShow(p.getRecomendacion(),this.recomendacion);

        //recomendacion.setText(UtilsStringSingleton.getInstance().gsonPretty().toJson(p));
        //recomendacion.setVisibility(View.VISIBLE);
        changeVisibility(View.VISIBLE);

        ErrorInforme i = new ErrorInforme();
        i.setPojo(p);
        i.setVersion(version.getText().toString());
        i.setActivity(SingletonCurrentActivity.getInstance().get().getClass().getName());


        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CopyPasteUtil.setClipboard(SingletonCurrentActivity.getInstance().get(), i.buildInfo());
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = i.buildInfo();
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Informar privacity");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                SingletonCurrentActivity.getInstance().get().startActivity(Intent.createChooser(sharingIntent, "Informar"));
            }
        });
    }

    private boolean isNotEmpty(String s){
        return (s != null && !"".equals(s.trim()));
    }
    private void checkToShow(String s, View v){
        checkToShow(isNotEmpty(s),v);
    }
    private void checkToShow(boolean b, View v){
        if (b){
            v.setVisibility(View.VISIBLE);
        }else{

            v.setVisibility(View.GONE);
        }
    }
    private String getStringResourceByName(String aString) {
        String packageName =getActivity().getPackageName();
        int resId = getActivity().getResources().getIdentifier(aString, "string", packageName);
        return getActivity().getString(resId);
    }
}
