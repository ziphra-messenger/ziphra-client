package com.privacity.cliente.activity.message;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.ContextMenu;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.addmember.AddMembersToGrupoActivity;
import com.privacity.cliente.activity.common.GrupoSelectedCustomAppCompatActivity;
import com.privacity.cliente.activity.grupoinfo.GrupoInfoActivity;
import com.privacity.cliente.activity.loading.LoadingActivity;
import com.privacity.cliente.activity.main.MainActivity;
import com.privacity.cliente.activity.message.attach.GetImage;
import com.privacity.cliente.activity.message.attach.MessageAttach;
import com.privacity.cliente.activity.message.avanzado.MessageAvanzado;
import com.privacity.cliente.activity.message.customactionbar.MessageCustomActionBar;
import com.privacity.cliente.activity.message.customactionbar.MessageReplyFrame;
import com.privacity.cliente.activity.message.delegate.BloqueoRemotoDelegate;
import com.privacity.cliente.activity.messagedetail.MessageDetailActivity;
import com.privacity.cliente.activity.messageresend.MessageResendActivity;
import com.privacity.cliente.activity.messageresend.MessageUtil;
import com.privacity.cliente.common.component.SecureFieldAndEye;
import com.privacity.cliente.common.error.SimpleErrorDialog;
import com.privacity.cliente.encrypt.AEStoUse;
import com.privacity.cliente.includes.SecureFieldAndEyeUtil;
import com.privacity.cliente.model.Grupo;
import com.privacity.cliente.model.Message;
import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.cliente.rest.restcalls.grupo.WrittingCallRest;
import com.privacity.cliente.rest.restcalls.message.GetMessageHistorialById;
import com.privacity.cliente.singleton.Observers;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.singleton.impl.SingletonServer;
import com.privacity.cliente.singleton.interfaces.ObservadoresGrupos;
import com.privacity.cliente.singleton.interfaces.ObservadoresMensajes;
import com.privacity.cliente.singleton.observers.ObserverGrupo;
import com.privacity.cliente.singleton.observers.ObserverMessage;
import com.privacity.cliente.util.ChangeVisibility;
import com.privacity.cliente.util.GsonFormated;
import com.privacity.common.dto.GrupoDTO;
import com.privacity.common.dto.GrupoUserConfDTO;
import com.privacity.common.dto.IdMessageDTO;
import com.privacity.common.dto.MediaDTO;
import com.privacity.common.dto.MessageDTO;
import com.privacity.common.dto.MessageDetailDTO;
import com.privacity.common.dto.ProtocoloDTO;
import com.privacity.common.dto.UsuarioDTO;
import com.privacity.common.dto.WrittingDTO;

import com.privacity.common.enumeration.ConfigurationStateEnum;
import com.privacity.common.enumeration.MediaTypeEnum;
import com.privacity.common.enumeration.MessageState;
import com.privacity.common.enumeration.ProtocoloActionsEnum;
import com.privacity.common.enumeration.ProtocoloComponentsEnum;
import com.vanniktech.emoji.EmojiPopup;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import lombok.Getter;

public class MessageActivity extends GrupoSelectedCustomAppCompatActivity
        implements ObservadoresMensajes, ObservadoresGrupos,
        RecyclerMessageAdapter.RecyclerItemClick, SearchView.OnQueryTextListener,
        View.OnCreateContextMenuListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {

    @Getter
    MessageCustomActionBar messageCustomActionBar;
    @Getter
    MessageReplyFrame messageReplyFrame;

    //private boolean reply=false;
    private MessageAttach messageAttach;





    private RecyclerView rvLista;

    private RecyclerMessageAdapter adapter;

    @Getter
    private List<ItemListMessage> items = new CopyOnWriteArrayList<>();

    private Button enviarMessageButton;
    EditText txt;
    EditText invitationCode;
    Spinner spAddGrupoDialogRoles;
    AlertDialog.Builder builder;
    AlertDialog ad;
    //private Button enviarEspecial;
    private Button enviarTime;
    private Button enviarAnonimo;
    //private Button enviarImagen;

    private Bitmap bitmapCompleto;
    private Bitmap bitmapMiniatura;

    //private Button btMessageImagenDelete;

    private Button enviarSecret;
    public EditText tvMessageSecretKey;
    public AEStoUse extraAesToUse;
    //private Button btMessageSecretKeyAplicar;
    private View viewDialogAddUser;
    private ImageButton avanzada;
    private View viewAvanzada;
    //private Spinner spinnerTime;
    final int[] actualValues={15,30,60,300,600,3000, 60000};
    private ImageView ibMessageAvanzadoCopy;

    private Button btMessageAvanzadaAutoGen;
    private ImageButton ibMessageAvanzadoShowSecretKey;
    private ImageButton ibMessageAvanzadoHideSecretKey;
    private Spinner spMessageAvanzadoBlackAlways;
    private Spinner spMessageAvanzadoAnonimoAlways;

    private Spinner spMessageAvanzadoAllowDownloadImage;
    private CheckBox cbMessageAvanzadoAllowDownloadAudio;
    private CheckBox cbMessageAvanzadoAllowDownloadVideo;

    private Spinner spMessageAvanzadoTimeAlways;
    private Spinner spMessageAvanzadoReenvio;
    private Spinner spMessageAvanzadoExtraAesAlways;
    public Spinner spMessageAvanzadoBlackRecepcion;
    public Spinner spMessageAvanzadoAnonimoRecepcion;
    private Button btMessageAvanzadoSave;
//    private EditText etMessageAvanzadoNicknameForGrupo;
//    private TextView tvMessageAvanzadoNicknameForGrupoValidacion;

    private Button enviarFile;
    //    private RadioButton rbMessageSendImage;
//    private RadioButton rbMessageSendFile;
    private Button refreshMensaje;
    private ImageButton btnEmojis;
    private ImageButton btnAttach;
    private Button btMessageAvanzadoClose;
    private ImageButton removeAttach;
    private ImageButton hidetxt;
    private ImageButton showtxt;

    private ImageButton messageDeleteAudio;
    private Button messageRecordAudio;
    private SeekBar auxiliarRecordAudioAnimation;
    private TextView secondsAudio;
    private LinearLayout messageSendTextRow;
    private LinearLayout messageSendAudioRow;
    //public TextView tvMessageShowOther;
    private int secondsAudioOriginalColor;
    private TableRow messageSendRowComplete;
    private TableRow messageSendRowReadOnly;

    private Spinner spMessageAvanzadoTimeValues;
    private Spinner spMessageAvanzadoChageNicknameNumber;

    @Override
    protected boolean isOnlyAdmin() {
        return false;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Observers.message().setMessageOnTop(true);
        try {
            crearVista();
        } catch (Exception e) {
            e.printStackTrace();
            SimpleErrorDialog.errorDialog(
                    this,
                    "ERROR A CARGAR LOS MENSAJES",
                    e.getMessage(),
                    () -> MessageActivity.this.finish()
            );

        }
    }

    public void cleanSelectedItems(){
        for ( ItemListMessage item : items){
            if ( item.getRch() != null &&  item.getRch().getLayoutSelected() != null){
                item.getRch().getLayoutSelected().setVisibility(View.INVISIBLE);
            }
        }
        Observers.message().getMessageSelected().clear();
        adapter.notifyDataSetChanged();
    }



    protected void crearVista() throws Exception{

        setContentView(R.layout.activity_message);
        Observers.message().suscribirse(this);
        Observers.grupo().suscribirse(this);

        messageCustomActionBar = new MessageCustomActionBar(this);
        messageReplyFrame = new MessageReplyFrame(this);




        actualizarLista();


        setBroadcast();


        initViews();
        adapter = new RecyclerMessageAdapter(this, items, this);
        rvLista.setAdapter(adapter);




        initListener();
        registerForContextMenu(rvLista);



        initListenersSend();
        initViewsAvanzado();
        MessageAvanzado.initListenerAvanzado(
                MessageActivity.this,
                ibMessageAvanzadoCopy,

                btMessageAvanzadaAutoGen,
                ibMessageAvanzadoShowSecretKey,
                ibMessageAvanzadoHideSecretKey,
                avanzada,
                viewAvanzada,
                null,
                tvMessageSecretKey);
        btMessageAvanzadoSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleErrorDialog.passwordValidation(MessageActivity.this, new SimpleErrorDialog.PasswordValidationI() {
                    @Override
                    public void action() {
                        ProtocoloDTO p = new ProtocoloDTO();
                        p.setComponent(ProtocoloComponentsEnum.GRUPO);
                        p.setAction(ProtocoloActionsEnum.GRUPO_SAVE_GRUPO_USER_CONF);

                        GrupoUserConfDTO c = getGrupoUserConfDTO();

                        p.setObjectDTO(GsonFormated.get().toJson(c));

                        RestExecute.doit(MessageActivity.this, p,
                                new CallbackRest(){

                                    @Override
                                    public void response(ResponseEntity<ProtocoloDTO> response) {

//                                        if (etMessageAvanzadoNicknameForGrupo.getText().equals("")){
//                                            ObservatorGrupos.getInstance().getGrupoById(c.getIdGrupo()).setNicknameForGrupo(null);
//                                        }else{
//                                            ObservatorGrupos.getInstance().getGrupoById(c.getIdGrupo()).setNicknameForGrupo(c.getNicknameForGrupo());
//                                        }
                                        Toast.makeText(getApplicationContext(),"Guardada",Toast. LENGTH_SHORT).show();
                                        viewAvanzada.setVisibility(View.GONE);

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

            }
        });

        loadGrupoUserConf(getGrupoSeleccionado().getIdGrupo());

        initValues();
        adapter.notifyDataSetChanged();

        initMessageAttach();

        rvLista.scrollToPosition(items.size() - 1);

        initSecureFieldAndEye();

//        enviarFile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                File selectedFile = new File(selectedFilePath);
//                archivo = new ArrayList<>();
//                try (
//                        InputStream inputStream = new FileInputStream(selectedFile);
//                ) {
//
//                    int byteRead;
//
//                    while ((byteRead = inputStream.read()) != -1) {
//                        archivo.add( (byte)byteRead);
//                    }
//
//                } catch (IOException ex) {
//                    ex.printStackTrace();
//                }
//            }
//        });
    }

    private void initSecureFieldAndEye() {

        SecureFieldAndEye messageEye = new SecureFieldAndEye(
                null,(EditText) findViewById(R.id.enviar_texto2),
                (ImageButton) findViewById(R.id.show_eye_txt),
                (ImageButton) findViewById(R.id.hide_txt)

        );

        SecureFieldAndEyeUtil.listener(messageEye,false);

        messageEye.getEyeHide().setVisibility(View.VISIBLE);
        messageEye.getEyeShow().setVisibility(View.GONE);


    }

    private void initMessageAttach() {
        messageAttach = new MessageAttach(this);
        messageAttach.setListener();

    }

    ArrayList<Byte> archivo;
    @NotNull
    private GrupoUserConfDTO getGrupoUserConfDTO() {
        GrupoUserConfDTO c = new GrupoUserConfDTO();

        c.setIdGrupo(getGrupoSeleccionado().getIdGrupo());
        c.setAnonimoAlways(MessageUtil.getGrupoUserConfEnum(spMessageAvanzadoAnonimoAlways,true));
        //c.setChangeNicknameToNumber(MessageUtil.getGrupoUserConfEnum(spMessageAvanzadoChageNicknameNumber,true));
        c.setAnonimoRecived(MessageUtil.getGrupoUserConfEnum(spMessageAvanzadoAnonimoRecepcion,false));
        c.setTimeMessageSeconds(MessageUtil.getGrupoUserConfMessageTimeGetSeconds(this, spMessageAvanzadoTimeValues));
        c.setBlackMessageAlways(MessageUtil.getGrupoUserConfEnum(spMessageAvanzadoBlackAlways,false));
        c.setBlackMessageRecived(MessageUtil.getGrupoUserConfEnum(spMessageAvanzadoBlackRecepcion, false));
        c.setTimeMessageAlways(MessageUtil.getGrupoUserConfEnum(spMessageAvanzadoTimeAlways, true));
        c.setPermitirReenvio(MessageUtil.getGrupoUserConfEnum(spMessageAvanzadoReenvio,true));
        c.setExtraAesAlways(MessageUtil.getGrupoUserConfEnum(spMessageAvanzadoExtraAesAlways,true));
        c.setDownloadAllowImage(MessageUtil.getGrupoUserConfEnum(spMessageAvanzadoAllowDownloadImage,true));
        //c.setNicknameForGrupo(etMessageAvanzadoNicknameForGrupo.getText().toString());
        return c;
    }

    private void loadGrupoUserConf(String idGrupo){
        GrupoUserConfDTO conf = Observers.grupo().getGrupoById(idGrupo).getUserConfDTO();

        //etMessageAvanzadoNicknameForGrupo.setText(ObservatorGrupos.getInstance().getGrupoById(idGrupo).getNicknameForGrupo());


        spMessageAvanzadoBlackAlways.setSelection(MessageUtil.getSpinnerItem(MasterGeneralConfiguration.buildSiempreBlackConfigurationByGrupo(getGrupoSeleccionado().getIdGrupo()),false));
//        setSpinnerListener(spMessageAvanzadoBlackAlways,0);
//        spMessageAvanzadoAnonimoAlways.setSelection(MessageUtil.getSpinnerItem(MasterGeneralConfiguration.buildSiempreAnonimoConfigurationByGrupo(grupoSeleccionado),true));
//        setSpinnerListener(spMessageAvanzadoAnonimoAlways,R.id.tv_message_avanzado_anonimo_always);
        spMessageAvanzadoTimeAlways.setSelection(MessageUtil.getSpinnerItem(MasterGeneralConfiguration.buildSiempreTemporalConfigurationByGrupo(getGrupoSeleccionado().getIdGrupo()),true));
        setSpinnerListener(spMessageAvanzadoTimeAlways,R.id.tv_message_avanzado_time_always);

        spMessageAvanzadoChageNicknameNumber.setSelection(MessageUtil.getSpinnerItem(MasterGeneralConfiguration.buildHideNicknameConfigurationByGrupo(getGrupoSeleccionado().getIdGrupo()),true));
        setSpinnerListener(spMessageAvanzadoChageNicknameNumber,R.id.tv_message_avanzado_change_nickname_number);


        spMessageAvanzadoAllowDownloadImage.setSelection(MessageUtil.getSpinnerItem(MasterGeneralConfiguration.buildImageDownload(getGrupoSeleccionado().getIdGrupo()),false));
        setSpinnerListener(spMessageAvanzadoAllowDownloadImage,R.id.tv_message_avanzado_allow_download_imagen);


        /*
        private CheckBox cbMessageAvanzadoAllowDownloadImage;
        private CheckBox cbMessageAvanzadoAllowDownloadAudio;
        private CheckBox cbMessageAvanzadoAllowDownloadVideo;

 */
        /*
                    Resources res = activity.getResources();
            String[] a = res.getStringArray(R.array.time_messages_values_in_seconds);
            int t = Integer.parseInt(a[this.temporal.getConfTemporalMaximoTiempoPermitido().getSelectedItemPosition()]);
            dto.setTimeMessageMaxTimeAllow(t);
         */
//        spMessageAvanzadoReenvio.setSelection(MessageUtil.getSpinnerItem(MasterGeneralConfiguration.buildResendPermitidoConfigurationByGrupo(grupoSeleccionado),true));
//        setSpinnerListener(spMessageAvanzadoReenvio,R.id.tv_message_avanzado_reenvio);
//        spMessageAvanzadoExtraAesAlways.setSelection(MessageUtil.getSpinnerItem(MasterGeneralConfiguration.buildSiempreExtraEncryptConfigurationByGrupo(grupoSeleccionado),true));
//        setSpinnerListener(spMessageAvanzadoExtraAesAlways,R.id.tv_message_avanzado_personal_encrypt_always);
//        spMessageAvanzadoExtraAesAlways.setSelection(MessageUtil.getSpinnerItem(conf.getExtraAesAlways()));
        spMessageAvanzadoBlackRecepcion.setSelection(MessageUtil.getSpinnerItem(MasterGeneralConfiguration.buildSiempreBlackReceptionConfigurationByGrupo(getGrupoSeleccionado().getIdGrupo()),false));
        spMessageAvanzadoAnonimoRecepcion.setSelection(MessageUtil.getSpinnerItem(MasterGeneralConfiguration.buildSiempreAnonimoReceptionConfigurationByGrupo(getGrupoSeleccionado().getIdGrupo()),false));
    }

    private void setSpinnerListener(Spinner mySpinner, int text){
        if (text == 0) return;

        TextView v = (TextView)findViewById(text);

//        v.setText(mySpinner.getSelectedItem().toString());
        v.setClickable(true);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleErrorDialog.errorDialog(MessageActivity.this, "Validación", "No se puede cambiar por las Reglas del Grupo");
            }
        });

    }

    private void setBroadcast() {
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent intent) {
                String action = intent.getAction();
                if (action.equals("finish_message_activity")) {
                    finish();
                }
                if (action.equals("reload_configuracion_avanzada_message_activity")) {
                    initViewsAvanzado();
                    loadGrupoUserConf(getGrupoSeleccionado().getIdGrupo());
                }

            }
        };

        registerReceiver(broadcastReceiver, new IntentFilter("finish_message_activity"));
        registerReceiver(broadcastReceiver, new IntentFilter("reload_configuracion_avanzada_message_activity"));
    }


    private void initViewsAvanzado() {

//        tvMessageAvanzadoNicknameForGrupoValidacion = (TextView) findViewById(R.id.tv_message_avanzado_nickname_for_grupo_validacion);
//        etMessageAvanzadoNicknameForGrupo = (EditText) findViewById(R.id.et_message_avanzado_nickname_for_grupo);
//        etMessageAvanzadoNicknameForGrupo.setFilters(new InputFilter[]{new InputFilter.LengthFilter(ConstantValidation.USER_NICKNAME_MAX_LENGTH)});
//        etMessageAvanzadoNicknameForGrupo.addTextChangedListener(new TextWatcher() {
//
//            public void afterTextChanged(Editable s) {
//
//                validarNickname();
//
//            }
//
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//
//            public void onTextChanged(CharSequence s, int start, int before, int count) {}
//        });

        //etMessageAvanzadoNicknameForGrupo.setFilters(new InputFilter[]{new InputFilter.LengthFilter(ConstantValidation.USER_NICKNAME_MAX_LENGTH)});
        btMessageAvanzadoSave = (Button) findViewById(R.id.bt_message_avanzado_save);
        btMessageAvanzadoClose = (Button) findViewById(R.id.bt_message_avanzado_clse);

        btMessageAvanzadoClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                viewAvanzada.setVisibility(View.GONE);

            }
        });

        ibMessageAvanzadoCopy = (ImageView) findViewById(R.id.ib_message_avanzado_copy);
        btMessageAvanzadaAutoGen = (Button) findViewById(R.id.bt_message_avanzada_auto_gen);
        ibMessageAvanzadoShowSecretKey = (ImageButton) findViewById(R.id.ib_message_avanzado_show_secret_key);
        ibMessageAvanzadoHideSecretKey = (ImageButton) findViewById(R.id.ib_message_avanzado_hide_secret_key);
        avanzada = (ImageButton) findViewById(R.id.boton_avanzado);
        viewAvanzada = findViewById(R.id.vista_avanzada);
        //spinnerTime = (Spinner) findViewById(R.id.sp_message_avanzado_time_values);
        //spinnerTime.setSelection(1);

        spMessageAvanzadoAllowDownloadImage = (Spinner) findViewById(R.id.sp_message_avanzado_allow_download_imagen);
        cbMessageAvanzadoAllowDownloadAudio = (CheckBox) findViewById(R.id.ib_message_avanzado_allow_download_audio);
        cbMessageAvanzadoAllowDownloadVideo = (CheckBox) findViewById(R.id.ib_message_avanzado_allow_download_video);



        spMessageAvanzadoChageNicknameNumber = (Spinner) findViewById(R.id.sp_message_avanzado_change_nickname_number);
        spMessageAvanzadoBlackAlways = (Spinner) findViewById(R.id.sp_message_avanzado_black_always);
        spMessageAvanzadoAnonimoAlways = (Spinner) findViewById(R.id.sp_message_avanzado_anonimo_always);

        spMessageAvanzadoTimeAlways = (Spinner) findViewById(R.id.sp_message_avanzado_time_always);
        spMessageAvanzadoTimeValues = (Spinner) findViewById(R.id.sp_message_avanzado_time_values);

        ArrayAdapter<CharSequence> spinnerArrayAdapter;
        spinnerArrayAdapter = ArrayAdapter.createFromResource(MessageActivity.this, R.array.time_messages_values, R.layout.support_simple_spinner_dropdown_item);
        spMessageAvanzadoTimeValues.setAdapter(spinnerArrayAdapter);

        spMessageAvanzadoTimeValues.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                GrupoUserConfDTO configuracion = Observers.grupo().getGrupoById(getGrupoSeleccionado().getIdGrupo()).getUserConfDTO();;
                configuracion.setTimeMessageSeconds(MessageUtil.getGrupoUserConfMessageTimeGetSeconds(MessageActivity.this, spMessageAvanzadoTimeValues));

                TextView childAt = (TextView) parent.getChildAt(0);
                childAt.setTextColor(Color.parseColor("#FF009688"));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        {


            spMessageAvanzadoTimeValues.setSelection(
                    MessageUtil.getGrupoUserConfMessageTimeGetSecondsIndex(
                            MessageActivity.this,
                            Observers.grupo().getGrupoById(getGrupoSeleccionado().getIdGrupo()).getUserConfDTO()
                                    .getTimeMessageSeconds()
                    )
            );
        }
        //c.setTimeMessageSeconds(MessageUtil.getGrupoUserConfMessageTimeSecondsIndex(this, spMessageAvanzadoTimeValues));
        spMessageAvanzadoReenvio = (Spinner) findViewById(R.id.sp_message_avanzado_reenvio);
        spMessageAvanzadoExtraAesAlways = (Spinner) findViewById(R.id.sp_message_avanzado_extra_aes_always);

        spMessageAvanzadoBlackRecepcion = (Spinner) findViewById(R.id.sp_message_avanzado_black_recepcion);
        spMessageAvanzadoAnonimoRecepcion = (Spinner) findViewById(R.id.sp_message_avanzado_anonimo_recepcion);

        renderSpinner(spMessageAvanzadoBlackAlways, MasterGeneralConfiguration.buildSiempreBlackConfigurationByGrupo(getGrupoSeleccionado().getIdGrupo()),false);
        renderSpinner(spMessageAvanzadoChageNicknameNumber, MasterGeneralConfiguration.buildHideNicknameConfigurationByGrupo(getGrupoSeleccionado().getIdGrupo()),true);
        renderSpinner(spMessageAvanzadoAnonimoAlways, MasterGeneralConfiguration.buildSiempreAnonimoConfigurationByGrupo(getGrupoSeleccionado().getIdGrupo()),true);

        renderSpinner(spMessageAvanzadoTimeAlways, MasterGeneralConfiguration.buildSiempreTemporalConfigurationByGrupo(getGrupoSeleccionado().getIdGrupo()),true);
        renderSpinner(spMessageAvanzadoReenvio, MasterGeneralConfiguration.buildResendPermitidoConfigurationByGrupo(getGrupoSeleccionado().getIdGrupo()),true);
//        renderSpinner(spMessageAvanzadoReenvio, SingletonValues.getInstance().getMessageConfDTO().isPermitirReenvio());

        renderSpinner(spMessageAvanzadoExtraAesAlways, MasterGeneralConfiguration.buildSiempreExtraEncryptConfigurationByGrupo(getGrupoSeleccionado().getIdGrupo()),true);
//
        renderSpinner(spMessageAvanzadoBlackRecepcion, MasterGeneralConfiguration.buildSiempreBlackReceptionConfigurationByGrupo(getGrupoSeleccionado().getIdGrupo()),false);
        renderSpinner(spMessageAvanzadoAnonimoRecepcion, MasterGeneralConfiguration.buildSiempreAnonimoReceptionConfigurationByGrupo(getGrupoSeleccionado().getIdGrupo()),false);


        renderSpinner(spMessageAvanzadoAllowDownloadImage, MasterGeneralConfiguration.buildImageDownload(getGrupoSeleccionado().getIdGrupo()),true);

//        if (GrupoUtil.isGrupoDeDos(MessageActivity.this.grupoSeleccionado)){
//            spMessageAvanzadoAnonimoAlways.setSelection(1);
//            spMessageAvanzadoAnonimoRecepcion.setSelection(1);
//
//            spMessageAvanzadoAnonimoAlways.setEnabled(false);
//            spMessageAvanzadoAnonimoRecepcion.setEnabled(false);
//
//        }

        setAvanzadoSpinnerColor(spMessageAvanzadoBlackAlways,false,null,null);
        setAvanzadoSpinnerColor(spMessageAvanzadoChageNicknameNumber,true,(TextView)findViewById(R.id.tv_message_avanzado_change_nickname_number),MasterGeneralConfiguration.buildHideNicknameConfigurationByGrupo(getGrupoSeleccionado().getIdGrupo()));
        setAvanzadoSpinnerColor(spMessageAvanzadoAnonimoAlways,true,(TextView)findViewById(R.id.tv_message_avanzado_anonimo_always),MasterGeneralConfiguration.buildSiempreAnonimoConfigurationByGrupo(getGrupoSeleccionado().getIdGrupo()));
        setAvanzadoSpinnerColor(spMessageAvanzadoAllowDownloadImage,true,(TextView)findViewById(R.id.tv_message_avanzado_allow_download_imagen),MasterGeneralConfiguration.buildImageDownload(getGrupoSeleccionado().getIdGrupo()));

        setAvanzadoSpinnerColor(spMessageAvanzadoTimeAlways,true,(TextView)findViewById(R.id.tv_message_avanzado_time_always),MasterGeneralConfiguration.buildSiempreTemporalConfigurationByGrupo(getGrupoSeleccionado().getIdGrupo()));
        setAvanzadoSpinnerColor(spMessageAvanzadoReenvio,true,(TextView)findViewById(R.id.tv_message_avanzado_reenvio),MasterGeneralConfiguration.buildResendPermitidoConfigurationByGrupo(getGrupoSeleccionado().getIdGrupo()));
        setAvanzadoSpinnerColor(spMessageAvanzadoExtraAesAlways,true,(TextView)findViewById(R.id.tv_message_avanzado_extra_aes_always),MasterGeneralConfiguration.buildSiempreExtraEncryptConfigurationByGrupo(getGrupoSeleccionado().getIdGrupo()));
//
//
        setAvanzadoSpinnerColor(spMessageAvanzadoBlackRecepcion, adapter,false,null,null);
        setAvanzadoSpinnerColor(spMessageAvanzadoAnonimoRecepcion,adapter,false,null,null);

        //setAvanzadoSpinnerColor(spMessageAvanzadoAllowDownloadImage,adapter,false,null,null);

        tvMessageSecretKey.setText(getGrupoSeleccionado().getPassword().getPasswordExtraEncrypt());
    }

    public void renderSpinner(Spinner mySpinner, ConfType defecto, boolean configurable) {
        List<String> myArraySpinner = new ArrayList<String>();
        mySpinner.setVisibility(View.VISIBLE);

        if (configurable) {

            if (defecto.isSuperiorValue()) myArraySpinner.add("✔");
            else myArraySpinner.add("✘");

        }
        myArraySpinner.add("✔");
        myArraySpinner.add("✘");

        ArrayAdapter<String> spinnerArrayAdapter;
        spinnerArrayAdapter = new ArrayAdapter<String>(MessageActivity.this,R.layout.support_simple_spinner_dropdown_item, myArraySpinner);
        mySpinner.setAdapter(spinnerArrayAdapter);
    }
//    private void validarNickname() {
//        if (etMessageAvanzadoNicknameForGrupo.getText().toString().length() > ConstantValidation.USER_NICKNAME_MAX_LENGTH){
//            tvMessageAvanzadoNicknameForGrupoValidacion.setTextColor(Color.RED);
//            tvMessageAvanzadoNicknameForGrupoValidacion.setText(R.string.registro_validation_nickname_too_long);
//
//            return;
//        }
//        tvMessageAvanzadoNicknameForGrupoValidacion.setText("");
//
//    }

    public  void setAvanzadoSpinnerColor(Spinner spinner, boolean configurable, TextView textFijo, ConfType defecto){
        setAvanzadoSpinnerColor(spinner, null,configurable, textFijo,defecto);
    }
    public  void setAvanzadoSpinnerColor(Spinner spinner, RecyclerMessageAdapter adapter, boolean configurable, TextView textFijo, ConfType defecto){

        spinner.setOnItemSelectedListener(new Listener(spinner,adapter,configurable,textFijo,defecto));
    }

    @Override
    public void actualizarLista() {
        messageCustomActionBar.setMainMembersOnLineCount();
        messageCustomActionBar.actualizarConfLockCandadoCerrado();


    }

    @Override
    public void nuevoGrupo(Grupo g) {

    }

    @Override
    public void cambioUnread(String idGrupo) {

    }

    @Override
    public void removeGrupo(String idGrupo) {

    }

    @Override
    public void avisarLock(GrupoDTO g) {
       messageCustomActionBar.actualizarConfLockCandadoCerrado();

    }


    class Listener implements AdapterView.OnItemSelectedListener {
        final Spinner spinner;
        final RecyclerMessageAdapter adapter;
        final boolean configurable;
        final TextView textFijo;
        final int conf;
        final ConfType defecto;
        Listener(Spinner spinner, RecyclerMessageAdapter adapter2, boolean configurable, TextView textFijo,ConfType defecto) {
            this.spinner = spinner;
            adapter = adapter2;
            this.configurable = configurable;
            this.textFijo = textFijo;
            this.defecto = defecto;

            if (!configurable){
                conf=-1;
            }else{
                conf =0;
            }
        }


        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            //if (spinner.isEnabled()) {
            final TextView childAt = (TextView) parent.getChildAt(0);
            if (spinner.getSelectedItemPosition() == (1+conf)) {

                childAt.setTextColor(Color.parseColor("#FF009688"));
                //((TextView) parent.getChildAt(0)).setTextColor(Color.GREEN);
                if (textFijo!=null)textFijo.setTextColor(Color.parseColor("#FF009688"));
            } else if (spinner.getSelectedItemPosition() == (2+conf)) {
                childAt.setTextColor(Color.RED);
                if (textFijo!=null)textFijo.setTextColor(Color.RED);
            } else {
                childAt.setTextColor(Color.BLUE);
                if (textFijo!=null)textFijo.setTextColor(Color.BLUE);
            }
            if (textFijo!=null)textFijo.setText(childAt.getText());



            //}
                /*else{
                    ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);
                }*/


            if (defecto != null) {
                if (defecto.isGanaGrupo()) {
                    spinner.setVisibility(View.GONE);
                    textFijo.setVisibility(View.VISIBLE);
                } else {
                    spinner.setVisibility(View.VISIBLE);
                    textFijo.setVisibility(View.GONE);
                    spinner.setEnabled(true);
                }
            }//else{

            Observers.grupo().getGrupoById(getGrupoSeleccionado().getIdGrupo()).setUserConfDTO(getGrupoUserConfDTO());
//                if (spinner.getId() == spMessageAvanzadoBlackAlways.getId()){
//                    GrupoUserConfDTO configuracion = ObservatorGrupos.getInstance().getGrupoById(grupoSeleccionado).getUserConfDTO();;
//                    configuracion.setBlackMessageAlways(MessageUtil.getGrupoUserConfEnum(spMessageAvanzadoBlackAlways,false));
//                }
//
//                if (spinner.getId() == spMessageAvanzadoBlackRecepcion.getId()){
//                    GrupoUserConfDTO configuracion = ObservatorGrupos.getInstance().getGrupoById(grupoSeleccionado).getUserConfDTO();;
//                    configuracion.setBlackMessageRecived(MessageUtil.getGrupoUserConfEnum(spMessageAvanzadoBlackRecepcion,false));
//
//                }
//
//                if (spinner.getId() == spMessageAvanzadoAnonimoRecepcion.getId()){
//                    GrupoUserConfDTO configuracion = ObservatorGrupos.getInstance().getGrupoById(grupoSeleccionado).getUserConfDTO();;
//                    configuracion.setAnonimoRecived(MessageUtil.getGrupoUserConfEnum(spMessageAvanzadoAnonimoRecepcion,false));
//
//                }

            if (adapter != null) adapter.notifyDataSetChanged();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
            /*
            if (spinner.getVisibility() == View.VISIBLE) {
                GrupoUserConfDTO conf = getGrupoUserConfDTO();

            }*/



    }

    @Override
    public void onCreateContextMenu (ContextMenu menu, View
            v, ContextMenu.ContextMenuInfo menuInfo){

        if (v.getId() == enviarMessageButton.getId()){
            menu.clear();
            menu.setHeaderTitle("Envio Especial");
            if (!getGrupoSeleccionado().getGralConfDTO().getAnonimo().equals(ConfigurationStateEnum.BLOCK)) {
                menu.add(Menu.NONE, 1, Menu.NONE, "Anonimo");
            }
            menu.add(Menu.NONE, 2, Menu.NONE, "Temporal - " + spMessageAvanzadoTimeValues.getSelectedItem().toString());
            menu.add(Menu.NONE, 3, Menu.NONE, "Black");
            if (!getGrupoSeleccionado().getGralConfDTO().getExtraEncrypt().equals(ConfigurationStateEnum.BLOCK)) {
                menu.add(Menu.NONE, 4, Menu.NONE, "Extra Encriptado");
            }


        }
    }



    private void initListenersSend() {
//        enviarEspecial.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                registerForContextMenu(enviarEspecial);
//                openContextMenu(enviarEspecial);
//            }
//        });

        tvMessageSecretKey.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (tvMessageSecretKey.getText().toString().equals("")){
                    extraAesToUse =null;
                }else{
                    extraAesToUse = null;
                    extraAesToUse = MessageAvanzado.aplicarExtraAES(
                            SingletonValues.getInstance().getGrupoSeleccionado(),
                            MessageActivity.this ,
                            tvMessageSecretKey,
                            extraAesToUse,false,null);
                }
                adapter.notifyDataSetChanged();
            }
        });

        enviarMessageButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                longClick=true;
                if (txt.getText().toString().trim().equals("")){

                    if (isViewSendMessageVisible()){
                        chageSendingMode();
                    }
                    if (!isViewSendMessageVisible() &&  thereIsAudioChat() && isButtonSendText ){

                    }else{
                        grabarAudio();

                        ClipData.Item item = new ClipData.Item((Intent) v.getTag());

                        // Create a new ClipData using the tag as a label, the plain text MIME type, and
                        // the already-created item. This will create a new ClipDescription object within the
                        // ClipData, and set its MIME type entry to "text/plain"
                        ClipData dragData = new ClipData(
                                (CharSequence) v.getTag(),
                                new String[] { ClipDescription.MIMETYPE_TEXT_PLAIN },
                                item);

                        // Instantiates the drag shadow builder.


// Creates a new ImageView

                        Drawable a2 = getResources().getDrawable(R.drawable.candadocerrado);

                        //imageView.invalidate();
                        BitmapDrawable drawable = (BitmapDrawable) a2;
                        Bitmap bitmap = drawable.getBitmap();

// Sets the bitmap for the ImageView from an icon bit map (defined elsewhere)


                        View.DragShadowBuilder myShadow = new MyDragShadowBuilder(enviarMessageButton);

                        // Starts the drag

                        v.startDrag(dragData,  // the data to be dragged
                                myShadow,  // the drag shadow builder
                                null,      // no need to use local data
                                0          // flags (not currently used, set to 0)
                        );

                        return true;
                    }
                }
                registerForContextMenu(enviarMessageButton);
                openContextMenu(enviarMessageButton);
                return true;
            }

        });

        enviarMessageButton.setOnDragListener(new View.OnDragListener() {

            // This is the method that the system calls when it dispatches a drag event to the
            // listener.
            public boolean onDrag(View v, DragEvent event) {

                // Defines a variable to store the action type for the incoming event
                final int action = event.getAction();

                // Handles each of the expected events
                if (action == DragEvent.ACTION_DROP || action == DragEvent.ACTION_DRAG_ENDED) {


                    if (event.getY() > messageDeleteAudio.getY() - 100 && event.getY() < messageDeleteAudio.getY() + 3000 &&
                            event.getX() > messageDeleteAudio.getX() +200 && event.getX() < messageDeleteAudio.getX() + 500 ) {

                        isRecordingAudio = false;
                        isPlayingAudio = false;
                        changeButtonSendToSendText();
                        messageSendAudioRow.setVisibility(View.GONE);
                        messageSendTextRow.setVisibility(View.VISIBLE);



                    }else{
                        if (isRecordingAudio){
                            if (isViewSendMessageVisible()){
                                chageSendingMode();
                            }
                            grabarAudio();
                        }

                    }




                }
                return true;

            }});

        View.OnTouchListener speakTouchListener = new View.OnTouchListener() {

            @Override
            public boolean onTouch(View pView, MotionEvent pEvent) {
                pView.onTouchEvent(pEvent);
                // We're only interested in when the button is released.
                if (pEvent.getAction() == MotionEvent.ACTION_UP) {
                    if(longClick && isRecordingAudio){

                        grabarAudio();
                    }
                }
                return true;
            }
        };
        enviarMessageButton.setOnTouchListener(speakTouchListener);

        if (SingletonServer.getInstance().isDeveloper()) {
            findViewById(R.id.mensaje_de_prueba_boton).setOnClickListener(v -> {
                txt.setText("Mensaje de prueba");
                //enviarMessageButtonOnClick();
            });

            findViewById(R.id.mensaje_de_prueba_boton2).setOnClickListener(v -> {
                txt.setText("Mensaje de prueba");
                enviarMessageButtonOnClick();
            });

            Button getHistorialPrueba = (Button) findViewById(R.id.mensaje_prueba_get_historial);
            getHistorialPrueba.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String idMessage=null;
                    if (items.size() > 0){
                        idMessage = items.get(0).getMessage().getIdMessage();
                    }
                    GetMessageHistorialById.loadMessagesContador(MessageActivity.this, MessageActivity.this.getGrupoSeleccionado().getIdGrupo(), idMessage);



                }
            });

        }else{
            findViewById(R.id.mensaje_de_prueba_boton).setVisibility(View.GONE);
            findViewById(R.id.mensaje_de_prueba_boton2).setVisibility(View.GONE);
            findViewById(R.id.mensaje_prueba_get_historial).setVisibility(View.GONE);

        }
        if ( Observers.grupo().amIReadOnly(SingletonValues.getInstance().getGrupoSeleccionado().getIdGrupo())){
            findViewById(R.id.mensaje_de_prueba).setVisibility(View.GONE);

        }

        enviarMessageButton.setOnClickListener(v -> enviarMessageButtonOnClick());

    }

    private void enviarMessageButtonOnClick() {
        longClick=false;
        if (txt.getText().toString().trim().equals("") && bitmapCompleto ==null){

            if (!isViewSendMessageVisible() &&  thereIsAudioChat() && !isButtonSendText ) {
                //changeButtonSendToSendText();

                if (isRecordingAudio) {
                    grabarAudio();
                    return;
                }
            } else if (!isViewSendMessageVisible() &&  thereIsAudioChat() && isButtonSendText ){

            }else{
                if (isViewSendMessageVisible()){
                    chageSendingMode();
                }
                grabarAudio();
                return;
            }

        }

        adapter.notifyDataSetChanged();


        final boolean black;
        final boolean time = getSpinner(spMessageAvanzadoTimeAlways);
        final boolean anonimo = getSpinner(spMessageAvanzadoAnonimoAlways);
        final boolean extraKey = getSpinner(spMessageAvanzadoExtraAesAlways);

        if (time){
            black=false;
        }else{

            black = getSpinner(spMessageAvanzadoBlackAlways);

        }

        if (extraKey){

            if (extraAesToUse ==null){
                extraAesToUse =
                        extraAesToUse = MessageAvanzado.aplicarExtraAES(
                                SingletonValues.getInstance().getGrupoSeleccionado(),
                                MessageActivity.this,
                                tvMessageSecretKey,
                                extraAesToUse, false, new ActionMessageEncryptKeyI() {
                                    @Override
                                    public void action() {
                                        callSendMethod(black, time, anonimo, extraKey);
                                    }
                                });
            }else {
                callSendMethod(black, time, anonimo, extraKey);


            }

        }else {
            try {
                sendMessageMethod(black, time, anonimo, extraKey,getPermitirReenvio());
            } catch (Exception e) {
                e.printStackTrace();
                SimpleErrorDialog.errorDialog(MessageActivity.this,
                        "MENSAJE DE ERROR", e.getMessage());
            }
        }
    }

    private void callSendMethod(boolean black, boolean time, boolean anonimo, boolean personalKey) {
        try {
            sendMessageMethod(black, time, anonimo, personalKey, getPermitirReenvio());
        } catch (Exception e) {
            e.printStackTrace();
            SimpleErrorDialog.errorDialog(MessageActivity.this,
                    "MENSAJE DE ERROR", e.getMessage());
        }
        adapter.notifyDataSetChanged();
    }

    boolean longClick=false;
    private boolean thereIsAudioChat(){
        return (audioList != null && audioList.size() > 0);

    }
    private boolean isViewSendMessageVisible(){
        return messageSendTextRow.getVisibility() == View.VISIBLE;
    }

    boolean isButtonSendText=true;
    private void changeButtonSendToSendText(){
        isButtonSendText=true;
        enviarMessageButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_send_white_24, 0, 0, 0);
    }

    private void changeButtonSendToSendAudio(){
        isButtonSendText=false;
        enviarMessageButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_mic_24, 0, 0, 0);
    }


    private void chageSendingMode(){
        MessageActivity.this.isRecordingAudio=false;
        MessageActivity.this.isPlayingAudio=false;
        audioList = null;
        MessageUtil.segundosCalculados(secondsAudio,audioList);

        if ( isViewSendMessageVisible() && isButtonSendText) {
            MessageUtil.segundosCalculados(secondsAudio, audioList);
            changeButtonSendToSendAudio();
            messageSendAudioRow.setVisibility(View.VISIBLE);
            messageSendTextRow.setVisibility(View.GONE);
        }else if ( !isViewSendMessageVisible() && !isButtonSendText){
            changeButtonSendToSendText();
            messageSendAudioRow.setVisibility(View.GONE);
            messageSendTextRow.setVisibility(View.VISIBLE);
        }
    }





    private static final int CAMERA_REQUEST = 1888;

    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK)
        {



            bitmapCompleto = (Bitmap) data.getExtras().get("data");

            if ((bitmapCompleto.getWidth() * bitmapCompleto.getHeight()) * (1 / Math.pow(1, 2)) >
                    IMAGE_MAX_SIZE_COMPLETO) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                byte[] byteArray = stream.toByteArray();
                bitmapCompleto = getBitmapCamera(getContentResolver(), byteArray,IMAGE_MAX_SIZE_COMPLETO);
            }
            if (bitmapCompleto == null){
                bitmapCompleto = (Bitmap) data.getExtras().get("data");
            }
            bitmapMiniatura = (Bitmap) data.getExtras().get("data");

            if ((bitmapMiniatura.getWidth() * bitmapMiniatura.getHeight()) * (1 / Math.pow(1, 2)) >
                    IMAGE_MAX_SIZE_MINIATURA) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                byte[] byteArray = stream.toByteArray();
                bitmapMiniatura = getBitmapCamera(getContentResolver(), byteArray, IMAGE_MAX_SIZE_MINIATURA);
            }

            ((ImageView)findViewById(R.id.miniatura)).setImageBitmap(bitmapMiniatura);
            View rowattach = findViewById(R.id.rowattach);
            rowattach.setVisibility(View.VISIBLE);
        }
        if (requestCode == GetImage.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();


            try {
                //Getting the Bitmap from Gallery



                bitmapCompleto = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

                if ((bitmapCompleto.getWidth() * bitmapCompleto.getHeight()) * (1 / Math.pow(1, 2)) >
                        IMAGE_MAX_SIZE_COMPLETO/100) {
                    bitmapCompleto = getBitmap(getContentResolver(), filePath, IMAGE_MAX_SIZE_COMPLETO);
                }

                bitmapMiniatura = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

                if ((bitmapCompleto.getWidth() * bitmapCompleto.getHeight()) * (1 / Math.pow(1, 2)) >
                        IMAGE_MAX_SIZE_MINIATURA/100) {
                    bitmapMiniatura = getBitmap(getContentResolver(), filePath,IMAGE_MAX_SIZE_COMPLETO);
                }


                ((ImageView)findViewById(R.id.miniatura)).setImageBitmap(bitmapMiniatura);
                View rowattach = findViewById(R.id.rowattach);
                rowattach.setVisibility(View.VISIBLE);

                //messageAttach.getContenedor().setVisibility(View.GONE);
                //rbMessageSendImage.setChecked(true);
                //Setting the Bitmap to ImageView
                //((ImageView)findViewById(R.id.imagen)).setImageBitmap(bitmap);
            } catch (Exception e) {
                //messageAttach.getContenedor().setVisibility(View.GONE);
                e.printStackTrace();
            }
        }
//        else if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
//            Uri selectedFileUri = data.getData();
//            selectedFilePath = FilePath.getPath(this,selectedFileUri);
//            txt.setText(selectedFilePath);
//            //MediaStore.Files.(getContentResolver(), filePath);
//            //rbMessageSendFile.setChecked(true);
//        }
    }
    private String selectedFilePath;

    private boolean getSpinner(Spinner s){
        if ( s.getSelectedItem().toString().equals("✔")){
            return true;
        }

        return false;
    }

    private boolean getPermitirReenvio(){
        if ( this.spMessageAvanzadoReenvio.getSelectedItem().toString().equals("✔")){
            return true;
        }

        return false;
    }
    private boolean getSiempreAnonimo(){
        if ( this.spMessageAvanzadoAnonimoAlways.getSelectedItem().toString().equals("✔")){
            return true;
        }

        return false;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        try {
            if ( item.getTitle().equals("Eliminar para Mi")) {
                //RestCalls.deleteForMeRest(MessageActivity.this);
            } else if ( item.getTitle().equals("Eliminar para todos")){
                //RestCalls.deleteForEveryone(MessageActivity.this);
            }else if ( item.getTitle().equals("Reenviar")){
                Intent intent = new Intent(MessageActivity.this, MessageResendActivity.class);

                //intent.pu("context", (Parcelable) this);
                startActivity(intent);
            }else if ( item.getTitle().equals("Responder")){
                modoResponder();
            }else if ( item.getTitle().equals("Reintentar")){
                RestCalls.retry(MessageActivity.this);

            }else if ( item.getTitle().equals("Black")){
                sendMessageMethod(true, false, getSiempreAnonimo(), false, getPermitirReenvio());
            }else if ( item.getTitle().equals("Anonimo")){
                sendMessageMethod(false, false, true, false,getPermitirReenvio());
            }else if ( item.getTitle().equals("Temporal - " + spMessageAvanzadoTimeValues.getSelectedItem().toString())) {
                sendMessageMethod(false, true, getSiempreAnonimo(), false,getPermitirReenvio());
            }else if ( item.getTitle().equals("Extra Encriptado")) {

                if (extraAesToUse ==null) {
                    extraAesToUse =MessageAvanzado.aplicarExtraAES(
                            SingletonValues.getInstance().getGrupoSeleccionado(),
                            MessageActivity.this,
                            tvMessageSecretKey,
                            extraAesToUse, false, new ActionMessageEncryptKeyI() {
                                @Override
                                public void action() {
                                    adapter.notifyDataSetChanged();
                                    try {
                                        sendMessageMethod(false, false, getSiempreAnonimo(), true,getPermitirReenvio());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        SimpleErrorDialog.errorDialog(MessageActivity.this,
                                                "MENSAJE DE ERROR", e.getMessage());

                                    }
                                }
                            });
                }else {
                    adapter.notifyDataSetChanged();
                    sendMessageMethod(false, false, getSiempreAnonimo(), true,getPermitirReenvio());

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            SimpleErrorDialog.errorDialog(MessageActivity.this,
                    "MENSAJE DE ERROR", e.getMessage());

        }

        return true;
    }

    private void modoResponder() {

/*        ItemListMessage item = SingletonValues.getInstance().getMessageDetailSeleccionado();
        Observers.message().getMessageSelected().add(item.getMessage());
        item.getRch().getLayoutSelected().setVisibility(View.VISIBLE);*/

    }


    private void sendMessageMethod(boolean black, boolean time, boolean anonimo, boolean extraAES,boolean permitirReenvio) throws Exception {
        Message replyParent=null;
        IdMessageDTO idMessageDTOreply=null;
        boolean reply = false;
        if ( messageReplyFrame.getMessage() != null) {
            replyParent = messageReplyFrame.getMessage();
            idMessageDTOreply = replyParent.buildIdMessageDTO();
            reply=true;
        }
        messageReplyFrame.getViewParent().setVisibility(View.GONE);
        messageReplyFrame.resetValues();
        getGrupoSeleccionado().getWrittingCountDownTimer(this).stop();
        Integer timeSeconds=null;
        if(time){
            timeSeconds = MessageUtil.getGrupoUserConfMessageTimeGetSeconds(MessageActivity.this, spMessageAvanzadoTimeValues);
        }
        MediaDTO mediaDTO = getMediaDTOToSend();
        removeAttach();
        // esto es solo desarrollo
        if (SingletonServer.getInstance().isDeveloper()) {
            txt.setText(txt.getText().toString()
                    + "\n Nickname: " + SingletonValues.getInstance().getUsuario().getNickname()
                    + "\n Counter nextvalue  " + SingletonValues.getInstance().getCounterNextValue());
        }

        if (replyParent != null){
            if ( replyParent.isBlackMessage()){
                black = true;
            }
            if ( replyParent.isSecretKeyPersonal()){
                extraAES = true;
            }

        }



        (new MessageUtil()).sendMessage(MessageActivity.this,
                idMessageDTOreply,
                extraAesToUse,
                getGrupoSeleccionado().getIdGrupo(),
                txt.getText().toString(),
                mediaDTO,
                black, time, anonimo, extraAES,permitirReenvio,false,timeSeconds);
        txt.setText("");
        changeButtonSendToSendText();
        messageSendAudioRow.setVisibility(View.GONE);
        messageSendTextRow.setVisibility(View.VISIBLE);
    }

    @Override
    public void finish() {
        if ( getGrupoSeleccionado() != null && getGrupoSeleccionado().getIdGrupo() != null){
            //ObservatorGrupos.getInstance().avisarCambioUnread(SingletonValues.getInstance().getGrupoSeleccionado().getIdGrupo());
        }

        Observers.message().setMessageOnTop(false);
        Observers.message().dessuscribirse(this);
        Observers.grupo().dessuscribirse(this);
        super.finish();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        this.finish();
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem itemMenu) {
        int id = itemMenu.getItemId();

        if ( id ==R.id.message_empty_list){
            RestCalls.emptyList(MessageActivity.this, getGrupoSeleccionado().getIdGrupo());
        }else if ( id == R.id.menu_message_add_contact){
            Intent intent = new Intent(this, AddMembersToGrupoActivity.class);
            startActivity(intent);
        }else if ( id == R.id.menu_message_load_old_messages){
            String idMessage=null;
            if (items.size() > 0){
                idMessage = items.get(0).getMessage().getIdMessage();
            }
            GetMessageHistorialById.loadMessagesContador(MessageActivity.this, MessageActivity.this.getGrupoSeleccionado().getIdGrupo(), idMessage);


        }else if ( id == R.id.menu_message_grupo_info){
            Intent intent = new Intent(this, GrupoInfoActivity.class);
            startActivity(intent);

        }else{
            if (getGrupoSeleccionado().getIdGrupo() != null){
                //ObservatorGrupos.getInstance().avisarCambioUnread(SingletonValues.getInstance().getGrupoSeleccionado().getIdGrupo());
            }
            Observers.message().dessuscribirse(this);
            SingletonValues.getInstance().setGrupoSeleccionado(null);
            this.finish();
        }

        return super.onOptionsItemSelected(itemMenu);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuBuilder nm = ((MenuBuilder) menu);

        getMenuInflater().inflate(R.menu.menu_message, menu);
        //((MenuBuilder) menu).clearHeader();
/*        if (ObservatorGrupos.getInstance().amIAdmin(grupoSeleccionado)){
            getMenuInflater().inflate(R.menu.menu_message, menu);
        }else{
            getMenuInflater().inflate(R.menu.menu_message_readonly, menu);
        }*/
        return true;

    }

    @Override
    public void nuevoMensaje(ProtocoloDTO protocoloDTO) {

        MessageDTO mP = protocoloDTO.getMessageDTO();


        Message m = Observers.message().getMensajesPorId(mP.buildIdMessageToMap());

        if (m.isReply()){
            adapter.notifyDataSetChanged();
            return;
        }
        if ( protocoloDTO.getAsyncId() != null){
            for (int k = 0 ; k < items.size() ; k++){
                ItemListMessage item = items.get(k);


                //if ( protocoloDTO.getMessageDTO().buildIdMessageToMap().equals(item.getMessage().buildIdMessageToMap())){

                if ( (protocoloDTO.getMessageDTO().idGrupo + "{-}" + protocoloDTO.getAsyncId()).equals(item.getMessage().buildIdMessageToMap())){
                    item.setMessage(m);

                    item.setMessage((Message)protocoloDTO.getMessageDTO());

                    for (int i = 0; i < m.getMessagesDetailDTO().length; i++) {
                        if (m.getMessagesDetailDTO()[i].getUsuarioDestino().getIdUsuario().equals(SingletonValues.getInstance().getUsuario().getIdUsuario())) {
                            item.setMessageDetailDTO(m.getMessagesDetailDTO()[i]);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        }else {
            if (!mP.getIdGrupo().equals(getGrupoSeleccionado().getIdGrupo())) return;
            for (int i = 0; i < m.getMessagesDetailDTO().length; i++) {
                if (m.getMessagesDetailDTO()[i].getUsuarioDestino().getIdUsuario().equals(SingletonValues.getInstance().getUsuario().getIdUsuario())) {
                    ItemListMessage n = new ItemListMessage(m, m.getMessagesDetailDTO()[i]);

                    if (items != null) {
                        items.add(n);
                        sortById(items);


                        int newIndex = items.indexOf(n);

                        rvLista.scrollToPosition(newIndex);

                        adapter.notifyDataSetChanged();
                        return;
                    }
                }
            }
        }
    }
    private  void sortById(List<ItemListMessage> items)
    {

        items.sort(new Comparator<ItemListMessage>() {
            @Override
            public int compare(ItemListMessage o1, ItemListMessage o2) {
                try {

                if ( o1.getMessage().getMessagesDetailDTO()[0].getEstado().equals(MessageState.MY_MESSAGE_SENDING) &&
                        o2.getMessage().getMessagesDetailDTO()[0].getEstado().equals(MessageState.MY_MESSAGE_SENDING)){

                    int value =  o1.getMessage().getIdMessage().compareTo(o2.getMessage().getIdMessage());
                    return value;

                }
                if (o1.getMessage().getMessagesDetailDTO()[0].getEstado().equals(MessageState.MY_MESSAGE_SENDING)){
                    return 1;
                };
                if (o2.getMessage().getMessagesDetailDTO()[0].getEstado().equals(MessageState.MY_MESSAGE_SENDING)){
                    return -1;
                };

                int value =  o1.getMessage().getIdMessage().compareTo(o2.getMessage().getIdMessage());
                return value;
                }catch (Exception e){
                    e.printStackTrace();

                }
                return 1;
            }
        });
    }
    @Override
    public void cambioEstado(MessageDetailDTO m) {
        if (adapter == null) return;
        adapter.notifyDataSetChanged();
       for ( int i=0; i < adapter.getItemCount(); i++){
           if (ObserverMessage.getInstance().getMensajesPorId(items.get(i).getMessage().buildIdMessageToMap()) == null) return;

           MessageDetailDTO[] md = ObserverMessage.getInstance().getMensajesPorId(items.get(i).getMessage().buildIdMessageToMap()).getMessagesDetailDTO();
           for ( int j=0; j <  md.length; j++) {
               if (md[j].getUsuarioDestino().getIdUsuario().equals( SingletonValues.getInstance().getUsuario().getIdUsuario())){
                   items.get(i).messageDetailDTO = (md[j]);
                   adapter.notifyItemChanged(i);
               }
           }

        }


        adapter.notifyDataSetChanged();
    }

    @Override
    public void emptyList() {

        items.clear();

        if (adapter == null) return;

        adapter.notifyDataSetChanged();
    }

    @Override
    public void mensajeAddItem(MessageDTO miMensaje, String asyncId) {
        if (miMensaje.getIdGrupo().equals(getGrupoSeleccionado().getIdGrupo())){
            ItemListMessage n = new ItemListMessage(new Message(miMensaje), miMensaje.getMessagesDetailDTO()[0]);
            //n.setAsyncId(asyncId);
            items.add(n);
            sortById(items);
            rvLista.scrollToPosition(items.size()-1);
        }

    }

    @Override
    public void borrarMensaje(MessageDetailDTO detail) {
        for ( int i = items.size()-1 ; i>=0 ; i--){
            if (items.get(i).getMessageDetailDTO().buildIdMessageToMap().equals(detail.buildIdMessageToMap())){
                items.remove(i);
                adapter.notifyDataSetChanged();
                return;
            }
        }
    }

    @Override
    public void writting(WrittingDTO w) {
        if (!getGrupoSeleccionadoIsNull() && getGrupoSeleccionado().isOtherAreWritting()){
            messageCustomActionBar.setMainSubtitleWriting(true);
        }
    }


    @Override
    public void writtingStop(WrittingDTO w) {

        if (!getGrupoSeleccionadoIsNull() && getGrupoSeleccionado().getIdGrupo().equals(w.getIdGrupo())){
            messageCustomActionBar.setMainSubtitleWriting(false);
        }
    }

    public static final int SAMPLE_RATE = 44100; // supported on all devices
    public static final int CHANNEL_CONFIG_IN = AudioFormat.CHANNEL_IN_MONO;
    public static final int CHANNEL_CONFIG_OUT = AudioFormat.CHANNEL_OUT_MONO;
    //public static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_8BIT; // not supported on all devices
    public static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_8BIT; // not supported on all devices
    public static final int BUFFER_SIZE_RECORDING = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG_IN, AUDIO_FORMAT);
    public static final int BUFFER_SIZE_PLAYING = AudioTrack.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG_OUT, AUDIO_FORMAT);
    boolean isRecordingAudio=false;
    AudioRecord audioRecord=null;
    File fileAudio=null;
    private Thread recordingThread;
    String fileNameAudio;


    private void initViews(){


        messageSendRowComplete = (TableRow) findViewById(R.id.tr_message_send_complete);
        messageSendRowReadOnly = (TableRow) findViewById(R.id.tr_message_send_readonly);

        //View menuAddContact = findViewById(R.id.menu_message_add_contact);

        if (Observers.grupo().amIReadOnly(getGrupoSeleccionado().getIdGrupo())){
            messageSendRowReadOnly.setVisibility(View.VISIBLE);
            messageSendRowComplete.setVisibility(View.GONE);
            //menuAddContact.setVisibility(View.GONE);
        }else{
            messageSendRowReadOnly.setVisibility(View.GONE);
            messageSendRowComplete.setVisibility(View.VISIBLE);
        }

        messageSendAudioRow = (LinearLayout) findViewById(R.id.tr_message_send_audio);
        messageSendTextRow = (LinearLayout) findViewById(R.id.tr_message_send_text);
        auxiliarRecordAudioAnimation = (SeekBar) findViewById(R.id.auxiliar);



        auxiliarRecordAudioAnimation.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 50){
                    changeButtonSendToSendText();
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



        messageDeleteAudio = (ImageButton) findViewById(R.id.bt_message_delete_audio);
        messageDeleteAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRecordingAudio=false;
                isPlayingAudio=false;
                changeButtonSendToSendAudio();
                chageSendingMode();

            }
        });

        fileNameAudio = getFilesDir().getPath() + "/testfile" + ".wav";
//        rbMessageSendImage = (RadioButton) findViewById(R.id.rb_message_send_imagen);
//        rbMessageSendFile = (RadioButton) findViewById(R.id.rb_message_send_file);
        removeAttach = (ImageButton) findViewById(R.id.remove_attach);

        removeAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeAttach();
            }
        });


        txt = (EditText) findViewById(R.id.enviar_texto2);
        txt.setText(SingletonValues.getInstance().getGrupoSeleccionado().getMessageInProcess().getTxt());

        secondsAudio = (TextView) findViewById(R.id.bt_message_seconds_audio);
        secondsAudioOriginalColor = secondsAudio.getCurrentTextColor();
        MessageUtil.segundosCalculados(secondsAudio,audioList);

        txt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(2048)});

        txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!SingletonValues.getInstance().getSystemGralConf().isMessagingWritingMessage()) return;

                SingletonValues.getInstance().getGrupoSeleccionado().getMessageInProcess().setTxt(txt.getText().toString());
                if (!MessageActivity.this.getGrupoSeleccionado().isIamWritting()){

                    try {
                        if (!txt.getText().toString().equals("")) {
                            if ( !getGrupoSeleccionado().getWrittingCountDownTimer(MessageActivity.this).isCountDownTimerRunning()){
                                WrittingCallRest.callRestWritting(MessageActivity.this, getGrupoSeleccionado());
                            }


                            getGrupoSeleccionado().getWrittingCountDownTimer(MessageActivity.this).restart();

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        try {
                            getGrupoSeleccionado().getWrittingCountDownTimer(MessageActivity.this).stop();
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }
                }else{
                    getGrupoSeleccionado().getWrittingCountDownTimer(MessageActivity.this).restart();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //grupo.getWrittingCountDownTimer(MessageActivity.this).stop();
            }
        });

        hidetxt = (ImageButton) findViewById(R.id.hide_txt);



        showtxt = (ImageButton) findViewById(R.id.show_txt);


        ((ImageButton) findViewById(R.id.bt_message_play_audio)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play2();
            }
        });

        eyeListener(hidetxt, showtxt, txt);

        refreshMensaje = (Button) findViewById(R.id.refresh_mensaje);

        final EmojiPopup popup = EmojiPopup.Builder
                .fromRootView(findViewById(R.id.rootView)).build(txt);
        btnAttach = (ImageButton) findViewById(R.id.btn_attach);
        btnEmojis = (ImageButton) findViewById(R.id.bt_emoji);

        btnEmojis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup.toggle();
            }
        });

        btnAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangeVisibility.changeState(messageAttach.getContenedor());
            }
        });

        refreshMensaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RestCalls.loadMessagesContador(MessageActivity.this);
            }
        });
        enviarMessageButton = (Button) findViewById(R.id.enviar2);
        enviarMessageButton.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4096)});

        enviarFile = (Button) findViewById(R.id.enviar_file);
        //enviarEspecial = (Button) findViewById(R.id.btn_message_send_especial);


        tvMessageSecretKey = (EditText) findViewById(R.id.tv_message_secret_key);

        rvLista = findViewById(R.id.rv_message_detail_list);

        ActivityCompat.requestPermissions(MessageActivity.this,
                new String[]{Manifest.permission.RECORD_AUDIO},
                enviarMessageButton.getId());

    }

    private void callRestWritting(Grupo grupo) throws Exception {
        WrittingDTO dto = new WrittingDTO();
        dto.setNickname(SingletonValues.getInstance().getUsuario().getNickname());
        dto.setIdGrupo(grupo.getIdGrupo());
        WrittingCallRest.call(this, dto);


    }


    private void removeAttach() {
        View rowattach = findViewById(R.id.rowattach);
        bitmapCompleto =null;
        rowattach.setVisibility(View.GONE);

        messageAttach.getContenedor().setVisibility(View.GONE);
    }

    private void grabarAudio() {
        try {
            if (!isRecordingAudio){
                //enviarMessage.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_outline_stop_circle_white_24, 0, 0, 0);
                audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, CHANNEL_CONFIG_IN, AUDIO_FORMAT, BUFFER_SIZE_RECORDING);

                audioRecord.startRecording();
                isRecordingAudio=true;

                auxiliarRecordAudioAnimation.setProgress(0);
                recordingThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        writeAudioDataToFile();
                    }
                });





                recordingThread.start();
            }else{
                changeButtonSendToSendText();
                secondsAudio.setTextColor(secondsAudioOriginalColor);

                isRecordingAudio=false;
//                        audioRecord.stop();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initValues() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rvLista.setLayoutManager(manager);
        if (items != null) emptyList();
        buildItems();

    }

    private void initListener() {




/*
        rbMessageSendFile.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (radioFile==true) {
                    selectedFilePath = null;
                    archivo = null;
                    radioFile=false;
                } else {
                    radioFile=false;
                    //rbMessageSendImage.setChecked(false);
                    Intent intent = new Intent();
                    //sets the select file to all types of files
                    intent.setType("*" +
                            "/*");
                    //allows to select data and return it
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    //starts new activity to select file and return data
                    startActivityForResult(Intent.createChooser(intent, "Choose File to Upload.."), PICK_FILE_REQUEST);
                }
            }

        });
*/

    }

    @Override
    protected void onResume() {

        super.onResume();
        Observers.message().setMessageOnTop(true);
        initValues();


        //messageCustomActionBar.onResumeActionBar();

        this.cleanSelectedItems();
        messageCustomActionBar.setOnActionBarMain();
        adapter.notifyDataSetChanged();
        writting(null);
    }

    protected void onStop() {
        super.onStop();
        Observers.message().setMessageOnTop(false);
        writting(null);
    }

    private void buildItems() {


        List<MessageDetailDTO> lista = Observers.message().getMensajesDetailsPorGrupo(
                getGrupoSeleccionado().getIdGrupo()
        );


        for ( MessageDetailDTO e : lista) {
            if (e.getUsuarioDestino().getIdUsuario().equals(SingletonValues.getInstance().getUsuario().getIdUsuario())) {


                MessageDTO m = Observers.message().getMensajesPorId(e.buildIdMessageToMap());

                boolean reciboBlack = (!SingletonValues.getInstance().getUsuario().getIdUsuario().equals(getIdUsuario(m.getUsuarioCreacion()))
                        &&
                        MasterGeneralConfiguration.buildSiempreBlackReceptionConfigurationByGrupo(getGrupoSeleccionado().getIdGrupo()).isValue()
                );


                if (m != null) {
                    if (!m.isBlackMessage() && !m.isTimeMessage() &&
                            !(reciboBlack)
                    ) {

                        Observers.message().cambiarEstadoUso(e, false, this);
                    }


                    items.add(
                            new ItemListMessage(Observers.message().getMensajesPorId(e.buildIdMessageToMap()),
                                    e)
                    );
                }
            }

        }
        sortById(items);
    }


    @Override
    public void itemClick(ItemListMessage item) {
        if (item.isOcularDetails()) return;

        SingletonValues.getInstance().setMessageDetailSeleccionado(item);
        Intent intent = new Intent(this, MessageDetailActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.filter(newText);
        return false;
    }

    private MediaDTO getMediaDTOToSend() {
        MediaDTO mediaDTO = null;
        if (bitmapCompleto != null) {
            mediaDTO = new MediaDTO();

            {
                if (bitmapCompleto.isRecycled()){

                    int size     = bitmapCompleto.getRowBytes() * bitmapCompleto.getHeight();
                    ByteBuffer b = ByteBuffer.allocate(size);

                    bitmapCompleto.copyPixelsToBuffer(b);

                    byte[] bytes = new byte[size];


                        b.get(bytes, 0, bytes.length);
                    mediaDTO.setData(bytes);
                }else {


                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmapCompleto.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    //String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

                    mediaDTO.setData(byteArray);
                }
                mediaDTO.setMediaType(MediaTypeEnum.IMAGE);
            }

            {
                if (bitmapMiniatura.isRecycled()){

                    int size     = bitmapMiniatura.getRowBytes() * bitmapMiniatura.getHeight();
                    ByteBuffer b = ByteBuffer.allocate(size);

                    bitmapMiniatura.copyPixelsToBuffer(b);

                    byte[] bytes = new byte[size];


                    b.get(bytes, 0, bytes.length);
                    mediaDTO.setMiniatura(bytes);
                }else {

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmapMiniatura.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    //String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

                    mediaDTO.setMiniatura(byteArray);
                }
            }
        }

        if (audioList != null && audioList.size() > 0) {
            mediaDTO = new MediaDTO();

            byte[] byteArray = new byte[audioList.size()];
            for (int i = 0 ; i<byteArray.length ; i++ ){
                byteArray[i] = (byte)audioList.get(i);

            }
            audioList.clear();

            mediaDTO.setData(byteArray);
            mediaDTO.setMediaType(MediaTypeEnum.AUDIO_MESSAGE);
        }


        return mediaDTO;
    }

    //final int IMAGE_MAX_SIZE = 1200000;
    final int IMAGE_MAX_SIZE_COMPLETO = 1200000;
    final int IMAGE_MAX_SIZE_MINIATURA = 50000;

    private Bitmap getBitmapCamera(   ContentResolver mContentResolver , byte[] b, int imagenMaxSize) {

        InputStream in = new ByteArrayInputStream(b);
        try {
            // 1.2MP


            // Decode image size
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, options);
            in.close();



            int scale = 1;
            while ((options.outWidth * options.outHeight) * (1 / Math.pow(scale, 2)) >
                    imagenMaxSize) {
                scale++;
            }
            Log.d(TAG, "scale = " + scale + ", orig-width: " + options.outWidth + ",  orig-height: " + options.outHeight);

            Bitmap resultBitmap = null;
            in = new ByteArrayInputStream(b);
            if (scale > 1) {
                scale--;
                // scale to max possible inSampleSize that still yields an image
                // larger than target
                options = new BitmapFactory.Options();
                options.inSampleSize = scale;
                resultBitmap = BitmapFactory.decodeStream(in, null, options);

                // resize to desired dimensions
                int height = resultBitmap.getHeight();
                int width = resultBitmap.getWidth();
                Log.d(TAG, "1th scale operation dimenions - width: " + width + ",   height: " + height);

                double y = Math.sqrt(imagenMaxSize
                        / (((double) width) / height));
                double x = (y / height) * width;

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(resultBitmap, (int) x,
                        (int) y, true);
               // resultBitmap.recycle();
                resultBitmap = scaledBitmap;

                System.gc();
            } else {
                resultBitmap = BitmapFactory.decodeStream(in);
            }
            in.close();

            //Log.d(TAG, "bitmap size - width: " +resultBitmap.getWidth() + ", height: " +
            //        resultBitmap.getHeight());
            return resultBitmap;
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(),e);
            return null;
        }
    }

    private Bitmap getBitmap(   ContentResolver mContentResolver , Uri uri, int imageMaxSize) {


        InputStream in = null;
        try {

            in = mContentResolver.openInputStream(uri);

            // Decode image size
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, options);
            in.close();



            int scale = 1;
            while ((options.outWidth * options.outHeight) * (1 / Math.pow(scale, 2)) >
                    imageMaxSize) {
                scale++;
            }
            Log.d(TAG, "scale = " + scale + ", orig-width: " + options.outWidth + ",  orig-height: " + options.outHeight);

            Bitmap resultBitmap = null;
            in = mContentResolver.openInputStream(uri);
            if (scale > 1) {
                scale--;
                // scale to max possible inSampleSize that still yields an image
                // larger than target
                options = new BitmapFactory.Options();
                options.inSampleSize = scale;
                resultBitmap = BitmapFactory.decodeStream(in, null, options);

                // resize to desired dimensions
                int height = resultBitmap.getHeight();
                int width = resultBitmap.getWidth();
                Log.d(TAG, "1th scale operation dimenions - width: " + width + ",   height: " + height);

                double y = Math.sqrt(imageMaxSize
                        / (((double) width) / height));
                double x = (y / height) * width;

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(resultBitmap, (int) x,
                        (int) y, true);
              //  resultBitmap.recycle();
                resultBitmap = scaledBitmap;

                System.gc();
            } else {
                resultBitmap = BitmapFactory.decodeStream(in);
            }
            in.close();

            Log.d(TAG, "bitmap size - width: " +resultBitmap.getWidth() + ", height: " +
                    resultBitmap.getHeight());
            return resultBitmap;
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(),e);
            return null;
        }
    }
    public String getIdUsuario(UsuarioDTO u){
        if (u == null) return null;

        return u.getIdUsuario();
    }



    private void eyeListener(ImageButton eyeHide, ImageButton eyeShow, EditText field){


        eyeShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (HideReturnsTransformationMethod.getInstance().equals(field.getTransformationMethod())){
                    field.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }else{
                    field.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                eyeHide.setVisibility(View.GONE);
                eyeShow.setVisibility(View.VISIBLE);


            }

        });
        eyeHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (HideReturnsTransformationMethod.getInstance().equals(field.getTransformationMethod())){
                    field.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }else{
                    field.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }

                eyeHide.setVisibility(View.VISIBLE);
                eyeShow.setVisibility(View.GONE);

            }

        });
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        System.out.println("ERRRRRRRRRRRRRRRRRRRRRRRROR");
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }
    ArrayList<Object> audioList = new ArrayList<Object>();


    private void writeAudioDataToFile() { // called inside Runnable of recordingThread
//        System.out.println(fileNameAudio);
//        fileAudio = new File(fileNameAudio);
//        try {
//            fileAudio.createNewFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        byte[] data = new byte[BUFFER_SIZE_RECORDING / 2]; // assign size so that bytes are read in in chunks inferior to AudioRecord internal buffer size

        //ileOutputStream outputStream = null;

//        try {
//            outputStream = new FileOutputStream(fileNameAudio);
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//            Toast.makeText(this, "Couldn't find file to write to", Toast.LENGTH_SHORT).show();
//            Log.e("aa", "file not found for file name , " + e.toString());
//            return;
//        }
        //android.util.StateSet.
        enviarMessageButton.getBackgroundTintList().withAlpha(0);
        int originalBackground = -12350493; // estoy hay que cambiarlo sale de enviarMessage.getBackgroundTintList().toString()

        audioList = new ArrayList<Object>();

        boolean colorFlag=true;
        int colorFlagCounter=0;

        MessageUtil.segundosCalculados(secondsAudio,audioList);

        while (isRecordingAudio) {
            int read = audioRecord.read(data, 0, data.length);
            try {

                for(int i = 0; i < data.length; i++)
                {

                    audioList.add(data[i]);
                }

                if (colorFlagCounter == 35) {
                    if (colorFlag) {
                        enviarMessageButton.setBackgroundColor(Color.RED); //-12350493

                        colorFlagCounter=0;
                        colorFlag=false;
                    }else {
                        enviarMessageButton.setBackgroundColor(originalBackground);

                        colorFlagCounter=0;
                        colorFlag=true;
                    }
                    int seg = MessageUtil.segundosCalculados(secondsAudio,audioList);

                    int maxSeconds = SingletonValues.getInstance().getSystemGralConf().getMyAccountConf().getAudioMaxTimeInSeconds();

                    if (seg >= maxSeconds ){
                        isRecordingAudio=false;
                        secondsAudio.setTextColor(secondsAudioOriginalColor);

                        auxiliarRecordAudioAnimation.setProgress(50);
                    } else if ( seg >= maxSeconds * 0.7){
                        secondsAudio.setTextColor(Color.RED);
                    }
                }


                colorFlagCounter++;


                //outputStream.write(data, 0, read);

            } catch (Exception e) {

                //Toast.makeText(this, "Couldn't write to file while recording", Toast.LENGTH_SHORT).show();
                Log.d("eew", "IOException while recording with AudioRecord, " + e.toString());
                e.printStackTrace();
            }
        }

//        try { // clean up file writing operations
//            outputStream.flush();
//            outputStream.close();
//        } catch (IOException e) {
//            Log.e("dada", "exception while closing output stream " + e.toString());
//            e.printStackTrace();
//        }

        audioRecord.stop();
        audioRecord.release();
        enviarMessageButton.setBackgroundColor(originalBackground);
        //auxiliarRecordAudioAnimation.setProgress(50);
        audioRecord = null;
        recordingThread = null;


    }

    String TAG="ddd";
    boolean isPlayingAudio=false;

    private void play2(){

//        MediaPlayer mediaPlayer = MediaPlayer.create(getApplication(), ((Button) findViewById(R.id.play)).getId());

//       MediaPlayer mediaPlayer = MediaPlayer.create(getApplication(), ((Button) findViewById(R.id.play)).getId());

        if ( !isPlayingAudio){
            try {
                AudioTrack audioTrack = new AudioTrack(
                        new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SPEECH).setUsage(AudioAttributes.USAGE_MEDIA).build(),
                        new AudioFormat.Builder()
                                .setEncoding(AudioFormat.ENCODING_PCM_8BIT)
                                .setSampleRate(SAMPLE_RATE)
                                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                                .build(),
                        BUFFER_SIZE_PLAYING,
                        AudioTrack.MODE_STREAM,
                        AudioManager.AUDIO_SESSION_ID_GENERATE
                );

                if (audioTrack.getState() != AudioTrack.STATE_INITIALIZED) {
                    Toast.makeText(this, "Couldn't initialize AudioTrack, check configuration", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "error initializing AudioTrack");
                    return;
                }

                audioTrack.play();
                Log.d(TAG, "playback started with AudioTrack");

                isPlayingAudio = true;

                Thread playingThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        readAudioDataFromFile2(audioTrack);
                    }
                });
                playingThread.start();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
    private void readAudioDataFromFile2(AudioTrack audioTrack) { // called inside Runnable of playingThread






        int i = 0;
        int k=0;
        while (isPlayingAudio && (i != -1)) { // continue until run out of data or user stops playback
            byte[] data2 = new byte[BUFFER_SIZE_PLAYING/2];
            int j=0;
            for (; j < data2.length && k < audioList.size() ; j++){
                data2[j] = ((byte)audioList.get(k));
                k++;
            }

            if ( k >= audioList.size()){

                i =-1;
            }

            audioTrack.write(data2, 0, j);

        }
        audioTrack.stop();
        audioTrack.release();
        // clean up resources
        isPlayingAudio = false;


    }
    @Override
    public void passwordExpired(Grupo g) {
        if (SingletonValues.getInstance().getGrupoSeleccionado().getIdGrupo().equals(g.getIdGrupo())) {
            finish();
        }
    }
    @Override
    public void passwordSet(Grupo g) {

    }



    @Override
    public void deleteExtraEncrypt(Grupo g){
        if (getGrupoSeleccionado().getIdGrupo().equals(g.idGrupo)){
            extraAesToUse = null;
            tvMessageSecretKey.setText("");

        }
    }

    @Override
    public void lock(Grupo g) {

    }


}
