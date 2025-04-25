package com.privacity.cliente.activity.message.validations;

import android.view.View;

import com.privacity.cliente.activity.message.ItemListMessage;
import com.privacity.cliente.activity.message.MessageActivity;
import com.privacity.cliente.activity.message.RecyclerHolderGeneric;
import com.privacity.cliente.activity.message.RecyclerMessageAdapter;
import com.privacity.common.enumeration.MediaTypeEnum;

import java.util.List;

public class Audio {
    private static final String TAG = "Audio";

    public static void processAudio(RecyclerMessageAdapter recyclerMessageAdapter, ItemListMessage item, RecyclerHolderGeneric rch, MessageActivity messageActivity, List<ItemListMessage> items) {


        if (item.getMessage().getMedia() != null && item.getMessage().getMedia().getMediaType().equals(MediaTypeEnum.AUDIO_MESSAGE)) {
            rch.getStateIcons().isAudioMessage().setVisibility(View.VISIBLE);
        } else {
            rch.getStateIcons().isAudioMessage().setVisibility(View.GONE);
        }
    }
}
