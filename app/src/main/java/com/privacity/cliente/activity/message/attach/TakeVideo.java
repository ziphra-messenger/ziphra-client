package com.privacity.cliente.activity.message.attach;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class TakeVideo
{
    private static final int CAMERA_REQUEST = 1888;
    private ImageView imageView;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;


    public static View.OnClickListener getListener(Activity activity)
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (activity.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    activity.requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                }
                else
                {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    activity.startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        };
    }

    public void onRequestPermissionsResult(Activity activity, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        activity.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(activity, "camera permission granted", Toast.LENGTH_SHORT).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
                activity.startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
            else
            {
                Toast.makeText(activity, "camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
