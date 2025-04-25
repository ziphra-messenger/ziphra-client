package com.privacity.cliente.rest;

import android.app.Activity;
import android.os.AsyncTask;

import com.privacity.cliente.singleton.impl.SingletonServer;

import java.net.URL;
import java.net.URLConnection;

public class AsyncCaller extends AsyncTask<Void, Void, Void>
    {
        public AsyncCaller(CallbackActionRest c, Activity activity) {

            this.c = c;
            this.activity=activity;
        }
        Activity activity;
        CallbackActionRest c;

        boolean success=false;
        @Override
        protected Void doInBackground(Void... voids) {
            try {

//                KeyStore trustStore = KeyStore.getInstance("BKS");
//                InputStream in = activity.getResources().openRawResource(R.raw.privacity);
//                trustStore.load(in, "privacity".toCharArray());
//                TrustManagerFactory trustManagerFactory =
//                        TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
//                trustManagerFactory.init(trustStore);
//
//                SSLContext context = SSLContext.getInstance("SSL");
//                context.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
//                HostnameVerifier allHostsValid = new HostnameVerifier() {
//                    public boolean verify(String hostname, SSLSession session) {
//                        //...
//                        return true;
//                    }
//                };
//                HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
//                HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());

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
