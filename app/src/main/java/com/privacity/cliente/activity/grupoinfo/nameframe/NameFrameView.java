package com.privacity.cliente.activity.grupoinfo.nameframe;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.common.GetButtonReady;
import com.privacity.cliente.activity.grupoinfo.GrupoInfoActivity;
import com.privacity.cliente.util.MenuAcordeonObject;
import com.privacity.cliente.util.MenuAcordeonUtil;
import com.privacity.cliente.util.NicknameUtil;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NameFrameView {
    private Button reset;
    private Button save;
    private EditText name;

    private MenuAcordeonObject menuAcordeon;
    private ProgressBar progressBar;
    private TextView messageInfo;

    private GrupoInfoActivity activity;


    public NameFrameView(GrupoInfoActivity activity){
       this.activity=activity;


        this.progressBar = progressBar;
        reset= GetButtonReady.get(activity, R.id.grupoinfo_name_reset);
        save= GetButtonReady.get(activity, R.id.grupoinfo_name_save);

        name=(EditText) activity.findViewById(R.id.grupoinfo_name_grupo_name);
        NicknameUtil.setNicknameMaxLenght(name);



        this.messageInfo =(TextView) activity.findViewById(R.id.grupo_info_name__mensaje__info);

        menuAcordeon= new MenuAcordeonObject(
                GetButtonReady.get(activity, R.id.bt_grupoinfo_menu_name),
                (View) activity.findViewById(R.id.tl_grupoinfo_menu_name_content));

        setListener();




    }



    @SuppressLint("ClickableViewAccessibility")
    public void setListener(){

        GetButtonReady.get(activity, R.id.bt_grupoinfo_menu_name);

        MenuAcordeonUtil.setActionMenu(menuAcordeon);


}
    }