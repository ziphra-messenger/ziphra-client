package com.privacity.cliente.rest;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.privacity.cliente.R;
import com.privacity.cliente.common.error.ErrorDialog;
import com.privacity.cliente.encrypt.AEStoUse;
import com.privacity.cliente.frame.error.ErrorPojo;
import com.privacity.cliente.frame.error.ErrorView;
import com.privacity.cliente.singleton.UtilsStringSingleton;
import com.privacity.cliente.singleton.impl.SingletonServer;
import com.privacity.common.config.SystemGralConfURLs;
import com.privacity.cliente.model.dto.Protocolo;
import com.privacity.common.dto.ProtocoloWrapperDTO;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public class RestTemplateProtocoloWrapper extends AsyncTask<Void, Void, ResponseEntity<Protocolo>> {

    private static final String TAG = "RestTemplateProtocoloWrapper";

    private final CallbackRest callbackRest;
    private final ProtocoloWrapperDTO protocoloWrapperDTO;
    private final Activity context;
    private boolean secure=false;
    private final AEStoUse aeStoUse;


    public RestTemplateProtocoloWrapper(AEStoUse aeStoUse, Activity context, ProtocoloWrapperDTO protocoloWrapperDTO, CallbackRest callbackRest) {
        this.callbackRest = callbackRest;
        this.protocoloWrapperDTO = protocoloWrapperDTO;
        this.context = context;
        this.aeStoUse = aeStoUse;
    }
    public RestTemplateProtocoloWrapper(AEStoUse aeStoUse, Activity context, ProtocoloWrapperDTO protocoloWrapperDTO, CallbackRest callbackRest, boolean secure) {
        this.callbackRest = callbackRest;
        this.protocoloWrapperDTO = protocoloWrapperDTO;
        this.secure = secure;
        this.context = context;
        this.aeStoUse = aeStoUse;
    }

    private static void showErrorSinInternet(Activity activity) {
        ErrorPojo pojo = new ErrorPojo();
        pojo.setUrl(SingletonServer.getInstance().getAppServer());
        pojo.setRecomendacion(activity.getString(R.string.main_login__alert__sin_internet__detail));
        pojo.setErrorDescription(activity.getString(R.string.main_login__alert__sin_internet__title));

        new ErrorView(activity).show(pojo);
    }
    @Override
    protected ResponseEntity<Protocolo> doInBackground(Void... voids) {



        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectTimeout(5000);
        httpRequestFactory.setReadTimeout(5000);
        RestTemplate rt = new RestTemplate();
        rt.setRequestFactory(httpRequestFactory);

        ResponseEntity<String> a = null;
        rt.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        try {

            HashMap<String, Object> map = new HashMap<String, Object>();

            {
                String prettyJsonString = UtilsStringSingleton.getInstance().gsonToSend(protocoloWrapperDTO);
                Log.i("ProtocoloWrapperDTO  prettyJsonString <<", prettyJsonString);
            }

            String toSend="";


           map.put("aesEncripted", protocoloWrapperDTO.getAesEncripted());
            map.put("protocoloDTO", protocoloWrapperDTO.getProtocoloDTO());





            HttpEntity<Map<String, Object>> entity;
                  entity = new HttpEntity<Map<String, Object>>(map, new HttpHeaders());
                a = rt.postForEntity(SingletonServer.getInstance().getAppServer() + SystemGralConfURLs.CONSTANT_URL_PATH_PUBLIC, entity, String.class);

            Log.d(TAG, "Body entrada :" + a.getBody());
            String uncompressB64 = UtilsStringSingleton.getInstance().uncompressB64(a.getBody());
            Log.d(TAG, "Body uncompressB64 :" + uncompressB64);



                String protocoloJson =  aeStoUse.getAESDecrypt(uncompressB64);

            Log.d(TAG, "protocoloJson :" + protocoloJson);
            return new ResponseEntity<Protocolo>(UtilsStringSingleton.getInstance().gson().fromJson(protocoloJson, Protocolo.class), HttpStatus.OK);

            //Log.i("ProtocoloDTO <<", a.getBody().toString());
//            return a;

        } catch (HttpClientErrorException e) {
            e.printStackTrace();

            Protocolo p = new Protocolo();
            p.setCodigoRespuesta(e.getMessage());

            return new ResponseEntity<Protocolo>(p, HttpStatus.OK);


        } catch (HttpMessageConversionException e) {

            e.printStackTrace();

            Protocolo p = new Protocolo();
            p.setCodigoRespuesta(e.getMessage());
            return new ResponseEntity<Protocolo>(p, HttpStatus.OK);

        } catch (RestClientException e) {
            e.printStackTrace();
            Protocolo p = new Protocolo();
            p.setCodigoRespuesta(e.getMessage());
            return new ResponseEntity<Protocolo>(p, HttpStatus.OK);


        } catch (Exception e) {
            e.printStackTrace();
            Protocolo p = new Protocolo();
            p.setCodigoRespuesta(e.getMessage());
            return new ResponseEntity<Protocolo>(p, HttpStatus.OK);
        }

        //return new ResponseEntity<ProtocoloWrapperDTO>(HttpStatus.BAD_REQUEST);
    }

    @Override
    protected void onPostExecute(ResponseEntity<Protocolo> response) {
        if (response.getBody() == null) return;
        if (response.getBody().getCodigoRespuesta() == null){
            this.callbackRest.response(response);
        }else{
            ErrorDialog.errorDialog(context,response,this.callbackRest );
        }

    }
}
