package com.privacity.cliente.activity.message.attach;

import android.app.Activity;
import android.view.View;
import android.widget.ImageButton;

import com.privacity.cliente.R;

import lombok.Getter;

@Getter
public class MessageAttach {

    protected final ImageButton image;
    protected final ImageButton audio;
    protected final ImageButton doc;
    protected final ImageButton camera;

    protected final View contenedor;

    protected final Activity activity;


    public MessageAttach(Activity activity) {
        this.activity = activity;
        image = (ImageButton) activity.findViewById(R.id.message_attach_image);
        audio = (ImageButton) activity.findViewById(R.id.message_attach_audio);
        doc = (ImageButton) activity.findViewById(R.id.message_attach_doc);
        camera = (ImageButton) activity.findViewById(R.id.message_attach_camera);

        contenedor  = (View) activity.findViewById(R.id.message_attach_contenedor);

        contenedor.setVisibility(View.GONE);

    }

    public void setListener(){

        camera.setOnClickListener(TakePhoto.getListener(activity));


        image.setOnClickListener(GetImage.getListener(activity));


    }




}

