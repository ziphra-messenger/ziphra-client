package com.privacity.cliente.activity.codigoinvitacion;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.privacity.cliente.activity.common.CustomAppCompatActivity;
import com.privacity.cliente.common.error.SimpleErrorDialog;
import com.privacity.cliente.encrypt.EncryptUtil;
import com.privacity.cliente.model.Grupo;
import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.cliente.singleton.Observers;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.singleton.interfaces.ObservadoresPasswordGrupo;
import com.privacity.cliente.util.GsonFormated;
import com.privacity.cliente.util.ToolsUtil;
import com.privacity.common.dto.EncryptKeysDTO;
import com.privacity.common.dto.ProtocoloDTO;
import com.privacity.common.dto.UserInvitationCodeDTO;
import com.privacity.common.dto.response.MyAccountGenerateInvitationCodeResponseDTO;

import org.springframework.http.ResponseEntity;

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

    @Override
    protected boolean isOnlyAdmin() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_codigo_invitacion);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Codigo de Invitacion");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Observers.passwordGrupo().suscribirse(this);

        qr = (ImageView)findViewById(R.id.qr);
        btMyAccountGenerarCodigoInvitacion = (Button)findViewById(R.id.bt_myaccount_generar_codigo_invitacion);
        tvCodigoInvitacion = (EditText)findViewById(R.id.codigo_invitacion);
        tvCodigoInvitacionValidate = (TextView) findViewById(R.id.codigo_invitacion_validate);
         tlMyaccountMenuCodigoInvitacionContent = (TableLayout)findViewById(R.id.tl_myaccount_menu_codigo_invitacion_content);
        btMyaccountShare = (ImageButton)findViewById(R.id.bt_myaccount_share);
        btCodigoInvitacionReset = (Button)findViewById(R.id.bt_codigo_invitacion_reset);

        ibCodigoInvitacionCopy = (ImageButton)findViewById(R.id.ib_codigo_invitacion_copy);

        ibCodigoInvitacionCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolsUtil.setClipboard(CodigoInvitacionActivity.this, tvCodigoInvitacion.getText().toString());
            }
        });
        btMyAccountCodigoInvitacionGuardar = (Button)findViewById(R.id.bt_myaccount_codigo_invitacion_guardar);

        btMyAccountCodigoInvitacionGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardar();
            }
        });
        originalValue = SingletonValues.getInstance().getInvitationCode();

        btCodigoInvitacionReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initValues();
            }
        });
        initValues();
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

                if ( tvCodigoInvitacion.getText().toString().equals(originalValue)){

                    tvCodigoInvitacionValidate.setTextColor(Color.BLACK);
                    tvCodigoInvitacionValidate.setText("Codigo sin cambios");

                    return;
                }

                if ( tvCodigoInvitacion.getText().toString().equals("")){

                    tvCodigoInvitacionValidate.setTextColor(Color.RED);
                    tvCodigoInvitacionValidate.setText("No puede estar vacio");

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



        tvCodigoInvitacion.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (tvCodigoInvitacion.getRight() - tvCodigoInvitacion.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        tvCodigoInvitacion.setText("");

                        return true;
                    }
                }
                return false;
            }
        });

        btMyAccountGenerarCodigoInvitacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProtocoloDTO p = new ProtocoloDTO();
                p.setComponent("/myAccount");
                p.setAction("/myAccount/invitationCodeGenerator");


                EncryptKeysDTO ek = null;
                try {
                    ek = EncryptUtil.invitationCodeEncryptKeysGenerator(SingletonValues.getInstance().getPersonalAEStoUse());
                } catch (Exception e) {
                    e.printStackTrace();
                    SimpleErrorDialog.errorDialog(CodigoInvitacionActivity.this, "Error generando invitation Code", e.getMessage());
                    return;
                }


                p.setObjectDTO(GsonFormated.get().toJson(ek));

                RestExecute.doit(CodigoInvitacionActivity.this, p,
                        new CallbackRest(){

                            @Override
                            public void response(ResponseEntity<ProtocoloDTO> response) {

                                MyAccountGenerateInvitationCodeResponseDTO m = GsonFormated.get().fromJson(response.getBody().getObjectDTO(), MyAccountGenerateInvitationCodeResponseDTO.class);


                                SingletonValues.getInstance().setInvitationCode(m.getInvitationCode());
                                originalValue=m.getInvitationCode();
                                tvCodigoInvitacion.setText(m.getInvitationCode());

                                try {
                                    qr.setImageBitmap(encodeAsBitmap(tvCodigoInvitacion.getText().toString()));
                                } catch (WriterException e) {
                                    e.printStackTrace();
                                }

                                tvCodigoInvitacionValidate.setText("");
                                Toast.makeText(getApplicationContext(),"Guardado",Toast. LENGTH_SHORT).show();
                            }

                            @Override
                    public void onError(ResponseEntity<ProtocoloDTO> response) {

                    }

                    @Override
                    public void beforeShowErrorMessage(String msg) {

                    }
                        });

            }
        });
        btMyaccountShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Mi codigo de invitacion es: " + tvCodigoInvitacion.getText().toString();
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Unite a la app de mensajeria mas segura del mundo");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Compartir"));
            }
        });


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
    private void guardar() {

        if (tvCodigoInvitacion.getText().toString().equals("")) return;

        if ( tvCodigoInvitacion.getText().toString().equals(originalValue)){
            return;
        }
        ProtocoloDTO p = new ProtocoloDTO();
        p.setComponent("/myAccount");
        p.setAction("/myAccount/saveCodeAvailable");


        EncryptKeysDTO ek = null;
        try {
            ek = EncryptUtil.invitationCodeEncryptKeysGenerator(SingletonValues.getInstance().getPersonalAEStoUse());
        } catch (Exception e) {
            e.printStackTrace();
            SimpleErrorDialog.errorDialog(CodigoInvitacionActivity.this, "Error generando invitation Code", e.getMessage());
            return;
        }
        UserInvitationCodeDTO obj = new UserInvitationCodeDTO();
        obj.setEncryptKeysDTO(ek);
        obj.setInvitationCode(tvCodigoInvitacion.getText().toString());

        p.setObjectDTO(GsonFormated.get().toJson(obj));

        RestExecute.doit(CodigoInvitacionActivity.this, p,
                new CallbackRest(){

                    @Override
                    public void response(ResponseEntity<ProtocoloDTO> response) {

                        MyAccountGenerateInvitationCodeResponseDTO m = GsonFormated.get().fromJson(response.getBody().getObjectDTO(), MyAccountGenerateInvitationCodeResponseDTO.class);

                        SingletonValues.getInstance().setInvitationCode(m.getInvitationCode());
                        try {
                            qr.setImageBitmap(encodeAsBitmap(tvCodigoInvitacion.getText().toString()));
                        } catch (WriterException e) {
                            e.printStackTrace();
                        }
                        originalValue=m.getInvitationCode();
                        tvCodigoInvitacionValidate.setText("");
                        Toast.makeText(getApplicationContext(),"Guardado",Toast. LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(ResponseEntity<ProtocoloDTO> response) {

                    }

                    @Override
                    public void beforeShowErrorMessage(String msg) {

                    }
                });

    }

    private void initValues()  {
        tvCodigoInvitacion.setText(originalValue);
        try {
            qr.setImageBitmap(encodeAsBitmap(originalValue));
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


        ProtocoloDTO p = new ProtocoloDTO();
        p.setComponent("/myAccount");
        p.setAction("/myAccount/isInvitationCodeAvailable");


        p.setObjectDTO(GsonFormated.get().toJson(tvCodigoInvitacion.getText().toString()));


        RestExecute.doit(CodigoInvitacionActivity.this, p,
                new CallbackRest(){

                    @Override
                    public void response(ResponseEntity<ProtocoloDTO> response) {

                        Boolean disponible = GsonFormated.get().fromJson(response.getBody().getObjectDTO(),Boolean.class);
                        if ( disponible){
                            tvCodigoInvitacionValidate.setTextColor(Color.BLACK);
                            tvCodigoInvitacionValidate.setText("Disponible");
                        }else{
                            tvCodigoInvitacionValidate.setTextColor(Color.RED);
                            tvCodigoInvitacionValidate.setText("No Disponible");

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
    public void finish() {
        super.finish();
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
}