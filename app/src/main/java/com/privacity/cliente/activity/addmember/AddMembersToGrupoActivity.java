package com.privacity.cliente.activity.addmember;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;

import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;
import com.privacity.cliente.R;
import com.privacity.cliente.activity.common.CustomAppCompatActivity;
import com.privacity.cliente.activity.common.GetButtonReady;
import com.privacity.cliente.activity.message.RestCalls;
import com.privacity.cliente.common.error.SimpleErrorDialog;
import com.privacity.cliente.encrypt.RSA;
import com.privacity.cliente.model.Grupo;
import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.cliente.singleton.Observers;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.singleton.UtilsStringSingleton;
import com.privacity.cliente.singleton.interfaces.ObservadoresPassword;
import com.privacity.cliente.util.CopyPasteUtil;
import com.privacity.cliente.util.RoleUtil;
import com.privacity.common.config.ConstantValidation;
import com.privacity.common.dto.AESDTO;
import com.privacity.common.dto.EncryptKeysDTO;
import com.privacity.cliente.model.dto.Protocolo;
import com.privacity.common.dto.request.GrupoAddUserRequestDTO;
import com.privacity.common.dto.request.PublicKeyByInvitationCodeRequestDTO;
import com.privacity.common.enumeration.ProtocoloActionsEnum;
import com.privacity.common.enumeration.ProtocoloComponentsEnum;
import com.privacity.common.exceptions.PrivacityException;

import org.springframework.http.ResponseEntity;

import java.security.PublicKey;
import java.util.Base64;

public class AddMembersToGrupoActivity extends CustomAppCompatActivity implements ObservadoresPassword
{
    private static final String TAG = "AddMembersToGrupoActivity";
    private static final String CONSTANT__CALL_ADD_USER_REST = "CallAddUserRest";
    private EditText codInvitation;
    private Spinner roles;
    private TextView validation;
    private boolean otraInvitacion=false;
    private EditText invitationMessage;
    private TextView messageCounter;
    private ImageButton paste;
    Button leerqr;
    Button aceptar;
    Button aceptarInvitarOtro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_members_to_grupo);

        initActionBar();

        Observers.password().suscribirse(this);

        initView();

        initListeners();

    }

    private void initListeners() {
        paste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codInvitation.setText(CopyPasteUtil.readFromClipboard(AddMembersToGrupoActivity.this));
            }
        });


        aceptarInvitarOtro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otraInvitacion=true;
                getAceptarListener();
            }
        });

        CopyPasteUtil.setListenerIconClearText(codInvitation);

        leerqr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(AddMembersToGrupoActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                integrator.setOrientationLocked(false);
                integrator.initiateScan();
            }
        });


        invitationMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                messageCounter.setText(
                        getString(R.string.addmember_activity__invitation_message__validate__caracteres_disponibles,""+(ConstantValidation.ADD_MEMBRES_MESSAGE_MAX_LENGTH - invitationMessage.getText().toString().length())));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    private void initView() {
        leerqr = GetButtonReady.get(this,R.id.leerqr);
        aceptar =  GetButtonReady.get(this,R.id.bt_add_member_aceptar, v -> {
            otraInvitacion=false;
            getAceptarListener();
        });
        aceptarInvitarOtro =GetButtonReady.get(this,R.id.bt_add_member_aceptar_e_invitar, v -> {
            otraInvitacion=true;
            getAceptarListener();
        });
        paste = (ImageButton) findViewById(R.id.ib_add_member_to_grupo_paste);





        roles = (Spinner) findViewById(R.id.add_member_roles);
        validation = (TextView) findViewById(R.id.add_member_cod_invitation_validation);
        validation.setTextColor(Color.RED);
        codInvitation = (EditText) findViewById(R.id.add_member_cod_invitation);
        invitationMessage = (EditText) findViewById(R.id.add_members_message);
        invitationMessage.setFilters(new InputFilter[]{new InputFilter.LengthFilter(ConstantValidation.ADD_MEMBRES_MESSAGE_MAX_LENGTH)});
        messageCounter = (TextView) findViewById(R.id.add_members_message_counter);

        roles.setSelection(1);
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) return;
        //actionBar.setTitle("Grupo: " + SingletonValues.getInstance().getGrupoSeleccionado().getName());
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void getAceptarListener() {

        try {
            if (!validarCodInv()) return;
            CallAddUserRest(codInvitation.getText().toString());

        } catch (Exception e) {
            e.printStackTrace();

            SimpleErrorDialog.errorDialog(AddMembersToGrupoActivity.this,
                    AddMembersToGrupoActivity.this.getString(R.string.general__error__call_rest__title, CONSTANT__CALL_ADD_USER_REST), e.getMessage());
        }
    }

    private boolean validarCodInv() {
        String error = "";
        if (!codInvitation.getText().toString().trim().equals("")) {
            validation.setText(error);
            return true;
        } else {
            error = getString(R.string.addmember_activity__invitation_code__validate__empty);

            validation.setText(error);
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case IntentIntegrator.REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {

                    String contents = data.getStringExtra(Intents.Scan.RESULT);
                    Log.d(TAG, "OK");
                    Log.d(TAG, "RESULT CONTENT : " + contents);
                    Log.d(TAG, "-----------");
                    codInvitation.setText(contents);
                    System.out.println(contents);
                    //mResult.setText(contents);
                } else {
                    Log.d(TAG, "NOT OK");
                }
                break;
            default:
                Log.d(TAG, "NOT RESULT CODE");
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

    private void CallAddUserRest(String invitationCode) throws PrivacityException {
        Grupo g = SingletonValues.getInstance().getGrupoSeleccionado();
        getPublicKeyByInvitationCodeRest(g.getIdGrupo(), invitationCode);
    }

    public void getPublicKeyByInvitationCodeRest(String idGrupo, String invitationCode) throws PrivacityException {
        Protocolo p = new Protocolo();
        p.setComponent(ProtocoloComponentsEnum.ENCRYPT_KEYS);
        p.setAction(ProtocoloActionsEnum.ENCRYPT_KEYS_GET);

        PublicKeyByInvitationCodeRequestDTO o = new PublicKeyByInvitationCodeRequestDTO(idGrupo, invitationCode);
        p.setObjectDTO(UtilsStringSingleton.getInstance().gsonToSend(o));

        RestExecute.doit(this, p,
                new CallbackRest() {

                    @Override
                    public void response(ResponseEntity<Protocolo> response) {
                        EncryptKeysDTO encryptKeysDTO = UtilsStringSingleton.getInstance().gson().fromJson(response.getBody().getObjectDTO(), EncryptKeysDTO.class);
                        try {

                            addUserRest(idGrupo, invitationCode, invitationMessage.getText().toString(), encryptKeysDTO);
                        } catch (Exception e) {
                            e.printStackTrace();
                            SimpleErrorDialog.errorDialog(AddMembersToGrupoActivity.this,
                                    getString(R.string.general__error_message_ph1,TAG), e.getMessage());
                        }

                    }

                    @Override
                    public void onError(ResponseEntity<Protocolo> response) {

                    }

                    @Override
                    public void beforeShowErrorMessage(String msg) {

                    }
                });

    }

    public void addUserRest(String idGrupo, String invitationCode, String invitationMessage, EncryptKeysDTO encryptKeysDTO) throws Exception {

        Grupo g = Observers.grupo().getGrupoById(idGrupo);

        PublicKey pk = RestCalls.getPublicKey(encryptKeysDTO);
        AESDTO aesGrupoDTO = RestCalls.encriptarAES(pk, g);


        Protocolo p = new Protocolo();
        p.setComponent(ProtocoloComponentsEnum.GRUPO);
        p.setAction(ProtocoloActionsEnum.GRUPO_SENT_INVITATION);

        GrupoAddUserRequestDTO grupoAddUserRequestDTO = new GrupoAddUserRequestDTO();
        grupoAddUserRequestDTO.setIdGrupo(idGrupo);
        grupoAddUserRequestDTO.setInvitationCode(invitationCode);
        grupoAddUserRequestDTO.setAesDTO(aesGrupoDTO);
        grupoAddUserRequestDTO.setAesDTO(aesGrupoDTO);
        Log.d(TAG, "invitationMessage: " + invitationMessage);
       // if (invitationMessage != null && !invitationMessage.trim().equals("")){
            RSA t = new RSA();

            byte[] enc = t.encryptFilePublic(invitationMessage.getBytes(), pk);
            grupoAddUserRequestDTO.setInvitationMessage(Base64.getEncoder().encodeToString(enc));
        //}
        Log.d(TAG, "setInvitationMessage envt: " + grupoAddUserRequestDTO.getInvitationMessage());
        grupoAddUserRequestDTO.setRole(RoleUtil.transformRole(roles));
        p.setObjectDTO(UtilsStringSingleton.getInstance().gsonToSend(grupoAddUserRequestDTO));

        RestExecute.doit(this, p,
                new CallbackRest() {

                    @Override
                    public void response(ResponseEntity<Protocolo> response) {
                        Toast toast=Toast. makeText(AddMembersToGrupoActivity.this,getString(R.string.addmember_activity__send_invitation__success),Toast. LENGTH_SHORT);
                        toast.show();
                        AddMembersToGrupoActivity.this.finish();
                        if (otraInvitacion){
                            Intent intent = new Intent(AddMembersToGrupoActivity.this, AddMembersToGrupoActivity.class);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onError(ResponseEntity<Protocolo> response) {

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