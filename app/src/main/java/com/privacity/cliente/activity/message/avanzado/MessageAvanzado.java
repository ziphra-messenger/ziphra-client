package com.privacity.cliente.activity.message.avanzado;

import android.graphics.Color;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.message.ActionMessageEncryptKeyI;
import com.privacity.cliente.activity.message.ConfType;
import com.privacity.cliente.activity.message.ListListener;
import com.privacity.cliente.activity.message.MasterGeneralConfiguration;
import com.privacity.cliente.activity.message.MessageActivity;
import com.privacity.cliente.activity.message.RecyclerMessageAdapter;
import com.privacity.cliente.activity.messageresend.MessageUtil;
import com.privacity.cliente.common.component.selectview.SelectView;
import com.privacity.cliente.common.component.selectview.ViewSelectCallBack;
import com.privacity.cliente.common.error.SimpleErrorDialog;
import com.privacity.cliente.encrypt.AEStoUse;
import com.privacity.cliente.encrypt.AEStoUseFactory;
import com.privacity.cliente.encrypt.EncryptUtil;
import com.privacity.cliente.model.Grupo;
import com.privacity.cliente.singleton.Observers;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.singleton.Singletons;
import com.privacity.cliente.singleton.activity.SingletonCurrentActivity;
import com.privacity.cliente.util.CopyPasteUtil;
import com.privacity.common.dto.GrupoUserConfDTO;
import com.privacity.common.enumeration.RulesConfEnum;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public class MessageAvanzado {



    private ImageView ibMessageAvanzadoCopy;

    public AEStoUse extraAesToUse;


    public EditText tvMessageSecretKey;

    private Button btMessageAvanzadaAutoGen;

    private ImageButton ibMessageAvanzadoShowSecretKey;
    private ImageButton ibMessageAvanzadoHideSecretKey;

    private View viewAvanzada;


    private Spinner spMessageAvanzadoExtraAesAlways;


    private static CountDownTimer secretKeyCounter=null;

    private View viewContentSave;
    private View viewContentSend;
    private View viewContentReception;

    public SelectView blockResend;
    public SelectView anonimoAlways;
    public SelectView black;
    public SelectView blackReceived;
    public SelectView blockMediaDownload;
    @Getter
    private RulesConfEnum blackNewUserValue;
    @Getter
    private RulesConfEnum blockMediaDownloadNewValue;
    @Getter
    private RulesConfEnum blackReceivedNewUserValue;
    @Getter
    private RulesConfEnum blockResendNewUserValue;
    @Getter
    private RulesConfEnum anonimoAlwaysNewUserValue;
    private BottomButtonsView bottomButtonsView;
    private Switch anonimoReception;
    private Switch anonimoReceptionMyMessage;
    private Button openNickname;
    private TextView nicknameTxt;


    public MessageAvanzado() {
        initView();
        initListeners();
        loadValues();
        initViewForRole();

    }

    public void reset() {
        loadValues();
        initViewForRole();
    }

    private void loadValues() {
        blockMediaDownload.applyRules();
        black.applyRules();
        anonimoAlways.applyRules();
        blockResend.applyRules();

        blockMediaDownload.resetView();
        black.resetView();
        anonimoAlways.resetView();
        blockResend.resetView();

        String nn = SingletonValues.getInstance().getGrupoSeleccionado().getUserForGrupoDTO().getNickname();
        if ( nn == null || nn.trim().equals("") ){
            nn = Singletons.usuario().getUsuario().getNickname();
        }

        nicknameTxt.setText(nn);



    }

    public void initViewForRole() {
        if ( Observers.grupo().amIReadOnly(SingletonValues.getInstance().getGrupoSeleccionado().getIdGrupo())){
            viewContentSend.setVisibility(View.GONE);
        }else {
            viewContentSend.setVisibility(View.VISIBLE);
        }
    }

    public void initView() {
        this.bottomButtonsView = new BottomButtonsView(this);
         ibMessageAvanzadoCopy = (ImageView) SingletonCurrentActivity.getInstance().getMessageActivity().findViewById(R.id.ib_message_avanzado_copy);
        btMessageAvanzadaAutoGen = (Button) SingletonCurrentActivity.getInstance().getMessageActivity().findViewById(R.id.bt_message_avanzada_auto_gen);
        ibMessageAvanzadoShowSecretKey = (ImageButton) SingletonCurrentActivity.getInstance().getMessageActivity().findViewById(R.id.ib_message_avanzado_show_secret_key);
        ibMessageAvanzadoHideSecretKey = (ImageButton) SingletonCurrentActivity.getInstance().getMessageActivity().findViewById(R.id.ib_message_avanzado_hide_secret_key);
        viewAvanzada = SingletonCurrentActivity.getInstance().getMessageActivity().findViewById(R.id.frame_message_avanzado__content__all);
        tvMessageSecretKey = (EditText)  SingletonCurrentActivity.getInstance().getMessageActivity().findViewById(R.id.tv_message_secret_key);
        spMessageAvanzadoExtraAesAlways = (Spinner) SingletonCurrentActivity.getInstance().getMessageActivity().findViewById(R.id.sp_message_avanzado_extra_aes_always);
        viewContentSave = (View) SingletonCurrentActivity.getInstance().getMessageActivity().findViewById(R.id.frame_message_avanzado__content__save);
        viewContentSend = (View) SingletonCurrentActivity.getInstance().getMessageActivity().findViewById(R.id.frame_message_avanzado__content__send);
       viewContentReception = (View) SingletonCurrentActivity.getInstance().getMessageActivity().findViewById(R.id.frame_message_avanzado__content__reception);
        this.anonimoReception = (Switch) SingletonCurrentActivity.getInstance().getMessageActivity().findViewById(R.id.message_avanzado__anonimo_received);
        this.anonimoReceptionMyMessage = (Switch) SingletonCurrentActivity.getInstance().getMessageActivity().findViewById(R.id.message_avanzado__anonimo_received_my_message);

        openNickname = (Button) SingletonCurrentActivity.getInstance().getMessageActivity().findViewById(R.id.button3);
        nicknameTxt = (TextView) SingletonCurrentActivity.getInstance().getMessageActivity().findViewById(R.id.message_avanzado__nickname__conclusion);

    }

    @NonNull
    private SelectView getSelectViewAlwaysAnonimo() {
        return new SelectView(SingletonCurrentActivity.getInstance().get()


                , new ViewSelectCallBack() {


            @Override
            public void action(RulesConfEnum user) {
                anonimoAlwaysNewUserValue =user;
            }

            @Override
            public List<RulesConfEnum> rulesConfNeeded() {
                List<RulesConfEnum> r = new ArrayList<RulesConfEnum>();
                r.add(RulesConfEnum.ON);
                r.add(RulesConfEnum.OFF);
                //r.add(RulesConfEnum.MANDATORY);
                r.add(RulesConfEnum.NULL);

                return r;
            }

            @Override
            public RulesConfEnum getMyAccountValue() {
                return RulesConfEnum.OFF;
            }

            @Override
            public RulesConfEnum getGrupoValue() {
                return  SingletonValues.getInstance().getGrupoSeleccionado().getGralConfDTO().getAnonimo();
            }

            @Override
            public RulesConfEnum getUserValue() {
                return anonimoAlwaysNewUserValue;
            }

            @Override
            public Switch getParentView() {
                return  (Switch)SingletonCurrentActivity.getInstance().getMessageActivity().findViewById(R.id.message_avanzado__anonimo_always__conclusion__null);
            }

            @Override
            public String descripcionText() {
                return "Configuracion de Envio anonimo";
            }
        });
    }

    @NonNull
    private SelectView getSelectViewBlockResend() {
        return new SelectView(SingletonCurrentActivity.getInstance().get()


                , new ViewSelectCallBack() {


            @Override
            public void action(RulesConfEnum user) {
                blockResendNewUserValue=user;
            }

            @Override
            public List<RulesConfEnum> rulesConfNeeded() {
                List<RulesConfEnum> r = new ArrayList<RulesConfEnum>();
                r.add(RulesConfEnum.ON);
                r.add(RulesConfEnum.OFF);
                //r.add(RulesConfEnum.MANDATORY);
                r.add(RulesConfEnum.NULL);

                return r;
            }

            @Override
            public RulesConfEnum getMyAccountValue() {

                if (SingletonValues.getInstance().getMyAccountConfDTO().isBlockResend()){
                    return RulesConfEnum.ON;
                }
                return RulesConfEnum.OFF;
            }

            @Override
            public RulesConfEnum getGrupoValue() {
                if (SingletonValues.getInstance().getGrupoSeleccionado().getGralConfDTO().isBlockResend()){
                    return RulesConfEnum.ON;
                }
                return RulesConfEnum.NULL;
            }

            @Override
            public RulesConfEnum getUserValue() {
                return blockResendNewUserValue;
            }

            @Override
            public Switch getParentView() {
                return  (Switch)SingletonCurrentActivity.getInstance().getMessageActivity().findViewById(R.id.message_avanzado__block_resend__conclusion__null);
            }

            @Override
            public String descripcionText() {
                return "Configuracion para el Bloqueo de Reenvio";
            }
        });
    }

    private SelectView getSelectViewBlockMediaDownload() {
        return new SelectView(SingletonCurrentActivity.getInstance().get()


                , new ViewSelectCallBack() {


            @Override
            public void action(RulesConfEnum user) {
                blockMediaDownloadNewValue=user;
            }

            @Override
            public List<RulesConfEnum> rulesConfNeeded() {
                List<RulesConfEnum> r = new ArrayList<RulesConfEnum>();
                r.add(RulesConfEnum.ON);
                r.add(RulesConfEnum.OFF);
                //r.add(RulesConfEnum.MANDATORY);
                r.add(RulesConfEnum.NULL);

                return r;
            }

            @Override
            public RulesConfEnum getMyAccountValue() {
                if (SingletonValues.getInstance().getMyAccountConfDTO().isBlockMediaDownload()){
                    return RulesConfEnum.ON;
                };
                return RulesConfEnum.OFF;
            }

            @Override
            public RulesConfEnum getGrupoValue() {
                if ( SingletonValues.getInstance().getGrupoSeleccionado().getGralConfDTO().isBlockMediaDownload()){
                    return RulesConfEnum.ON;

                }
                return RulesConfEnum.NULL;
            }

            @Override
            public RulesConfEnum getUserValue() {
                return blockMediaDownloadNewValue;
            }

            @Override
            public Switch getParentView() {
                return (Switch)SingletonCurrentActivity.getInstance().getMessageActivity().findViewById(R.id.message_avanzado__block_block_media_download__conclusion__null);
            }

            @Override
            public String descripcionText() {
                return "Configuracion para el Bloqueo de las descargas de las Imagenes y Videos adjubtos";
            }
        });
    }

    @NonNull
    private SelectView getSelectViewBlackAttachMandatory() {
        return new SelectView(SingletonCurrentActivity.getInstance().get()


                , new ViewSelectCallBack() {


            @Override
            public void action(RulesConfEnum user) {
                blackNewUserValue=user;
            }

            @Override
            public List<RulesConfEnum> rulesConfNeeded() {
                List<RulesConfEnum> r = new ArrayList<RulesConfEnum>();
                r.add(RulesConfEnum.ON);
                r.add(RulesConfEnum.OFF);
                //r.add(RulesConfEnum.MANDATORY);
                r.add(RulesConfEnum.NULL);

                return r;
            }

            @Override
            public RulesConfEnum getMyAccountValue() {
                if (SingletonValues.getInstance().getMyAccountConfDTO().isBlackMessageAttachMandatory()){
                    return RulesConfEnum.ON;
                };
                return RulesConfEnum.OFF;
            }

            @Override
            public RulesConfEnum getGrupoValue() {
                if ( SingletonValues.getInstance().getGrupoSeleccionado().getGralConfDTO().isBlackMessageAttachMandatory()){
                    return RulesConfEnum.ON;

                }
                return RulesConfEnum.NULL;
            }

            @Override
            public RulesConfEnum getUserValue() {
                return blackNewUserValue;
            }

            @Override
            public Switch getParentView() {
                return (Switch)SingletonCurrentActivity.getInstance().getMessageActivity().findViewById(R.id.message_avanzado__black_message_attach_mandatory__conclusion__null);
            }

            @Override
            public String descripcionText() {
                return "Enviar Siempre en Modo Black cuando contenga Imagen o Video";
            }
        });
    }
    private SelectView getSelectViewBlackAttachMandatoryReceived() {
        return new SelectView(SingletonCurrentActivity.getInstance().get()


                , new ViewSelectCallBack() {


            @Override
            public void action(RulesConfEnum user) {
                blackReceivedNewUserValue=user;
            }

            @Override
            public List<RulesConfEnum> rulesConfNeeded() {
                List<RulesConfEnum> r = new ArrayList<RulesConfEnum>();
                r.add(RulesConfEnum.ON);
                r.add(RulesConfEnum.OFF);
                //r.add(RulesConfEnum.MANDATORY);
                r.add(RulesConfEnum.NULL);

                return r;
            }

            @Override
            public RulesConfEnum getMyAccountValue() {
                if (SingletonValues.getInstance().getMyAccountConfDTO().isBlackMessageAttachMandatoryReceived()){
                return RulesConfEnum.ON;
            }
                return RulesConfEnum.OFF;
            }

            @Override
            public RulesConfEnum getGrupoValue() {
                return null;
            }

            @Override
            public RulesConfEnum getUserValue() {
                return blackReceivedNewUserValue;
            }

            @Override
            public Switch getParentView() {
                return (Switch)SingletonCurrentActivity.getInstance().getMessageActivity().findViewById(R.id.message_avanzado__black_message_attach_mandatory_received__conclusion__null);
            }

            @Override
            public String descripcionText() {
                return "Recibir Siempre en Modo Black cuando contenga Imagen o Video";
            }
        });
    }





    public boolean getSiempreAnonimo(){
        return SingletonValues.getInstance().getGrupoSeleccionado().getUserConfDTO().isAnonimoRecived();
    }

    public void deleteExtraEncrypt(Grupo g){
        if (getGrupoSeleccionado().getIdGrupo().equals(g.getIdGrupo())){
            extraAesToUse = null;
            tvMessageSecretKey.setText("");

        }
    }
    @NotNull
    public GrupoUserConfDTO getGrupoUserConfDTO() {
        GrupoUserConfDTO c = new GrupoUserConfDTO();

        c.setIdGrupo(getGrupoSeleccionado().getIdGrupo());

        c.setAnonimoRecived(anonimoReception.isChecked());
        c.setAnonimoRecivedMyMessage(anonimoReceptionMyMessage.isChecked());

        c.setBlackMessageAttachMandatory(blackNewUserValue);
        c.setBlockMediaDownload(blockMediaDownloadNewValue);
        c.setBlackMessageAttachMandatoryReceived(blackReceivedNewUserValue);

        c.setBlockResend(blockResendNewUserValue);
        c.setAnonimoAlways(anonimoAlwaysNewUserValue);
        c.setExtraAesAlways(MessageUtil.getRulesConfEnum(spMessageAvanzadoExtraAesAlways,true));

        return c;
    }
    private Grupo getGrupoSeleccionado() {
        return SingletonValues.getInstance().getGrupoSeleccionado();
    }

    public void initListeners() {

        tvMessageSecretKey.setText(getGrupoSeleccionado().getPassword().getPasswordExtraEncrypt());

        openNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new NickNameView(SingletonCurrentActivity.getInstance().getMessageActivity());
                SingletonCurrentActivity.getInstance().getMessageActivity().findViewById(
                        R.id.include__nickname__content__all
                ).setVisibility(View.VISIBLE);
            }
        });
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
                            SingletonCurrentActivity.getInstance().getMessageActivity(),
                            tvMessageSecretKey,
                            extraAesToUse,false,null);
                }
                SingletonCurrentActivity.getInstance().getMessageActivity().getAdapter().notifyDataSetChanged();           }        });

        ibMessageAvanzadoCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleErrorDialog.passwordValidation(SingletonCurrentActivity.getInstance().getMessageActivity(), new SimpleErrorDialog.PasswordValidationI() {
                    @Override
                    public void action() {
                        CopyPasteUtil.setClipboard(SingletonCurrentActivity.getInstance().getMessageActivity(), tvMessageSecretKey.getText().toString());
                    }
                });

            }
        });



        EncryptUtil.generateSecretPersonalKeyListener(btMessageAvanzadaAutoGen,tvMessageSecretKey);

        //SecureFieldAndEye extraAes = new SecureFieldAndEye(null, tvMessageSecretKey, ibMessageAvanzadoShowSecretKey, ibMessageAvanzadoHideSecretKey);
        ibMessageAvanzadoShowSecretKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleErrorDialog.passwordValidationObligatorio(SingletonCurrentActivity.getInstance().getMessageActivity(), "Será visible por 15 segundos" , new SimpleErrorDialog.PasswordValidationI() {
                    @Override
                    public void action() {
                        secretKeyCounter = new CountDownTimer(15000, 1000) {

                            public void onTick(long millisUntilFinished) {
                                ibMessageAvanzadoHideSecretKey.setVisibility(View.VISIBLE);
                                ibMessageAvanzadoShowSecretKey.setVisibility(View.GONE);
                                tvMessageSecretKey.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            }

                            public void onFinish() {
                                tvMessageSecretKey.setTransformationMethod(PasswordTransformationMethod.getInstance());
                                ibMessageAvanzadoHideSecretKey.setVisibility(View.GONE);
                                ibMessageAvanzadoShowSecretKey.setVisibility(View.VISIBLE);
                            }
                        };
                        secretKeyCounter.start();
                    }
                });

            }
        });
        ibMessageAvanzadoHideSecretKey.setOnClickListener(new View.OnClickListener() {
                                                              @Override
                                                              public void onClick(View v) {

                                                                  if (secretKeyCounter != null) secretKeyCounter.cancel();

                                                                  tvMessageSecretKey.setTransformationMethod(PasswordTransformationMethod.getInstance());
                                                                  ibMessageAvanzadoHideSecretKey.setVisibility(View.GONE);
                                                                  ibMessageAvanzadoShowSecretKey.setVisibility(View.VISIBLE);

                                                              }

                                                          }
        );
        blackNewUserValue=SingletonValues.getInstance().getGrupoSeleccionado().getUserConfDTO().getBlackMessageAttachMandatory();
        black = getSelectViewBlackAttachMandatory();

        blackReceivedNewUserValue=SingletonValues.getInstance().getGrupoSeleccionado().getUserConfDTO().getBlackMessageAttachMandatoryReceived();
        blackReceived = getSelectViewBlackAttachMandatoryReceived();

        blockMediaDownloadNewValue=SingletonValues.getInstance().getGrupoSeleccionado().getUserConfDTO().getBlockMediaDownload();
        blockMediaDownload = getSelectViewBlockMediaDownload();

        anonimoAlwaysNewUserValue=SingletonValues.getInstance().getGrupoSeleccionado().getUserConfDTO().getAnonimoAlways();
        anonimoAlways = getSelectViewAlwaysAnonimo();

        blockResendNewUserValue=SingletonValues.getInstance().getGrupoSeleccionado().getUserConfDTO().getBlockResend();
        blockResend = getSelectViewBlockResend();

        anonimoReception.setChecked(SingletonValues.getInstance().getGrupoSeleccionado().getUserConfDTO().isAnonimoRecived());
        anonimoReceptionMyMessage.setChecked(SingletonValues.getInstance().getGrupoSeleccionado().getUserConfDTO().isAnonimoRecivedMyMessage());
    }

    @NonNull


    public void openAvanzada() {
        reset();
         viewAvanzada.setVisibility(View.VISIBLE);



    }


    public static AEStoUse aplicarExtraAES(Grupo grupo, MessageActivity messageActivity, EditText tvMessageSecretKey, AEStoUse extraAEStoUse, boolean showMessage, ActionMessageEncryptKeyI actionMessageEncryptKeyI){


        if (tvMessageSecretKey.getText().toString().equals("")) {
            ListListener.dialogSecretKey(messageActivity, actionMessageEncryptKeyI);
        } else {
            try {
                if (tvMessageSecretKey.getText().toString().equals("")) {
                    SimpleErrorDialog.errorDialog(messageActivity, "Extra Encrypt Key", "No puede ser nulo. Debe completar el valor haciendo click en el boton CONFIG");
                    return extraAEStoUse;
                }

                if (extraAEStoUse == null) {

                    extraAEStoUse = AEStoUseFactory.getAEStoUseExtra(
                            tvMessageSecretKey.getText().toString(), tvMessageSecretKey.getText().toString());
                    return extraAEStoUse;

                }
            } catch(Exception e){
                e.printStackTrace();
                SimpleErrorDialog.errorDialog(messageActivity, "Extra Encrypt Key", e.getMessage());
                return extraAEStoUse;
            }
        }
        return extraAEStoUse;
    }


}
