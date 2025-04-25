package com.privacity.cliente.activity.message.customactionbar;

import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.main.TextHighlighter;
import com.privacity.cliente.activity.message.ItemListMessage;
import com.privacity.cliente.activity.message.MessageActivity;
import com.privacity.cliente.activity.messageresend.MessageResendActivity;
import com.privacity.cliente.common.constants.DeveloperConstant;
import com.privacity.cliente.common.error.SimpleErrorDialog;
import com.privacity.cliente.singleton.Observers;
import com.privacity.cliente.singleton.SingletonValues;import com.privacity.cliente.singleton.Singletons;
import com.privacity.cliente.singleton.impl.SingletonServer;
import com.privacity.cliente.singleton.observers.ObserverGrupo;
import com.privacity.cliente.singleton.toast.SingletonToastManager;

import java.util.Iterator;
import java.util.Set;

import lombok.Data;

@Data
public class MessageCustomActionBar {

    private final MessageActivity activity;
    private final LinearLayout layoutContainer;
    private final TextView mainTitle;
    private final TextView mainSubtitle;
    private final LinearLayout layoutMain;
    private final LinearLayout layoutActions;
    private final Button membersOnlineCount;
    private final ImageView grupoLockCandadoCerrado;
    private final ImageButton actionResend;
    private final ImageButton actionReply;
    private final ImageButton actionDelete;
    private final ImageButton actionClose;
    private final LinearLayout layoutBuscar;

    public MessageCustomActionBar(MessageActivity activity) {

        activity.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        activity.getSupportActionBar().setCustomView(R.layout.actionbar_message_custom);

        this.activity = activity;
        this.layoutContainer = (LinearLayout) getActionBarView().findViewById(R.id.message_custom_actionbar_container);
        this.mainTitle = (TextView) getActionBarView().findViewById(R.id.message_custom_actionbar_title);
        this.mainSubtitle = (TextView) getActionBarView().findViewById(R.id.message_custom_actionbar_subtitle);
        this.layoutMain = (LinearLayout) getActionBarView().findViewById(R.id.message_custom_actionbar_main);
        this.layoutBuscar = (LinearLayout) getActionBarView().findViewById(R.id.message_custom_actionbar_buscar_content);

        this.layoutActions = (LinearLayout) getActionBarView().findViewById(R.id.message_custom_actionbar_actions);
        this.membersOnlineCount = (Button) getActionBarView().findViewById(R.id.message_custom_actionbar_members_online_count);
        this.grupoLockCandadoCerrado = (ImageView) getActionBarView().findViewById(R.id.message_custom_actionbar_candado_cerrado);
        this.actionResend = (ImageButton) getActionBarView().findViewById(R.id.message_custom_actionbar_resend);
        this.actionReply = (ImageButton) getActionBarView().findViewById(R.id.message_custom_actionbar_reply);
        this.actionDelete = (ImageButton) getActionBarView().findViewById(R.id.message_custom_actionbar_delete);
        this.actionClose = (ImageButton) getActionBarView().findViewById(R.id.message_custom_actionbar_close);

        setListener();
        loadValues();
    }

    public void setOnActionBarMain(){
        layoutActions.setVisibility(View.GONE);
        layoutMain.setVisibility(View.VISIBLE);
        layoutBuscar.setVisibility(View.GONE);
        if(activity.getSupportActionBar()!=null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public void setOnActionBarActions(){
        layoutActions.setVisibility(View.VISIBLE);
        layoutMain.setVisibility(View.GONE);
        layoutBuscar.setVisibility(View.GONE);
        if(activity.getSupportActionBar()!=null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        changeSelectItems();
    }
    public void setOnActionBarBuscar(){
        layoutActions.setVisibility(View.GONE);
        layoutMain.setVisibility(View.GONE);
        layoutBuscar.setVisibility(View.VISIBLE);
        if(activity.getSupportActionBar()!=null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }


    }

    public void onResumeActionBar(){

        if (getSelectedItems().size() == 0 ){
            setOnActionBarMain();
        }else{
            setOnActionBarActions();

        }
    }

    public void setListener(){

        actionClose.setOnClickListener(v -> {
            try{
                activity.cleanSelectedItems();
                setOnActionBarMain();
            }catch (Exception e){
                e.printStackTrace();
            }
        });

        actionDelete.setOnClickListener(v -> {

            try {
                SimpleErrorDialog.messageDelete(activity);
            }catch (Exception e){
                e.printStackTrace();
            }

        });


        actionResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Iterator<ItemListMessage> i = getSelectedItems().iterator();
                boolean isMessageWithBlockResend=false;
                if (i.hasNext()){
                    ItemListMessage ilm =i.next();
                    if (ilm.getMessage().isBlockResend()){
                        isMessageWithBlockResend=true;
                    }
                }
                if (!isMessageWithBlockResend || DeveloperConstant.validateBlockResend==false) {
                    Intent intent = new Intent(activity, MessageResendActivity.class);
                    activity.startActivity(intent);
                }else {
                    actionResend.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            SingletonToastManager.getInstance().showToadShort(activity, "Hay al menos un mensaje configurado con bloqueo de reenvio");
                        }
                    });
                }
            }
        });

        actionReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Iterator<ItemListMessage> i = getSelectedItems().iterator();
                ItemListMessage parentReply= null;
                if (i.hasNext()){
                    parentReply=i.next();
                }
                setOnActionBarMain();
                activity.cleanSelectedItems();
                activity.getMessageReplyFrame().loadValues(parentReply);
            }
        });

        ((TextView)activity.findViewById(R.id.message_custom_actionbar_buscar)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                TextHighlighter t=  new TextHighlighter();


                for (ItemListMessage cc : activity.getItems()){
                    try {
                        t.addTarget(cc.getRch().getTvMessageListText());
                    } catch (Exception e) {
e.printStackTrace();
                    }
                    t.setBackgroundColor(Color.parseColor("#FFFF00"))
                            .setForegroundColor(Color.GREEN)
                            .highlight(((TextView)activity.findViewById(R.id.message_custom_actionbar_buscar)).getText().toString(), TextHighlighter.BASE_MATCHER);

                    //.   gemUsername.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private Set<ItemListMessage> getSelectedItems(){
        return Observers.message().getMessageSelected();
    }
    private View getActionBarView(){
        return activity.getSupportActionBar().getCustomView();
    }

    public void setMainSubtitleWriting(boolean isWriting){
        if (isWriting) mainSubtitle.setText("Escribiendo... ");
        else mainSubtitle.setText("");
    }

    public void loadValues(){
        mainTitle.setSingleLine();
        mainTitle.setEllipsize(TextUtils.TruncateAt.END);

        if (SingletonServer.getInstance().isDeveloper()) {
            mainTitle.setText(SingletonValues.getInstance().getGrupoSeleccionado().getName()
                    + " - " + Singletons.usuario().getUsuario().getNickname());
        }else{
            mainTitle.setText(SingletonValues.getInstance().getGrupoSeleccionado().getName());

        }


        actualizarConfLockCandadoCerrado();

        onResumeActionBar();
    }

    public void actualizarConfLockCandadoCerrado() {
        if (SingletonValues.getInstance().getGrupoSeleccionado().getPassword().isEnabled()){
            grupoLockCandadoCerrado.setVisibility(View.VISIBLE);
        }else{
            grupoLockCandadoCerrado.setVisibility(View.GONE);
        }
    }


    public void setMainMembersOnLineCount(){
        if (activity.getGrupoSeleccionado() == null) {
            if (membersOnlineCount != null){
                membersOnlineCount.setVisibility(View.GONE);
            }
            return;
        }
        if ( activity.getGrupoSeleccionado().getMembersQuantityDTO().getQuantityOnline() > 1){
            membersOnlineCount.setText((activity.getGrupoSeleccionado().getMembersQuantityDTO().getQuantityOnline()-1)+"");
            membersOnlineCount.setVisibility(View.VISIBLE);
        }else{
            membersOnlineCount.setVisibility(View.GONE);
        }
    }

    public void changeSelectItems(){
        Iterator<ItemListMessage> i = getSelectedItems().iterator();
        ItemListMessage parentReply= null;
        if (!ObserverGrupo.getInstance().amIReadOnly(SingletonValues.getInstance().getGrupoSeleccionado().getIdGrupo())){

            actionReply.setEnabled(getSelectedItems().size() == 1);

            int count=0;

            while (i.hasNext()){
                parentReply=i.next();
            }
        }else{
            actionReply.setVisibility(View.INVISIBLE);
            actionReply.setEnabled(false);
        }
    }
}

