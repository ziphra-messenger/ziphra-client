package com.privacity.cliente.activity.addmember;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;

import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;
import com.privacity.cliente.R;
import com.privacity.cliente.activity.common.CustomAppCompatActivity;
import com.privacity.cliente.activity.message.RestCalls;
import com.privacity.cliente.common.error.SimpleErrorDialog;
import com.privacity.cliente.model.Grupo;
import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.cliente.singleton.Observers;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.singleton.interfaces.ObservadoresPassword;
import com.privacity.cliente.util.GsonFormated;
import com.privacity.common.config.ConstantProtocolo;
import com.privacity.common.config.ConstantValidation;
import com.privacity.common.dto.AESDTO;
import com.privacity.common.dto.EncryptKeysDTO;
import com.privacity.common.dto.ProtocoloDTO;
import com.privacity.common.dto.request.GrupoAddUserRequestDTO;
import com.privacity.common.dto.request.PublicKeyByInvitationCodeRequestDTO;
import com.privacity.common.enumeration.GrupoRolesEnum;

import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class AddMembersToGrupoActivity extends CustomAppCompatActivity implements ObservadoresPassword
{

    private TextView codInvitation;
    private Spinner roles;
    private TextView validation;
    private boolean otraInvitacion=false;
    private EditText message;
    private TextView messageCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_members_to_grupo);

        ActionBar actionBar = getSupportActionBar();
        //actionBar.setTitle("Grupo: " + SingletonValues.getInstance().getGrupoSeleccionado().getName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Observers.password().suscribirse(this);
        Button leerqr = (Button) findViewById(R.id.leerqr);
        Button aceptar = (Button) findViewById(R.id.bt_add_member_aceptar);
        Button aceptarInvitarOtro = (Button) findViewById(R.id.bt_add_member_aceptar_e_invitar);

        roles = (Spinner) findViewById(R.id.add_member_roles);
        validation = (TextView) findViewById(R.id.add_member_cod_invitation_validation);

        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otraInvitacion=false;
                getAceptarListener();
            }
        });
        aceptarInvitarOtro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otraInvitacion=true;
                getAceptarListener();
            }
        });
        codInvitation = (TextView) findViewById(R.id.add_member_cod_invitation);
        leerqr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(AddMembersToGrupoActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                integrator.setOrientationLocked(false);
                integrator.initiateScan();
                ;
            }
        });

        message = (EditText) findViewById(R.id.add_members_message);
        message.setFilters(new InputFilter[]{new InputFilter.LengthFilter(ConstantValidation.ADD_MEMBRES_MESSAGE_MAX_LENGTH)});

        messageCounter = (TextView) findViewById(R.id.add_members_message_counter);
        message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                messageCounter.setText("Caracteres disponibles "+(ConstantValidation.ADD_MEMBRES_MESSAGE_MAX_LENGTH - message.getText().toString().length()) );
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        
    }

    private void getAceptarListener() {
        try {
            if (!validarCodInv()) return;
            CallAddUserRest(codInvitation.getText().toString());

        } catch (Exception e) {
            e.printStackTrace();

            SimpleErrorDialog.errorDialog(AddMembersToGrupoActivity.this, "ERROR CallAddUserRest ", e.getMessage());
        }
    }

    private boolean validarCodInv() {
        String error = "";
        if (codInvitation.getText().toString().trim() == null) {
            error = "El codigo de invitacion no puede estar vacio";
            validation.setText(error);
            return false;
        }
        validation.setText(error);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case IntentIntegrator.REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {

                    String contents = data.getStringExtra(Intents.Scan.RESULT);
                    Log.d("TAG", "OK");
                    Log.d("TAG", "RESULT CONTENT : " + contents);
                    Log.d("TAG", "-----------");
                    codInvitation.setText(contents);
                    System.out.println(contents);
                    //mResult.setText(contents);
                } else {
                    Log.d("TAG", "NOT OK");
                }
                break;
            default:
                Log.d("TAG", "NOT RESULT CODE");
        }
    }

    @Override
    public void finish() {
        super.finish();
        Observers.password().remove(this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem itemMenu) {
        finish();
        return true;
    }

    @Override
    public boolean isOnlyAdmin(){
        return true;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    private void CallAddUserRest(String invitationCode) throws GeneralSecurityException, IOException {
        Grupo g = SingletonValues.getInstance().getGrupoSeleccionado();
        getPublicKeyByInvitationCodeRest(g.getIdGrupo(), invitationCode);
    }

    public void getPublicKeyByInvitationCodeRest(String idGrupo, String invitationCode) {
        ProtocoloDTO p = new ProtocoloDTO();
        p.setComponent(ConstantProtocolo.PROTOCOLO_COMPONENT_ENCRYPT_KEYS);
        p.setAction(ConstantProtocolo.PROTOCOLO_ACTION_ENCRYPT_KEYS_GET);

        PublicKeyByInvitationCodeRequestDTO o = new PublicKeyByInvitationCodeRequestDTO(idGrupo, invitationCode);
        p.setObjectDTO(GsonFormated.get().toJson(o));

        RestExecute.doit(this, p,
                new CallbackRest() {

                    @Override
                    public void response(ResponseEntity<ProtocoloDTO> response) {
                        EncryptKeysDTO encryptKeysDTO = GsonFormated.get().fromJson(response.getBody().getObjectDTO(), EncryptKeysDTO.class);
                        try {
                            addUserRest(idGrupo, invitationCode, encryptKeysDTO);
                        } catch (Exception e) {
                            e.printStackTrace();
                            SimpleErrorDialog.errorDialog(AddMembersToGrupoActivity.this, "ERROR ADD USER REST", e.getMessage());
                            return;
                        }

                    }

                    @Override
                    public void onError(ResponseEntity<ProtocoloDTO> response) {

                    }

                    @Override
                    public void beforeShowErrorMessage(String msg) {

                    }
                });

    }

    public void addUserRest(String idGrupo, String invitationCode, EncryptKeysDTO encryptKeysDTO) throws Exception {

        Grupo g = Observers.grupo().getGrupoById(idGrupo);

        AESDTO aesGrupoDTO = RestCalls.encriptarAES(g, encryptKeysDTO);

        ProtocoloDTO p = new ProtocoloDTO();
        p.setComponent("/grupo");
        p.setAction("/grupo/sentInvitation");

        GrupoAddUserRequestDTO o = new GrupoAddUserRequestDTO();
        o.setIdGrupo(idGrupo);
        o.setInvitationCode(invitationCode);
        o.setAesDTO(aesGrupoDTO);


        String rol = roles.getSelectedItem().toString();
        if (rol.equals(getString(R.string.rol_miembro))){
            o.setRole(GrupoRolesEnum.MEMBER);
        }else if (rol.equals(getString(R.string.rol_admin))){
            o.setRole(GrupoRolesEnum.ADMIN);
        }else if (rol.equals(getString(R.string.rol_lectura))){
            o.setRole(GrupoRolesEnum.READONLY);
        }else {
            throw new Exception("NO EXISTE EL ROL");
        }
        p.setObjectDTO(GsonFormated.get().toJson(o));

        RestExecute.doit(this, p,
                new CallbackRest() {

                    @Override
                    public void response(ResponseEntity<ProtocoloDTO> response) {
                        Toast toast=Toast. makeText(AddMembersToGrupoActivity.this,"El Usuario ha sido Invitado",Toast. LENGTH_SHORT);
                        toast.show();
                        AddMembersToGrupoActivity.this.finish();
                        if (otraInvitacion){
                            Intent intent = new Intent(AddMembersToGrupoActivity.this, AddMembersToGrupoActivity.class);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onError(ResponseEntity<ProtocoloDTO> response) {

                    }

                    @Override
                    public void beforeShowErrorMessage(String msg) {

                    }
                });

    }

    @Override
    public void passwordExpired() {
        if (SingletonValues.getInstance().getGrupoSeleccionado().getPassword().isEnabled()){
            this.finish();
        }
    }
    @Override
    public void passwordSet() {

    }
}