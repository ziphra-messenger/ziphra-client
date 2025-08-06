package com.privacity.cliente.activity.codigoinvitacion;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.privacity.cliente.R;
import com.privacity.cliente.activity.common.ButtonLongClickMaquee;
import com.privacity.cliente.activity.common.CustomAppCompatActivity;
import com.privacity.cliente.activity.common.GetButtonReady;
import com.privacity.cliente.common.error.SimpleErrorDialog;
import com.privacity.cliente.encrypt.EncryptUtil;
import com.privacity.cliente.includes.ProgressBarUtil;
import com.privacity.cliente.model.Grupo;
import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.cliente.singleton.Observers;
import com.privacity.cliente.singleton.SingletonValues;import com.privacity.cliente.singleton.Singletons;
import com.privacity.cliente.singleton.UtilsStringSingleton;
import com.privacity.cliente.singleton.interfaces.ObservadoresPasswordGrupo;
import com.privacity.cliente.singleton.interfaces.ViewCallbackActionInterface;
import com.privacity.cliente.util.CopyPasteUtil;
import com.privacity.common.dto.EncryptKeysDTO;
import com.privacity.cliente.model.dto.Protocolo;
import com.privacity.common.dto.UserInvitationCodeDTO;
import com.privacity.common.dto.response.MyAccountGenerateInvitationCodeResponseDTO;
import com.privacity.common.enumeration.ProtocoloActionsEnum;
import com.privacity.common.enumeration.ProtocoloComponentsEnum;

import org.springframework.http.ResponseEntity;

import lombok.Getter;
import lombok.Setter;

import static android.graphics.Color.WHITE;

public class CodigoInvitacionActivity extends CustomAppCompatActivity implements ObservadoresPasswordGrupo {
    private Button btMyaccountMenuCodigoInvitacion;
    private Button btMyAccountGenerarCodigoInvitacion;
    private EditText tvCodigoInvitacion;
    private TableLayout tlMyaccountMenuCodigoInvitacionContent;
    private ImageView qr;
    private ImageButton btMyaccountShare;
    private TextView tvCodigoInvitacionValidate;
    private Button btCodigoInvitacionReset;
    String originalValue;
    private Button btMyAccountCodigoInvitacionGuardar;
    private ImageButton ibCodigoInvitacionCopy;
    @Setter
    @Getter
    EncryptKeysDTO ek;
    @Getter
    private ProgressBar progressBar;
    @Override
    protected boolean isOnlyAdmin() {
        return false;
    }
    @Setter
    @Getter
    private boolean errorGenerandoCodigoInvitacion=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_codigo_invitacion);

        originalValue = Singletons.usuario().getInvitationCode().trim();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ek = EncryptUtil.invitationCodeEncryptKeysGenerator(SingletonValues.getInstance().getPersonalAEStoUse());
                } catch (Exception e) {
                    e.printStackTrace();
                    errorGenerandoCodigoInvitacion=true;
                    ProgressBarUtil.hide(CodigoInvitacionActivity.this, progressBar);
                    SimpleErrorDialog.errorDialog(CodigoInvitacionActivity.this, getString(R.string.acodigoinvitacion_activity__alert__error_generated_title), e.getMessage());
                    return;
                }
            }
        }).start();

        initActionBar();
        Observers.passwordGrupo().suscribirse(this);

        initView();
        initValues();

        setListeners();


    }

    private void setListeners() {
        ibCodigoInvitacionCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CopyPasteUtil.setClipboard(CodigoInvitacionActivity.this, tvCodigoInvitacion.getText().toString());
            }
        });


        ButtonLongClickMaquee.setListener(btCodigoInvitacionReset);
        btCodigoInvitacionReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initValues();
            }
        });

        tvCodigoInvitacion.addTextChangedListener(new TextWatcher(){

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                try {
                    qr.setImageBitmap(encodeAsBitmap(tvCodigoInvitacion.getText().toString()));
                } catch (WriterException e) {
                    e.printStackTrace();
                }

                if ( tvCodigoInvitacion.getText().toString().trim().equals(originalValue)){
                    btMyAccountCodigoInvitacionGuardar.setEnabled(false);
                    tvCodigoInvitacionValidate.setTextColor(Color.BLACK);
                    tvCodigoInvitacionValidate.setText(getString(R.string.codigoinvitacion_activity__codigo_sin_cambios));

                    return;
                }

                if ( tvCodigoInvitacion.getText().toString().trim().equals("")){
                    btMyAccountCodigoInvitacionGuardar.setEnabled(false);
                    tvCodigoInvitacionValidate.setTextColor(Color.RED);
                    tvCodigoInvitacionValidate.setText(getString(R.string.codigoinvitacion_activity__codigo_validate__empty));

                    return;
                }
                new Runnable(){

                    @Override
                    public void run() {
                        try {
                            validarCodigoInvitacionRest();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.run();
            }
        });


        CopyPasteUtil.setListenerIconClearText(tvCodigoInvitacion);


        btMyaccountShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvCodigoInvitacion.getText().toString().trim().equals("")) return;

                if ( !tvCodigoInvitacion.getText().toString().trim().equals(originalValue.trim())){
                    tvCodigoInvitacionValidate.setTextColor(Color.RED);
                    tvCodigoInvitacionValidate.setText(getString(R.string.codigoinvitacion_activity__codigo_validate__no_save));
                    return;

                }

                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = getString(R.string.codigoinvitacion_activity__share__message__body,tvCodigoInvitacion.getText().toString());
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.codigoinvitacion_activity__share__message__extra_subject));
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, getString(R.string.general__share)));
            }
        });
    }
private ViewCallbackActionInterface getListenerGenerarCodigoInvitacion(){
   return new ViewCallbackActionInterface() {
        @Override
        public void action(View v) {
            ProgressBarUtil.show(CodigoInvitacionActivity.this, progressBar);
            ProgresBarAsynk task = new ProgresBarAsynk(CodigoInvitacionActivity.this, progressBar);
            task.doInBackground();

        }
    };
}
    private void initView() {
        progressBar = (ProgressBar) findViewById(R.id.common__progress_bar);
        ProgressBarUtil.hide(this, progressBar);
        qr = (ImageView)findViewById(R.id.qr);

        tvCodigoInvitacion = (EditText)findViewById(R.id.codigo_invitacion);
        tvCodigoInvitacionValidate = (TextView) findViewById(R.id.codigo_invitacion_validate);
        tlMyaccountMenuCodigoInvitacionContent = (TableLayout)findViewById(R.id.tl_myaccount_menu_codigo_invitacion_content);
        btMyaccountShare = (ImageButton)findViewById(R.id.bt_myaccount_share);

        ibCodigoInvitacionCopy = (ImageButton)findViewById(R.id.ib_codigo_invitacion_copy);

        btMyAccountCodigoInvitacionGuardar = GetButtonReady.get(this,R.id.bt_myaccount_codigo_invitacion_guardar, v -> guardar());



        btCodigoInvitacionReset = GetButtonReady.get(this,R.id.bt_codigo_invitacion_reset);
        btMyAccountGenerarCodigoInvitacion =GetButtonReady.get(this,R.id.bt_myaccount_generar_codigo_invitacion,getListenerGenerarCodigoInvitacion());

    }

    public void callGenerator(){
        Protocolo p = new Protocolo();
        p.setComponent(ProtocoloComponentsEnum.MY_ACCOUNT);
        p.setAction(ProtocoloActionsEnum.MY_ACCOUNT_INVITATION_CODE_GENERATOR);

        p.setObjectDTO(UtilsStringSingleton.getInstance().gsonToSend(ek));


        RestExecute.doit(CodigoInvitacionActivity.this, p,
                new CallbackRest(){

                    @Override
                    public void response(ResponseEntity<Protocolo> response) {

                        MyAccountGenerateInvitationCodeResponseDTO m = UtilsStringSingleton.getInstance().gson().fromJson(response.getBody().getObjectDTO(), MyAccountGenerateInvitationCodeResponseDTO.class);


                        Singletons.usuario().setInvitationCode(m.getInvitationCode());
                        originalValue=m.getInvitationCode().trim();
                        tvCodigoInvitacion.setText(m.getInvitationCode());

                        try {
                            qr.setImageBitmap(encodeAsBitmap(tvCodigoInvitacion.getText().toString().trim()));
                        } catch (WriterException e) {
                            e.printStackTrace();
                        }

                        tvCodigoInvitacionValidate.setText("");
                        ProgressBarUtil.hide(CodigoInvitacionActivity.this, progressBar);
                        Toast.makeText(getApplicationContext(),getString(R.string.codigoinvitacion_activity__save__ok),Toast. LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(ResponseEntity<Protocolo> response) {
                        ProgressBarUtil.hide(CodigoInvitacionActivity.this, progressBar);
                    }

                    @Override
                    public void beforeShowErrorMessage(String msg) {
                        ProgressBarUtil.hide(CodigoInvitacionActivity.this, progressBar);
                    }
                });
    }
    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if(actionBar==null)return;
        actionBar.setTitle(getString(R.string.codigoinvitacion_activity__title));
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ProgressBarUtil.hide(this, progressBar);
        this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem itemMenu) {
        this.finish();
        return true;
    }
    private void guardar() {

        if (tvCodigoInvitacion.getText().toString().trim().equals("")) return;

        if ( tvCodigoInvitacion.getText().toString().trim().equals(originalValue.trim())){
            return;

        }

        ProgressBarUtil.show(CodigoInvitacionActivity.this, progressBar);

        while (ek == null || errorGenerandoCodigoInvitacion){}
        Protocolo p = new Protocolo();
        p.setComponent(ProtocoloComponentsEnum.MY_ACCOUNT);
        p.setAction(ProtocoloActionsEnum.MY_ACCOUNT_SAVE_CODE_AVAILABLE);



        UserInvitationCodeDTO obj = new UserInvitationCodeDTO();
        obj.setEncryptKeysDTO(ek);
        obj.setInvitationCode(tvCodigoInvitacion.getText().toString().trim());

        p.setObjectDTO(UtilsStringSingleton.getInstance().gsonToSend(obj));

        RestExecute.doit(CodigoInvitacionActivity.this, p,
                new CallbackRest(){

                    @Override
                    public void response(ResponseEntity<Protocolo> response) {

                        MyAccountGenerateInvitationCodeResponseDTO m = UtilsStringSingleton.getInstance().gson().fromJson(response.getBody().getObjectDTO(), MyAccountGenerateInvitationCodeResponseDTO.class);

                        Singletons.usuario().setInvitationCode(m.getInvitationCode());
                        try {
                            qr.setImageBitmap(encodeAsBitmap(tvCodigoInvitacion.getText().toString()));
                        } catch (WriterException e) {
                            e.printStackTrace();
                        }
                        originalValue=m.getInvitationCode().trim();
                        tvCodigoInvitacionValidate.setText("");
                        ProgressBarUtil.hide(CodigoInvitacionActivity.this, progressBar);
                        btMyAccountCodigoInvitacionGuardar.setEnabled(false);
                        Toast.makeText(getApplicationContext(),getApplicationContext().getString(R.string.general__saved),Toast. LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(ResponseEntity<Protocolo> response) {
                        ProgressBarUtil.hide(CodigoInvitacionActivity.this, progressBar);
                    }

                    @Override
                    public void beforeShowErrorMessage(String msg) {
                        ProgressBarUtil.hide(CodigoInvitacionActivity.this, progressBar);
                    }
                });

    }

    private void initValues()  {
        btMyAccountCodigoInvitacionGuardar.setEnabled(false);
        tvCodigoInvitacion.setText(originalValue.trim());
        try {
            qr.setImageBitmap(encodeAsBitmap(originalValue.trim()));
        } catch (WriterException e) {
            e.printStackTrace();
        }
        //setClipboard(CodigoInvitacion.this,tvCodigoInvitacion.getText().toString());
    }

    Bitmap encodeAsBitmap(String str) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, 500, 500, null);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? Color.BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, 500, 0, 0, w, h);
        return bitmap;
    }



    private void validarCodigoInvitacionRest() throws Exception {


        Protocolo p = new Protocolo();
        p.setComponent(ProtocoloComponentsEnum.MY_ACCOUNT);
        p.setAction(ProtocoloActionsEnum.MY_ACCOUNT_IS_INVITATION_CODE_AVAILABLE);


        p.setObjectDTO(UtilsStringSingleton.getInstance().gsonToSend(tvCodigoInvitacion.getText().toString().trim()));


        RestExecute.doit(CodigoInvitacionActivity.this, p,
                new CallbackRest(){

                    @Override
                    public void response(ResponseEntity<Protocolo> response) {

                        Boolean disponible = UtilsStringSingleton.getInstance().gson().fromJson(response.getBody().getObjectDTO(),Boolean.class);
                        if ( disponible){
                            tvCodigoInvitacionValidate.setTextColor(Color.BLACK);
                            tvCodigoInvitacionValidate.setText(getString(R.string.general__available));
                            btMyAccountCodigoInvitacionGuardar.setEnabled(true);
                        }else{
                            tvCodigoInvitacionValidate.setTextColor(Color.RED);
                            tvCodigoInvitacionValidate.setText(getString(R.string.general__not_available));
                            btMyAccountCodigoInvitacionGuardar.setEnabled(false);

                        }

                    }

                    @Override
                    public void onError(ResponseEntity<Protocolo> response) {
                        btMyAccountCodigoInvitacionGuardar.setEnabled(false);
                    }

                    @Override
                    public void beforeShowErrorMessage(String msg) {
                        btMyAccountCodigoInvitacionGuardar.setEnabled(false);
                    }
                });

    }
    @Override
    public void finish() {
        super.finish();
        ProgressBarUtil.hide(this, progressBar);
        Observers.passwordGrupo().remove(this);
    }
    @Override
    public void passwordExpired(Grupo g) {
        if (SingletonValues.getInstance().getGrupoSeleccionado().getIdGrupo().equals(g.getIdGrupo())){
            this.finish();
        }
    }

    @Override
    public void passwordSet(Grupo g) {

    }

    @Override
    public void deleteExtraEncrypt(Grupo g) {

    }

    @Override
    public void lock(Grupo g) {

    }
    @Override
    protected void onResume() {


        super.onResume();
        ProgressBarUtil.hide(this, progressBar);

    }
}