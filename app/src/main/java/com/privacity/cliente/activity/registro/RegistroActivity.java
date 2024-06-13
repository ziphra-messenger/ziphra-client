package com.privacity.cliente.activity.registro;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.privacity.cliente.R;
import com.privacity.cliente.common.error.SimpleErrorDialog;
import com.privacity.cliente.encrypt.AEStoUseFactory;
import com.privacity.cliente.singleton.SingletonLoginValues;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.util.GsonFormated;
import com.privacity.cliente.encrypt.AEStoUse;
import com.privacity.cliente.encrypt.EncryptUtil;
import com.privacity.cliente.encrypt.RSA;
import com.privacity.cliente.includes.ProgressBarUtil;
import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.rest.InnerCallbackRest;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.cliente.rest.restcalls.ServerConfRest;
import com.privacity.cliente.util.NicknameUtil;
import com.privacity.common.config.ConstantProtocolo;
import com.privacity.common.config.ConstantValidation;
import com.privacity.common.dto.AESDTO;
import com.privacity.common.dto.EncryptKeysDTO;
import com.privacity.common.dto.ProtocoloDTO;
import com.privacity.common.dto.request.RegisterUserRequestDTO;
import com.privacity.common.dto.request.ValidateUsernameDTO;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;

import java.security.KeyPair;

import static com.privacity.cliente.encrypt.EncryptUtil.toHash;

//import com.privacity.common.dto.UsuarioLoginRegisterDTO;

public class RegistroActivity extends AppCompatActivity {


    public static final String TARGET = "%1$";
    private Button registroCrearUsuario;
    private EditText usernameRegistro;
    private EditText passwordRegistro2;
    private EditText passwordRegistro1;
    private TextView tvRegistroUsernameValidate;
    private boolean usuarioValidacionOK=false;
    private boolean nicknameValidacionOK=false;
    private boolean password1ValidacionOK=false;
    private boolean password2ValidacionOK=false;
    private TextView tvRegistroPassword2ValidateEqual;
    private TextView tvRegistroPassword1Validate;
    private EditText tvRegistroNickname;
    private ProgressBar pbRegistro;
    private TextView tvRegistroNicknameValidate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.registro_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        registroCrearUsuario = (Button) findViewById(R.id.tv_registro_aceptar);

        usernameRegistro = (EditText) findViewById(R.id.tv_registro_username);
        tvRegistroNickname = (EditText) findViewById(R.id.tv_registro_nickname);
        tvRegistroNicknameValidate = (TextView) findViewById(R.id.tv_registro_nickname_validate);
        tvRegistroUsernameValidate = (TextView) findViewById(R.id.tv_registro_username_validate);
        passwordRegistro1 = (EditText) findViewById(R.id.tv_registro_password1);
        passwordRegistro2 = (EditText) findViewById(R.id.tv_registro_password2);

        pbRegistro = (android.widget.ProgressBar) findViewById(R.id.gral_progress_bar);
        pbRegistro.setVisibility(View.GONE);

        tvRegistroPassword1Validate = (TextView) findViewById(R.id.tv_registro_password1_validacion);
        tvRegistroPassword2ValidateEqual = (TextView) findViewById(R.id.tv_registro_password2_validacion_equal);


        usernameRegistro.setFilters(new InputFilter[]{new InputFilter.LengthFilter(ConstantValidation.USER_USERNAME_MAX_LENGTH)});
        NicknameUtil.setNicknameMaxLenght(tvRegistroNickname);

        passwordRegistro1.setFilters(new InputFilter[]{new InputFilter.LengthFilter(ConstantValidation.USER_PASSWORD_MAX_LENGTH)});
        passwordRegistro2.setFilters(new InputFilter[]{new InputFilter.LengthFilter(ConstantValidation.USER_PASSWORD_MAX_LENGTH)});

        initListener();


    }

    private void initListener() {
        passwordRegistro2.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                validarPassword2();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        passwordRegistro1.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                validarPassword1();
                if (password2ValidacionOK) validarPassword2();

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        tvRegistroNickname.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                validarNickname();

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        usernameRegistro.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                if ( validarUsuario()){

                        new Runnable(){

                            @Override
                            public void run() {
                                try {
                                validarUsuarioRest();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }.run();


                };

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        registroCrearUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ProgressBarUtil.changeState(RegistroActivity.this, pbRegistro);

                validarUsuario();
                validarNickname();
                validarPassword1();
                validarPassword2();
                if (usuarioValidacionOK && nicknameValidacionOK && password1ValidacionOK && password2ValidacionOK){
                    try {

                        callNewUserRest();
                    } catch (Exception e) {
                        e.printStackTrace();
                        ProgressBarUtil.hide(RegistroActivity.this, pbRegistro);
                        SimpleErrorDialog.errorDialog(RegistroActivity.this, "ERROR REGISTRO", e.getMessage());

                    }
                }else{
                    ProgressBarUtil.changeState(RegistroActivity.this, pbRegistro);
                    Toast toast=Toast. makeText(getApplicationContext(),R.string.registro_validation_error,Toast. LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }

    private void callNewUserRest() {

        ServerConfRest.getTime(this, newUserCallback(), new InnerCallbackRest() {
            @Override
            public void action(Context context) {
                try {
                    callNewUserRestInnerCallback();
                } catch (Exception e) {
                    e.printStackTrace();
                    ProgressBarUtil.hide(RegistroActivity.this, pbRegistro);
                    SimpleErrorDialog.errorDialog(RegistroActivity.this, "ERROR REGISTRO", e.getMessage());


                }
            }
        });
    }

    private void callNewUserRestInnerCallback() throws Exception {

        AESDTO personalAES = EncryptUtil.createPersonalAES(usernameRegistro.getText().toString());
        AEStoUse personalAEStoUse = AEStoUseFactory.getAEStoUsePersonal(personalAES.getSecretKeyAES(), personalAES.getSaltAES());

        EncryptKeysDTO keys = createEncryptKeys(personalAEStoUse);



        EncryptKeysDTO encriptionCodeEncryptKeys = EncryptUtil.invitationCodeEncryptKeysGenerator(personalAEStoUse);

        ProtocoloDTO p = new ProtocoloDTO();
        p.setComponent(ConstantProtocolo.PROTOCOLO_COMPONENT_AUTH);
        p.setAction(ConstantProtocolo.PROTOCOLO_ACTION_AUTH_REGISTER);

        RegisterUserRequestDTO u = new RegisterUserRequestDTO();
        u.setInvitationCodeEncryptKeysDTO(encriptionCodeEncryptKeys);
        u.setEncryptKeysDTO(keys);

        u.setNickname(tvRegistroNickname.getText().toString());
        u.setPassword(EncryptUtil.toHash(passwordRegistro1.getText().toString()));
        u.setUsername(EncryptUtil.toHash(usernameRegistro.getText().toString()));

        p.setObjectDTO(GsonFormated.get().toJson(u));
        RestExecute.doitPublic(RegistroActivity.this, p,newUserCallback()
                , SingletonLoginValues.getInstance().getAEStoUse(),
                SingletonLoginValues.getInstance().getAEStoSend());

    }

    private CallbackRest newUserCallback(){
        CallbackRest r = new CallbackRest(){

            @Override
            public void response(ResponseEntity<ProtocoloDTO> response) {

                ProgressBarUtil.changeState(RegistroActivity.this, pbRegistro);
                AlertDialog dialog = getAlertDialog();
                dialog.show();

            }

            @Override
            public void onError(ResponseEntity<ProtocoloDTO> response) {


            }

            @Override
            public void beforeShowErrorMessage(String msg) {
                ProgressBarUtil.changeState(RegistroActivity.this, pbRegistro);
            }

        };
        return r;
    }
    @NotNull
    private AlertDialog getAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RegistroActivity.this);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                RegistroActivity.this.finish();
            }
        });

        builder.setMessage(R.string.registro_user_created);
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                RegistroActivity.this.finish();
            }
        });
        AlertDialog dialog = builder.create();
        return dialog;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem itemMenu) {
        finish();
        return true;
    }

    private void validarPassword2() {
        if (password1ValidacionOK) {
            if (!passwordRegistro1.getText().toString().equals(passwordRegistro2.getText().toString())) {
                tvRegistroPassword2ValidateEqual.setTextColor(Color.RED);
                tvRegistroPassword2ValidateEqual.setText(R.string.registro_validation_password2);
                password2ValidacionOK = false;
                return;
            }

            tvRegistroPassword2ValidateEqual.setTextColor(Color.BLACK);
            tvRegistroPassword2ValidateEqual.setText(R.string.registro_validation_password2_ok);
            password2ValidacionOK = true;
        }else{
            tvRegistroPassword2ValidateEqual.setText("");
            password2ValidacionOK = false;
        }
    }

    private boolean validarUsuario() {

        if (usernameRegistro.getText().toString().equals("")){
            tvRegistroUsernameValidate.setTextColor(Color.RED);
            tvRegistroUsernameValidate.setText(R.string.registro_validation_user_empty);
            usuarioValidacionOK=false;
            return false;
        }else if (usernameRegistro.getText().toString().length() < ConstantValidation.USER_USERNAME_MIN_LENGTH){
            tvRegistroUsernameValidate.setTextColor(Color.RED);
            tvRegistroUsernameValidate.setText(getResources().getString(R.string.registro_validation_user_too_short).replace(TARGET, String.valueOf(ConstantValidation.USER_USERNAME_MIN_LENGTH)));
            usuarioValidacionOK=false;
            return false;
        } else if (usernameRegistro.getText().toString().length() > ConstantValidation.USER_USERNAME_MAX_LENGTH){
            tvRegistroUsernameValidate.setTextColor(Color.RED);
            tvRegistroUsernameValidate.setText((getResources().getString(R.string.registro_validation_user_too_long).replace(TARGET, String.valueOf(ConstantValidation.USER_USERNAME_MAX_LENGTH))));
            usuarioValidacionOK=false;
            return false;
        }
        String busqueda;
        Object numEncontrados;
        String txt = getResources().getString(R.string.registro_validation_user_too_short).replace(TARGET, "3");
        String msg = String.format(txt);

        tvRegistroUsernameValidate.setText("");
        return true;


    }

    private void validarNickname() {

        if (tvRegistroNickname.getText().toString().trim().equals("")){
            tvRegistroNickname.setText(NicknameUtil.generateRandomNickname());
        } else if (tvRegistroNickname.getText().toString().length() > ConstantValidation.USER_NICKNAME_MAX_LENGTH){
            tvRegistroNicknameValidate.setTextColor(Color.RED);
            tvRegistroNicknameValidate.setText(R.string.registro_validation_nickname_too_long);
            nicknameValidacionOK=false;
            return;
        }
        tvRegistroNicknameValidate.setText("");
        nicknameValidacionOK=true;
    }



    private void validarUsuarioRest() {

        ServerConfRest.getTime(this, null, new InnerCallbackRest() {
            @Override
            public void action(Context context) {
                try {
                    validarUsuarioRestInnerCallback();
                } catch (Exception e) {
                    e.printStackTrace();
                    ProgressBarUtil.hide(RegistroActivity.this, pbRegistro);
                    SimpleErrorDialog.errorDialog(RegistroActivity.this, "ERROR REGISTRO", e.getMessage());

                }
            }
        });
    }
    private void validarUsuarioRestInnerCallback() throws Exception {

        ProtocoloDTO p = new ProtocoloDTO();
        p.setComponent(ConstantProtocolo.PROTOCOLO_COMPONENT_AUTH);
        p.setAction(ConstantProtocolo.PROTOCOLO_ACTION_AUTH_VALIDATE_USERNAME);

        ValidateUsernameDTO u = new ValidateUsernameDTO();
        u.setUsername(toHash(usernameRegistro.getText().toString()));
        p.setObjectDTO(GsonFormated.get().toJson(u));


        RestExecute.doitPublic(RegistroActivity.this, p,
                new CallbackRest(){

                    @Override
                    public void response(ResponseEntity<ProtocoloDTO> response) {

                            Boolean existe = GsonFormated.get().fromJson(response.getBody().getObjectDTO(),Boolean.class);
                            if ( existe){
                                tvRegistroUsernameValidate.setTextColor(Color.RED);
                                tvRegistroUsernameValidate.setText(R.string.registro_validation_user_exists);
                                usuarioValidacionOK=false;
                            }else{
                                tvRegistroUsernameValidate.setTextColor(Color.BLACK);
                                tvRegistroUsernameValidate.setText(R.string.registro_validation_user_ok);
                                usuarioValidacionOK=true;
                            }

                    }

                    @Override
                    public void onError(ResponseEntity<ProtocoloDTO> response) {
                        usuarioValidacionOK=false;
                    }

                    @Override
                    public void beforeShowErrorMessage(String msg) {

                    }
                }, SingletonLoginValues.getInstance().getAEStoUse(),
                SingletonLoginValues.getInstance().getAEStoSend());

    }

    private void validarPassword1() {
        if (passwordRegistro1.getText().toString().equals("")){
            tvRegistroPassword1Validate.setTextColor(Color.RED);
            tvRegistroPassword1Validate.setText(R.string.registro_validation_password1_empty);
            password1ValidacionOK=false;
            return;
        }else if (passwordRegistro1.getText().toString().length() < ConstantValidation.USER_PASSWORD_MIN_LENGTH){
            tvRegistroPassword1Validate.setTextColor(Color.RED);
            tvRegistroPassword1Validate.setText(R.string.registro_validation_password1_too_short);
            password1ValidacionOK=false;
            return;
        }else if (passwordRegistro1.getText().toString().length() > ConstantValidation.USER_PASSWORD_MAX_LENGTH){
            tvRegistroPassword1Validate.setTextColor(Color.RED);
            tvRegistroPassword1Validate.setText(R.string.registro_validation_password1_too_long);
            password1ValidacionOK=false;
            return;
        }

        tvRegistroPassword1Validate.setTextColor(Color.BLACK);
        tvRegistroPassword1Validate.setText(R.string.registro_validation_password1_ok);
        password1ValidacionOK=true;
    }

    private EncryptKeysDTO createEncryptKeys(AEStoUse personalAEStoUse) throws Exception {
        RSA t = new RSA();
        KeyPair keyPair = null;

        keyPair = t.generateKeyPair();

    byte[] privateKey = keyPair.getPrivate().getEncoded();
    byte[] publicKey = keyPair.getPublic().getEncoded();

    SingletonValues.getInstance().pkRegistro = keyPair.getPublic();
    SingletonValues.getInstance().privateRegistro = keyPair.getPrivate();

    EncryptKeysDTO encryptKeysDTO =  new EncryptKeysDTO();




    byte[] privateKeyEncrypt = personalAEStoUse.getAES(privateKey);
    byte[] publicKeyEncrypt = personalAEStoUse.getAES(publicKey);
        //Base64.getDecoder().decode(
    String privateKeyComplete = GsonFormated.get().toJson(privateKeyEncrypt);
    String publicKeyComplete = GsonFormated.get().toJson(publicKeyEncrypt);
    String publicKeyNoEncryptComplete =GsonFormated.get().toJson(publicKey);

//    String privateKeyComplete = Base64.getEncoder().withoutPadding().encodeToString(privateKeyEncrypt);
//    String publicKeyComplete = Base64.getEncoder().withoutPadding().encodeToString(publicKeyEncrypt);
//    String publicKeyNoEncryptComplete =Base64.getEncoder().withoutPadding().encodeToString(publicKey);

    encryptKeysDTO.setPrivateKey(privateKeyComplete);
    encryptKeysDTO.setPublicKey(publicKeyComplete);
    encryptKeysDTO.setPublicKeyNoEncrypt(publicKeyNoEncryptComplete);
    return encryptKeysDTO;
    }


}