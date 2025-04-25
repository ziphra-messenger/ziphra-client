package com.privacity.cliente.rest;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.privacity.cliente.common.error.ErrorDialog;
import com.privacity.cliente.frame.error.ErrorPojo;
import com.privacity.cliente.model.dto.Protocolo;
import com.privacity.cliente.rest.restcalls.reconnect.ReconnectionSyncSession;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.singleton.UtilsStringSingleton;
import com.privacity.cliente.singleton.activity.SingletonCurrentActivity;
import com.privacity.cliente.singleton.impl.SingletonServer;
import com.privacity.cliente.singleton.usuario.SingletonSessionClosing;
import com.privacity.common.config.SystemGralConfURLs;


import com.privacity.common.enumeration.ExceptionReturnCode;
import com.privacity.common.enumeration.ProtocoloActionsEnum;

import org.apache.commons.lang3.exception.ExceptionUtils;
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

public class RestTemplateProtocolo extends AsyncTask<Void, Void, ResponseEntity<Protocolo>> {
    private static final String TAG = "RestTemplateProtocolo";
    public static final String CONSTANT__REQUEST_PARAMETER__ACTION = "action";
    public static final String CONSTANT__REQUEST_PARAMETER__COMPONENT = "component";
    public static final String CONSTANT__REQUEST_PARAMETER__OBJECT_DTO = "objectDTO";
    public static final String CONSTANT__REQUEST_PARAMETER__ASYNC_ID = "asyncId";
    public static final String CONSTANT__REQUEST_PARAMETER__REQUEST = "request";
    private CallbackRest callbackRest;
    private Protocolo protocolo;
    private Activity context;
    private boolean secure=true;
    public RestTemplateProtocolo(Protocolo Protocolo) {
        this.protocolo = protocolo;
    }

    public RestTemplateProtocolo(Activity context, Protocolo protocolo, CallbackRest callbackRest) {
        this.callbackRest = callbackRest;
        this.protocolo = protocolo;
        this.context = context;
    }
    public RestTemplateProtocolo(Activity context, Protocolo protocolo, CallbackRest callbackRest, boolean secure) {
        this.callbackRest = callbackRest;
        this.protocolo = protocolo;
        this.secure = secure;
        this.context = context;
    }

    @Override
    protected ResponseEntity<Protocolo> doInBackground(Void... voids) {
        String url="";
        final boolean  logOn;
        if ( protocolo.getAction().equals(ProtocoloActionsEnum.REQUEST_ID_PUBLIC_GET) ||
                protocolo.getAction().equals(ProtocoloActionsEnum.REQUEST_ID_PRIVATE_GET)
        ){
            logOn= true;
        }else{
            // ACA VA TRUE
            logOn= true;
        }

        if (logOn) Log.i("------------------- Action", protocolo.getAction().toString());


        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectTimeout(5000);
        httpRequestFactory.setReadTimeout(5000);
        RestTemplate rt = new RestTemplate();
        rt.setRequestFactory(httpRequestFactory);

        ResponseEntity<Protocolo> a = null;
        rt.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        try {


            HashMap<String, Object> map = new HashMap<String, Object>();

            {
                String prettyJsonString = UtilsStringSingleton.getInstance().gsonToSend(protocolo);
                if (logOn) Log.i(">> ProtocoloDTO >> ", prettyJsonString);
            }

            String toSend="";
            if (SingletonSessionClosing.getInstance().isClosing() && !protocolo.getAction().equals(ProtocoloActionsEnum.MY_ACCOUNT_CLOSE_SESSION))return null;


            if (secure){
                /*
                if (
                        protocoloDTO.getAction().equals("/message/send") &&
                        protocoloDTO.getMessage().getMedia() != null){
                    protocoloDTO.getMessage().getMedia().setData(
                    ZipUtil.compress(protocoloDTO.getMessage().getMedia().getData())
                    );
                }*/

                System.out.println(SingletonValues.getInstance().toString());
                System.out.println(SingletonValues.getInstance().getSessionAEStoUse());
//                System.out.println(SingletonValues.getInstance().getSessionAEStoUse().toString());
               // System.out.println(protocoloDTO);

                toSend = UtilsStringSingleton.getInstance().protocoloToSendEncrypt(SingletonValues.getInstance().getSessionAEStoUse(), protocolo.convert());


                map.put(CONSTANT__REQUEST_PARAMETER__REQUEST, toSend);

                //if (logOn)  Log.i(">> Rest", "Authorization: " + SingletonValues.getInstance().getToken());
            }else{
                map.put(CONSTANT__REQUEST_PARAMETER__COMPONENT, protocolo.getComponent());
                map.put(CONSTANT__REQUEST_PARAMETER__ACTION, protocolo.getAction());
                if ( protocolo.getObjectDTO() != null) map.put(CONSTANT__REQUEST_PARAMETER__OBJECT_DTO, protocolo.getObjectDTO());
                map.put(CONSTANT__REQUEST_PARAMETER__ASYNC_ID, protocolo.getAsyncId());

            }


            HttpEntity<Map<String, Object>> entity;
            if (secure){

                entity = new HttpEntity<Map<String, Object>>(map, new HttpHeaders() {{
                    String authHeader = SingletonValues.getInstance().getToken();
                    set( org.apache.hc.core5.http.HttpHeaders.AUTHORIZATION, authHeader );
                }});

                //if (logOn) Log.i(">> ProtocoloDTO ENCRIPTADO >>", toSend);

                if (protocolo.getMessage() != null){
                    url = SingletonServer.getInstance().getAppServer() + SystemGralConfURLs.CONSTANT_URL_PATH_PRIVATE_SEND;
                }else{
                    url = SingletonServer.getInstance().getAppServer() + SystemGralConfURLs.CONSTANT_URL_PATH_PRIVATE;
                }

                //Log.d(TAG, "URL destino :" + url);
                //Log.d(TAG, "secure: :" + secure);
                //Log.d(TAG, "toSend: :" + toSend);

                ResponseEntity<String> s = null;
                s = rt.postForEntity(url, entity, String.class);

                Log.d(TAG, "entrada :" + s.getBody());
               // String uncompressB64 = UtilsStringSingleton.getInstance().uncompressB64(s.getBody());
                //Log.d(TAG, "uncompressB64 :" + uncompressB64);


                //if (logOn) Log.i(">> ProtocoloDTO ENCRIPTADO ", s.getBody().toString());
                //String sBodyDescr = SingletonValues.getInstance().getSessionAEStoUseServerEncrypt().getAESDecrypt(uncompressB64);
                //Log.i(TAG, "ProtocoloDTO  sBodyDescr << " + sBodyDescr);
                Protocolo pReturn = Protocolo.convert(UtilsStringSingleton.getInstance().protocoloToSendDecrypt(
                        SingletonValues.getInstance().getSessionAEStoUseServerEncrypt(),
                        s.getBody()));

                {
                    String prettyJsonString = UtilsStringSingleton.getInstance().gsonPretty().toJson(pReturn.getObjectDTO());
                    Log.i(TAG,"<< ObjectDTO << " + prettyJsonString);
                }
                a = new ResponseEntity<Protocolo>(pReturn, HttpStatus.OK);

            }else{
                entity = new HttpEntity<Map<String, Object>>(map, new HttpHeaders());

                url=SingletonServer.getInstance().getAppServer() + SystemGralConfURLs.CONSTANT_URL_PATH_FREE;
                Log.i("<< url << ", url);
                //url="http://www.stackoverflow.com";
                a = rt.postForEntity(url, entity, Protocolo.class);
                if(a.getBody() != null){
                    Log.i("<< ProtocoloDTO << ", UtilsStringSingleton.getInstance().gsonToSend(a.getBody()));
                }else{
                    Log.i("<< ProtocoloDTO << ", "null");
                }
                if (!HttpStatus.OK.equals(a.getStatusCode())){
                    protocolo.setCodigoRespuesta("http_" + a.getStatusCode().toString());
                    //protocoloDTO.setMensajeRespuesta(url + " "  + a.getStatusCode().getReasonPhrase());
                    ErrorPojo pojo = new ErrorPojo();
                    pojo.setHttpStatus(a.getStatusCode())
                            .setUrl(url);
                    protocolo.setObjectDTO(UtilsStringSingleton.getInstance().gsonToSend(pojo));
                    return new ResponseEntity<Protocolo>(protocolo, HttpStatus.OK);
                }

            }


            return a;

        } catch (HttpClientErrorException e) {
            e.printStackTrace();

            if ((org.apache.hc.core5.http.HttpStatus.SC_FORBIDDEN+"").equals(e.getMessage().trim())){

                Protocolo p = new Protocolo();
                p.setCodigoRespuesta(ExceptionReturnCode.AUTH_SESSION_OUTOFSYNC.getCode());

                return new ResponseEntity<Protocolo>(p, HttpStatus.OK);


            }
            protocolo.setCodigoRespuesta(e.getMessage());
            return new ResponseEntity<Protocolo>(protocolo, HttpStatus.OK);


        } catch (HttpMessageConversionException e) {

            e.printStackTrace();

            protocolo.setCodigoRespuesta(e.getMessage());
            return new ResponseEntity<Protocolo>(protocolo, HttpStatus.OK);

        } catch (RestClientException e) {
            e.printStackTrace();
            protocolo.setCodigoRespuesta(e.getMessage());
            return new ResponseEntity<Protocolo>(protocolo, HttpStatus.OK);

        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            ErrorPojo pojo = new ErrorPojo();
            pojo.setStackTrace(e);
            pojo.setErrorDescription(e.getMessage());



            if (protocolo.getAction().equalsName(ProtocoloActionsEnum.MY_ACCOUNT_CLOSE_SESSION.toString())){
                protocolo.setCodigoRespuesta(null);
                return new ResponseEntity<Protocolo>(protocolo, HttpStatus.OK);
            }
            e.printStackTrace();
            protocolo.setCodigoRespuesta(e.getMessage());
            protocolo.setObjectDTO(UtilsStringSingleton.getInstance().gsonToSend(pojo));

            return new ResponseEntity<Protocolo>(protocolo, HttpStatus.OK);

        }

        //return new ResponseEntity<ProtocoloDTO>(HttpStatus.BAD_REQUEST);
    }

    public void onPostExecutePublic(ResponseEntity<Protocolo> response) {
        onPostExecute(response);
    }
    @Override
    protected void onPostExecute(ResponseEntity<Protocolo> response) {
        if (SingletonSessionClosing.getInstance().isClosing() && !protocolo.getAction().equals(ProtocoloActionsEnum.MY_ACCOUNT_CLOSE_SESSION))return;

        if (response == null) return;
        if (response.getBody() == null) return;
        if (response.getBody().getCodigoRespuesta() == null){


            try {
                //Log.d(TAG, "Body entrada :" + response.getBody().getObjectDTO());
   //             String uncompressB64 = UtilsString.uncompressB64(response.getBody().getObjectDTO());
//                Log.d(TAG, "Body uncompressB64 :" + uncompressB64);

                //response.getBody().setObjectDTO(response.getBody() );
                this.callbackRest.response(response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{

            if(response.getBody().getCodigoRespuesta().equals(ExceptionReturnCode.AUTH_SESSION_OUTOFSYNC.getCode())){

                ErrorDialog.errorDialog(context,response, new CallbackRest(){
                    @Override
                    public void onError(ResponseEntity<Protocolo> response) {
                        try {
                            ReconnectionSyncSession.sync(context, null);
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            return;
                        }
                    }

                    @Override
                    public void response(ResponseEntity<Protocolo> response) {}
                    @Override
                    public void beforeShowErrorMessage(String msg) {}
                });

                return;
            }
            if (SingletonSessionClosing.getInstance().isClosing())return;
            try {
                ErrorDialog.errorDialog(SingletonCurrentActivity.getInstance().get(), response,this.callbackRest );
            } catch (Exception e) {

            }
        }

    }
}
