package com.privacity.cliente.common.component.selectview;

import android.animation.Animator;
import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;

import com.privacity.cliente.R;
import com.privacity.cliente.singleton.usuario.SingletonSessionClosing;
import com.privacity.common.enumeration.RulesConfEnum;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public class SelectView {

    private ViewSelectCallBack cb;
    private Activity activity;
    private RadioButton radioUserOff;
    private RadioButton radioUserOn;
    private RadioButton radioUserNull;
    private Button close;
    private View frame;
    private Switch myAccountOff;
    private Switch myAccountOn;
    private Switch myAccountNull;
    private Switch grupoOff;
    private Switch grupoOn;
    private Switch grupoNull;
    private List<Switch> myAccount;
    private List<Switch> grupo;
    private List<Switch> conclusion;
    private List<RadioButton> user;


    private RadioButton radioUserMandatory;
    private RadioButton radioUserAllow;
    private Switch myAccountMandatory;
    private Switch myAccountAllow;
    private Switch grupoAllow;
    private Switch grupoMandatory;
    private Switch conclusionOff;
    private Switch conclusionOn;
    private Switch conclusionNull;
    private Switch conclusionAllow;
    private Switch conclusionMandatory;
    private TextView grupoTxt;

    @Getter
    private RulesConfEnum newUserValue;
    private Switch grupoBlock;
    private Switch conclusionBlock;
    private TextView descripcion;
    private TextView conclusionGrupoTxt;

    public SelectView(Activity activity, ViewSelectCallBack cb) {
        this.activity = activity;

        this.cb=cb;
        initView();
        hideView();
        loadValues();
        applyRules(cb.getUserValue());
        setListeners();


    }

    public static boolean isMyAccount(boolean myAccount, RulesConfEnum user){
        return
                convertRulesConfEnum(applyRules(

                        ((myAccount)?RulesConfEnum.ON:RulesConfEnum.OFF)

                        ,null,user
                ));
        }

    public static boolean isFull(boolean myAccount, boolean grupo, RulesConfEnum user){
        return
                convertRulesConfEnum(applyRules(

                        ((myAccount)?RulesConfEnum.ON:RulesConfEnum.OFF)

                        ,((grupo)?RulesConfEnum.ON:RulesConfEnum.NULL),user
                ));
    }
    public static RulesConfEnum applyRules(RulesConfEnum myAccountValue, RulesConfEnum grupoValue, RulesConfEnum userValue) {

        if (grupoValue != null && (grupoValue.equals(RulesConfEnum.ON)
                || grupoValue.equals(RulesConfEnum.OFF)
                || grupoValue.equals(RulesConfEnum.BLOCK)
                || grupoValue.equals(RulesConfEnum.MANDATORY)
        )){
            return grupoValue;
        }else {
            if (userValue.equals(RulesConfEnum.NULL)){
                    return myAccountValue;
            }else{
                return userValue;
            }
        }
    }


    private void applyRules(RulesConfEnum userVariable) {
        RulesConfEnum r = applyRules(cb.getMyAccountValue(), cb.getGrupoValue(), userVariable);
        setValuesSwitch(r, conclusion);
        setValuesParentSwitch(r, cb.getParentView());

    }
    public void applyRules() {

        RulesConfEnum r = applyRules(cb.getMyAccountValue(), cb.getGrupoValue(), cb.getUserValue());
        setValuesSwitch(r, conclusion);
        setValuesParentSwitch(r, cb.getParentView());

    }

    public RulesConfEnum getConclusion(){
        for (View v : conclusion){
            if (v.getVisibility() ==View.VISIBLE){
                return RulesConfEnum.valueOf(v.getTag().toString());
            }
        }
        return null;
    }
    private void loadValues() {
        myAccount= new ArrayList<>();
        myAccount.add(myAccountNull);
        myAccount.add(myAccountOn);
        myAccount.add(myAccountOff);
        myAccount.add(myAccountMandatory);
        myAccount.add(myAccountAllow);
        setValuesSwitch(cb.getMyAccountValue(), myAccount);

        grupo= new ArrayList<>();
        grupo.add(grupoNull);
        grupo.add(grupoOn);
        grupo.add(grupoOff);
        grupo.add(grupoAllow);
        grupo.add(grupoMandatory);
        grupo.add(grupoBlock);
        setValuesSwitch(cb.getGrupoValue(), grupo);

        user= new ArrayList<>();
        user.add(radioUserNull);
        user.add(radioUserOn);
        user.add(radioUserOff);
        user.add(radioUserAllow);
        user.add(radioUserMandatory);

        setValuesRadio(cb.getUserValue(), user, cb.rulesConfNeeded());

        conclusion= new ArrayList<>();
        conclusion.add(conclusionNull);
        conclusion.add(conclusionOn);
        conclusion.add(conclusionOff);
        conclusion.add(conclusionAllow);
        conclusion.add(conclusionMandatory);
        conclusion.add(conclusionBlock);
        setValuesSwitch(cb.getUserValue(), conclusion);

        descripcion.setText(cb.descripcionText());
        if (cb.getGrupoValue() != null && (cb.getGrupoValue().equals(RulesConfEnum.ON)
                || cb.getGrupoValue().equals(RulesConfEnum.OFF)
                || cb.getGrupoValue().equals(RulesConfEnum.BLOCK)
                || cb.getGrupoValue().equals(RulesConfEnum.MANDATORY))){
            conclusionGrupoTxt.setVisibility(View.VISIBLE);
        }else{
            conclusionGrupoTxt.setVisibility(View.GONE);
        }



    }
    Switch con;
    private void setValuesSwitch(RulesConfEnum generalValue, List<Switch> switches) {

        for (View v : switches){
            if (generalValue != null && RulesConfEnum.valueOf(v.getTag().toString()).equals(generalValue)){
                v.setVisibility(View.VISIBLE);
                ((View)v.getParent()).setVisibility(View.VISIBLE);
                con= (Switch) v;

            }else{
                v.setVisibility(View.GONE);
                ((View)v.getParent()).setVisibility(View.GONE);
            }
            v.setEnabled(false);
        }
    }

    public static boolean convertRulesConfEnum(RulesConfEnum e){
        if (e.equals(RulesConfEnum.NULL) || e.equals(RulesConfEnum.OFF)){
            return false;
        }
        return true;
    }
    public static RulesConfEnum convertRulesConfEnum(boolean e){
        if (e){
            return RulesConfEnum.ON;
        }
        return RulesConfEnum.NULL;
    }
    public static RulesConfEnum convertRulesConfEnumOnOff(boolean e){
        if (e){
            return RulesConfEnum.ON;
        }
        return RulesConfEnum.OFF;
    }
    private void setValuesParentSwitch(RulesConfEnum generalValue, Switch v) {


            if (generalValue != null){
                v.setVisibility(View.VISIBLE);
                ((View)v.getParent()).setVisibility(View.VISIBLE);
                ((View)v.getParent()).setTooltipText(con.getTooltipText());
                ((Switch)v).setThumbTintList(con.getThumbTintList());

/*                v.setVisibility(View.GONE);
                ((View)v.getParent()).setVisibility(View.GONE);*/

            }
            ((View)v.getParent()).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    showView(cb);
                }
            });
            v.setEnabled(false);

    }

    private void setValuesRadio(RulesConfEnum actualUserValue, List<RadioButton> users, List<RulesConfEnum> rulesConfNeeded) {

        for (RadioButton v : users){

            RulesConfEnum tagEnum = RulesConfEnum.valueOf(v.getTag().toString());

            if (tagEnum.equals(actualUserValue)){
                v.setChecked(true);
            }else{
                v.setChecked(false);
            }

            if (rulesConfNeeded.contains(tagEnum)){
                v.setVisibility(View.VISIBLE);
                ((View)v.getParent()).setVisibility(View.VISIBLE);
            }else{

                    v.setVisibility(View.GONE);
                    ((View)v.getParent()).setVisibility(View.GONE);

            }
        }
    }
    private void disabledUser(List<View> v) {

        for (View vv : v){

                vv.setVisibility(View.GONE);
        }
    }
    private void enabledUser(List<View> v) {

        for (View vv : v){

            vv.setVisibility(View.VISIBLE);
        }
    }
    public RulesConfEnum getValuesRadio() {
        return getValuesRadio(user);
    }
    public RulesConfEnum getValuesRadio(List<RadioButton> radioButtonList) {

        for (RadioButton v : radioButtonList){
            if (v.isChecked()){
               return RulesConfEnum.valueOf(v.getTag().toString());
            }
        }
        return RulesConfEnum.NULL;
    }

    private void setListeners() {
        GRadioGroup gr = new GRadioGroup(new RadioCallBack() {
            @Override
            public void action() {
                applyRules(getValuesRadio(user));
            }
        }, radioUserNull, radioUserOn, radioUserOff,radioUserAllow,radioUserMandatory);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cb.action(getValuesRadio(user));
                newUserValue =    getValuesRadio(user);
                frame.setVisibility(View.GONE);
            }
        });
    }

    public void resetView(){


        initView();
        hideView();
        loadValues();
        applyRules(getValuesRadio(user));
        setListeners();


        ArrayList<View> grupoHide = new ArrayList<>();
        grupoHide.add(grupoTxt);
        grupoHide.add(grupoNull);
        grupoHide.add(grupoOn);
        grupoHide.add(grupoOff);
        grupoHide.add(grupoAllow);
        grupoHide.add(grupoMandatory);
        grupoHide.add(grupoBlock);
        if ( this.cb.getGrupoValue() == null){


            disabledUser(grupoHide);
        }else {
            enabledUser(grupoHide);
        }
    }
    public void showView(ViewSelectCallBack cb){


        this.cb=cb;
        initView();
        hideView();
        loadValues();
        applyRules(cb.getUserValue());
        setListeners();


        ArrayList<View> grupoHide = new ArrayList<>();
        grupoHide.add(grupoTxt);
        grupoHide.add(grupoNull);
        grupoHide.add(grupoOn);
        grupoHide.add(grupoOff);
        grupoHide.add(grupoAllow);
        grupoHide.add(grupoMandatory);
        grupoHide.add(grupoBlock);
        if ( this.cb.getGrupoValue() == null){


            disabledUser(grupoHide);
        }else {
            enabledUser(grupoHide);
        }

        frame.setVisibility(View.VISIBLE);

    }


    public void hideView(){
        this.frame = activity.findViewById(R.id.common__select__view__content__all);
            frame.setVisibility(View.GONE);

    }

    private void initView() {

        this.radioUserNull = (RadioButton)activity.findViewById(R.id.common__select__view__user__null);
        this.radioUserOn = (RadioButton)activity.findViewById(R.id.common__select__view__user__on);
        this.radioUserOff = (RadioButton)activity.findViewById(R.id.common__select__view__user__off);
        this.radioUserAllow = (RadioButton)activity.findViewById(R.id.common__select__view__user__allow);
        this.radioUserMandatory = (RadioButton)activity.findViewById(R.id.common__select__view__user__mandatory);

        this.close = (Button)activity.findViewById(R.id.common__select__view__close);
        this.frame = (FrameLayout)activity.findViewById(R.id.common__select__view__content__frame);

        this.myAccountNull = (Switch)activity.findViewById(R.id.common__select__view__myaccount__null);
        this.myAccountOn = (Switch)activity.findViewById(R.id.common__select__view__myaccount__on);
        this.myAccountOff = (Switch)activity.findViewById(R.id.common__select__view__myaccount__off);
        this.myAccountAllow = (Switch)activity.findViewById(R.id.common__select__view__myaccount__allow);
        this.myAccountMandatory = (Switch)activity.findViewById(R.id.common__select__view__myaccount__mandatory);

        this.grupoTxt = (TextView)activity.findViewById(R.id.common__select__view__grupo__txt);
        this.conclusionGrupoTxt = (TextView)activity.findViewById(R.id.common__select__view__txt__conclusion_grupo);

        this.grupoNull = (Switch)activity.findViewById(R.id.common__select__view__grupo__null);
        this.grupoOn = (Switch)activity.findViewById(R.id.common__select__view__grupo__on);
        this.grupoOff = (Switch)activity.findViewById(R.id.common__select__view__grupo__off);
        this.grupoAllow = (Switch)activity.findViewById(R.id.common__select__view__grupo__allow);
        this.grupoMandatory = (Switch)activity.findViewById(R.id.common__select__view__grupo__mandatory);
        this.grupoBlock= (Switch)activity.findViewById(R.id.common__select__view__grupo__block);
        
        this.conclusionNull = (Switch)activity.findViewById(R.id.common__select__view__conclusion__null);
        this.conclusionOn = (Switch)activity.findViewById(R.id.common__select__view__conclusion__on);
        this.conclusionOff = (Switch)activity.findViewById(R.id.common__select__view__conclusion__off);
        this.conclusionAllow = (Switch)activity.findViewById(R.id.common__select__view__conclusion__allow);
        this.conclusionMandatory = (Switch)activity.findViewById(R.id.common__select__view__conclusion__mandatory);
        this.conclusionBlock = (Switch)activity.findViewById(R.id.common__select__view__conclusion__block);

        this.descripcion = (TextView)activity.findViewById(R.id.common__select__view__txt__descripcion);


    }


}
