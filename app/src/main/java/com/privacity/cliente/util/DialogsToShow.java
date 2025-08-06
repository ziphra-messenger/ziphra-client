package com.privacity.cliente.util;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.grupo.GrupoUtil;
import com.privacity.cliente.model.Grupo;
import com.privacity.cliente.singleton.Observers;
import com.privacity.cliente.singleton.SingletonValues;import com.privacity.cliente.singleton.Singletons;
import com.privacity.cliente.singleton.toast.SingletonToastManager;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class DialogsToShow {
    public static void noAdminDialog(Activity activity, Grupo grupo, View parent) {
        if (grupo == null) return;
        noAdminDialog(activity,grupo.getIdGrupo(),parent);

    }
    public static void noAdminDialog(Activity activity, String idGrupo, View parent) {

        if (!Observers.grupo().amIAdmin(idGrupo)) {
            ArrayList<View> views = getAllChildren(parent);
            for (View v : views) {

                if (v.getTag() != null && v.getTag().toString().contains("[Admin]")) {
                    noAdminDialog(activity, v, idGrupo,false);
                }else{
                    if (activity.getResources().getString(R.string.rol_no_admin_message).equals(v.getTag())) {
                        v.setVisibility(View.VISIBLE);
                    }/*else{
                        v.setVisibility(View.GONE);
                    }*/

                }


            }
        }

    }

    public static void noAdminDialog(Activity activity, String idGrupo) {
        noAdminDialog(activity, idGrupo, activity.getWindow().getDecorView().findViewById(android.R.id.content));
    }

    public static ArrayList<View> getAllChildren(View v) {

        if (!(v instanceof ViewGroup) || (v instanceof Spinner)) {
            ArrayList<View> viewArrayList = new ArrayList<View>();
            viewArrayList.add(v);
            return viewArrayList;
        }

        ArrayList<View> result = new ArrayList<View>();

        ViewGroup viewGroup = (ViewGroup) v;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {

            View child = viewGroup.getChildAt(i);

            ArrayList<View> viewArrayList = new ArrayList<View>();
            viewArrayList.add(v);
            viewArrayList.addAll(getAllChildren(child));

            result.addAll(viewArrayList);
        }
        return result;
    }


    public static void noAdminDialog(Activity context, View view, String idGrupo){
        noAdminDialog(context,view, idGrupo,true);
    }
    public static void noAdminDialog(Activity activity, View view, String idGrupo, boolean showDialog){

        try {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    String mes = (activity.getString(R.string.dialog_to_show__alert__validation_by_role,
                            GrupoUtil.transformGrupoRoleEnumToCompleteString(activity, Observers.grupo().whichIsMyRole(idGrupo))));
                    SingletonToastManager.getInstance().showToadShort(activity,mes);
                }
            });
        } catch (Exception e) {

        }

        try {
            System.out.println(activity.
                    getResources().getResourceEntryName(view.getId()) +   "  - " + view.getTag());
        } catch (Resources.NotFoundException e) {
           // e.printStackTrace();
        }

        boolean iamadmin = SingletonValues.getInstance().getGrupoSeleccionado().iAmAdmin();

        if (!iamadmin && view.getTag() != null &&view.getTag().toString().contains("[gone]")){

            view.setVisibility(View.GONE);
        }
        if (!iamadmin && view.getTag() != null && view.getTag().toString().contains("[disabled]")){

            view.setClickable(false);
            view.setContextClickable(false);
        }
        if (!iamadmin && view.getTag() != null && view.getTag().toString().contains("[clickcontext]")){

            view.setClickable(true);
            view.setContextClickable(true);


        }
        if (!iamadmin && view.getTag() != null && view.getTag().toString().contains("[enabledfalse]")){

            view.setEnabled(false);
            view.setClickable(true);
            view.setContextClickable(false);
        }
       // noAdminDialog(activity, idGrupo,view);


//        view.setBackgroundColor(Color.LTGRAY);
/*
        view.setFocusable(false);
        view.setLongClickable(false);

        if (!showDialog){
            view.setEnabled(false);
            view.setClickable(false);
        }

        MyListener r = new DialogsToShow.MyListener();
        r.setContext(context);
        r.setMessage(context.getString(R.string.dialog_to_show__alert__validation_by_role, GrupoUtil.transformGrupoRoleEnumToCompleteString(context, myRole)));
        r.setHasNegativeButton(false);
        r.setHasPositiveButton(true);
        r.setShowDialog(showDialog);

        if (view instanceof ImageButton){
            ImageButton button = (ImageButton) view;
            Drawable buttonDrawable = button. getBackground();
            buttonDrawable = DrawableCompat.wrap(buttonDrawable);
  //          DrawableCompat. setTint(buttonDrawable, Color.LTGRAY);
            button.setBackground(buttonDrawable);
            button.setVisibility(View.GONE);
        }

        if (view instanceof CheckBox) {
            CheckBox c = (CheckBox) view;



//            c.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//
//                    if (isChecked == true) {
//                        buttonView.setChecked(false);
//                    } else {
//                        buttonView.setChecked(true);
//                    }
//
//                }
//
//            });

        }else if (view instanceof TextView){

                ((TextView)view).setFocusable(false);
                ((TextView)view).setTextColor(Color.GRAY);
                ((TextView)view).setFocusableInTouchMode(false);
                //((AdapterView)view).setOnItemSelectedListener(r);
                //((AdapterView)view).setOnTouchListener(r);
                view.setOnClickListener(r);
        }else if (view instanceof Spinner){


            Spinner s = (Spinner)view;

            s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    TextView childAt = (TextView) parent.getChildAt(s.getSelectedItemPosition());
                    if (childAt == null ) return;

                    childAt.setOnClickListener(r);
                    if (s.getChildCount() == 3 && s.getSelectedItemPosition() == 0){
                        if ("✔".equals(childAt.getText().toString())){
                            childAt.setTextColor(Color.BLUE);
                        }
                        if ("✘".equals(childAt.getText().toString())){
                            childAt.setTextColor(Color.BLUE);
                        }
                    }else{
                        if ("✔".equals(childAt.getText().toString())){
                            childAt.setTextColor(Color.parseColor("#FF009688"));
                        }
                        if ("✘".equals(childAt.getText().toString())){
                            childAt.setTextColor(Color.RED);
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            s.getChildAt(0);
            //s.addView(tv);
            //((AdapterView)view).setEnabled(false);
            //((AdapterView)view).setOnItemSelectedListener(r);
            //((AdapterView)view).setOnTouchListener(r);
        }else{
            view.setOnClickListener(r);

        }
        return r;
*/
    }



    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class MyListener implements View.OnClickListener, AdapterView.OnItemClickListener, View.OnTouchListener, AdapterView.OnItemSelectedListener {

        private CallbackAction actionBeforeMessage;
        private CallbackAction actionAfterMessage;
        private CallbackAction actionOnPositiveBoton;
        private CallbackAction actionOnNegativeBoton;
        private String message;
        private Context context;

        private boolean showDialog;
        private boolean hasPositiveButton;
        private boolean hasNegativeButton;

        @Override
        public void onClick(View v) {
            doit();
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            doit();
        }
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            doit();
            return false;
        }
        public void doit() {
            if(actionBeforeMessage != null) actionBeforeMessage.action();

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            if (hasPositiveButton) {
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (actionOnPositiveBoton != null) actionOnPositiveBoton.action();
                    }
                });
            }
            if (hasNegativeButton){
                builder.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(actionOnNegativeBoton != null) actionOnNegativeBoton.action();
                    }
                });
            }


            builder.setMessage(message);


            AlertDialog dialog = builder.create();
            if (showDialog) dialog.show();
            if(actionAfterMessage != null)actionAfterMessage.action();
        }


        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            doit();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            doit();
        }
    }
}


