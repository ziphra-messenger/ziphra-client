package com.privacity.cliente.activity.imagefull;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import org.apache.commons.lang3.RandomStringUtils;

import java.io.File;
import java.io.FileOutputStream;

public class Save {
 
    private Context TheThis;
    private final String NameOfFolder = "";///""/Nuevacarpeta";
    //private String NameOfFile = "imagen";

    public static String generate() {
        return RandomStringUtils.randomNumeric(15);
    }
    public void SaveImage(Context context, Bitmap ImageToSave) {
 
        TheThis = context;
        //String file_path =
        String CurrentDateAndTime = generate();
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
 
        if (!dir.exists()) {
            dir.mkdirs();
        }
 
        File file = new File(dir, CurrentDateAndTime + ".jpg");
 
        try {
            FileOutputStream fOut = new FileOutputStream(file);
 
            ImageToSave.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
            MakeSureFileWasCreatedThenMakeAvabile(file);
            AbleToSave();

        } catch(Exception e) {
            e.printStackTrace();
            UnableToSave();
        }

    }
 
    private void MakeSureFileWasCreatedThenMakeAvabile(File file){
        MediaScannerConnection.scanFile(TheThis,
            new String[] { file.toString() } , null,
            new MediaScannerConnection.OnScanCompletedListener() {

                public void onScanCompleted(String path, Uri uri) {
                }
            });
    }
    /*
    private String getCurrentDateAndTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-­ss");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }
    */
    private void UnableToSave() {
        Toast.makeText(TheThis, "¡No se ha podido guardar la imagen!", Toast.LENGTH_SHORT).show();
    }
 
    private void AbleToSave() {
       // Toast.makeText(TheThis, "Imagen guardada en la galería.", Toast.LENGTH_SHORT).show();
    }
}