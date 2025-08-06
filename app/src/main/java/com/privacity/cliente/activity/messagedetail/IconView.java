package com.privacity.cliente.activity.messagedetail;

import android.view.View;
import android.widget.Button;

import com.privacity.cliente.model.Message;
import com.privacity.cliente.singleton.Observers;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.singleton.activity.SingletonCurrentActivity;
import com.privacity.cliente.singleton.toast.SingletonToastManager;
import com.privacity.common.enumeration.MediaTypeEnum;

public class IconView {


    private Button iconBlockDownload;
    private Button iconIsAudioMessage;
    private Button iconIsTime;
    private Button iconHasImage;
    private Button iconIsResend;
    private Button iconIsReply;
    private Button iconIsExtraEncrypt;
    private Button iconBlockResendTrue;
    private Button iconIsBlack;
    private Button iconBlockResendFalse;
    private Button iconIsAnon;
    private Button iconRandomNickname;
    private Button iconisHideMessageReadState;

    public IconView(){
        initView();
        initVisibility();
    }

    private void initVisibility() {
        Message m = Observers.message().getMensajesPorId(
                SingletonValues.getInstance().getMessageDetailSeleccionado().getMessage().buildIdMessageToMap());

        setVisibility ( iconBlockDownload, m.hasMedia() && !m.canDownloadMyMedia());
        setVisibility ( iconIsAudioMessage, m.hasMedia() && m.getMedia().getMediaType().equals(MediaTypeEnum.AUDIO_MESSAGE) );
        setVisibility ( iconIsTime, m.amITimeMessage() );
        setVisibility ( iconHasImage, m.hasMedia() && m.getMedia().getMediaType().equals(MediaTypeEnum.IMAGE) );
        setVisibility ( iconIsResend, m.getParentResend() != null );
        setVisibility ( iconIsReply, m.amIReplyMessage() );
        setVisibility ( iconIsExtraEncrypt, m.isSecretKeyPersonal() );
        setVisibility ( iconBlockResendTrue, m.isBlockResend() );
        setVisibility ( iconIsBlack, m.isBlackMessage() );
        setVisibility ( iconBlockResendFalse, !m.isBlockResend() );
        setVisibility ( iconIsAnon, m.getShowingAnonimo() );
        setVisibility ( iconisHideMessageReadState, m.isHideMessageReadState() );
        setVisibility ( iconRandomNickname, m.isChangeNicknameToRandom() && !m.getShowingAnonimo());
    }

    private void initView() {
        iconBlockDownload=initButtonViewAndListener("message_detail__icon__block_download");
        iconIsAudioMessage=initButtonViewAndListener("message_detail__icon__is_audio_message");
        iconIsTime=initButtonViewAndListener("message_detail__icon__is_time");
        iconHasImage=initButtonViewAndListener("message_detail__icon__has_image");
        iconIsResend=initButtonViewAndListener("message_detail__icon__is_resend");
        iconIsReply=initButtonViewAndListener("message_detail__icon__is_reply");
        iconIsExtraEncrypt=initButtonViewAndListener("message_detail__icon__is_extra_encrypt");
        iconBlockResendTrue=initButtonViewAndListener("message_detail__icon__block_resend_true");
        iconIsBlack=initButtonViewAndListener("message_detail__icon__is_black");
        iconBlockResendFalse = initButtonViewAndListener("message_detail__icon__block_resend_false");
        iconIsAnon = initButtonViewAndListener("message_detail__icon__anonimo");
        iconRandomNickname = initButtonViewAndListener("message_detail__icon__random_nickname");
        iconisHideMessageReadState = initButtonViewAndListener("message_detail__icon__is_hide_message_read_state");
    }

    private void setVisibility(View v, boolean show){
        if (show){  v.setVisibility(View.VISIBLE);}
        else {v.setVisibility(View.GONE);}
    }

    public Button initButtonViewAndListener(String strid){
        int id = SingletonCurrentActivity.getInstance().get().getResources().getIdentifier(strid, "id", SingletonCurrentActivity.getInstance().get().getPackageName());

        Button b = (Button)SingletonCurrentActivity.getInstance().get().findViewById(id);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SingletonToastManager.getInstance().showToadShort(
                        SingletonCurrentActivity.getInstance().get(),
                        SingletonCurrentActivity.getInstance().get().getString(

                                SingletonCurrentActivity.getInstance().get().getResources().getIdentifier(strid, "string", SingletonCurrentActivity.getInstance().get().getPackageName())));


            }});

        return b;
    }


}




