package com.privacity.cliente.activity.message.attach;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import org.jetbrains.annotations.NotNull;

public class GetImage {

    public static final int PICK_IMAGE_REQUEST = 1;
    @NotNull
    public static View.OnClickListener getListener(Activity activity) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("*/*");

                intent.setAction(Intent.ACTION_GET_CONTENT);
                activity.startActivityForResult(Intent.createChooser(intent, "Select Picture"), GetImage.PICK_IMAGE_REQUEST);

            }
        };
    }
}
