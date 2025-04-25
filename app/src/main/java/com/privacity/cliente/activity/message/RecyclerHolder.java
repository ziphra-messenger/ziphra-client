package com.privacity.cliente.activity.message;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.ContextMenu;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.errorconsole.ErrorConsoleActivity;
import com.privacity.cliente.activity.main.TextHighlighter;
import com.privacity.cliente.activity.message.validations.Black;
import com.privacity.cliente.activity.message.validations.ExtraEncrypt;
import com.privacity.cliente.activity.message.validations.RandomNickname;
import com.privacity.cliente.activity.message.validations.Resend;
import com.privacity.cliente.activity.userhelper.UserHelperPagesContant;
import com.privacity.cliente.common.constants.DeveloperConstant;
import com.privacity.cliente.singleton.SingletonValues;import com.privacity.cliente.singleton.Singletons;
import com.privacity.common.dto.UsuarioDTO;
import com.privacity.common.enumeration.MediaTypeEnum;
import com.privacity.common.enumeration.MessageState;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecyclerHolder extends RecyclerView.ViewHolder /*implements View.OnCreateContextMenuListener */ {

    private final RelativeLayout layoutSelected;
    private final LinearLayout layoutEveryMessages;
    private final LinearLayout layoutAllMessage;


    // my message empty message
    private final LinearLayout layoutEmptyMessage;
    private final ImageButton emptyMessageDownload;

    // my_message_reply
    private final LinearLayout layoutPersonalEncryptReply;
    private final ImageButton btPersonalEncryptLockCloseReply;
    private final Button btPersonalEncryptLockOpenReply;
    private final LinearLayout layoutAllMessageSinPersonalEncryptReply;
    private final LinearLayout layoutBlackmessageLocksReply;
    private final Button btMessageBlackEyeShowReply;
    private final Button btMessageBlackEyeHideReply;
    private final LinearLayout layoutMessageFrameReply;
    private final LinearLayout layoutMessageFrameContentUsuarioReply;
    private final TextView tvRemitenteReply;
    private final LinearLayout layoutMessageFrameContentReply;
    private final Button btActivateMessageTimeReply;
    private final LinearLayout layoutMessageContentDataReply;
    private final TextView tvMessageListTextReply;
    private final TextView tvLeermasReply;
    private final TextView tvLeermenosReply;
    private final LinearLayout layoutAudiochatReply;
    private final ImageButton ibMessageItemlistMediaAudioStopReply;
    private final ImageButton ibMessageItemlistMediaAudioPlayReply;
    private final SeekBar sbMessageItemlistMediaAudioProgressReply;
    private final Button btMessageItemlistMediaAudioVelocityReply;
    private final TextView tvMessageItemlistMediaAudioSecondsReply;
    private final ProgressBar pbMessageItemlistMediaAudioProgressBarReply;
    private final ImageView ivItemListImageMediaReply;
    private final ImageButton ibMessageItemlistErrorReply;
    private final LinearLayout layoutRemitenteContent;
    private final LinearLayout layoutRemitenteContentReply;
    private final LinearLayout layoutAllMessageReply;
    private final LinearLayout layoutItemListImageReply;
    private final ImageButton ibMessageItemlistMediaDownloadReply;
    private final ProgressBar progressBarMessageItemlistMediaDownloadReply;

    private final Button tvStateReply;

    //my_message
    private final LinearLayout layoutPersonalEncrypt;
    private final Button btPersonalEncryptLockClose;
    private final Button btPersonalEncryptLockOpen;
    private final LinearLayout layoutAllMessageSinPersonalEncrypt;
    private final LinearLayout layoutBlackmessageLocks;
    private final Button btMessageBlackEyeShow;
    private final Button btMessageBlackEyeHide;
    private final LinearLayout layoutMessageFrame;
    private final LinearLayout layoutMessageFrameContentUsuario;
    private final TextView tvRemitente;
    private final LinearLayout layoutMessageFrameContent;
    private final Button btActivateMessageTime;
    private final LinearLayout layoutMessageContentData;
    private final TextView tvMessageListText;
    private final TextView tvLeermas;
    private final TextView tvLeermenos;
    private final LinearLayout layoutAudiochat;
    private final ImageButton ibMessageItemlistMediaAudioStop;
    private final ImageButton ibMessageItemlistMediaAudioPlay;
    private final SeekBar sbMessageItemlistMediaAudioProgress;
    private final Button btMessageItemlistMediaAudioVelocity;
    private final TextView tvMessageItemlistMediaAudioSeconds;
    private final ProgressBar pbMessageItemlistMediaAudioProgressBar;
    private final ImageView ivItemListImageMedia;
    private final ImageButton ibMessageItemlistErrorRec;
    private final ImageButton ibMessageItemlistError;
    private final StateIcons stateIcons;

    private LinearLayout layoutItemListImage;
    private ImageButton ibMessageItemlistMediaDownload;
    private ProgressBar progressBarMessageItemlistMediaDownload;


    private final TextView tvState;
    private Button btIsAnonimo;
    private Button btIsHideReadState;

    private Button btIsBlockResend;
    // recibido empty message
    private final LinearLayout layoutEmptyMessageRec;
    private final ImageButton emptyMessageDownloadRec;
    // reply recibido

    private final LinearLayout layoutPersonalEncryptReplyRec;
    private final ImageButton btPersonalEncryptLockCloseReplyRec;
    private final Button btPersonalEncryptLockOpenReplyRec;
    private final LinearLayout layoutAllMessageSinPersonalEncryptReplyRec;
    private final LinearLayout layoutBlackmessageLocksReplyRec;
    private final Button btMessageBlackEyeShowReplyRec;
    private final Button btMessageBlackEyeHideReplyRec;
    private final LinearLayout layoutMessageFrameReplyRec;
    private final LinearLayout layoutMessageFrameContentUsuarioReplyRec;
    private final TextView tvRemitenteReplyRec;
    private final LinearLayout layoutMessageFrameContentReplyRec;
    private final Button btActivateMessageTimeReplyRec;
    private final LinearLayout layoutMessageContentDataReplyRec;
    private final TextView tvMessageListTextReplyRec;
    private final TextView tvLeermasReplyRec;
    private final TextView tvLeermenosReplyRec;
    private final LinearLayout layoutAudiochatReplyRec;
    private final ImageButton ibMessageItemlistMediaAudioStopReplyRec;
    private final ImageButton ibMessageItemlistMediaAudioPlayReplyRec;
    private final SeekBar sbMessageItemlistMediaAudioProgressReplyRec;
    private final Button btMessageItemlistMediaAudioVelocityReplyRec;
    private final TextView tvMessageItemlistMediaAudioSecondsReplyRec;
    private final ProgressBar pbMessageItemlistMediaAudioProgressBarReplyRec;
    private final ImageView ivItemListImageMediaReplyRec;
    private final ImageButton ibMessageItemlistErrorReplyRec;
    private final LinearLayout layoutRemitenteContentRec;
    private final LinearLayout layoutRemitenteContentReplyRec;
    private final LinearLayout layoutAllMessageReplyRec;
    private final LinearLayout layoutItemListImageReplyRec;
    private final ImageButton ibMessageItemlistMediaDownloadReplyRec;
    private final ProgressBar progressBarMessageItemlistMediaDownloadReplyRec;
    private final Button tvStateReplyRec;
    //recibido

    private final LinearLayout layoutAllMessageRec;
    private final LinearLayout layoutPersonalEncryptRec;
    private final Button btPersonalEncryptLockCloseRec;
    private final Button btPersonalEncryptLockOpenRec;
    private final LinearLayout layoutAllMessageSinPersonalEncryptRec;
    private final LinearLayout layoutBlackmessageLocksRec;
    private final Button btMessageBlackEyeShowRec;
    private final Button btMessageBlackEyeHideRec;
    private final LinearLayout layoutMessageFrameRec;
    private final LinearLayout layoutMessageFrameContentUsuarioRec;
    private final TextView tvRemitenteRec;
    private final LinearLayout layoutMessageFrameContentRec;
    private final Button btActivateMessageTimeRec;
    private final LinearLayout layoutMessageContentDataRec;
    private final TextView tvMessageListTextRec;
    private final TextView tvLeermasRec;
    private final TextView tvLeermenosRec;
    private final LinearLayout layoutAudiochatRec;
    private final ImageButton ibMessageItemlistMediaAudioStopRec;
    private final ProgressBar pbMessageItemlistMediaAudioProgressBarRec;

    private final ImageButton ibMessageItemlistMediaAudioPlayRec;
    private final SeekBar sbMessageItemlistMediaAudioProgressRec;
    private final Button btMessageItemlistMediaAudioVelocityRec;

    private final TextView tvMessageItemlistMediaAudioSecondsRec;
    private final ImageView ivItemListImageMediaRec;

    private LinearLayout layoutItemListImageRec;
    private ImageButton ibMessageItemlistMediaDownloadRec;
    private ProgressBar progressBarMessageItemlistMediaDownloadRec;
    private final StateIcons stateIconsRec;
    private final Button tvStateRec;
    private Button btIsAnonimoRec;
    private Button btIsHideReadStateRec;
    private Button btIsBlockResendRec;
    // SYSTEM
    private final TextView tvMessageListTextSystem;
    private final LinearLayout layoutSystem;
    //general

    private ItemListMessage itemListMessage;

    //

    private RecyclerHolderGeneric rch;
    private MessageSenderEnum messageSender;

    public RecyclerHolderGeneric getRCH(MessageSenderEnum messageSender) {
        //layoutAllMessage.setBackgroundResource(R.drawable.rounf);
        //layoutAllMessageRec.setVisibility(View.GONE);

        this.messageSender = messageSender;
        rch = new RecyclerHolderGeneric();

        rch.setLayoutSelected( layoutSelected);
       // rch.setLayoutEveryMessages(layoutEveryMessages);
        if (messageSender.equals(MessageSenderEnum.MY_MESSAGE)) {



            rch.setReply(new RecyclerHolderGeneric());

            rch.getReply().setLayoutEmptyMessage(layoutEmptyMessage);
            rch.getReply().setEmptyMessageDownload(emptyMessageDownload);
            rch.getReply().setLayoutAllMessage(layoutAllMessageReply);
            rch.getReply().setLayoutPersonalEncrypt(layoutPersonalEncryptReply);
            rch.getReply().setBtPersonalEncryptLockClose(btPersonalEncryptLockCloseReply);
            rch.getReply().setBtPersonalEncryptLockOpen(btPersonalEncryptLockOpenReply);
            rch.getReply().setLayoutAllMessageSinPersonalEncrypt(layoutAllMessageSinPersonalEncryptReply);
            rch.getReply().setLayoutBlackmessageLocks(layoutBlackmessageLocksReply);
            rch.getReply().setBtMessageBlackEyeShow(btMessageBlackEyeShowReply);
            rch.getReply().setBtMessageBlackEyeHide(btMessageBlackEyeHideReply);
            rch.getReply().setLayoutMessageFrame(layoutMessageFrameReply);
            rch.getReply().setLayoutMessageFrameContentUsuario(layoutMessageFrameContentUsuarioReply);
            rch.getReply().setTvRemitente(tvRemitenteReply);
            rch.getReply().setLayoutMessageFrameContent(layoutMessageFrameContentReply);
            rch.getReply().setBtActivateMessageTime(btActivateMessageTimeReply);
            rch.getReply().setLayoutMessageContentData(layoutMessageContentDataReply);
            rch.getReply().setTvMessageListText(tvMessageListTextReply);
            rch.getReply().setTvLeermas(tvLeermasReply);
            rch.getReply().setTvLeermenos(tvLeermenosReply);
            rch.getReply().setLayoutAudiochat(layoutAudiochatReply);

            rch.getReply().setIbMessageItemlistMediaAudioStop(ibMessageItemlistMediaAudioStopReply);
            rch.getReply().setIbMessageItemlistMediaAudioPlay(ibMessageItemlistMediaAudioPlayReply);
            rch.getReply().setSbMessageItemlistMediaAudioProgress(sbMessageItemlistMediaAudioProgressReply);
            rch.getReply().setBtMessageItemlistMediaAudioVelocity(btMessageItemlistMediaAudioVelocityReply);
            rch.getReply().setPbMessageItemlistMediaAudioProgressBar(pbMessageItemlistMediaAudioProgressBarReply);

            rch.getReply().setTvMessageItemlistMediaAudioSeconds(tvMessageItemlistMediaAudioSecondsReply);
            rch.getReply().setIvItemListImageMedia(ivItemListImageMediaReply);
            rch.getReply().setIbMessageItemlistError(ibMessageItemlistErrorReply);


            rch.getReply().setLayoutItemListImage(layoutItemListImageReply);
            rch.getReply().setIbMessageItemlistMediaDownload(ibMessageItemlistMediaDownloadReply);
            rch.getReply().setProgressBarMessageItemlistMediaDownload(progressBarMessageItemlistMediaDownloadReply);

            rch.getReply().setTvState(tvStateReply);
            rch.getReply().setLayoutRemitenteContent(layoutRemitenteContentReply);





            layoutAllMessage.setVisibility(View.VISIBLE);
            layoutAllMessageRec.setVisibility(View.GONE);
            layoutSystem.setVisibility(View.GONE);

            rch.setLayoutAllMessage(layoutAllMessage );



            rch.setLayoutPersonalEncrypt(layoutPersonalEncrypt );
            rch.setBtPersonalEncryptLockClose(btPersonalEncryptLockClose );
            rch.setBtPersonalEncryptLockOpen(btPersonalEncryptLockOpen );
            rch.setLayoutAllMessageSinPersonalEncrypt(layoutAllMessageSinPersonalEncrypt );
            rch.setLayoutBlackmessageLocks(layoutBlackmessageLocks );
            rch.setBtMessageBlackEyeShow(btMessageBlackEyeShow );
            rch.setBtMessageBlackEyeHide(btMessageBlackEyeHide );
            rch.setLayoutMessageFrame(layoutMessageFrame );
            rch.setLayoutMessageFrameContentUsuario(layoutMessageFrameContentUsuario );
            rch.setTvRemitente(tvRemitente );
            rch.setLayoutMessageFrameContent(layoutMessageFrameContent );
            rch.setBtActivateMessageTime(btActivateMessageTime );
            rch.setLayoutMessageContentData(layoutMessageContentData );
            rch.setTvMessageListText(tvMessageListText );
            rch.setTvLeermas(tvLeermas );
            rch.setTvLeermenos(tvLeermenos );
            rch.setLayoutAudiochat(layoutAudiochat );

            rch.setIbMessageItemlistMediaAudioStop(ibMessageItemlistMediaAudioStop );
            rch.setIbMessageItemlistMediaAudioPlay(ibMessageItemlistMediaAudioPlay );
            rch.setSbMessageItemlistMediaAudioProgress(sbMessageItemlistMediaAudioProgress );
            rch.setBtMessageItemlistMediaAudioVelocity(btMessageItemlistMediaAudioVelocity );
            rch.setPbMessageItemlistMediaAudioProgressBar(pbMessageItemlistMediaAudioProgressBar);

            rch.setTvMessageItemlistMediaAudioSeconds(tvMessageItemlistMediaAudioSeconds );
            rch.setIvItemListImageMedia(ivItemListImageMedia );
            rch.setIbMessageItemlistError(ibMessageItemlistError );


            rch.setLayoutItemListImage(layoutItemListImage);
            rch.setIbMessageItemlistMediaDownload(ibMessageItemlistMediaDownload);
            rch.setProgressBarMessageItemlistMediaDownload(progressBarMessageItemlistMediaDownload);
            rch.setStateIcons(stateIcons);
            rch.setTvState(tvState );
            rch.getReply().setLayoutRemitenteContent(layoutRemitenteContent);

        } else if (messageSender.equals(MessageSenderEnum.MESSAGE_RECEIVED)) {
            rch.setReply(new RecyclerHolderGeneric());

            rch.getReply().setLayoutEmptyMessage(layoutEmptyMessageRec);
            rch.getReply().setEmptyMessageDownload(emptyMessageDownloadRec);

            rch.getReply().setLayoutAllMessage(layoutAllMessageReplyRec);
            rch.getReply().setLayoutPersonalEncrypt(layoutPersonalEncryptReplyRec);
            rch.getReply().setBtPersonalEncryptLockClose(btPersonalEncryptLockCloseReplyRec);
            rch.getReply().setBtPersonalEncryptLockOpen(btPersonalEncryptLockOpenReplyRec);
            rch.getReply().setLayoutAllMessageSinPersonalEncrypt(layoutAllMessageSinPersonalEncryptReplyRec);
            rch.getReply().setLayoutBlackmessageLocks(layoutBlackmessageLocksReplyRec);
            rch.getReply().setBtMessageBlackEyeShow(btMessageBlackEyeShowReplyRec);
            rch.getReply().setBtMessageBlackEyeHide(btMessageBlackEyeHideReplyRec);
            rch.getReply().setLayoutMessageFrame(layoutMessageFrameReplyRec);
            rch.getReply().setLayoutMessageFrameContentUsuario(layoutMessageFrameContentUsuarioReplyRec);
            rch.getReply().setTvRemitente(tvRemitenteReplyRec);
            rch.getReply().setLayoutMessageFrameContent(layoutMessageFrameContentReplyRec);
            rch.getReply().setBtActivateMessageTime(btActivateMessageTimeReplyRec);
            rch.getReply().setLayoutMessageContentData(layoutMessageContentDataReplyRec);
            rch.getReply().setTvMessageListText(tvMessageListTextReplyRec);
            rch.getReply().setTvLeermas(tvLeermasReplyRec);
            rch.getReply().setTvLeermenos(tvLeermenosReplyRec);
            rch.getReply().setLayoutAudiochat(layoutAudiochatReplyRec);

            rch.getReply().setIbMessageItemlistMediaAudioStop(ibMessageItemlistMediaAudioStopReplyRec);
            rch.getReply().setIbMessageItemlistMediaAudioPlay(ibMessageItemlistMediaAudioPlayReplyRec);
            rch.getReply().setSbMessageItemlistMediaAudioProgress(sbMessageItemlistMediaAudioProgressReplyRec);
            rch.getReply().setBtMessageItemlistMediaAudioVelocity(btMessageItemlistMediaAudioVelocityReplyRec);
            rch.getReply().setPbMessageItemlistMediaAudioProgressBar(pbMessageItemlistMediaAudioProgressBarReplyRec);

            rch.getReply().setTvMessageItemlistMediaAudioSeconds(tvMessageItemlistMediaAudioSecondsReplyRec);
            rch.getReply().setIvItemListImageMedia(ivItemListImageMediaReplyRec);
            rch.getReply().setIbMessageItemlistError(ibMessageItemlistErrorReplyRec);


            rch.getReply().setLayoutItemListImage(layoutItemListImageReplyRec);
            rch.getReply().setIbMessageItemlistMediaDownload(ibMessageItemlistMediaDownloadReplyRec);
            rch.getReply().setProgressBarMessageItemlistMediaDownload(progressBarMessageItemlistMediaDownloadReplyRec);

            rch.getReply().setTvState(tvStateReplyRec);
            rch.getReply().setLayoutRemitenteContent(layoutRemitenteContentReplyRec);


            layoutAllMessage.setVisibility(View.GONE);
            layoutAllMessageRec.setVisibility(View.VISIBLE);
            layoutSystem.setVisibility(View.GONE);

            rch.setLayoutAllMessage(layoutAllMessageRec );
            rch.setLayoutPersonalEncrypt(layoutPersonalEncryptRec );
            rch.setBtPersonalEncryptLockClose(btPersonalEncryptLockCloseRec );
            rch.setBtPersonalEncryptLockOpen(btPersonalEncryptLockOpenRec );
            rch.setLayoutAllMessageSinPersonalEncrypt(layoutAllMessageSinPersonalEncryptRec );
            rch.setLayoutBlackmessageLocks(layoutBlackmessageLocksRec );
            rch.setBtMessageBlackEyeShow(btMessageBlackEyeShowRec );
            rch.setBtMessageBlackEyeHide(btMessageBlackEyeHideRec );
            rch.setLayoutMessageFrame(layoutMessageFrameRec );
            rch.setLayoutMessageFrameContentUsuario(layoutMessageFrameContentUsuarioRec );
            rch.setTvRemitente(tvRemitenteRec );
            rch.setLayoutMessageFrameContent(layoutMessageFrameContentRec );
            rch.setBtActivateMessageTime(btActivateMessageTimeRec );
            rch.setLayoutMessageContentData(layoutMessageContentDataRec );
            rch.setTvMessageListText(tvMessageListTextRec );
            rch.setTvLeermas(tvLeermasRec );
            rch.setTvLeermenos(tvLeermenosRec );
            rch.setLayoutAudiochat(layoutAudiochatRec );
            rch.setPbMessageItemlistMediaAudioProgressBar(pbMessageItemlistMediaAudioProgressBarRec);

            rch.setIbMessageItemlistMediaAudioStop(ibMessageItemlistMediaAudioStopRec );
            rch.setIbMessageItemlistMediaAudioPlay(ibMessageItemlistMediaAudioPlayRec );
            rch.setSbMessageItemlistMediaAudioProgress(sbMessageItemlistMediaAudioProgressRec );
            rch.setBtMessageItemlistMediaAudioVelocity(btMessageItemlistMediaAudioVelocityRec );

            rch.setTvMessageItemlistMediaAudioSeconds(tvMessageItemlistMediaAudioSecondsRec );
            rch.setIvItemListImageMedia(ivItemListImageMediaRec );

            rch.setIbMessageItemlistError(ibMessageItemlistErrorRec);

            rch.setLayoutItemListImage(layoutItemListImageRec);
            rch.setIbMessageItemlistMediaDownload(ibMessageItemlistMediaDownloadRec);
            rch.setProgressBarMessageItemlistMediaDownload(progressBarMessageItemlistMediaDownloadRec);

            rch.setStateIcons(stateIconsRec);
            rch.setTvState(tvStateRec );
            rch.getReply().setLayoutRemitenteContent(layoutRemitenteContentRec);
        } else if (messageSender.equals(MessageSenderEnum.SYSTEM_MESSAGE)) {

        }

        return rch;


    }
    public String getIdUsuario(UsuarioDTO u){
        if (u == null) return null;

        return u.getIdUsuario();
    }
    MessageActivity messageActivity;
    public void initViewShow(RecyclerHolderGeneric rch, ItemListMessage item, boolean isReply, MessageActivity messageActivity) {
        this.messageActivity=messageActivity;
        rch.getLayoutAudiochat().setClickable(false);
        rch.getLayoutAudiochat().setOnClickListener(v1 -> {});
        rch.getLayoutAudiochat().setEnabled(false);
        //rch.getLayoutAllMessageSinPersonalEncrypt().setVisibility(View.VISIBLE);
        rch.getLayoutMessageFrame().setVisibility(View.GONE);


//        rch.getTvMessageItemlistMediaAudioSeconds().setClickable(false);
//        rch.getTvMessageItemlistMediaAudioSeconds().setOnClickListener(v1 -> {});
//        rch.getTvMessageItemlistMediaAudioSeconds().setEnabled(false);

        if (item.getMessage().isBlockResend()&& !isReply){
            rch.getStateIcons().isBlockResend().setVisibility(View.VISIBLE);
        }else{
            rch.getStateIcons().isBlockResend().setVisibility(View.GONE);
        }
        if (item.getMessage().isAnonimo()&& !isReply){
            rch.getStateIcons().isAnonimo().setVisibility(View.VISIBLE);
        }else{
            rch.getStateIcons().isAnonimo().setVisibility(View.GONE);
        }

        if (item.getMessage().isHideMessageReadState()&& !isReply){
            rch.getStateIcons().isHideReadState().setVisibility(View.VISIBLE);
        }else{
            rch.getStateIcons().isHideReadState().setVisibility(View.GONE);
        }

        if (item.getMessage().isSecretKeyPersonal() && !isReply){
            rch.getLayoutPersonalEncrypt().setVisibility(View.VISIBLE);
            rch.getBtPersonalEncryptLockClose().setVisibility(View.VISIBLE);
            rch.getBtPersonalEncryptLockOpen().setVisibility(View.GONE);
            rch.getLayoutAllMessageSinPersonalEncrypt().setVisibility(View.GONE);
            //rch.getAllMessageLayout().setBackgroundColor(Color.parseColor("#A3A0A0"));
            rch.setSecretPersonal(true);
        }else{
            rch.getLayoutAllMessageSinPersonalEncrypt().setVisibility(View.VISIBLE);
            rch.getLayoutPersonalEncrypt().setVisibility(View.GONE);
            rch.getBtPersonalEncryptLockClose().setVisibility(View.GONE);
            rch.getBtPersonalEncryptLockOpen().setVisibility(View.GONE);
            rch.setSecretPersonal(false);
        }


        rch.getTvRemitente().setVisibility(View.VISIBLE);
        rch.getLayoutMessageContentData().setVisibility(View.VISIBLE);


        if (item.getMessage().amITimeMessage()){
            rch.setMessageTimeActive(true);
            rch.getBtActivateMessageTime().setVisibility(View.VISIBLE);
            rch.getLayoutMessageFrame().setVisibility(View.VISIBLE);
            rch.getLayoutMessageContentData().setVisibility(View.GONE);


        }else{
            rch.setMessageTimeActive(false);
            rch.getBtActivateMessageTime().setVisibility(View.GONE);

        }

    /*    if (!rch.isMessageBlackActive() &&  !rch.isMessageTimeActive()){
            rch.getLayoutAllMessageSinPersonalEncrypt().setVisibility(View.VISIBLE);
        }*/
        if (item.getMessage().getText() != null && !item.getMessage().getText().equals("")){
            rch.getTvMessageListText().setVisibility(View.VISIBLE);
        }else{
            rch.getTvMessageListText().setVisibility(View.GONE);
        }


        if (item.getMessage().getMedia() != null && item.getMessage().getMedia().getMediaType().equals(MediaTypeEnum.AUDIO_MESSAGE)){

            rch.getLayoutAudiochat().setVisibility(View.VISIBLE);

            if(item.isPlaying){
                rch.getIbMessageItemlistMediaAudioPlay().setVisibility(View.GONE);
                rch.getIbMessageItemlistMediaAudioStop().setVisibility(View.VISIBLE);
            }else{
                rch.getIbMessageItemlistMediaAudioPlay().setVisibility(View.VISIBLE);
                rch.getIbMessageItemlistMediaAudioStop().setVisibility(View.GONE);
            }

            rch.getSbMessageItemlistMediaAudioProgress().setVisibility(View.VISIBLE);
            rch.getTvMessageItemlistMediaAudioSeconds().setVisibility(View.VISIBLE);
            //rch.getStopMedia().setVisibility(View.VISIBLE);
            rch.getBtMessageItemlistMediaAudioVelocity().setVisibility(View.VISIBLE);

            rch.setHasMediaAudioChat(true);
        }else{
            rch.getLayoutAudiochat().setVisibility(View.GONE);
            rch.setHasMediaAudioChat(false);
        }
        if (item.getMessage().getMedia() != null && item.getMessage().getMedia().getMediaType().equals(MediaTypeEnum.IMAGE)){
            rch.getLayoutItemListImage().setVisibility(View.VISIBLE);

            if (item.getMessage().getMedia().getData() != null){
                rch.getIvItemListImageMedia().setVisibility(View.VISIBLE);
                rch.getIbMessageItemlistMediaDownload().setVisibility(View.GONE);
                rch.getProgressBarMessageItemlistMediaDownload().setVisibility(View.GONE);
            }else{
                //Drawable d = new BitmapDrawable(getResources(), );


                rch.getIvItemListImageMedia().setVisibility(View.GONE);

                if ( item.getMessage().canDownloadMyMedia()){
                    rch.getIbMessageItemlistMediaDownload().setVisibility(View.GONE);
                    rch.getProgressBarMessageItemlistMediaDownload().setVisibility(View.VISIBLE);
                }else{
                    rch.getIbMessageItemlistMediaDownload().setVisibility(View.VISIBLE);
                    rch.getProgressBarMessageItemlistMediaDownload().setVisibility(View.GONE);
                }


            }

            rch.setHasMediaImage(true);
        }else{
            rch.getLayoutItemListImage().setVisibility(View.GONE);
            rch.getProgressBarMessageItemlistMediaDownload().setVisibility(View.GONE);
            rch.getIvItemListImageMedia().setVisibility(View.GONE);
            rch.getIbMessageItemlistMediaDownload().setVisibility(View.GONE);


            rch.setHasMediaImage(false);

        }


    }

    public void endViewShow(RecyclerHolderGeneric rch, ItemListMessage item, Activity activity, boolean isReply) {

        if (rch.getTvState().getText().toString().trim().equals("") || isReply){
            rch.getTvState().setVisibility(View.GONE);
        }else{
            rch.getTvState().setVisibility(View.VISIBLE);
        }

/*        if (rch.getBtPersonalEncryptLockOpen().getVisibility() == View.VISIBLE){
            rch.getLayoutRemitenteContent().setVisibility(View.VISIBLE);
        }else{
            rch.getLayoutRemitenteContent().setVisibility(View.GONE);
        }
*/
        if (item.getMessage().amIMessageCreator() && !item.getMessage().isAnonimo()){
            if (rch.getTvRemitente() != null) rch.getTvRemitente().setVisibility(View.GONE);
            if (rch.getLayoutRemitenteContent() != null)rch.getLayoutRemitenteContent().setVisibility(View.GONE);
        }else {
            if (rch.getTvRemitente() != null) rch.getTvRemitente().setVisibility(View.VISIBLE);
            if (rch.getLayoutRemitenteContent() != null)rch.getLayoutRemitenteContent().setVisibility(View.VISIBLE);

        }

        try {


            if (SingletonValues.getInstance().getGrupoSeleccionado().getMembersQuantityDTO().getTotalQuantity() < 3) {
                rch.getTvRemitente().setVisibility(View.GONE);
                rch.getLayoutRemitenteContent().setVisibility(View.GONE);
            }
        }catch (Exception e){

        }

        rch.getIbMessageItemlistError().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, ErrorConsoleActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(UserHelperPagesContant.CONSOLE_ERROR_INTENT, rch.getErrors());
                i.putExtras(bundle);
                activity.startActivity(i);
            }
        });
        if (DeveloperConstant.showRemitenteMessage && !item.getMessage().isAnonimo()){
            rch.getTvRemitente().setText("(Dev)"+ rch.getTvRemitente().getText());
            rch.getTvRemitente().setVisibility(View.VISIBLE);
        }
        if ( SingletonValues.getInstance().getGrupoSeleccionado().getGralConfDTO().isHideMessageDetails()){
            rch.getTvState().setVisibility(View.GONE);
        }
        Black blackManager = new Black(rch, item,messageActivity);
        ExtraEncrypt extraEncrypt = new ExtraEncrypt(rch, item,messageActivity);

        Resend.processResend(item,rch,isReply,messageActivity);
        RandomNickname.processRandomNickname(item,rch,isReply,messageActivity);
        if(!item.getMessage().isBlackMessage() &&
                !item.getMessage().amITimeMessage() &&
                !item.getMessage().isSecretKeyPersonal() &&
                !item.getMessage().amIReplyMessage()     ){

            rch.getLayoutMessageFrame().setVisibility(View.VISIBLE);

        }
        if (messageActivity.findViewById(R.id.show_eye_txt).getVisibility() == View.VISIBLE){
         try {
/*                  if (rch.getTvMessageListText().getLineCount() != 0 ) {
                    rch.getTvMessageListText().setMaxLines(rch.getTvMessageListText().getLineCount());*/
                    rch.getTvMessageListText().setLetterSpacing((float)0.177);

                    rch.getTvMessageListText().setTransformationMethod(PasswordTransformationMethod.getInstance());
    //            }
            } catch (Exception e) {

            }
            try {
            ColorStateList csl = AppCompatResources.getColorStateList(messageActivity, R.color.black);
            ImageViewCompat.setImageTintList(rch.getIvItemListImageMedia(), csl);
            } catch (Exception e) {

            }
        }else{

            try {

                rch.getTvMessageListText().setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                ;
                rch.getTvMessageListText().setLetterSpacing(0);
                rch.getTvMessageListText().setMaxLines(Integer.MAX_VALUE);

            } catch (Exception e) {

            }
            try {
                ImageViewCompat.setImageTintList(rch.getIvItemListImageMedia(), null);
            } catch (Exception e) {

            }
        }
        item.url=null;
        if (rch.getTvMessageListText() != null && rch.getTvMessageListText().getText() != null) {
            List<String> url = extractUrls(item.getRch().getTvMessageListText().getText().toString());
            item.url=url;
            for (String u : url) {

                new TextHighlighter()
                        .setBackgroundColor(Color.parseColor("#FFFF00"))
                        .setForegroundColor(Color.GREEN)
                        .addTarget(rch.getTvMessageListText())

                        .highlight(u, TextHighlighter.BASE_MATCHER);


            }

        }

    }
    public static List<String> extractUrls(String text)
    {
        List<String> containedUrls = new ArrayList<String>();
        String urlRegex = "\\b((?:https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:, .;]*[-a-zA-Z0-9+&@#/%=~_|])";

        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(text);

        while (urlMatcher.find())
        {
            containedUrls.add(text.substring(urlMatcher.start(0),
                    urlMatcher.end(0)));
        }

        return containedUrls;
    }

    private ArrayList<View> getAllChildren(View v) {
        try{
            System.out.println(itemView.getResources().getResourceName(v.getId()));
            v.setVisibility(View.GONE);
            //System.out.println(" --> ACTION");
        }catch (Exception e){
            v.setVisibility(View.VISIBLE);
        }
        if (!(v instanceof ViewGroup)) {
            ArrayList<View> viewArrayList = new ArrayList<View>();

            viewArrayList.add(v);

            return viewArrayList;
        }

        ArrayList<View> result = new ArrayList<View>();

        ViewGroup viewGroup = (ViewGroup) v;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {

            View child = viewGroup.getChildAt(i);

            ArrayList<View> viewArrayList = new ArrayList<View>();
            viewArrayList.add(v);
            viewArrayList.addAll(getAllChildren(child));

            result.addAll(viewArrayList);
        }
        return result;
    }



    public RecyclerHolder(@NonNull View itemView) {
        super(itemView);
            /*
        recTvMessageListItemState = itemView.findViewById(R.id.rec_tv_message_list_state);
            recTvTitulo = itemView.findViewById(R.id.rec_tvTitulo);
            recTvMessageListText = itemView.findViewById(R.id.rec_tv_message_list_text);

            recBtnItemListMessageTime = (Button)itemView.findViewById(R.id.rec_btn_item_list_message_time);
            recIvItemListImage = (ImageView)itemView.findViewById(R.id.rec_iv_item_list_image);
            */
        //this.itemView_1 = itemView_1;


        //itemView.setOnCreateContextMenuListener(this);

        layoutSelected = (RelativeLayout) itemView.findViewById(R.id.msg_list_layout_selected);

        layoutEveryMessages = (LinearLayout) itemView.findViewById(R.id.msg_list_layout_every_messages);
        //empty message
        layoutEmptyMessage = (LinearLayout) itemView.findViewById(R.id.reply_msg_list_layout_empty_message);
        emptyMessageDownload = (ImageButton) itemView.findViewById(R.id.reply_msg_list_layout_empty_message_dowload);

        // my_message_reply

        this.layoutAllMessageReply = (LinearLayout) itemView.findViewById(R.id.reply_msg_list_layout_all_message);
        layoutPersonalEncryptReply = (LinearLayout) itemView.findViewById(R.id.reply_msg_list_layout_personal_encrypt);
        btPersonalEncryptLockCloseReply = (ImageButton) itemView.findViewById(R.id.reply_msg_list_bt_personal_encrypt_lock_close);
        btPersonalEncryptLockOpenReply = (Button) itemView.findViewById(R.id.reply_msg_list_bt_personal_encrypt_lock_open);
        layoutAllMessageSinPersonalEncryptReply = (LinearLayout) itemView.findViewById(R.id.reply_msg_list_layout_all_message_sin_personal_encrypt);
        layoutBlackmessageLocksReply = (LinearLayout) itemView.findViewById(R.id.reply_msg_list_layout_blackmessage_locks);
        btMessageBlackEyeShowReply = (Button) itemView.findViewById(R.id.reply_msg_list_bt_message_black_eye_show);
        btMessageBlackEyeHideReply = (Button) itemView.findViewById(R.id.reply_msg_list_bt_message_black_eye_hide);
        layoutMessageFrameReply = (LinearLayout) itemView.findViewById(R.id.reply_msg_list_layout_message_frame);
        layoutMessageFrameContentUsuarioReply = (LinearLayout) itemView.findViewById(R.id.reply_msg_list_layout_message_frame_content_usuario);
        tvRemitenteReply = (TextView) itemView.findViewById(R.id.reply_msg_list_tv_remitente);
        layoutMessageFrameContentReply = (LinearLayout) itemView.findViewById(R.id.reply_msg_list_layout_message_frame_content);
        btActivateMessageTimeReply = (Button) itemView.findViewById(R.id.reply_msg_list_bt_activate_message_time);
        layoutMessageContentDataReply = (LinearLayout) itemView.findViewById(R.id.reply_msg_list_layout_message_content_data);
        tvMessageListTextReply = (TextView) itemView.findViewById(R.id.reply_tv_message_list_text);
        tvLeermasReply = (TextView) itemView.findViewById(R.id.reply_msg_list_tv_leermas);
        tvLeermenosReply = (TextView) itemView.findViewById(R.id.reply_msg_list_tv_leermenos);
        layoutAudiochatReply = (LinearLayout) itemView.findViewById(R.id.reply_msg_list_layout_audiochat);

        this.layoutRemitenteContentReply = (LinearLayout) itemView.findViewById(R.id.reply_msg_list_layout_message_frame_remitente_content);

        /////////
        ibMessageItemlistMediaAudioStopReply = (ImageButton) itemView.findViewById(R.id.reply_ib_message_itemlist_media_audio_stop);
        ibMessageItemlistMediaAudioPlayReply = (ImageButton) itemView.findViewById(R.id.reply_ib_message_itemlist_media_audio_play);
        sbMessageItemlistMediaAudioProgressReply = (SeekBar) itemView.findViewById(R.id.reply_sb_message_itemlist_media_audio_progress);
        btMessageItemlistMediaAudioVelocityReply = (Button) itemView.findViewById(R.id.reply_bt_message_itemlist_media_audio_velocity);
        pbMessageItemlistMediaAudioProgressBarReply = (ProgressBar) itemView.findViewById(R.id.reply_pb_message_itemlist_media_audio_progressbar);

        ibMessageItemlistErrorReply = (ImageButton) itemView.findViewById(R.id.reply_ib_msg_list_error);

        tvMessageItemlistMediaAudioSecondsReply = (TextView) itemView.findViewById(R.id.reply_tv_message_itemlist_media_audio_seconds);

        ivItemListImageMediaReply = (ImageView) itemView.findViewById(R.id.reply_iv_item_list_image_media);

        layoutItemListImageReply = (LinearLayout) itemView.findViewById(R.id.reply_msg_list_layout_image);
        ibMessageItemlistMediaDownloadReply = (ImageButton) itemView.findViewById(R.id.reply_msg_list_layout_image_download);
        progressBarMessageItemlistMediaDownloadReply = (ProgressBar) itemView.findViewById(R.id.reply_msg_list_layout_progressbar_download);

        tvStateReply = (Button) itemView.findViewById(R.id.reply_msg_list_tv_state);
        //my_message
        layoutAllMessage = (LinearLayout) itemView.findViewById(R.id.msg_list_layout_all_message);
        layoutPersonalEncrypt = (LinearLayout) itemView.findViewById(R.id.msg_list_layout_personal_encrypt);
        btPersonalEncryptLockClose = (Button) itemView.findViewById(R.id.msg_list_bt_personal_encrypt_lock_close);
        btPersonalEncryptLockOpen = (Button) itemView.findViewById(R.id.msg_list_bt_personal_encrypt_lock_open);
        layoutAllMessageSinPersonalEncrypt = (LinearLayout) itemView.findViewById(R.id.msg_list_layout_all_message_sin_personal_encrypt);
        layoutBlackmessageLocks = (LinearLayout) itemView.findViewById(R.id.msg_list_layout_blackmessage_locks);
        btMessageBlackEyeShow = (Button) itemView.findViewById(R.id.msg_list_bt_message_black_eye_show);
        btMessageBlackEyeHide = (Button) itemView.findViewById(R.id.msg_list_bt_message_black_eye_hide);
        layoutMessageFrame = (LinearLayout) itemView.findViewById(R.id.msg_list_layout_message_frame);
        layoutMessageFrameContentUsuario = (LinearLayout) itemView.findViewById(R.id.msg_list_layout_message_frame_content_usuario);
        tvRemitente = (TextView) itemView.findViewById(R.id.msg_list_tv_remitente);
        layoutMessageFrameContent = (LinearLayout) itemView.findViewById(R.id.msg_list_layout_message_frame_content);
        btActivateMessageTime = (Button) itemView.findViewById(R.id.msg_list_bt_activate_message_time);
        layoutMessageContentData = (LinearLayout) itemView.findViewById(R.id.msg_list_layout_message_content_data);
        tvMessageListText = (TextView) itemView.findViewById(R.id.tv_message_list_text);
        tvLeermas = (TextView) itemView.findViewById(R.id.msg_list_tv_leermas);
        tvLeermenos = (TextView) itemView.findViewById(R.id.msg_list_tv_leermenos);
        layoutAudiochat = (LinearLayout) itemView.findViewById(R.id.msg_list_layout_audiochat);

        ibMessageItemlistMediaAudioStop = (ImageButton) itemView.findViewById(R.id.ib_message_itemlist_media_audio_stop);
        ibMessageItemlistMediaAudioPlay = (ImageButton) itemView.findViewById(R.id.ib_message_itemlist_media_audio_play);
        sbMessageItemlistMediaAudioProgress = (SeekBar) itemView.findViewById(R.id.sb_message_itemlist_media_audio_progress);
        btMessageItemlistMediaAudioVelocity = (Button) itemView.findViewById(R.id.bt_message_itemlist_media_audio_velocity);
        pbMessageItemlistMediaAudioProgressBar = (ProgressBar) itemView.findViewById(R.id.pb_message_itemlist_media_audio_progressbar);

        ibMessageItemlistError = (ImageButton) itemView.findViewById(R.id.ib_msg_list_error);

        tvMessageItemlistMediaAudioSeconds = (TextView) itemView.findViewById(R.id.tv_message_itemlist_media_audio_seconds);

        ivItemListImageMedia = (ImageView) itemView.findViewById(R.id.iv_item_list_image_media);

        layoutItemListImage = (LinearLayout) itemView.findViewById(R.id.msg_list_layout_image);
        ibMessageItemlistMediaDownload = (ImageButton) itemView.findViewById(R.id.msg_list_layout_image_download);
        progressBarMessageItemlistMediaDownload = (ProgressBar) itemView.findViewById(R.id.msg_list_layout_progressbar_download);

        tvState = (TextView) itemView.findViewById(R.id.msg_list_tv_state);

        this.stateIcons = new StateIcons()
                .isHideReadState((Button) itemView.findViewById(R.id.msg_list_hide_read_state_icon))
                .isExtraEncrypt((Button) itemView.findViewById(R.id.msg_list_extraencrypt_icon))
                .isBlack((Button) itemView.findViewById(R.id.msg_list_black_icon))
                .isRandomNickname((Button) itemView.findViewById(R.id.msg_list_random_nickname_icon))
                .isAnonimo((Button) itemView.findViewById(R.id.msg_list_anonimo_icon))
                .isResend((Button) itemView.findViewById(R.id.msg_list_is_resend_icon))
                .isBlockDownload((Button) itemView.findViewById(R.id.msg_list_block_download_icon))
                .isTime((Button) itemView.findViewById(R.id.msg_list_is_time_icon))
                .isAudioMessage((Button) itemView.findViewById(R.id.msg_list_is_audio_message_icon))
                .isBlockResend((Button) itemView.findViewById(R.id.msg_list_block_resend_icon));










        /*













          */

        tvLeermas.setPaintFlags(tvLeermas.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tvLeermenos.setPaintFlags(tvLeermenos.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        this.layoutRemitenteContent = (LinearLayout) itemView.findViewById(R.id.msg_list_layout_message_frame_remitente_content);

        //recibido empty message
        layoutEmptyMessageRec = (LinearLayout) itemView.findViewById(R.id.reply_msg_list_layout_empty_message_rec);
        emptyMessageDownloadRec = (ImageButton) itemView.findViewById(R.id.reply_msg_list_layout_empty_message_dowload_rec);

        // reply recibido
        this.layoutAllMessageReplyRec = (LinearLayout) itemView.findViewById(R.id.reply_msg_list_layout_all_message_rec);
        layoutPersonalEncryptReplyRec = (LinearLayout) itemView.findViewById(R.id.reply_msg_list_layout_personal_encrypt_rec);
        btPersonalEncryptLockCloseReplyRec = (ImageButton) itemView.findViewById(R.id.reply_msg_list_bt_personal_encrypt_lock_close_rec);
        btPersonalEncryptLockOpenReplyRec = (Button) itemView.findViewById(R.id.reply_msg_list_bt_personal_encrypt_lock_open_rec);
        layoutAllMessageSinPersonalEncryptReplyRec = (LinearLayout) itemView.findViewById(R.id.reply_msg_list_layout_all_message_sin_personal_encrypt_rec);
        layoutBlackmessageLocksReplyRec = (LinearLayout) itemView.findViewById(R.id.reply_msg_list_layout_blackmessage_locks_rec);
        btMessageBlackEyeShowReplyRec = (Button) itemView.findViewById(R.id.reply_msg_list_bt_message_black_eye_show_rec);
        btMessageBlackEyeHideReplyRec = (Button) itemView.findViewById(R.id.reply_msg_list_bt_message_black_eye_hide_rec);
        layoutMessageFrameReplyRec = (LinearLayout) itemView.findViewById(R.id.reply_msg_list_layout_message_frame_rec);
        layoutMessageFrameContentUsuarioReplyRec = (LinearLayout) itemView.findViewById(R.id.reply_msg_list_layout_message_frame_content_usuario_rec);
        tvRemitenteReplyRec = (TextView) itemView.findViewById(R.id.reply_msg_list_tv_remitente_rec);
        layoutMessageFrameContentReplyRec = (LinearLayout) itemView.findViewById(R.id.reply_msg_list_layout_message_frame_content_rec);
        btActivateMessageTimeReplyRec = (Button) itemView.findViewById(R.id.reply_msg_list_bt_activate_message_time_rec);
        layoutMessageContentDataReplyRec = (LinearLayout) itemView.findViewById(R.id.reply_msg_list_layout_message_content_data_rec);
        tvMessageListTextReplyRec = (TextView) itemView.findViewById(R.id.reply_tv_message_list_text_rec);
        tvLeermasReplyRec = (TextView) itemView.findViewById(R.id.reply_msg_list_tv_leermas_rec);
        tvLeermenosReplyRec = (TextView) itemView.findViewById(R.id.reply_msg_list_tv_leermenos_rec);
        layoutAudiochatReplyRec = (LinearLayout) itemView.findViewById(R.id.reply_msg_list_layout_audiochat_rec);

        this.layoutRemitenteContentReplyRec = (LinearLayout) itemView.findViewById(R.id.reply_msg_list_layout_message_frame_remitente_content_rec);

        /////////
        ibMessageItemlistMediaAudioStopReplyRec = (ImageButton) itemView.findViewById(R.id.reply_ib_message_itemlist_media_audio_stop_rec);
        ibMessageItemlistMediaAudioPlayReplyRec = (ImageButton) itemView.findViewById(R.id.reply_ib_message_itemlist_media_audio_play_rec);
        sbMessageItemlistMediaAudioProgressReplyRec = (SeekBar) itemView.findViewById(R.id.reply_sb_message_itemlist_media_audio_progress_rec);
        btMessageItemlistMediaAudioVelocityReplyRec = (Button) itemView.findViewById(R.id.reply_bt_message_itemlist_media_audio_velocity_rec);
        pbMessageItemlistMediaAudioProgressBarReplyRec = (ProgressBar) itemView.findViewById(R.id.reply_pb_message_itemlist_media_audio_progressbar_rec);

        ibMessageItemlistErrorReplyRec = (ImageButton) itemView.findViewById(R.id.reply_ib_msg_list_error_rec);

        tvMessageItemlistMediaAudioSecondsReplyRec = (TextView) itemView.findViewById(R.id.reply_tv_message_itemlist_media_audio_seconds_rec);

        ivItemListImageMediaReplyRec = (ImageView) itemView.findViewById(R.id.reply_iv_item_list_image_media_rec);

        layoutItemListImageReplyRec = (LinearLayout) itemView.findViewById(R.id.reply_msg_list_layout_image_rec);
        ibMessageItemlistMediaDownloadReplyRec = (ImageButton) itemView.findViewById(R.id.reply_msg_list_layout_image_download_rec);
        progressBarMessageItemlistMediaDownloadReplyRec = (ProgressBar) itemView.findViewById(R.id.reply_msg_list_layout_progressbar_download_rec);

        tvStateReplyRec = (Button) itemView.findViewById(R.id.reply_msg_list_tv_state_rec);


        /// recibido

        layoutAllMessageRec = (LinearLayout) itemView.findViewById(R.id.msg_list_layout_all_message_rec);
        layoutPersonalEncryptRec = (LinearLayout) itemView.findViewById(R.id.msg_list_layout_personal_encrypt_rec);
        btPersonalEncryptLockCloseRec = (Button) itemView.findViewById(R.id.msg_list_bt_personal_encrypt_lock_close_rec);
        btPersonalEncryptLockOpenRec = (Button) itemView.findViewById(R.id.msg_list_bt_personal_encrypt_lock_open_rec);
        layoutAllMessageSinPersonalEncryptRec = (LinearLayout) itemView.findViewById(R.id.msg_list_layout_all_message_sin_personal_encrypt_rec);
        layoutBlackmessageLocksRec = (LinearLayout) itemView.findViewById(R.id.msg_list_layout_blackmessage_locks_rec);
        btMessageBlackEyeShowRec = (Button) itemView.findViewById(R.id.msg_list_bt_message_black_eye_show_rec);
        btMessageBlackEyeHideRec = (Button) itemView.findViewById(R.id.msg_list_bt_message_black_eye_hide_rec);
        layoutMessageFrameRec = (LinearLayout) itemView.findViewById(R.id.msg_list_layout_message_frame_rec);
        layoutMessageFrameContentUsuarioRec = (LinearLayout) itemView.findViewById(R.id.msg_list_layout_message_frame_content_usuario_rec);
        tvRemitenteRec = (TextView) itemView.findViewById(R.id.msg_list_tv_remitente_rec);
        layoutMessageFrameContentRec = (LinearLayout) itemView.findViewById(R.id.msg_list_layout_message_frame_content_rec);
        btActivateMessageTimeRec = (Button) itemView.findViewById(R.id.msg_list_bt_activate_message_time_rec);
        layoutMessageContentDataRec = (LinearLayout) itemView.findViewById(R.id.msg_list_layout_message_content_data_rec);
        tvMessageListTextRec = (TextView) itemView.findViewById(R.id.tv_message_list_text_rec);
        tvLeermasRec = (TextView) itemView.findViewById(R.id.msg_list_tv_leermas_rec);
        tvLeermenosRec = (TextView) itemView.findViewById(R.id.msg_list_tv_leermenos_rec);
        layoutAudiochatRec = (LinearLayout) itemView.findViewById(R.id.msg_list_layout_audiochat_rec);

        ibMessageItemlistMediaAudioStopRec = (ImageButton) itemView.findViewById(R.id.ib_message_itemlist_media_audio_stop_rec);
        ibMessageItemlistMediaAudioPlayRec = (ImageButton) itemView.findViewById(R.id.ib_message_itemlist_media_audio_play_rec);
        sbMessageItemlistMediaAudioProgressRec = (SeekBar) itemView.findViewById(R.id.sb_message_itemlist_media_audio_progress_rec);
        btMessageItemlistMediaAudioVelocityRec = (Button) itemView.findViewById(R.id.bt_message_itemlist_media_audio_velocity_rec);
        pbMessageItemlistMediaAudioProgressBarRec = (ProgressBar) itemView.findViewById(R.id.pb_message_itemlist_media_audio_progressbar_rec);

        ibMessageItemlistErrorRec = (ImageButton) itemView.findViewById(R.id.ib_msg_list_error_rec);



        tvMessageItemlistMediaAudioSecondsRec = (TextView) itemView.findViewById(R.id.tv_message_itemlist_media_audio_seconds_rec);
        ivItemListImageMediaRec = (ImageView) itemView.findViewById(R.id.iv_item_list_image_media_rec);

        layoutItemListImageRec = (LinearLayout) itemView.findViewById(R.id.msg_list_layout_image_rec);
        ibMessageItemlistMediaDownloadRec = (ImageButton) itemView.findViewById(R.id.msg_list_layout_image_download_rec);
        progressBarMessageItemlistMediaDownloadRec = (ProgressBar) itemView.findViewById(R.id.msg_list_layout_progressbar_download_rec);

        this.stateIconsRec = new StateIcons()
                .isHideReadState((Button) itemView.findViewById(R.id.msg_list_hide_read_state_icon_rec))
                .isExtraEncrypt((Button) itemView.findViewById(R.id.msg_list_extraencrypt_icon_rec))
                .isBlack((Button) itemView.findViewById(R.id.msg_list_black_icon_rec))
                .isRandomNickname((Button) itemView.findViewById(R.id.msg_list_random_nickname_icon_rec))
                .isAnonimo((Button) itemView.findViewById(R.id.msg_list_anonimo_icon_rec))
                .isResend((Button) itemView.findViewById(R.id.msg_list_is_resend_icon_rec))
                .isBlockDownload((Button) itemView.findViewById(R.id.msg_list_block_download_icon_rec))
                .isTime((Button) itemView.findViewById(R.id.msg_list_is_time_icon_rec))
                .isAudioMessage((Button) itemView.findViewById(R.id.msg_list_is_audio_message_icon_rec))
                .isBlockResend((Button) itemView.findViewById(R.id.msg_list_block_resend_icon_rec));

        tvStateRec = (Button) itemView.findViewById(R.id.msg_list_tv_state_rec);


        tvLeermasRec.setPaintFlags(tvLeermasRec.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tvLeermenosRec.setPaintFlags(tvLeermenosRec.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        this.layoutRemitenteContentRec = (LinearLayout) itemView.findViewById(R.id.msg_list_layout_message_frame_remitente_content_rec);

        // system
        tvMessageListTextSystem = (TextView) itemView.findViewById(R.id.tv_message_list_text_system);
        layoutSystem = (LinearLayout) itemView.findViewById(R.id.layout_system);


    }

    //@Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        SingletonValues.getInstance().setMessageDetailSeleccionado(this.itemListMessage);
        menu.clear();

        menu.add(1, v.getId(), 0, messageActivity.getString(R.string.message_activity_message_actions__response));//groupId, itemId, order, title
        if (!this.itemListMessage.getMessage().amITimeMessage() && !this.itemListMessage.getMessage().isSystemMessage()
                && !this.itemListMessage.getMessageDetail().getEstado().equals(MessageState.MY_MESSAGE_SENDING)
                && !this.itemListMessage.getMessageDetail().getEstado().equals(MessageState.MY_MESSAGE_ERROR_NOT_SEND)
                && this.itemListMessage.getMessage().isBlockResend() )
        {
            menu.add(1, v.getId(), 0, messageActivity.getString(R.string.message_activity_message_actions__forward));//groupId, itemId, order, title
        }


        if (this.itemListMessage.getMessageDetail().getEstado().equals(MessageState.MY_MESSAGE_ERROR_NOT_SEND)){
            menu.add(1, v.getId(), 0, messageActivity.getString(R.string.message_activity_message_actions__reintentar));//groupId, itemId, order, title
        }
        SubMenu subMenuEliminar = menu.addSubMenu(messageActivity.getString(R.string.message_activity_message_actions__delete));

        subMenuEliminar.add(0, v.getId(), 1,messageActivity.getString(R.string.message_activity__delete_message__for_me) );//groupId, itemId, order, title

        if (this.itemListMessage.getMessage().getUsuarioCreacion() != null
                && this.itemListMessage.getMessage().getUsuarioCreacion().getIdUsuario() != null
                && this.itemListMessage.getMessage().getUsuarioCreacion().getIdUsuario().equals(Singletons.usuario().getUsuario().getIdUsuario())){
            subMenuEliminar.add(0, v.getId(), 2, messageActivity.getString(R.string.message_activity__delete_message__for_everyone));
            subMenuEliminar.add(0, v.getId(), 3, messageActivity.getString(R.string.message_activity__delete_message__for_everyone_and_resend));
        }



    }



}
