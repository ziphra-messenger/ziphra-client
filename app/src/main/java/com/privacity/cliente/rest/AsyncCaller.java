package com.privacity.cliente.rest;

import android.os.AsyncTask;

import com.privacity.cliente.singleton.impl.SingletonServer;

import java.net.URL;
import java.net.URLConnection;

public class AsyncCaller extends AsyncTask<Void, Void, Void>
    {
        public AsyncCaller(CallbackActionRest c) {
            this.c = c;
        }

        CallbackActionRest c;

        boolean success=false;
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                String url = SingletonServer.getInstance().getAppServer();
                int timeout = 3000;
                URL myUrl = new URL(url);
                URLConnection connection = myUrl.openConnection();
                connection.setConnectTimeout(timeout);

                connection.connect();

                success=true;

            } catch (Exception e) {
                e.printStackTrace();

            }

            return null;
        }
            @Override
            protected void onPostExecute(Void result) {
                if (success){
                    c.onSucess();
                    return;
                }

                c.onError();
            }

    }
