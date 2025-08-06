package com.privacity.cliente.singleton.localconfiguration;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.privacity.cliente.R;
import com.privacity.cliente.singleton.interfaces.ViewCallbackActionInterface;
import com.privacity.cliente.singleton.sharedpreferences.SharedPreferencesUtil;
import com.privacity.cliente.singleton.toast.SingletonToastManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SingletonTextSizeMessage {
    public static final boolean CONSTANT__RECONNECT_STATUS = false;
    public static final boolean CONSTANT__RECONNECT_LOG_STATUS = false;
    public static final int CONSTANT__DEFAULT_VALUE__SIZE = 0;
    public static final int CONSTANT__DEFAULT_VALUE__VIEW_STATUS = View.GONE;
    public static final int CONSTANT__MAX_SIZE = 20;
    public static final int CONSTANT__MIN_SIZE = -10;
    public static final Map<Integer, Float> originalSize = new HashMap<>();
    public static final Map<Integer, Integer> originalSizeIcon = new HashMap<>();
    static private SingletonTextSizeMessage instance;
    private DisplayMetrics metrics;
    private int size;
    private int viewStatus;
    private boolean valuesWasInit = false;

    private SingletonTextSizeMessage() {

    }

    public static SingletonTextSizeMessage getInstance() {
        if (instance == null) {
            instance = new SingletonTextSizeMessage();
        }

        return instance;
    }

    private static ArrayList<View> getAllChildren(View v, ViewCallbackActionInterface actionI) {

        if (!(v instanceof ViewGroup) || (v instanceof Spinner)) {
            ArrayList<View> viewArrayList = new ArrayList<View>();
            viewArrayList.add(v);
            actionI.action(v);
            return viewArrayList;
        }

        ArrayList<View> result = new ArrayList<View>();

        ViewGroup viewGroup = (ViewGroup) v;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {

            View child = viewGroup.getChildAt(i);

            ArrayList<View> viewArrayList = new ArrayList<View>();
            viewArrayList.add(v);
            if (child.getVisibility() != View.GONE) {
                viewArrayList.addAll(getAllChildren(child, actionI));

                result.addAll(viewArrayList);
            }
        }
        return result;
    }

    private void initValues(Activity activity) {
        size = Integer.valueOf(SharedPreferencesUtil.getTextSizeMessageSize(activity));
        viewStatus = Integer.valueOf(SharedPreferencesUtil.getTextSizeMessageViewStatus(activity));
        metrics = getDisplayMetrics(activity);
        valuesWasInit = true;
    }

    public int getViewStatus(Activity activity) {
        if (!valuesWasInit) {
            initValues(activity);
        }
        return viewStatus;
    }

    public void setViewStatus(Activity activity, int newViewStatus) {
        viewStatus = newViewStatus;
        SharedPreferencesUtil.saveTextSizeMessageViewStatus(activity, newViewStatus + "");

    }

    public int getSize(Activity activity) {
        if (!valuesWasInit) {
            initValues(activity);
        }
        return size;
    }
    boolean toastShowing=false;
    public boolean canSizePlus1(Activity activity) {
        if (!valuesWasInit) {
            initValues(activity);
        }
        size++;
        if ((size ) >= CONSTANT__MAX_SIZE) {
           // size = CONSTANT__MAX_SIZE;

            SingletonToastManager.getInstance().showToadShort(activity,activity.getString(R.string.frame_text_resize__max_size));

            return false;
        } else {

            SingletonToastManager.getInstance().showToadShort(activity,size+"");


            return true;
        }


    }


    public boolean canSizeMinus1(Activity activity) {
        if (!valuesWasInit) {
            initValues(activity);
        }
        size--;
        if ((size) <= CONSTANT__MIN_SIZE) {
            //size = CONSTANT__MIN_SIZE;
            SingletonToastManager.getInstance().showToadShort(activity,activity.getString(R.string.frame_text_resize__min_size));

            return false;
        } else {

            SingletonToastManager.getInstance().showToadShort(activity,size+"");


            return true;
        }

    }

    public void setSize(Activity activity, int newSize) {
        size = newSize;
        SharedPreferencesUtil.saveTextSizeMessageSize(activity, newSize + "");
    }

    public Float getOriginalSize(Activity activity, TextView v) {
        if (!valuesWasInit) {
            initValues(activity);
        }
        if (!originalSize.containsKey(v.getId())) {

            originalSize.put(v.getId(), v.getTextSize() / metrics.density);
            //System.out.println("original: " + v.getId() + " si: " + originalSize.get(v.getId()));
        } else {
           // System.out.println("original: " + v.getId() + " lo tiene");
        }
        return originalSize.get(v.getId());
    }
    public int getOriginalSizeIcon(Activity activity, TextView v) {
        if (!valuesWasInit) {
            initValues(activity);
        }

        if (v instanceof MaterialButton) {
            MaterialButton b= (MaterialButton) v;
            if (!originalSizeIcon.containsKey(v.getId())) {

                originalSizeIcon.put(v.getId(), ((MaterialButton) v).getIconSize() );
                //System.out.println("original: " + v.getId() + " si: " + originalSize.get(v.getId()));
            } else {
                // System.out.println("original: " + v.getId() + " lo tiene");
            }
        }
        return originalSizeIcon.get(v.getId());
    }
    private DisplayMetrics getDisplayMetrics(Activity activity) {
        DisplayMetrics metrics;
        metrics = activity.getApplicationContext().getResources().getDisplayMetrics();
        return metrics;
    }

    public void setIncreaseSizeListener(Activity activity, View increaase, View decrease, View parent) {
        ArrayList<View> parents = new ArrayList<>();
        parents.add(parent);
        setIncreaseSizeListener(activity, increaase, decrease, parents);
    }

    public void setIncreaseSizeListener(Activity activity, View increaase, View decrease, View parent1, View parent2) {
        ArrayList<View> parents = new ArrayList<>();
        parents.add(parent1);
        parents.add(parent2);
        setIncreaseSizeListener(activity, increaase,decrease, parents);
    }

    public void setIncreaseSizeListener(Activity activity, View increaase, View decrease, ArrayList<View> parents) {
        increaase.setOnClickListener(new View.OnClickListener() {
                                 @Override
                                 public void onClick(View view) {
                                     if (canSizePlus1(activity)) {
                                         changeTextSize(parents, activity);
                                         decrease.setClickable(true);
                                         decrease.setEnabled(true);
                                         if ((size) >= CONSTANT__MAX_SIZE){
                                             increaase.setClickable(false);
                                             increaase.setEnabled(false);
                                         }else {
                                             increaase.setClickable(true);
                                             increaase.setEnabled(true);
                                         }
                                     }else{
                                         increaase.setClickable(false);
                                         increaase.setEnabled(false);
                                     }

                                 }
                             }
        );
    }
    public void setDecreaseSizeListener(Activity activity, View increaase, View decrease, View parent1, View parent2) {
        ArrayList<View> parents = new ArrayList<>();
        parents.add(parent1);
        parents.add(parent2);
        setDecreaseSizeListener(activity,  increaase, decrease, parents);
    }
    public void setDecreaseSizeListener(Activity activity, View increaase, View decrease, ArrayList<View> parents) {
        decrease.setOnClickListener(new View.OnClickListener() {
                                 @Override
                                 public void onClick(View view) {
                                     if (canSizeMinus1(activity)) {
                                         changeTextSize(parents, activity);
                                         increaase.setClickable(true);
                                         increaase.setEnabled(true);
                                         if ((size) <= CONSTANT__MIN_SIZE){
                                             decrease.setClickable(false);
                                             decrease.setEnabled(false);
                                         }else {
                                             decrease.setClickable(true);
                                             decrease.setEnabled(true);
                                         }

                                     }else{
                                         decrease.setClickable(false);
                                         decrease.setEnabled(false);
                                     }

                                 }
                             }
        );
    }

    public void refreshTextSize(Activity activity, ArrayList<View> parents) {
               changeTextSize(parents, activity);
    }
    public void refreshTextSize(Activity activity, View v) {
        ArrayList<View> parents = new ArrayList<>();
        parents.add(v);
        refreshTextSize(activity, parents);
    }
    public void refreshTextSize(Activity activity, View parent1, View parent2) {
        ArrayList<View> parents = new ArrayList<>();
        parents.add(parent1);
        parents.add(parent2);
        refreshTextSize(activity, parents);
    }

    private void changeTextSize(ArrayList<View> parents, Activity activity) {
        SharedPreferencesUtil.saveTextSizeMessageSize(activity,getSize(activity)+"");
        for (View parent : parents) {
            getAllChildren(parent, new ViewCallbackActionInterface() {
                        @Override
                        public void action(View v) {
                            if (v.getVisibility() != View.GONE) {
                                if (v instanceof TextView) {
                                    TextView x = (TextView) v;
                                    float textsize = getOriginalSize(activity, x);
                                    if (((textsize + getSize(activity)) != x.getTextSize())) {
                                        if (x.getText() != null && !x.getText().toString().equals("")) {
                                            x.setTextSize(textsize + getSize(activity));
                                        }
                                    }

                                    if (v instanceof MaterialButton){
                                        MaterialButton b = (MaterialButton) v;
                                        int iconize = getOriginalSizeIcon(activity, b);
                                        if (((iconize + getSize(activity)) != b.getIconSize())) {
                                            if (iconize + getSize(activity) > -1) {
                                                b.setIconSize(iconize + getSize(activity));
                                            }

                                        }

                                    }

                                }
                            }


                        }
                    }
            );
        }
    }

    public void saveChangeViewStatus(Activity activity){
        int newViewStatus;
        if (View.VISIBLE == viewStatus){
            newViewStatus=View.GONE;
        } else{
            newViewStatus=View.VISIBLE;
        }
        viewStatus=newViewStatus;
        SharedPreferencesUtil.saveTextSizeMessageViewStatus(activity, newViewStatus+"");
    }

}
