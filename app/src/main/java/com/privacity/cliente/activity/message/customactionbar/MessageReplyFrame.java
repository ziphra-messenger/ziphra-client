package com.privacity.cliente.activity.message.customactionbar;

import android.text.InputType;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.message.ItemListMessage;
import com.privacity.cliente.activity.message.MessageActivity;
import com.privacity.cliente.model.Message;
import com.privacity.common.enumeration.MediaTypeEnum;

import lombok.Getter;

public class MessageReplyFrame {

    private final MessageActivity activity;
    @Getter
    private final View viewParent;
    private final TextView remitente;
    private final TextView text;
    private final ImageView image;
    private final ImageButton close;

    public MessageReplyFrame(MessageActivity activity) {
        this.viewParent = activity.findViewById(R.id.tr_message_reply_view);

        this.remitente = (TextView) activity.findViewById(R.id.message_reply_remitente);
        this.text = (TextView) activity.findViewById(R.id.message_reply_text);

        this.image = (ImageView) activity.findViewById(R.id.message_reply_image);

        this.close = (ImageButton) activity.findViewById(R.id.message_reply_close);

        this.activity = activity;

        text.setElegantTextHeight(true);
        text.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        text.setSingleLine(false);

        resetValues();
        setListener();
    }

    public void setListener(){

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetValues();
                viewParent.setVisibility(View.GONE);
            }
        });

    }

    public void resetValues(){
        message = null;
        remitente.setText("");
        text.setText("");
        image.setImageBitmap(null);
        viewParent.setVisibility(View.GONE);
        remitente.setVisibility(View.VISIBLE);
        image.setVisibility(View.VISIBLE);
    }
    @Getter
    private Message message;
    public void loadValues(ItemListMessage parentReply){
        resetValues();
        message = parentReply.getMessage();
        populate(parentReply);
    }
    private void populate(ItemListMessage parentReply){

        if (parentReply.getRch().getTvRemitente() != null){
            remitente.setText(parentReply.getRch().getTvRemitente().getText());
            if ( remitente.getText().toString().trim().equals("")){
                remitente.setVisibility(View.GONE);
            }
        }

        if ( parentReply.getMessage().getMediaDTO() != null ){
            if (parentReply.getMessage().getMediaDTO().getMediaType().equals(MediaTypeEnum.AUDIO_MESSAGE.name())){
                text.setText("Mensaje de Audio");
            }

            if (parentReply.getMessage().getMediaDTO().getMediaType().equals(MediaTypeEnum.IMAGE.name())){
                image.setImageDrawable(parentReply.getRch().getIvItemListImageMedia().getDrawable());
            }else{
                image.setVisibility(View.GONE);
            }
        }
        if ( text.getText().toString().trim().equals("")){
            text.setText(parentReply.getRch().getTvMessageListText().getText().toString());
        }

        if ( text.getText().toString().length() > 20){
            text.setText( text.getText().toString().substring(0,20) + "...");
        }


        viewParent.setVisibility(View.VISIBLE);
    }
}

