package com.privacity.cliente.frame.help;

import android.animation.Animator;
import android.app.Activity;
import android.os.Build;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.privacity.cliente.R;
import com.privacity.cliente.singleton.activity.SingletonCurrentActivity;

/* android:id="@+id/frame_help__close"
         android:id="@+id/frame_help__content_close"
<TextView
                        android:id="@+id/frame_help__txt"
<ScrollView
                    android:id="@+id/frame_help__info__scroll"
<LinearLayout
                android:id="@+id/frame_help__content_info"
<LinearLayout
            android:id="@+id/frame_help__content_all"
<ScrollView
        android:id="@+id/"*/
public class HelpView {

    private View scrollAll;
    private Button close;
    private TextView txt;
    private View contentAll;

    public HelpView() {
        initView();
        initListeners();
    }

    private void initListeners() {
        // A previously invisible view.

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeVisibility(View.GONE);

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
        this.scrollAll = getActivity().findViewById(R.id.common__help__scroll);
        this.close = (Button)getActivity().findViewById(R.id.common__help__close);
        this.txt = (TextView)getActivity().findViewById(R.id.common__help__txt);
        this.contentAll = getActivity().findViewById(R.id.common__help__content_all);

        txt.setMovementMethod(ScrollingMovementMethod.getInstance());
        //txt.setScrollBarStyle(0x03000000);
        txt.setVerticalScrollBarEnabled(true);
        txt.setTextColor(0xFF000000);
    }

    private Activity getActivity() {
        return SingletonCurrentActivity.getInstance().get();
    }

    public void show(String s) {
        txt.setText(s);

        changeVisibility(View.VISIBLE);
    }

    private String getStringResourceByName(String aString) {
        String packageName =getActivity().getPackageName();
        int resId = getActivity().getResources().getIdentifier(aString, "string", packageName);
        return getActivity().getString(resId);
    }
}
