package com.privacity.cliente.activity.message.utils;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class BitMapTools {
    private static final String TAG = "BitMapTools";

    public static Bitmap getBitmap(ContentResolver mContentResolver, Uri uri, int imageMaxSize) {


        InputStream in = null;
        try {

            in = mContentResolver.openInputStream(uri);

            // Decode image size
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, options);
            in.close();



            int scale = 1;
            while ((options.outWidth * options.outHeight) * (1 / Math.pow(scale, 2)) >
                    imageMaxSize) {
                scale++;
            }
            Log.d(TAG, "scale = " + scale + ", orig-width: " + options.outWidth + ",  orig-height: " + options.outHeight);

            Bitmap resultBitmap = null;
            in = mContentResolver.openInputStream(uri);
            if (scale > 1) {
                scale--;
                // scale to max possible inSampleSize that still yields an image
                // larger than target
                options = new BitmapFactory.Options();
                options.inSampleSize = scale;
                resultBitmap = BitmapFactory.decodeStream(in, null, options);

                // resize to desired dimensions
                int height = resultBitmap.getHeight();
                int width = resultBitmap.getWidth();
                Log.d(TAG, "1th scale operation dimenions - width: " + width + ",   height: " + height);

                double y = Math.sqrt(imageMaxSize
                        / (((double) width) / height));
                double x = (y / height) * width;

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(resultBitmap, (int) x,
                        (int) y, true);
                //  resultBitmap.recycle();
                resultBitmap = scaledBitmap;

                System.gc();
            } else {
                resultBitmap = BitmapFactory.decodeStream(in);
            }
            in.close();

            Log.d(TAG, "bitmap size - width: " +resultBitmap.getWidth() + ", height: " +
                    resultBitmap.getHeight());
            return resultBitmap;
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(),e);
            return null;
        }
    }

    public static Bitmap getBitmapCamera(ContentResolver mContentResolver, byte[] b, int imagenMaxSize) {

        InputStream in = new ByteArrayInputStream(b);
        try {
            // 1.2MP


            // Decode image size
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, options);
            in.close();



            int scale = 1;
            while ((options.outWidth * options.outHeight) * (1 / Math.pow(scale, 2)) >
                    imagenMaxSize) {
                scale++;
            }
            Log.d(TAG, "scale = " + scale + ", orig-width: " + options.outWidth + ",  orig-height: " + options.outHeight);

            Bitmap resultBitmap = null;
            in = new ByteArrayInputStream(b);
            if (scale > 1) {
                scale--;
                // scale to max possible inSampleSize that still yields an image
                // larger than target
                options = new BitmapFactory.Options();
                options.inSampleSize = scale;
                resultBitmap = BitmapFactory.decodeStream(in, null, options);

                // resize to desired dimensions
                int height = resultBitmap.getHeight();
                int width = resultBitmap.getWidth();
                Log.d(TAG, "1th scale operation dimenions - width: " + width + ",   height: " + height);

                double y = Math.sqrt(imagenMaxSize
                        / (((double) width) / height));
                double x = (y / height) * width;

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(resultBitmap, (int) x,
                        (int) y, true);
                // resultBitmap.recycle();
                resultBitmap = scaledBitmap;

                System.gc();
            } else {
                resultBitmap = BitmapFactory.decodeStream(in);
            }
            in.close();

            //Log.d(TAG, "bitmap size - width: " +resultBitmap.getWidth() + ", height: " +
            //        resultBitmap.getHeight());
            return resultBitmap;
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(),e);
            return null;
        }
    }

}
