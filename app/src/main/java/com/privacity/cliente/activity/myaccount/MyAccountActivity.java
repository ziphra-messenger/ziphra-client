package com.privacity.cliente.activity.myaccount;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;

import com.privacity.cliente.activity.common.CustomAppCompatActivity;
import com.privacity.cliente.R;

public class MyAccountActivity extends CustomAppCompatActivity {

    public ProgressBar progressBar;

    private MyAccountConfiguracionGeneralFrame configuracionGeneral;
    private MyAccountPasswordFrame password;
    private MyAccountNicknameFrame nickname;
    private MyAccountLockFrame lock;

    private MyAccountLoginSkipFrame loginSkip;

    @Override
    protected boolean isOnlyAdmin() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Mi Cuenta");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = (ProgressBar) findViewById(R.id.gral_progress_bar);
        progressBar.setVisibility(View.GONE);
        iniciarLock();
        iniciarConfiguracion();
        iniciarPassword();
        iniciarNickname();
        iniciarLoginSkip();



//        etMyAccountNickname = (EditText)findViewById(R.id.grupoinfo_name_grupo_name);
//        etMyAccountNickname.setText(SingletonValues.getInstance().getUsuario().getNickname());
//
//        spinnerTime = (Spinner) findViewById(R.id.sp_message_avanzado_time_values);
//
//        btMyAccountNicknameSave = (Button)findViewById(R.id.bt_my_account_nickname_save);
//
//
//        btMyAccountNicknameSave.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                SimpleErrorDialog.passwordValidation(MyAccount.this, new SimpleErrorDialog.PasswordValidationI() {
//                    @Override
//                    public void action() {
//                        ProtocoloDTO p = new ProtocoloDTO();
//                        p.setComponent("/myAccount");
//                        p.setAction("/myAccount/save/nickname");
//
//                        UsuarioDTO c = new UsuarioDTO();
//                        c.setNickname(etMyAccountNickname.getText().toString());
//                        p.setObjectDTO(GsonFormated.get().toJson(c));
//
//                        RestExecute.doit(MyAccount.this, p,
//                                new CallbackRest(){
//
//                                    @Override
//                                    public void response(ResponseEntity<ProtocoloDTO> response) {
//                                        SingletonValues.getInstance().getUsuario().setNickname(c.getNickname());
//                                        Toast.makeText(getApplicationContext(),"Guardada",Toast. LENGTH_SHORT).show();
//
//                                    }
//
//                                    @Override
//                    public void onError(ResponseEntity<ProtocoloDTO> response) {
//
//                    }
//
//                    @Override
//                    public void beforeShowErrorMessage(String msg) {
//
//                    }
//                                });
//
//
//                    }
//                });
//
//            }
//        });
//
//        btMyAccountNicknameReset = (Button)findViewById(R.id.bt_my_account_nickname_reset);
//        btMyAccountNicknameReset.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                etMyAccountNickname.setText(SingletonValues.getInstance().getUsuario().getNickname());
//            }
//        });
//
//
//
//        btMyaccountMenuNickname = (Button)findViewById(R.id.bt_myaccount_menu_nickname);
//
//        btMyaccountShowPassword = (android.widget.ImageButton)findViewById(R.id.bt_myaccount_show_password);
//        tpMyaccountNewPassword = (EditText)findViewById(R.id.tp_myaccount_new_password);
//
//        btMyaccountShowPassword2 = (android.widget.ImageButton)findViewById(R.id.bt_myaccount_show_password2);
//        tpMyaccountNewPassword2 = (EditText)findViewById(R.id.tp_myaccount_new_password2);
//
//
//        btMyaccountMenuNickname = (Button)findViewById(R.id.bt_myaccount_menu_nickname);
//        tlMyaccountMenuNicknameContent = (TableLayout)findViewById(R.id.tl_myaccount_menu_nickname_content);
//
//
//
//        //btMyaccountMenuMessage = (Button)findViewById(R.id.bt_myaccount_menu_message);
//
//        passwordConfTitle = (Button)findViewById(R.id.my_account_password_conf_title);
//        passwordConfContent = (TableLayout)findViewById(R.id.my_account_password_conf_content);

//        gralConfTitle = (Button)findViewById(R.id.my_account_conf_gral_conf_title);
//        gralConfContent = (TableLayout)findViewById(R.id.my_account_conf_gral_conf_content);

//        tlMyaccountMenuMessageDefaultContent = (TableLayout)findViewById(R.id.tl_grupoinfo_menu_message_content);


//        ToolsUtil.setActionShowPassword(btMyaccountShowPassword, tpMyaccountNewPassword);
//        ToolsUtil.setActionShowPassword(btMyaccountShowPassword2, tpMyaccountNewPassword2);
//
//        MenuAcordeonUtil.setActionMenu(passwordConfTitle, passwordConfContent);

//        // setActionMenu(btMyaccountMenuCodigoInvitacion, tlMyaccountMenuCodigoInvitacionContent);
//        setActionMenu(btMyaccountMenuPassword, tlMyaccountMenuPasswordContent);
//
//        setActionMenu(btMyaccountMenuMessage, tlMyaccountMenuMessageDefaultContent);



    }

    private void iniciarLoginSkip() {
        loginSkip = new MyAccountLoginSkipFrame(this, progressBar);
        loginSkip.setListener();
        loginSkip.loadValues();

    }


    private void iniciarLock() {
        lock = new MyAccountLockFrame(this, progressBar);
        lock.setListener();
        lock.loadValues();
    }
    private void iniciarConfiguracion() {
        configuracionGeneral = new MyAccountConfiguracionGeneralFrame(this);
        configuracionGeneral.setListener();
        configuracionGeneral.loadValues();
    }
    private void iniciarPassword() {
        password = new MyAccountPasswordFrame(this);
        password.setListener();

    }
    private void iniciarNickname() {
        nickname = new MyAccountNicknameFrame(this);
        nickname.loadValues();
        nickname.setListener();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem itemMenu) {
        this.finish();
        return true;
    }


}
