package com.privacity.cliente.rest;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.privacity.cliente.activity.MultipartFileResource;
import com.privacity.cliente.common.error.ErrorDialog;
import com.privacity.cliente.rest.restcalls.reconnect.ReconnectionSyncSession;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.singleton.Singletons;
import com.privacity.cliente.singleton.UtilsStringSingleton;
import com.privacity.cliente.singleton.activity.SingletonCurrentActivity;
import com.privacity.cliente.singleton.impl.SingletonServer;
import com.privacity.cliente.model.dto.Protocolo;
import com.privacity.common.dto.ProtocoloDTO;
import com.privacity.common.enumeration.ExceptionReturnCode;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class RestTemplateProtocoloFile extends AsyncTask<Void, Void, ResponseEntity<Protocolo>> {
    private static final String TAG = "RestTemplateProtocoloFile";
    private final CallbackRest callbackRest;
    private final Protocolo protocolo;
    private final Activity context;
    private byte[] data;

    public RestTemplateProtocoloFile(Activity context, Protocolo protocolo, byte[] data, CallbackRest callbackRest) {
        this.callbackRest = callbackRest;
        this.protocolo = protocolo;
        this.data = data;
        this.context = SingletonCurrentActivity.getInstance().get();
    }


    @Override
    protected ResponseEntity<Protocolo> doInBackground(Void... voids) {
        //System.gc();
            try {
                Log.d(TAG, "protocoloDTO: " + protocolo);
                if ( protocolo.getMessage().getMedia() != null &&  protocolo.getMessage().getMedia().getData() != null){
                    Log.d(TAG, "protocoloDTO.getMessage(): " + protocolo.getMessage());
                    protocolo.getMessage().getMedia().setData(null);
                }
                String toSend="";
                toSend = SingletonValues.getInstance().getSessionAEStoUse().getAES(UtilsStringSingleton.getInstance().gsonToSend(protocolo.convert()));
                Log.d(TAG, "toSend: " + toSend);

                if (protocolo.getMessage() != null) {
                    ProtocoloDTO p2 = protocolo.convert();
                    p2.getMessage().setMedia(null);
                    System.out.println(Singletons.utilsString().gsonPretty().toJson(p2));
                }

                if ( data != null){
                    data = SingletonValues.getInstance().getSessionAEStoUse().getAES(data).getBytes();
                }
                final boolean  logOn;

                logOn= true;


                RestTemplate template = getRestTemplate();


                String url = SingletonServer.getInstance().getAppServer() + "/entry/CONSTANT_URL_PATH_PRIVATE_SEND";


                final HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.MULTIPART_FORM_DATA);
                String authHeader = SingletonValues.getInstance().getToken();
                headers.set( org.apache.hc.core5.http.HttpHeaders.AUTHORIZATION, authHeader );



                MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<String, Object>();
                parameters.set("Content-Type","multipart/form-data");

                parameters.add("request",toSend );

                MultipartFileResource e=null;
                if ( data != null){

                    e = new MultipartFileResource(data, protocolo.getAsyncId());
                    parameters.add("data", e);
                }else{
                    parameters.add("data", "".getBytes());
                }
                final HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<MultiValueMap<String, Object>>(
                        parameters, headers);

                Log.d(TAG, "url: " + url);
                template.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                template.getMessageConverters().add(new StringHttpMessageConverter());

                final ResponseEntity<String> s = template.exchange(url,
                        HttpMethod.POST, httpEntity, String.class);
                Log.d(TAG, "s.getBody(): " + s.getBody());

/*                String uncompressB64 = UtilsStringSingleton.getInstance().uncompressB64(s.getBody());
                Log.d(TAG, "Body uncompressB64 :" + uncompressB64);


                String sBodyDescr = SingletonValues.getInstance().getSessionAEStoUseServerEncrypt().getAESDecrypt(uncompressB64);*/
                 Protocolo pReturn = Protocolo.convert(UtilsStringSingleton.getInstance().protocoloToSendDecrypt(
                         SingletonValues.getInstance().getSessionAEStoUseServerEncrypt(),
                         s.getBody()));

                return new ResponseEntity<Protocolo>(pReturn, HttpStatus.OK);






            } catch (HttpClientErrorException e) {
                e.printStackTrace();

                if ("403".equals(e.getMessage().trim())){

                    Protocolo p = new Protocolo();
                    p.setCodigoRespuesta(ExceptionReturnCode.AUTH_SESSION_OUTOFSYNC.getCode());

                    return new ResponseEntity<Protocolo>(p, HttpStatus.OK);


                }else if ("3333".equals(e.getMessage().trim())){
                    Protocolo p = new Protocolo();
                    p.setCodigoRespuesta("3333 - Server id message down");

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
                e.printStackTrace();
                protocolo.setCodigoRespuesta(e.getMessage());

                return new ResponseEntity<Protocolo>(protocolo, HttpStatus.OK);

            }

            //return new ResponseEntity<ProtocoloDTO>(HttpStatus.BAD_REQUEST);
        }

    @Override
    protected void onPostExecute(ResponseEntity<Protocolo> response) {
        if (response.getBody() == null) return;
        if (response.getBody().getCodigoRespuesta() == null){



            this.callbackRest.response(response);
        }else{

            if(response.getBody().getCodigoRespuesta().equals(ExceptionReturnCode.AUTH_SESSION_OUTOFSYNC.getCode())){

                ErrorDialog.errorDialog(SingletonCurrentActivity.getInstance().get(),response, new CallbackRest(){
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
            ErrorDialog.errorDialog(SingletonCurrentActivity.getInstance().get(), response,this.callbackRest );
        }

    }


    public  RestTemplate getRestTemplate() {
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectTimeout(5000);
        httpRequestFactory.setReadTimeout(5000);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(httpRequestFactory);
        //restTemplate.setRequestFactory(httpRequestFactory()); // apache http library
        restTemplate.setMessageConverters(getMessageConverters());
        return restTemplate;
    }


    private  List<HttpMessageConverter<?>> getMessageConverters() {
        final List<HttpMessageConverter<?>> converters = new ArrayList<HttpMessageConverter<?>>();
        final FormHttpMessageConverter e = new FormHttpMessageConverter();

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Arrays.asList(MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON));

        //MappingJackson2HttpMessageConverter jsonHttpMessageConverter = new MappingJackson2HttpMessageConverter();
        //converter.getObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        //e.addPartConverter(jsonHttpMessageConverter);
        converters.add(e);
        converters.add(converter);
        return converters;
    }


    public CommonsMultipartResolver multipartResolver() {
        final CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver();
        return commonsMultipartResolver;
    }
}
