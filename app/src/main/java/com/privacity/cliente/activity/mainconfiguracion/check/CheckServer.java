package com.privacity.cliente.activity.mainconfiguracion.check;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.privacity.cliente.frame.error.ErrorPojo;
import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.singleton.UtilsStringSingleton;
import com.privacity.cliente.singleton.impl.SingletonServer;
import com.privacity.common.config.SystemGralConfURLs;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;


public class CheckServer extends AsyncTask<Void, Void, ResponseEntity<ErrorPojo>> {
    private static final String TAG = "RestTemplateProtocolo";
    public static final String CONSTANT__REQUEST_PARAMETER__ACTION = "action";
    public static final String CONSTANT__REQUEST_PARAMETER__COMPONENT = "component";
    public static final String CONSTANT__REQUEST_PARAMETER__OBJECT_DTO = "objectDTO";
    public static final String CONSTANT__REQUEST_PARAMETER__ASYNC_ID = "asyncId";
    public static final String CONSTANT__REQUEST_PARAMETER__REQUEST = "request";
    private CallbackCheckConf callbackRest;

    private Activity context;
    private boolean secure=true;
    private String url;
    public CheckServer(Activity context, CallbackCheckConf callbackRest,String url) {
        this.callbackRest = callbackRest;
    this.url=url;
        this.context = context;
    }


    @Override
    protected ResponseEntity<ErrorPojo> doInBackground(Void... voids) {

    
        ErrorPojo pojo = new ErrorPojo();
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectTimeout(5000);
        httpRequestFactory.setReadTimeout(5000);
        RestTemplate rt = new RestTemplate();
rt.setRequestFactory(httpRequestFactory);
        ResponseEntity<String> a = null;
        rt.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        try {


            HashMap<String, Object> map = new HashMap<String, Object>();

   





            HttpEntity<Map<String, Object>> entity;
  {
                entity = new HttpEntity<Map<String, Object>>(map, new HttpHeaders());

                 Log.i("<< url << ", url);

      pojo.setUrl(url);
                a = rt.postForEntity(url, entity, String.class);
                if(a.getBody() != null){
                    callbackRest.response(null);
                    Log.i("<< //protocoloDTO << ", UtilsStringSingleton.getInstance().gsonToSend(a.getBody()));
                }else{
                    Log.i("<< //protocoloDTO << ", "null");
                }


            }


            return null;

        } catch (HttpClientErrorException e) {
            pojo.setStackTrace(e);
            pojo.setErrorDescription(e.getMessage());

            callbackRest.onError(pojo);
            e.printStackTrace();
            //protocoloDTO.setCodigoRespuesta(e.getMessage());
            return null;


        } catch (HttpMessageConversionException e) {

            pojo.setStackTrace(e);
            pojo.setErrorDescription(e.getMessage());
            callbackRest.onError(pojo);
            e.printStackTrace();

            //protocoloDTO.setCodigoRespuesta(e.getMessage());
            return null;

        } catch (RestClientException e) {

            pojo.setStackTrace(e);
            pojo.setErrorDescription(e.getMessage());
            callbackRest.onError(pojo);
            e.printStackTrace();
            //protocoloDTO.setCodigoRespuesta(e.getMessage());
            return null;

        } catch (Exception e) {

            String stacktrace = ExceptionUtils.getStackTrace(e);
            pojo.setStackTrace(e);
            pojo.setErrorDescription(e.getMessage());


            callbackRest.onError(pojo);

            e.printStackTrace();
            //protocoloDTO.setCodigoRespuesta(e.getMessage());
            //protocoloDTO.setObjectDTO(UtilsStringSingleton.getInstance().gsonToSend(pojo));

            return null;

        }

        //return new ResponseEntity<//protocoloDTO>(HttpStatus.BAD_REQUEST);
    }





}
