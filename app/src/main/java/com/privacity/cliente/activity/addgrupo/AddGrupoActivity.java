package com.privacity.cliente.activity.addgrupo;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.addmember.AddMembersToGrupoActivity;
import com.privacity.cliente.activity.common.CustomAppCompatActivity;
import com.privacity.cliente.activity.grupoinfo.GrupoInfoActivity;
import com.privacity.cliente.common.error.SimpleErrorDialog;
import com.privacity.cliente.encrypt.AESEncrypt;
import com.privacity.cliente.encrypt.AESFactory;
import com.privacity.cliente.model.Grupo;
import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.cliente.singleton.Observers;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.util.GsonFormated;
import com.privacity.common.enumeration.ProtocoloComponentsEnum;import com.privacity.common.enumeration.ProtocoloActionsEnum;

import com.privacity.common.dto.AESDTO;
import com.privacity.common.dto.ProtocoloDTO;
import com.privacity.common.dto.request.GrupoNewRequestDTO;

import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;

public class AddGrupoActivity extends CustomAppCompatActivity {

    private Button btnAddGrupoOk;
    private TextView tvAddGrupoName;
    private LinearLayout layoutGrupoCreado;
    private TextView tvMensajeGrupoCreado;

    @Override
    protected boolean isOnlyAdmin() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_grupo);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("PrivaCity - Crear Grupo");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        layoutGrupoCreado = (LinearLayout) findViewById(R.id.layout_grupo_creado);


        tvMensajeGrupoCreado = (TextView) findViewById(R.id.add_grupo_mensaje_grupo_creado);
        btnAddGrupoOk = (Button) findViewById(R.id.btn_add_grupo_ok);
        tvAddGrupoName = (TextView) findViewById(R.id.pt_add_grupo_name);
        tvAddGrupoName.setText(generateNombre());
        btnAddGrupoOk.setOnClickListener(callRestCrearGrupo());
        ((Button)findViewById(R.id.btn_add_grupo_add_member)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddGrupoActivity.this.finish();
                Intent intent = new Intent(AddGrupoActivity.this, AddMembersToGrupoActivity.class);
                startActivity(intent);
            }
        });
        ((Button)findViewById(R.id.btn_add_grupo_grupo_info)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddGrupoActivity.this.finish();
                Intent intent = new Intent(AddGrupoActivity.this, GrupoInfoActivity.class);
                startActivity(intent);
            }
        });
        ((Button)findViewById(R.id.btn_add_grupo_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddGrupoActivity.this.finish();
            }
        });

        ((Button)findViewById(R.id.btn_add_grupo_add_grupo)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddGrupoActivity.this.finish();
                Intent intent = new Intent(AddGrupoActivity.this, AddGrupoActivity.class);
                startActivity(intent);
            }
        });


    }

    @NotNull
    private View.OnClickListener callRestCrearGrupo() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //GrupoNewRequestDTO
                GrupoNewRequestDTO request = new GrupoNewRequestDTO();

                Grupo newGrupo = new Grupo(tvAddGrupoName.getText().toString());

                try {
                    AESDTO aesdtoEncript = AESEncrypt.messaging(AESFactory.getAESMessaging());

                    request.setAesDTO(aesdtoEncript);

                    request.setGrupoDTO(newGrupo);

                } catch (Exception e) {
                    e.printStackTrace();
                    SimpleErrorDialog.errorDialog(AddGrupoActivity.this, "ERROR ENCRIPTACION", e.getMessage());
                    return;
                }


                ProtocoloDTO p = new ProtocoloDTO();
                p.setComponent(ProtocoloComponentsEnum.GRUPO);
                p.setAction(ProtocoloActionsEnum.GRUPO_NEW_GRUPO);

                p.setObjectDTO(GsonFormated.get().toJson(request));

                RestExecute.doit(AddGrupoActivity.this, p,
                        new CallbackRest() {

                            @Override
                            public void response(ResponseEntity<ProtocoloDTO> response) {
                                Grupo l = GsonFormated.get().fromJson(response.getBody().getObjectDTO(), Grupo.class);
                                Observers.grupo().addGrupo(response.getBody());
                                SingletonValues.getInstance().setGrupoSeleccionado(GsonFormated.get().fromJson(response.getBody().getObjectDTO(), Grupo.class));


//                                ObservatorGrupos.getInstance().getGrupoUserConfPorId().put(
//                                        SingletonValues.getInstance().getGrupoSeleccionado().getIdGrupo(),
//                                        MessageUtil.getDefaultGrupoUserConf(
//                                                SingletonValues.getInstance().getGrupoSeleccionado().getIdGrupo(),
//                                                SingletonValues.getInstance().getUsuario().getIdUsuario()
//                                        )
//                                );

                                //Toast.makeText(getApplicationContext(), "Grupo " + l.getName() + " Agregado", Toast.LENGTH_SHORT).show();
                                //finish();
                                layoutGrupoCreado.setVisibility(View.VISIBLE);
                                tvMensajeGrupoCreado.setVisibility(View.VISIBLE);
                                btnAddGrupoOk.setVisibility(View.GONE);
                                tvAddGrupoName.setEnabled(false);
                            }

                            @Override
                    public void onError(ResponseEntity<ProtocoloDTO> response) {

                    }

                    @Override
                    public void beforeShowErrorMessage(String msg) {

                    }
                        });


            }
        };
    }

//    private KeyPair createEncryptKey() throws NoSuchAlgorithmException, NoSuchProviderException {
//
//        RSA t = new RSA();
//        KeyPair keyPair = null;
//
//        keyPair = t.generateKeyPair();
//
//        //keyPair.getPrivate().getEncoded();
//        //System.out.println(GsonFormated.get().toJson(keyPair.getPublic().getEncoded()));
//
//        return keyPair;
//
//    }


    public String generateNombre() {
        return RandomStringUtils.randomAlphanumeric(8);
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem itemMenu) {
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}