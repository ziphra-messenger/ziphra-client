package com.privacity.cliente.activity.message.textsizemessage;

import android.app.Activity;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.privacity.cliente.R;
import com.privacity.cliente.activity.message.MessageActivity;
import com.privacity.cliente.activity.message.avanzado.MessageAvanzado;
import com.privacity.cliente.singleton.activity.SingletonCurrentActivity;
import com.privacity.cliente.singleton.localconfiguration.SingletonTextSizeMessage;

import lombok.Getter;

@Getter
public class TextSizeMessageView {
    private MessageActivity activity;
    private Button increase;
    private Button decrease;

    private View content;
    private View contentAll;
    private Button close;


    private Button open;

    public TextSizeMessageView(MessageActivity activity) {
        this.activity = activity;


        open=(Button)activity.findViewById(R.id.frame_text_resize__open);
        close=(Button)activity.findViewById(R.id.frame_text_resize__close);
        increase=(Button)activity.findViewById(R.id.frame_text_resize__increase);
        decrease=(Button)activity.findViewById(R.id.frame_text_resize__decrease);

        content=activity.findViewById(R.id.frame_text_resize__content);
        contentAll=activity.findViewById(R.id.frame_text_resize__content_all);

        initValues();
        initListeners();
    }

    private void initListeners() {
        close.setOnClickListener(getListenerChangeViewStatus());
        open.setOnClickListener(getListenerChangeViewStatus());
    }

    private void initValues() {
        contentAll.setVisibility(SingletonTextSizeMessage.getInstance().getViewStatus(activity));

    }

    @NonNull
    private View.OnClickListener getListenerChangeViewStatus() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SingletonTextSizeMessage.getInstance().saveChangeViewStatus(activity);
                contentAll.setVisibility(SingletonTextSizeMessage.getInstance().getViewStatus(activity));

               SingletonCurrentActivity.getInstance().getMessageActivity().messageAvanzadoClose();
            }
        };
    }


}
