package com.privacity.cliente.activity.message;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.privacity.cliente.activity.messageresend.MessageUtil;

import lombok.Data;

@Data
public class RecyclerHolderGeneric {

    private RelativeLayout layoutSelected;
    private LinearLayout layoutEveryMessages;
    private LinearLayout layoutAllMessage;
    private LinearLayout layoutPersonalEncrypt;
    private View btPersonalEncryptLockClose;
    private Button btPersonalEncryptLockOpen;
    private LinearLayout layoutAllMessageSinPersonalEncrypt;
    private LinearLayout layoutBlackmessageLocks;
    private Button btMessageBlackEyeShow;
    private Button btMessageBlackEyeHide;
    private LinearLayout layoutMessageFrame;
    private LinearLayout layoutMessageFrameContentUsuario;
    private TextView tvRemitente;
    private LinearLayout layoutMessageFrameContent;
    private Button btActivateMessageTime;
    private LinearLayout layoutMessageContentData;
    private TextView tvMessageListText;
    private TextView tvLeermas;
    private TextView tvLeermenos;
    private LinearLayout layoutAudiochat;
    private ImageButton ibMessageItemlistMediaAudioStop;
    private ImageButton ibMessageItemlistMediaAudioPlay;
    private ProgressBar pbMessageItemlistMediaAudioProgressBar;

    private ImageButton ibMessageItemlistError;

    private SeekBar sbMessageItemlistMediaAudioProgress;
    private Button btMessageItemlistMediaAudioVelocity;
    private TextView tvMessageItemlistMediaAudioSeconds;
    private ImageView ivItemListImageMedia;

    private LinearLayout layoutItemListImage;
    private ImageButton ibMessageItemlistMediaDownload;
    private ProgressBar progressBarMessageItemlistMediaDownload;

    private LinearLayout layoutRemitenteContent;

    private TextView tvState;

    private StateIcons stateIcons;

    private boolean messageTimeActive=false;
    private boolean hasMediaAudioChat=false;
    private boolean hasMediaImage=false;
    private boolean isSecretPersonal=false;

    private String errors="";

    RecyclerHolderGeneric reply;
    private LinearLayout layoutEmptyMessage;
    private ImageButton emptyMessageDownload;

    public void addError(String error){
        this.getIbMessageItemlistError().setVisibility(View.VISIBLE);
        errors = errors + error + "\n";
    }

    public void setPersonalEncryptLockOpen(boolean isReply){
        if ( isReply ) return;
        btPersonalEncryptLockOpen.setVisibility(View.VISIBLE);
        btPersonalEncryptLockClose.setVisibility(View.GONE);
        layoutMessageFrame.setVisibility(View.VISIBLE);

    }

    public void setPersonalEncryptLockClose(boolean isReply){
        if ( isReply ) return;
        btPersonalEncryptLockOpen.setVisibility(View.GONE);
        btPersonalEncryptLockClose.setVisibility(View.VISIBLE);
        layoutMessageFrame.setVisibility(View.GONE);

    }
    public void setSecondsMediaValue(int dataAudioLenght){
        MessageUtil.segundosCalculados(tvMessageItemlistMediaAudioSeconds,dataAudioLenght);

    }
    public void setSecondsMediaValue(byte[] dataAudio){
        if (dataAudio == null){
            MessageUtil.segundosCalculados(tvMessageItemlistMediaAudioSeconds,0);
        }else{
            MessageUtil.segundosCalculados(tvMessageItemlistMediaAudioSeconds,dataAudio.length);
        }

    }
}
