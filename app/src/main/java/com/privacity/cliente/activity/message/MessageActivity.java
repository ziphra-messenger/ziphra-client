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
import android.content.res.ColorStateList;
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
import android.widget.Button;
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
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.addmember.AddMembersToGrupoActivity;
import com.privacity.cliente.activity.common.GetButtonReady;
import com.privacity.cliente.activity.common.GrupoSelectedCustomAppCompatActivity;
import com.privacity.cliente.activity.grupoinfo.GrupoInfoActivity;
import com.privacity.cliente.activity.message.attach.GetImage;
import com.privacity.cliente.activity.message.attach.MessageAttach;
import com.privacity.cliente.activity.message.avanzado.MessageAvanzado;
import com.privacity.cliente.activity.message.customactionbar.MessageCustomActionBar;
import com.privacity.cliente.activity.message.customactionbar.MessageReplyFrame;
import com.privacity.cliente.activity.message.messageconfig.AnonimoConfig;
import com.privacity.cliente.activity.message.textsizemessage.TextSizeMessageView;
import com.privacity.cliente.activity.message.utils.BitMapTools;
import com.privacity.cliente.activity.message.validations.MessageValidations;
import com.privacity.cliente.activity.messagedetail.MessageDetailActivity;
import com.privacity.cliente.activity.messageresend.MessageResendActivity;
import com.privacity.cliente.activity.messageresend.MessageUtil;
import com.privacity.cliente.common.component.SecureFieldAndEye;
import com.privacity.cliente.activity.message.envioespecial.MessageEnvioEspecial;
import com.privacity.cliente.common.error.SimpleErrorDialog;
import com.privacity.cliente.encrypt.AEStoUse;
import com.privacity.cliente.frame.error.ErrorPojo;
import com.privacity.cliente.frame.error.ErrorView;
import com.privacity.cliente.includes.CallBackSecureField;
import com.privacity.cliente.includes.SecureFieldAndEyeUtil;
import com.privacity.cliente.model.Grupo;
import com.privacity.cliente.model.Message;
import com.privacity.cliente.rest.restcalls.grupo.WrittingCallRest;
import com.privacity.cliente.rest.restcalls.message.GetMessageHistorialById;
import com.privacity.cliente.singleton.Observers;
import com.privacity.cliente.singleton.SingletonValues;import com.privacity.cliente.singleton.Singletons;
import com.privacity.cliente.singleton.activity.SingletonCurrentActivity;
import com.privacity.cliente.singleton.impl.SingletonServer;
import com.privacity.cliente.singleton.interfaces.ObservadoresGrupos;
import com.privacity.cliente.singleton.interfaces.ObservadoresMensajes;
import com.privacity.cliente.singleton.localconfiguration.SingletonTextSizeMessage;
import com.privacity.cliente.singleton.observers.ObserverMessage;
import com.privacity.cliente.singleton.reconnect.SingletonReconnect;
import com.privacity.cliente.singleton.serverconfiguration.SingletonServerConfiguration;
import com.privacity.common.BroadcastConstant;
import com.privacity.common.dto.IdMessageDTO;
import com.privacity.common.dto.MediaDTO;
import com.privacity.cliente.model.dto.MessageDetail;
import com.privacity.cliente.model.dto.Protocolo;
import com.privacity.common.dto.UsuarioDTO;
import com.privacity.common.dto.WrittingDTO;
import com.privacity.common.enumeration.ConfigurationStateEnum;
import com.privacity.common.enumeration.MediaTypeEnum;
import com.privacity.common.enumeration.MessageState;
import com.vanniktech.emoji.EmojiPopup;

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
import lombok.SneakyThrows;

public class MessageActivity extends GrupoSelectedCustomAppCompatActivity
        implements ObservadoresMensajes, ObservadoresGrupos,
        RecyclerMessageAdapter.RecyclerItemClick, SearchView.OnQueryTextListener,
        View.OnCreateContextMenuListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {

    public static final String CONSTANT_EXTRA_DATA = "data";
    @Getter
    MessageCustomActionBar messageCustomActionBar;
    @Getter
    MessageReplyFrame messageReplyFrame;

    //private boolean reply=false;
    private MessageAttach messageAttach;



    private ImageButton avanzada2;


    private RecyclerView rvLista;


    @Getter
    private RecyclerMessageAdapter adapter;

    @Getter
    private final List<ItemListMessage> items = new CopyOnWriteArrayList<>();

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
 
    public AEStoUse extraAesToUse;
    //private Button btMessageSecretKeyAplicar;
    private View viewDialogAddUser;
    private ImageButton avanzada;

    //private Spinner spinnerTime;
    final int[] actualValues={15,30,60,300,600,3000, 60000};

    private Button enviarFile;
    //    private RadioButton rbMessageSendImage;
//    private RadioButton rbMessageSendFile;
    private Button refreshMensaje;
    private ImageButton btnEmojis;
    private ImageButton btnAttach;

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




    private TextSizeMessageView textSizeMessageView;

    private MessageAvanzado messageAvanzado;
    private MessageEnvioEspecial envioEspecial;

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
            initListener();


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





        registerForContextMenu(rvLista);



        initListenersSend();


        initValues();
        adapter.notifyDataSetChanged();

        initMessageAttach();

        if (items.size() != 0 ) {
            rvLista.scrollToPosition(items.size() - 1);
        }
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

        textSizeMessageView = new TextSizeMessageView(this);
    }

    private void initSecureFieldAndEye() {

        SecureFieldAndEye messageEye = new SecureFieldAndEye(
                null,(EditText) findViewById(R.id.enviar_texto2),
                (ImageButton) findViewById(R.id.show_eye_txt),
                (ImageButton) findViewById(R.id.hide_txt)

        );


        SecureFieldAndEyeUtil.listener(messageEye, false, new CallBackSecureField() {
            @Override
            public void showAction() {
                for (ItemListMessage i : items){
                    SingletonValues.getInstance().getGrupoSeleccionado().setSecureFieldActivated(false);
                    try {
                        i.rch.getTvMessageListText().setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        i.rch.getTvMessageListText().setLetterSpacing(0);
//                        i.rch.getTvMessageListText().setMaxLines(Integer.MAX_VALUE);
                    } catch (Exception e) {

                    }
                    try {
                        ImageViewCompat.setImageTintList(i.rch.getIvItemListImageMedia(), null);
                    } catch (Exception e) {

                    }

                    //.   gemUsername.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }

            @Override
            public void hideAction() {
                SingletonValues.getInstance().getGrupoSeleccionado().setSecureFieldActivated(true);
                for (ItemListMessage i : items){
                    try {
/*
                        if (i.rch.getTvMessageListText().getLineCount() != 0 ) {
                            i.rch.getTvMessageListText().setMaxLines(i.rch.getTvMessageListText().getLineCount());
*/
                            i.rch.getTvMessageListText().setTransformationMethod(PasswordTransformationMethod.getInstance());

                            i.rch.getTvMessageListText().setLetterSpacing((float) 0.177);
                    //    }

                    } catch (Exception e) {

                    }

                    try {
                        ColorStateList csl = AppCompatResources.getColorStateList(MessageActivity.this, R.color.black);
                        ImageViewCompat.setImageTintList(i.rch.getIvItemListImageMedia(), csl);
                    } catch (Exception e) {

                    }

                    //.   gemUsername.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }

            }
        });
        if (SingletonValues.getInstance().getGrupoSeleccionado().isSecureFieldActivated()){
            messageEye.getEyeHide().setVisibility(View.GONE);
            messageEye.getEyeShow().setVisibility(View.VISIBLE);
        }else{
            messageEye.getEyeHide().setVisibility(View.VISIBLE);
            messageEye.getEyeShow().setVisibility(View.GONE);
        }




    }

    private void initMessageAttach() {
        messageAttach = new MessageAttach(this);
        messageAttach.setListener();

    }

    ArrayList<Byte> archivo;






    private void setBroadcast() {
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent intent) {
                String action = intent.getAction();
                if (action.equals(BroadcastConstant.BROADCAST__FINISH_MESSAGE_ACTIVITY)) {
                    finish();
                }
                if (action.equals(BroadcastConstant.BROADCAST__RELOAD_CONFIGURACION_AVANZADA_MESSAGE_ACTIVITY)) {
                    //messageAvanzado=null;
                }

            }
        };

        registerReceiver(broadcastReceiver, new IntentFilter(BroadcastConstant.BROADCAST__FINISH_MESSAGE_ACTIVITY));
        registerReceiver(broadcastReceiver, new IntentFilter(BroadcastConstant.BROADCAST__RELOAD_CONFIGURACION_AVANZADA_MESSAGE_ACTIVITY));
    }

    public MessageAvanzado getMessageAvanzado() {
        return messageAvanzado;
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
    public void avisarLock(Grupo g) {
        messageCustomActionBar.actualizarConfLockCandadoCerrado();

    }

    @Override
    public void avisarRoleChange(Grupo g) {

        if (getGrupoSeleccionado().getIdGrupo().equals(g.getIdGrupo())) {
            Intent intent = new Intent(BroadcastConstant.BROADCAST__FINISH_GRUPO_INFO_ACTIVITY);
            this.sendBroadcast(intent);

            messageAvanzadoReset();
            roleViewSetup();
        }
        
    }

    private void messageAvanzadoReset() {
        if (getMessageAvanzado() != null){
            getMessageAvanzado().reset();
        }
    }

    @Override
    public void avisarCambioGrupoGralConf(Grupo g) {
        if (getGrupoSeleccionado().getIdGrupo().equals(g.getIdGrupo())) {

            messageAvanzadoReset();


            Intent intent = new Intent(BroadcastConstant.BROADCAST__FINISH_GRUPO_INFO_ACTIVITY);
            this.sendBroadcast(intent);



        }

    }


    @Override
    public void onCreateContextMenu (ContextMenu menu, View
            v, ContextMenu.ContextMenuInfo menuInfo){


    }


    private boolean showErrorSinInternet(Activity activity) {
        if (!SingletonReconnect.isOnline(activity)) {

            ErrorPojo pojo = new ErrorPojo();
            pojo.setUrl(SingletonServer.getInstance().getAppServer());
            pojo.setRecomendacion(activity.getString(R.string.main_login__alert__sin_internet__detail));
            pojo.setErrorDescription(activity.getString(R.string.main_login__alert__sin_internet__title));

            new ErrorView(activity).show(pojo);
            return false;
        }
        return true;
    }

    private void initListenersSend() {
//        enviarEspecial.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                registerForContextMenu(enviarEspecial);
//                openContextMenu(enviarEspecial);
//            }
//        });



        enviarMessageButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                messageAttach.getContenedor().setVisibility(View.GONE);
                return setLongClickSendMessage(v);
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
                txt.setText("Mensaje de prueba " +  Singletons.generatorMessageTest().getCount());
                //enviarMessageButtonOnClick();
            });

            findViewById(R.id.mensaje_de_prueba_boton2).setOnClickListener(v -> {
                txt.setText("www.google.com " +  Singletons.generatorMessageTest().getCount());
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




    }

    private boolean setLongClickSendMessage(View v) {
        messageAttach.getContenedor().setVisibility(View.GONE);
        if (!showErrorSinInternet(MessageActivity.this)){return false;}
        longClick=true;
        if (txt.getText().toString().trim().equals("")){
            if (MessageValidations.blockAudioMessageValidation()){
                longClick=false;
                return false;

            }


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

                // Starts the dragT

                v.startDrag(dragData,  // the data to be dragged
                        myShadow,  // the drag shadow builder
                        null,      // no need to use local data
                        0          // flags (not currently used, set to 0)
                );

                return true;
            }
        }
        //registerForContextMenu(enviarMessageButton);
        //openContextMenu(enviarMessageButton);

        if (envioEspecial == null){
            envioEspecial = new MessageEnvioEspecial(this);
        }
        envioEspecial.open();
        return true;
    }


    private void enviarMessageButtonOnClick() {
        messageAttach.getContenedor().setVisibility(View.GONE);
        if (!showErrorSinInternet(MessageActivity.this)){return;}
        longClick=false;
        if (txt.getText().toString().trim().equals("") && bitmapCompleto ==null){
            if (MessageValidations.blockAudioMessageValidation()){
                longClick=false;
                return;

            }
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
        final boolean time = false;
        final boolean anonimo = AnonimoConfig.alwaysSendAnonino(getGrupoSeleccionado().getIdGrupo());
        final boolean extraKey = false;

        if (time){
            black=false;
        }else{

            black = false;

        }

        if (extraKey){

            if (extraAesToUse ==null){
/*                extraAesToUse =
                        extraAesToUse = MessageAvanzado.aplicarExtraAES(
                                SingletonValues.getInstance().getGrupoSeleccionado(),
                                MessageActivity.this,
                if (getMessageAvanzado()!= null )getMessageAvanzado().getTvMessageSecretKey(),
                                extraAesToUse, false, new ActionMessageEncryptKeyI() {
                                    @Override
                                    public void action() {
                                        callSendMethod(black, time, anonimo, extraKey);
                                    }
                                });*/
            }else {
                callSendMethod(black, time, anonimo, extraKey);


            }

        }else {
            try {
                sendMessageMethod(black, time,0, anonimo, extraKey,  MessageValidations.isBlockResend(getGrupoSeleccionado().getIdGrupo()));
            } catch (Exception e) {
                e.printStackTrace();
                SimpleErrorDialog.errorDialog(MessageActivity.this,
                        MessageActivity.this.getString(R.string.general__error_message), e.getMessage());
            }
        }
    }

    private void callSendMethod(boolean black, boolean time, boolean anonimo, boolean personalKey) {
        try {
            sendMessageMethod(black, time, 0,anonimo, personalKey, MessageValidations.isBlockResend(getGrupoSeleccionado().getIdGrupo()));
        } catch (Exception e) {
            e.printStackTrace();
            SimpleErrorDialog.errorDialog(MessageActivity.this,
                    MessageActivity.this.getString(R.string.general__error_message), e.getMessage());
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
        messageAttach.getContenedor().setVisibility(View.GONE);
        MessageActivity.this.isRecordingAudio=false;
        MessageActivity.this.isPlayingAudio=false;
        audioList = null;
        MessageUtil.segundosCalculados(secondsAudio,audioList);
        if (MessageValidations.blockAudioMessageValidation()){
            longClick=false;
            return;

        }

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
                bitmapCompleto = BitMapTools.getBitmapCamera(getContentResolver(), byteArray,IMAGE_MAX_SIZE_COMPLETO);
            }
            if (bitmapCompleto == null){
                bitmapCompleto = (Bitmap) data.getExtras().get(CONSTANT_EXTRA_DATA);
            }
            bitmapMiniatura = (Bitmap) data.getExtras().get("data");

            if ((bitmapMiniatura.getWidth() * bitmapMiniatura.getHeight()) * (1 / Math.pow(1, 2)) >
                    IMAGE_MAX_SIZE_MINIATURA) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                byte[] byteArray = stream.toByteArray();
                bitmapMiniatura = BitMapTools.getBitmapCamera(getContentResolver(), byteArray, IMAGE_MAX_SIZE_MINIATURA);
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
                    bitmapCompleto = BitMapTools.getBitmap(getContentResolver(), filePath, IMAGE_MAX_SIZE_COMPLETO);
                }

                bitmapMiniatura = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

                if ((bitmapCompleto.getWidth() * bitmapCompleto.getHeight()) * (1 / Math.pow(1, 2)) >
                        IMAGE_MAX_SIZE_MINIATURA/100) {
                    bitmapMiniatura = BitMapTools.getBitmap(getContentResolver(), filePath,IMAGE_MAX_SIZE_COMPLETO);
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
        return s.getSelectedItem().toString().equals("✔");
    }



    @Override
    public boolean onContextItemSelected(MenuItem item) {

        try {
            if ( item.getTitle().equals(getString(R.string.message_activity__delete_message__for_me))) {
                //RestCalls.deleteForMeRest(MessageActivity.this);
            } else if ( item.getTitle().equals(getString(R.string.message_activity__delete_message__for_everyone))) {
                //RestCalls.deleteForEveryone(MessageActivity.this);
            }else if ( item.getTitle().equals(getString(R.string.message_activity_message_actions__forward))){
                Intent intent = new Intent(MessageActivity.this, MessageResendActivity.class);

                //intent.pu("context", (Parcelable) this);
                startActivity(intent);
            }else if ( item.getTitle().equals(getString(R.string.message_activity_message_actions__response))){
                modoResponder();
            }else if ( item.getTitle().equals(getString(R.string.message_activity_message_actions__reintentar))){
                RestCalls.retry(MessageActivity.this);

            }else if ( item.getTitle().equals(this.getString(R.string.general__message_type__black))){
                sendMessageMethod(true, false,0,false, false, MessageValidations.isBlockResend(getGrupoSeleccionado().getIdGrupo()));
            }else if ( item.getTitle().equals(this.getString(R.string.general__message_type__anonimous))){
                sendMessageMethod(false, false,0, true, false, MessageValidations.isBlockResend(getGrupoSeleccionado().getIdGrupo()));
            }else if ( item.getTitle().equals( this.getString(R.string.general__message_type__temporal))) {
                sendMessageMethod(false, true, 0,false, false, MessageValidations.isBlockResend(getGrupoSeleccionado().getIdGrupo()));
            }else if ( item.getTitle().equals(this.getString(R.string.general__message_type__extra_encriptado))) {

                if (extraAesToUse ==null) {
            /*        extraAesToUse =MessageAvanzado.aplicarExtraAES(
                            SingletonValues.getInstance().getGrupoSeleccionado(),
                            MessageActivity.this,
                    if (getMessageAvanzado()!= null )getMessageAvanzado().getTvMessageSecretKey(),
                            extraAesToUse, false, new ActionMessageEncryptKeyI() {
                                @Override
                                public void action() {
                                    adapter.notifyDataSetChanged();
                                    
                                    try {
                                        //sendMessageMethod(false, false, 0, getMessageAvanzado().getSiempreAnonimo(), true, MessageValidations.isBlockResend(getGrupoSeleccionado().getIdGrupo()));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        SimpleErrorDialog.errorDialog(MessageActivity.this,
                                                MessageActivity.this.getString(R.string.general__error_message), e.getMessage());

                                    }
                                }
                            });*/
                }else {
                    adapter.notifyDataSetChanged();
                    sendMessageMethod(false, false, 0, getMessageAvanzado().getSiempreAnonimo(), true, MessageValidations.isBlockResend(getGrupoSeleccionado().getIdGrupo()));
                    

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            SimpleErrorDialog.errorDialog(MessageActivity.this,
                    MessageActivity.this.getString(R.string.general__error_message), e.getMessage());

        }

        return true;
    }

    private void modoResponder() {

/*        ItemListMessage item = SingletonValues.getInstance().getMessageDetailSeleccionado();
        Observers.message().getMessageSelected().add(item.getMessage());
        item.getRch().getLayoutSelected().setVisibility(View.VISIBLE);*/

    }


    public void sendMessageMethod(boolean black, boolean time,int seconds, boolean anonimo, boolean extraAES,boolean isBlockResend) throws Exception {
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
            timeSeconds =seconds; //MessageUtil.getGrupoUserConfMessageTimeGetSeconds();
        }
        MediaDTO mediaDTO = getMediaDTOToSend();
        removeAttach();
        // esto es solo desarrollo

//        if (SingletonServer.getInstance().isDeveloper()) {
//            txt.setText(txt.getText().toString()
//                    + "\n Nickname: " + Singletons.usuario().getUsuario().getNickname()
//                    + "\n Counter nextvalue  " + SingletonValues.getInstance().getCounterNextValue());
//        }

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
                black, time, anonimo, extraAES,MessageValidations.isBlockResend(getGrupoSeleccionado().getIdGrupo()),false,timeSeconds, null);
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
        //super.onBackPressed();

        if (getMessageAvanzado()!= null &&  getMessageAvanzado().getViewAvanzada().getVisibility() == View.VISIBLE){
            messageAvanzadoClose();
        }else{
            this.finish();
        }

    }

    public void messageAvanzadoClose(){
        if (getMessageAvanzado()!= null) {
            messageAvanzado.getViewAvanzada().setVisibility(View.GONE);

        }
      getSupportActionBar().setHomeButtonEnabled(true); // disable the button
       getSupportActionBar().setDisplayHomeAsUpEnabled(true); // remove the left caret
        getSupportActionBar().setDisplayShowHomeEnabled(true); // remove the icon


    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem itemMenu) {
        messageAttach.getContenedor().setVisibility(View.GONE);
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

        }else if ( id == R.id.menu_message_buscar){
           messageCustomActionBar.setOnActionBarBuscar();


       //     message_custom_actionbar_buscar_content

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
    public void nuevoMensaje(Protocolo protocolo) {

        Message mP = protocolo.getMessage();


        Message m = Observers.message().getMensajesPorId(mP.buildIdMessageToMap());

        if (m.amIReplyMessage()){
            adapter.notifyDataSetChanged();
            
            return;
        }
        if ( protocolo.getAsyncId() != null){
            for (int k = 0 ; k < items.size() ; k++){
                ItemListMessage item = items.get(k);


                //if ( protocoloDTO.getMessage().buildIdMessageToMap().equals(item.getMessage().buildIdMessageToMap())){

                if ( (protocolo.getMessage().getIdGrupo() + "{-}" + protocolo.getAsyncId()).equals(item.getMessage().buildIdMessageToMap())){
                    item.setMessage(m);

                    item.setMessage(m);

                    for (int i = 0; i < m.getMessagesDetail().length; i++) {
                        if (m.getMessagesDetail()[i].getUsuarioDestino().getIdUsuario().equals(Singletons.usuario().getUsuario().getIdUsuario())) {
                            item.setMessageDetail(m.getMessagesDetail()[i]);
                            adapter.notifyDataSetChanged();
                            
                        }
                    }
                }
            }
        }else {
            if (!mP.getIdGrupo().equals(getGrupoSeleccionado().getIdGrupo())) return;
            for (int i = 0; i < m.getMessagesDetail().length; i++) {
                if (m.getMessagesDetail()[i].getUsuarioDestino().getIdUsuario().equals(Singletons.usuario().getUsuario().getIdUsuario())) {
                    ItemListMessage n = new ItemListMessage(m, m.getMessagesDetail()[i]);

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


                    if ( o1.getMessage().isHistorial() && o2.getMessage().isHistorial() ) {
                        int value = o1.getMessage().getIdMessage().compareTo(o2.getMessage().getIdMessage());
                        return value;
                    }

                    if ( !o1.getMessage().isHistorial() && !o2.getMessage().isHistorial() ) {
                        int value =  o1.getMessage().getOrden().compareTo(o2.getMessage().getOrden());
                        return value;
                    }

                    if (o1.getMessage().isHistorial() && !o2.getMessage().isHistorial()) {
                        return -1;
                    }
                    if (!o1.getMessage().isHistorial() && o2.getMessage().isHistorial()) {
                        return 1;
                    }
                    return 0;
                }

        });
    }
    @Override
    public void cambioEstado(MessageDetail m) {
        if (adapter == null) return;
        adapter.notifyDataSetChanged();

        for ( int i=0; i < adapter.getItemCount(); i++){
            if (ObserverMessage.getInstance().getMensajesPorId(items.get(i).getMessage().buildIdMessageToMap()) == null) return;

            MessageDetail[] md = ObserverMessage.getInstance().getMensajesPorId(items.get(i).getMessage().buildIdMessageToMap()).getMessagesDetail();
            for (MessageDetail messageDetail : md) {
                if (messageDetail.getUsuarioDestino().getIdUsuario().equals(Singletons.usuario().getUsuario().getIdUsuario())) {
                    items.get(i).messageDetail = messageDetail;
                    adapter.notifyItemChanged(i);
                }
            }

        }


        adapter.notifyDataSetChanged();
        
    }

    @Override
    public void emptyList(String idGrupo) {

        items.clear();

        if (adapter == null) return;

        adapter.notifyDataSetChanged();
    }


    @Override
    public void mensajeAddItem(Message miMensaje, String asyncId) {
        if (miMensaje.getIdGrupo().equals(getGrupoSeleccionado().getIdGrupo())){
            ItemListMessage n = new ItemListMessage(miMensaje, miMensaje.getMessagesDetail()[0]);
            //n.setAsyncId(asyncId);
            items.add(n);
            sortById(items);
            rvLista.scrollToPosition(items.size()-1);
            
        }

    }

    @Override
    public void borrarMessageDetail(MessageDetail detail) {

           borrarMessage(detail.buildIdMessageToMap());

    }

    @Override
    public void borrarMessage(String idMessageToMap) {
            for ( int i = items.size()-1 ; i>=0 ; i--){
                if (items.get(i).getMessageDetail().buildIdMessageToMap().equals(idMessageToMap)){
                    items.remove(i);
                    adapter.notifyDataSetChanged();

                    return;
                }
            }
    }

    @Override
    public void writting(WrittingDTO w) {
        if (getGrupoSeleccionadoIsNull() && getGrupoSeleccionado().isOtherAreWritting()){
            messageCustomActionBar.setMainSubtitleWriting(true);
        }
    }


    @Override
    public void writtingStop(WrittingDTO w) {

        if (getGrupoSeleccionadoIsNull() && getGrupoSeleccionado().getIdGrupo().equals(w.getIdGrupo())){
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

      //  messageAvanzado = new MessageAvanzado();
        //getMessageAvanzado().loadGrupoUserConf(getGrupoSeleccionado().getIdGrupo());

        messageSendRowComplete = (TableRow) findViewById(R.id.tr_message_send_complete);
        messageSendRowReadOnly = (TableRow) findViewById(R.id.tr_message_send_readonly);

        //View menuAddContact = findViewById(R.id.menu_message_add_contact);

        avanzada = (ImageButton) SingletonCurrentActivity.getInstance().getMessageActivity().findViewById(R.id.boton_avanzado);
        avanzada2 = (ImageButton) SingletonCurrentActivity.getInstance().getMessageActivity().findViewById(R.id.boton_avanzado2);


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
                messageAttach.getContenedor().setVisibility(View.GONE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SingletonValues.getInstance().getGrupoSeleccionado().getMessageInProcess().setTxt(txt.getText().toString());

                if (!SingletonServerConfiguration.getInstance().getSystemGralConf().isMessagingWritingMessage()) return;


                if (!MessageActivity.this.getGrupoSeleccionado().isIAmWritting()){

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

                messageAttach.getContenedor().setVisibility(View.GONE);
                popup.toggle();
            }
        });

        btnAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                messageAttach.getContenedor().setVisibility(View.VISIBLE);
            }
        });

        refreshMensaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RestCalls.loadMessagesContador(MessageActivity.this);
            }
        });
        enviarMessageButton = GetButtonReady.get(this,R.id.enviar2,v -> enviarMessageButtonOnClick());
        enviarMessageButton.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4096)});

        enviarFile = (Button) findViewById(R.id.enviar_file);
        //enviarEspecial = (Button) findViewById(R.id.btn_message_send_especial);




        rvLista = findViewById(R.id.rv_message_detail_list);

        ActivityCompat.requestPermissions(MessageActivity.this,
                new String[]{Manifest.permission.RECORD_AUDIO},
                enviarMessageButton.getId());
        roleViewSetup();
    }

    private void roleViewSetup() {

        if ( Observers.grupo().amIReadOnly(SingletonValues.getInstance().getGrupoSeleccionado().getIdGrupo())){


        }
        if (Observers.grupo().amIReadOnly(getGrupoSeleccionado().getIdGrupo())){
            messageSendRowReadOnly.setVisibility(View.VISIBLE);
            messageSendRowComplete.setVisibility(View.GONE);
            findViewById(R.id.mensaje_de_prueba).setVisibility(View.GONE);
           if (getMessageAvanzado()!= null ) getMessageAvanzado().initViewForRole();
            //menuAddContact.setVisibility(View.GONE);
        }else{
            if (SingletonServer.getInstance().isDeveloper()) {
                findViewById(R.id.mensaje_de_prueba).setVisibility(View.VISIBLE);
            }
            messageSendRowReadOnly.setVisibility(View.GONE);
            messageSendRowComplete.setVisibility(View.VISIBLE);
        }
    }

    private void callRestWritting(Grupo grupo) throws Exception {
        WrittingDTO dto = new WrittingDTO();
        dto.setNickname(Singletons.usuario().getUsuario().getNickname());
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
        rvLista = findViewById(R.id.rv_message_detail_list);
        rvLista.setLayoutManager(manager);
        if (items != null) emptyList(SingletonValues.getInstance().getGrupoSeleccionado().getIdGrupo());
        buildItems();

    }

    private void initListener() {

        avanzada.setOnClickListener(v -> messageAvanzadoOpen());
        avanzada2.setOnClickListener(v -> messageAvanzadoOpen());



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
        SingletonTextSizeMessage.getInstance().setIncreaseSizeListener(this,
                textSizeMessageView.getIncrease(),
                textSizeMessageView.getDecrease(),
                findViewById(R.id.rv_message_detail_list),
                findViewById(R.id.enviar_texto2));
        SingletonTextSizeMessage.getInstance().setDecreaseSizeListener(this,
                textSizeMessageView.getIncrease(),
                textSizeMessageView.getDecrease(),
                findViewById(R.id.rv_message_detail_list),
                findViewById(R.id.enviar_texto2));
    }

    private void messageAvanzadoOpen() {
        if (getMessageAvanzado() == null){
            messageAvanzado=new MessageAvanzado();
        }
        SingletonCurrentActivity.getInstance().getMessageActivity().getSupportActionBar().setHomeButtonEnabled(false); // disable the button
        SingletonCurrentActivity.getInstance().getMessageActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(false); // remove the left caret
        SingletonCurrentActivity.getInstance().getMessageActivity().getSupportActionBar().setDisplayShowHomeEnabled(false); // remove the icon

        getMessageAvanzado().openAvanzada();
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
        SingletonTextSizeMessage.getInstance().refreshTextSize(this,

                findViewById(R.id.rv_message_detail_list),
                findViewById(R.id.enviar_texto2));




        if (getMessageAvanzado()!= null ){
            if (getMessageAvanzado().getViewAvanzada().getVisibility() == View.VISIBLE){
                getMessageAvanzado().reset();
            }

        }

    }

    protected void onStop() {
        super.onStop();
        Observers.message().setMessageOnTop(false);
        //messageAvanzado=null;
        writting(null);
    }

    private void buildItems() {


        List<MessageDetail> lista = Observers.message().getMensajesDetailsPorGrupo(
                getGrupoSeleccionado().getIdGrupo()
        );


        for ( MessageDetail e : lista) {
            if (!e.isDeleted()) {
                if (e.getUsuarioDestino().getIdUsuario().equals(Singletons.usuario().getUsuario().getIdUsuario())) {


                    Message m = Observers.message().getMensajesPorId(e.buildIdMessageToMap());
                    if (!m.isDeleted()) {
                        boolean reciboBlack = (!Singletons.usuario().getUsuario().getIdUsuario().equals(getIdUsuario(m.getUsuarioCreacion()))
                                &&
                                MasterGeneralConfiguration.buildSiempreBlackReceptionConfigurationByGrupo(getGrupoSeleccionado().getIdGrupo()).isValue()
                        );


                        if (m != null) {
                            if (!m.isBlackMessage() && !m.amITimeMessage() &&
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

                    int maxSeconds = SingletonServerConfiguration.getInstance().getSystemGralConf().getMyAccountConf().getAudioMaxTimeInSeconds();

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
                    Toast.makeText(this, getString(R.string.message_activity_audio__error__init), Toast.LENGTH_SHORT).show();
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
    public void deleteExtraEncrypt(Grupo g) {

    }


    @Override
    public void lock(Grupo g) {

    }


}
