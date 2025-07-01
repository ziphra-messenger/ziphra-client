package com.privacity.cliente.activity.myaccount;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.common.GetButtonReady;
import com.privacity.cliente.activity.main.MainActivi2ty;
import com.privacity.cliente.common.enumerators.ButtonSetupEnum;
import com.privacity.cliente.frame.help.HelpFrame;
import com.privacity.cliente.singleton.activity.SingletonCurrentActivity;
import com.privacity.cliente.singleton.localconfiguration.SingletonButtonSetup;
import com.privacity.cliente.singleton.localconfiguration.SingletonLang;
import com.privacity.cliente.util.MenuAcordeonObject;
import com.privacity.cliente.util.MenuAcordeonUtil;

import java.util.Locale; 

public class MyAccountLangFrame {
    private static final String TAG = "MyAccountLangFrame";

    private final Button title;

    private final Activity activity;

    private final MenuAcordeonObject menuAcordeon;
    private final RadioGroup radioGruopLangs1;
    private final RadioGroup radioGruopLangs2;
    private final RadioGroup radioGruopButtonSetup;
    private final Button example;

    public MyAccountLangFrame(Activity activity) {
        currentLanguage = activity.getIntent().getStringExtra("currentLang");

        if ((currentLanguage == null) || (currentLanguage.equals(""))){
            currentLanguage=SingletonLang.getInstance().get(activity);
        }
        example =(GetButtonReady.get(activity, R.id.frame_my_account__button__example));
        radioGruopLangs1 = (RadioGroup) activity.findViewById(R.id.frame_my_account__radio_group__lang_1);
        radioGruopLangs2 = (RadioGroup) activity.findViewById(R.id.frame_my_account__radio_group__lang_2);

        radioGruopButtonSetup = (RadioGroup) activity.findViewById(R.id.frame_my_account__radio_group__lang_hide);

        title= (Button) activity.findViewById(R.id.my_account_lang_title);




        menuAcordeon= new MenuAcordeonObject(
               title,
                (View) activity.findViewById(R.id.my_account_lang_content));

        this.activity = activity;
        loadValues();
        setListener();



    }
    Locale myLocale;
    String currentLanguage;
    public void setLocale(String idioma) {
        if (currentLanguage==null) currentLanguage="";
    //    if  (idioma.equals(SingletonLang.getInstance().get(activity))) return;

        //if  (currentLanguage.equals(idioma)) return;
        SingletonLang.getInstance().save(activity, idioma);
        myLocale = new Locale(idioma);
        Resources res = activity.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);

        Intent refresh = new Intent(activity, activity.getClass());
        refresh.putExtra("currentLang", idioma);
        currentLanguage=idioma;
        SingletonCurrentActivity.getInstance().setReLoad(true);
        activity.startActivity(refresh);
        activity.finish();


    }

    private void setListenerRadioLang( RadioGroup rg ){
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                try{
                    setLocale( ((RadioButton)activity.findViewById(rg.getCheckedRadioButtonId())).getTag().toString());

                } catch (Exception e) {
                    setCurrentLang();
                    new HelpFrame().show("no implementado ");
                }


            }
        });

    }
    public void setListener(){
        setListenerRadioLang(radioGruopLangs1);
        setListenerRadioLang(radioGruopLangs2);

        MenuAcordeonUtil.setActionMenu(menuAcordeon);

        radioGruopButtonSetup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                SingletonButtonSetup.getInstance().save(activity, ButtonSetupEnum.valueOf(((RadioButton)activity.findViewById(radioGruopButtonSetup.getCheckedRadioButtonId())).getTag().toString()));
                Intent refresh = new Intent(activity, activity.getClass());
                SingletonCurrentActivity.getInstance().setReLoad(true);
                activity.startActivity(refresh);
                activity.finish();
            }
        });



        example.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new HelpFrame().show("* Si el texto del botón esta oculto al mantenerlo presionado el texto se mostrará \n" +
                        "* Si el texto del botón es demasiado largo al mantenerlo presionado el texto se moverá como una marquesina." +
                        "");
            }
        });

    }



    private static void setCurrentLang(String currentLang, RadioGroup radioGruopLangs){
        for (int i = 0  ; i != radioGruopLangs.getChildCount() ; i++){

            ((RadioButton)radioGruopLangs.getChildAt(i)).setChecked(false);


            try {
//                Log.d(TAG, "setCurrentLang radio for:" + (ButtonSetupEnum.valueOf(radioGruopLangs.getChildAt(i).getTag().toString())));

                if (
                       radioGruopLangs.getChildAt(i).getTag().toString().equals(currentLang)
                ){
                    ((RadioButton)radioGruopLangs.getChildAt(i)).setChecked(true);
                }
            }catch (Exception e){
                Log.e(TAG, "setCurrentLang :" + e.getMessage());
            //    e.printStackTrace();

            }

        }
    }

    public void loadValues() {


        setCurrentLang();

        ButtonSetupEnum bs = SingletonButtonSetup.getInstance().get(activity);

        for (int i = 0  ; i != radioGruopButtonSetup.getChildCount() ; i++){
           if ( ButtonSetupEnum.valueOf(radioGruopButtonSetup.getChildAt(i).getTag().toString()).equals(bs)){
               ((RadioButton)radioGruopButtonSetup.getChildAt(i)).setChecked(true);
            }else{
               ((RadioButton)radioGruopButtonSetup.getChildAt(i)).setChecked(false);
           }
        }


            if (SingletonCurrentActivity.getInstance().isReLoad()) {
                MenuAcordeonUtil.changeVisibily(menuAcordeon);
                SingletonCurrentActivity.getInstance().setReLoad(false);

        }
    }

    private void setCurrentLang() {
        String currectLang = SingletonLang.getInstance().get(activity);
        Log.d(TAG, "currectLang :" + currectLang);
        setCurrentLang(currectLang, radioGruopLangs1 );
        setCurrentLang(currectLang, radioGruopLangs2 );
    }
}
