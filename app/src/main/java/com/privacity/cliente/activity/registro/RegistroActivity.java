package com.privacity.cliente.activity.registro;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.google.gson.GsonBuilder;
import com.privacity.cliente.R;
import com.privacity.cliente.activity.common.GetButtonReady;
import com.privacity.cliente.activity.main.MainActivi2ty;
import com.privacity.cliente.common.component.SecureFieldAndEye;
import com.privacity.cliente.common.constants.IntentConstant;
import com.privacity.cliente.common.error.SimpleErrorDialog;
import com.privacity.cliente.encrypt.AEStoUse;
import com.privacity.cliente.encrypt.AEStoUseFactory;
import com.privacity.cliente.encrypt.EncryptUtil;
import com.privacity.cliente.encrypt.RSA;
import com.privacity.cliente.includes.ProgressBarUtil;
import com.privacity.cliente.includes.SecureFieldAndEyeUtil;
import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.rest.InnerCallbackRest;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.cliente.rest.restcalls.ServerConfRest;
import com.privacity.cliente.singleton.SingletonLoginValues;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.singleton.UtilsStringSingleton;
import com.privacity.cliente.singleton.activity.SingletonCurrentActivity;
import com.privacity.cliente.singleton.serverconfiguration.SingletonServerConfiguration;
import com.privacity.cliente.util.NicknameUtil;
import com.privacity.cliente.util.ValidarUsuarioPassword;
import com.privacity.common.adapters.LocalDateAdapter;
import com.privacity.common.config.ConstantValidation;
import com.privacity.common.dto.AESDTO;
import com.privacity.common.dto.EncryptKeysDTO;
import com.privacity.cliente.model.dto.Protocolo;
import com.privacity.common.dto.request.RegisterUserRequestDTO;
import com.privacity.common.dto.request.ValidateUsernameDTO;
import com.privacity.common.dto.servergralconf.PasswordRulesDTO;
import com.privacity.common.enumeration.ProtocoloActionsEnum;
import com.privacity.common.enumeration.ProtocoloComponentsEnum;
import com.privacity.common.util.RandomNicknameUtil;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;

import java.security.KeyPair;
import java.time.LocalDateTime;

import static com.privacity.cliente.encrypt.EncryptUtil.toHash;

//import com.privacity.common.dto.UsuarioLoginRegisterDTO;

public class RegistroActivity extends AppCompatActivity {

    private static final String TAG = "RegistroActivity";
    public static final String TARGET = "%1$";

    private Button registroCrearUsuario;
    private EditText usernameRegistro;
    private EditText passwordRegistro2;
    private EditText passwordRegistro1;
    private TextView tvRegistroUsernameValidate;
    private boolean usuarioValidacionOK = false;
    private boolean usuarioDisponible = false;

    private boolean password1ValidacionOK = false;
    private final boolean password2ValidacionOK = false;
    private TextView tvRegistroPassword2ValidateEqual;
    private TextView tvRegistroPassword1Validate;

    private ProgressBar pbRegistro;

    private TextView tvRegistroCreando;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState!= null){
   /*When rotation occurs
    Example : time = savedInstanceState.getLong("time_state", 0); */
        } else {
            //When onCreate is called for the first time
        }
        setContentView(R.layout.activity_registro);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.registro_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SingletonCurrentActivity.getInstance().set(this);

        registroCrearUsuario = GetButtonReady.get(this,R.id.tv_registro_aceptar, view -> save());


        usernameRegistro = findViewById(R.id.tv_registro_username);

        tvRegistroUsernameValidate = findViewById(R.id.tv_registro_username_validate);
        passwordRegistro1 = findViewById(R.id.tv_registro_password1);
        passwordRegistro2 = findViewById(R.id.tv_registro_password2);
        tvRegistroCreando = findViewById(R.id.tv_registro_creando);
        pbRegistro = findViewById(R.id.common__progress_bar);
        pbRegistro.setVisibility(View.GONE);
        tvRegistroCreando.setVisibility(View.GONE);


        tvRegistroPassword1Validate = findViewById(R.id.tv_registro_password1_validacion);
        tvRegistroPassword2ValidateEqual = findViewById(R.id.tv_registro_password2_validacion_equal);


        usernameRegistro.setFilters(new InputFilter[]{new InputFilter.LengthFilter(ConstantValidation.USER_USERNAME_MAX_LENGTH)});


        passwordRegistro1.setFilters(new InputFilter[]{new InputFilter.LengthFilter(ConstantValidation.USER_PASSWORD_MAX_LENGTH)});
        passwordRegistro2.setFilters(new InputFilter[]{new InputFilter.LengthFilter(ConstantValidation.USER_PASSWORD_MAX_LENGTH)});


        initListener();


    }

    private void initListener() {
        passwordRegistro2.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                validarPassword1();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        passwordRegistro1.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                validarPassword1();


            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });



        usernameRegistro.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                try {
                    validarUsuarioRest();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });



        SecureFieldAndEye passwordSecureFieldAndEye  = new SecureFieldAndEye(null,passwordRegistro1,
                findViewById(R.id.password_eye_confirmation_show),
                findViewById(R.id.password_eye_confirmation_hide));
        SecureFieldAndEyeUtil.listener(passwordSecureFieldAndEye);

        SecureFieldAndEye passwordSecureFieldAndEye2  = new SecureFieldAndEye(null,passwordRegistro2,
                findViewById(R.id.password_eye_confirmation_show2),
                findViewById(R.id.password_eye_confirmation_hide2));
        SecureFieldAndEyeUtil.listener(passwordSecureFieldAndEye);

    }

    private void save() {
        ProgressBarUtil.changeState(RegistroActivity.this, pbRegistro);
        tvRegistroCreando.setVisibility(pbRegistro.getVisibility());
        validarPassword1();
        if (usuarioValidacionOK && password1ValidacionOK) {
            try {

                callNewUserRest();
            } catch (Exception e) {
                e.printStackTrace();
                ProgressBarUtil.hide(RegistroActivity.this, pbRegistro);
                tvRegistroCreando.setVisibility(pbRegistro.getVisibility());
                SimpleErrorDialog.errorDialog(RegistroActivity.this, getString(R.string.general__error_message_ph1,TAG), e.getMessage());

            }
        } else {
            ProgressBarUtil.changeState(RegistroActivity.this, pbRegistro);
            tvRegistroCreando.setVisibility(pbRegistro.getVisibility());
            Toast toast = Toast.makeText(getApplicationContext(), R.string.registro_validation_error, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void callNewUserRest() {
        tvRegistroCreando.setText("Creando Claves de Encriptación");
        ServerConfRest.getTime(this, newUserCallback(), new InnerCallbackRest() {
            @Override
            public void action(Context context) {
                try {
                    callNewUserRestInnerCallback();
                } catch (Exception e) {
                    e.printStackTrace();
                    ProgressBarUtil.hide(RegistroActivity.this, pbRegistro);
                    tvRegistroCreando.setVisibility(pbRegistro.getVisibility());
                    SimpleErrorDialog.errorDialog(RegistroActivity.this, "ERROR REGISTRO", e.getMessage());


                }
            }
        });
    }

    private void callNewUserRestInnerCallback() throws Exception {

        tvRegistroCreando.setText("Finalizando Configuración");
        AESDTO personalAES = EncryptUtil.createPersonalAES(usernameRegistro.getText().toString());
        AEStoUse personalAEStoUse = AEStoUseFactory.getAEStoUsePersonal(personalAES);

        EncryptKeysDTO keys = createEncryptKeys(personalAEStoUse, usernameRegistro.getText().toString());


        EncryptKeysDTO encriptionCodeEncryptKeys = EncryptUtil.invitationCodeEncryptKeysGenerator(personalAEStoUse);

        Protocolo p = new Protocolo();
        p.setComponent(ProtocoloComponentsEnum.AUTH);
        p.setAction(ProtocoloActionsEnum.AUTH_REGISTER);

        RegisterUserRequestDTO u = new RegisterUserRequestDTO();
        u.setNickname(RandomNicknameUtil.get());
        u.setInvitationCodeEncryptKeysDTO(encriptionCodeEncryptKeys);
        u.setEncryptKeysDTO(keys);


        u.setPassword(EncryptUtil.toHash(passwordRegistro1.getText().toString()));
        u.setUsername(EncryptUtil.toHash(usernameRegistro.getText().toString()));


        p.setObjectDTO(UtilsStringSingleton.getInstance().gsonToSend(u));
        RestExecute.doitPublic(RegistroActivity.this, p, newUserCallback()
                , SingletonLoginValues.getInstance().getAEStoUse(),
                SingletonLoginValues.getInstance().getAEStoSend());

    }

    private CallbackRest newUserCallback() {
        CallbackRest r = new CallbackRest() {

            @Override
            public void response(ResponseEntity<Protocolo> response) {

                ProgressBarUtil.changeState(RegistroActivity.this, pbRegistro);
                tvRegistroCreando.setVisibility(View.GONE);
                AlertDialog dialog = getAlertDialog();
                dialog.show();

            }

            @Override
            public void onError(ResponseEntity<Protocolo> response) {
                tvRegistroCreando.setVisibility(View.GONE);

            }

            @Override
            public void beforeShowErrorMessage(String msg) {
                tvRegistroCreando.setVisibility(View.GONE);

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
                Intent i = new Intent(RegistroActivity.this, MainActivi2ty.class);

                i.putExtra(IntentConstant.USERNAME, usernameRegistro.getText().toString());
                i.putExtra(IntentConstant.PASSWORD, passwordRegistro1.getText().toString());

                RegistroActivity.this.startActivity(i);
                RegistroActivity.this.finish();

                //RegistroActivity.this.finish();
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







    private void validarUsuarioRest() throws Exception {

        if (SingletonServerConfiguration.getInstance().getSystemGralConf()== null || !validateUsername(this, tvRegistroUsernameValidate,SingletonServerConfiguration.getInstance().getSystemGralConf().getPasswordConfig().getUsuario(),usernameRegistro.getText().toString())){
            usuarioValidacionOK=false;
            return;
        }

        Protocolo p = new Protocolo();
        p.setComponent(ProtocoloComponentsEnum.AUTH);
        p.setAction(ProtocoloActionsEnum.AUTH_VALIDATE_USERNAME);

        ValidateUsernameDTO u = new ValidateUsernameDTO();
        u.setUsername(toHash(usernameRegistro.getText().toString()));
        p.setObjectDTO(UtilsStringSingleton.getInstance().gsonToSend(u));


        RestExecute.doitPublic(RegistroActivity.this, p,
                new CallbackRest() {

                    @Override
                    public void response(ResponseEntity<Protocolo> response) {

                        Boolean existe = UtilsStringSingleton.getInstance().gson().fromJson(response.getBody().getObjectDTO(), Boolean.class);
                        validateUsername(RegistroActivity.this, tvRegistroUsernameValidate, SingletonServerConfiguration.getInstance().getSystemGralConf().getPasswordConfig().getUsuario(),usernameRegistro.getText().toString());

                            if (existe) {
                            tvRegistroUsernameValidate.setTextColor(Color.RED);
                            tvRegistroUsernameValidate.setText(RegistroActivity.this.getString(R.string.registro_validation_user_exists) + "\n" +tvRegistroUsernameValidate.getText().toString());
                            usuarioValidacionOK = false;
                            usuarioDisponible=false;
                            } else {
                            tvRegistroUsernameValidate.setTextColor(Color.BLACK);
                            tvRegistroUsernameValidate.setText(RegistroActivity.this.getString(R.string.registro_validation_user_ok) + "\n" +tvRegistroUsernameValidate.getText().toString());
                            usuarioValidacionOK = true;
                            usuarioDisponible=true;
                            }
                        validarPassword1();

                    }

                    @Override
                    public void onError(ResponseEntity<Protocolo> response) {
                        usuarioValidacionOK = false;
                        usuarioDisponible=false;
                    }

                    @Override
                    public void beforeShowErrorMessage(String msg) {

                    }
                }, SingletonLoginValues.getInstance().getAEStoUse(),
                SingletonLoginValues.getInstance().getAEStoSend());

    }


    public boolean validateUsername(Activity activity, TextView validationText, PasswordRulesDTO rulesConfig, String username) {


        return ValidarUsuarioPassword.validateUsername(
                this,tvRegistroUsernameValidate,SingletonServerConfiguration.getInstance().getSystemGralConf().getPasswordConfig().getUsuario()
                , usernameRegistro.getText().toString());
    }

    private void validarPassword1() {
        if ( SingletonServerConfiguration.getInstance().getSystemGralConf() == null || usernameRegistro.getText().toString().trim().equals("")) return;

        password1ValidacionOK = ValidarUsuarioPassword.validatePassword(
                this,tvRegistroPassword1Validate,SingletonServerConfiguration.getInstance().getSystemGralConf().getPasswordConfig().getPasswordUsuarioRegistration()
        , usernameRegistro.getText().toString()
        , passwordRegistro1.getText().toString(), passwordRegistro2.getText().toString(),tvRegistroPassword2ValidateEqual,null,true);
    }
/*
public void givenStringPassword_whenUsingDynamicPasswordValidationRules_thenCheckIfPasswordValid() {
    boolean result = false;
    try {
        if (password != null) {
            String MIN_LENGTH = "8";
            String MAX_LENGTH = "20";
            boolean SPECIAL_CHAR_NEEDED = false;

            String ONE_DIGIT = "(?=.*[0-9])";
            String LOWER_CASE = "(?=.*[a-z])";
            String UPPER_CASE = "(?=.*[A-Z])";
            String SPECIAL_CHAR = SPECIAL_CHAR_NEEDED ? "(?=.*[@#$%^&+=])" : "";
            String NO_SPACE = "(?=\\S+$)";

            String MIN_MAX_CHAR = ".{" + MIN_LENGTH + "," + MAX_LENGTH + "}";
            String PATTERN = ONE_DIGIT + LOWER_CASE + UPPER_CASE + SPECIAL_CHAR + NO_SPACE + MIN_MAX_CHAR;

            assertTrue(password.matches(PATTERN));
        }

    } catch (Exception ex) {
        ex.printStackTrace();
        fail("Exception occurred: " + ex.getMessage());
    }
}
 */
    private EncryptKeysDTO createEncryptKeys(AEStoUse personalAEStoUse, String username) throws Exception {

        RSA t = new RSA();
        KeyPair keyPair = null;

        keyPair = t.generateKeyPair();

        byte[] privateKey = keyPair.getPrivate().getEncoded();
        byte[] publicKey = keyPair.getPublic().getEncoded();

//        MixBytesUtil mix = new MixBytesUtil();
//        privateKey = mix.mix(privateKey, username.length());

        SingletonValues.getInstance().pkRegistro = keyPair.getPublic();
        SingletonValues.getInstance().privateRegistro = keyPair.getPrivate();

        EncryptKeysDTO encryptKeysDTO = new EncryptKeysDTO();


        byte[] privateKeyEncrypt = personalAEStoUse.getAESData(privateKey);
        byte[] publicKeyEncrypt = personalAEStoUse.getAESData(publicKey);
        //Base64.getDecoder().decode(
        String privateKeyComplete = gsonToSend(privateKeyEncrypt);
        String publicKeyComplete = gsonToSend(publicKeyEncrypt);
        String publicKeyNoEncryptComplete = gsonToSend(publicKey);

//    String privateKeyComplete = Base64.getEncoder().withoutPadding().encodeToString(privateKeyEncrypt);
//    String publicKeyComplete = Base64.getEncoder().withoutPadding().encodeToString(publicKeyEncrypt);
//    String publicKeyNoEncryptComplete =Base64.getEncoder().withoutPadding().encodeToString(publicKey);

        encryptKeysDTO.setPrivateKey(privateKeyComplete);
        encryptKeysDTO.setPublicKey(publicKeyComplete);
        encryptKeysDTO.setPublicKeyNoEncrypt(publicKeyNoEncryptComplete);
        return encryptKeysDTO;
    }

    public static String gsonToSend(Object s) {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
                .create().toJson(s).replace("\n", "").replace("\t", "").replace("\r", "").replace("\b", "").replace("\f", "")
                .replace(" ", "");

    }
}