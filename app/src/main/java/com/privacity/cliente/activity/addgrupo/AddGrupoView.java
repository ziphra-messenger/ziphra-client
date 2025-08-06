package com.privacity.cliente.activity.addgrupo;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.addmember.AddMembersToGrupoActivity;
import com.privacity.cliente.activity.common.GetButtonReady;
import com.privacity.cliente.activity.grupoinfo.GrupoInfoActivity;
import com.privacity.cliente.rest.restcalls.grupo.AddGrupo;
import com.privacity.cliente.singleton.interfaces.ViewCallbackActionInterface;
import com.privacity.cliente.util.CopyPasteUtil;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddGrupoView {

    private Button btnAddGrupoOk;
    private TextView tvAddGrupoName;
    private LinearLayout layoutGrupoCreado;
    private TextView tvMensajeGrupoCreado;

    private AddGrupoActivity activity;

    public AddGrupoView(AddGrupoActivity activity) {
        this.activity = activity;

        initView();
        initValues();
        setListeners();

    }

    private void initValues() {
        tvAddGrupoName.setText(activity.generateNombre());
    }

    private void setListeners() {
        GetButtonReady.get(activity,R.id.btn_add_grupo_add_member).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
                Intent intent = new Intent(activity, AddMembersToGrupoActivity.class);
                activity.startActivity(intent);
            }
        });

        GetButtonReady.get(activity,R.id.btn_add_grupo_grupo_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
                Intent intent = new Intent(activity, GrupoInfoActivity.class);
                activity.startActivity(intent);
            }
        });

        GetButtonReady.get(activity,R.id.btn_add_grupo_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });


        GetButtonReady.get(activity,R.id.btn_add_grupo_add_grupo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
                Intent intent = new Intent(activity, AddGrupoActivity.class);
                activity.startActivity(intent);
            }
        });

        CopyPasteUtil.setListenerIconClearText((EditText) tvAddGrupoName);
    }

    private ViewCallbackActionInterface callRestCrearGrupo() {
        return new ViewCallbackActionInterface(){

            @Override
            public void action(View v) {
                AddGrupo.addGrupo(activity, AddGrupoView.this);
            }
        };
    }

    private void initView() {
        tvMensajeGrupoCreado = (TextView) activity.findViewById(R.id.add_grupo_mensaje_grupo_creado);
        btnAddGrupoOk = GetButtonReady.get(activity,R.id.btn_add_grupo_ok,callRestCrearGrupo());
        tvAddGrupoName = (TextView) activity.findViewById(R.id.pt_add_grupo_name);
        layoutGrupoCreado = (LinearLayout) activity.findViewById(R.id.layout_grupo_creado);

    }
}
