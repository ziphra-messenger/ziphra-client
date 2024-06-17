package com.privacity.cliente.activity.message.customactionbar;

import android.content.Intent;
import android.media.Image;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.message.ItemListMessage;
import com.privacity.cliente.activity.message.MessageActivity;
import com.privacity.cliente.activity.messageresend.MessageResendActivity;
import com.privacity.cliente.common.error.SimpleErrorDialog;
import com.privacity.cliente.model.Grupo;
import com.privacity.cliente.singleton.Observers;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.singleton.observers.ObserverGrupo;

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

    public MessageCustomActionBar(MessageActivity activity) {

        activity.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        activity.getSupportActionBar().setCustomView(R.layout.actionbar_message_custom);

        this.activity = activity;
        this.layoutContainer = (LinearLayout) getActionBarView().findViewById(R.id.message_custom_actionbar_container);
        this.mainTitle = (TextView) getActionBarView().findViewById(R.id.message_custom_actionbar_title);
        this.mainSubtitle = (TextView) getActionBarView().findViewById(R.id.message_custom_actionbar_subtitle);
        this.layoutMain = (LinearLayout) getActionBarView().findViewById(R.id.message_custom_actionbar_main);
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
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void setOnActionBarActions(){
        layoutActions.setVisibility(View.VISIBLE);
        layoutMain.setVisibility(View.GONE);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        changeSelectItems();
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
                Intent intent = new Intent(activity, MessageResendActivity.class);
                activity.startActivity(intent);
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
        mainTitle.setText(SingletonValues.getInstance().getGrupoSeleccionado().getName());

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
        if ( activity.getGrupoSeleccionado().getMembersOnLine() > 1){
            membersOnlineCount.setText((activity.getGrupoSeleccionado().getMembersOnLine()-1)+"");
            membersOnlineCount.setVisibility(View.VISIBLE);
        }else{
            membersOnlineCount.setVisibility(View.GONE);
        }
    }

    public void changeSelectItems(){
        Iterator<ItemListMessage> i = getSelectedItems().iterator();
        ItemListMessage parentReply= null;
        if (!ObserverGrupo.getInstance().amIReadOnly(SingletonValues.getInstance().getGrupoSeleccionado().getIdGrupo())){

            if (getSelectedItems().size() != 1){
                actionReply.setEnabled(false);
            }else{
                actionReply.setEnabled(true);
            }

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

