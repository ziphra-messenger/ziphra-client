package com.privacity.cliente.rest;

import android.app.Activity;
import android.os.AsyncTask;

import com.privacity.cliente.activity.MultipartFileResource;
import com.privacity.cliente.common.error.ErrorDialog;
import com.privacity.cliente.rest.restcalls.reconnect.ReconnectionSyncSession;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.singleton.impl.SingletonServer;
import com.privacity.cliente.util.GsonFormated;
import com.privacity.common.config.SystemGralConfURLs;
import com.privacity.common.dto.ProtocoloDTO;
import com.privacity.common.enumeration.ExceptionReturnCode;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageConverter;
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


public class RestTemplateProtocoloFile extends AsyncTask<Void, Void, ResponseEntity<ProtocoloDTO>> {

    private CallbackRest callbackRest;
    private ProtocoloDTO protocoloDTO;
    private Activity context;
    private byte[] data;

    public RestTemplateProtocoloFile(Activity context, ProtocoloDTO protocoloDTO, byte[] data, CallbackRest callbackRest) {
        this.callbackRest = callbackRest;
        this.protocoloDTO = protocoloDTO;
        this.data = data;
        this.context = context;
    }


    @Override
    protected ResponseEntity<ProtocoloDTO> doInBackground(Void... voids) {
        //System.gc();
            try {

                if ( protocoloDTO.getMessageDTO().getMediaDTO() != null &&  protocoloDTO.getMessageDTO().getMediaDTO().getData() != null){
                    protocoloDTO.getMessageDTO().getMediaDTO().setData(null);
                }
                String toSend="";
                toSend = SingletonValues.getInstance().getSessionAEStoUse().getAES(GsonFormated.get().toJson(protocoloDTO));

                if ( data != null){
                    data = SingletonValues.getInstance().getSessionAEStoUse().getAES(data);
                }
                final boolean  logOn;

                logOn= true;


                RestTemplate template = getRestTemplate();


                String url = SingletonServer.getInstance().getAppServer() + SystemGralConfURLs.CONSTANT_URL_PATH_PRIVATE_SEND;


                final HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.MULTIPART_FORM_DATA);
                String authHeader = SingletonValues.getInstance().getToken();
                headers.set( "Authorization", authHeader );



                MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<String, Object>();
                parameters.set("Content-Type","multipart/form-data");

                parameters.add("request",toSend );

                MultipartFileResource e=null;
                if ( data != null){

                    e = new MultipartFileResource(data, protocoloDTO.getAsyncId());
                    parameters.add("data", e);
                }else{
                    parameters.add("data", "".getBytes());
                }
                final HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<MultiValueMap<String, Object>>(
                        parameters, headers);

                final ResponseEntity<String> s = template.exchange(url,
                        HttpMethod.POST, httpEntity, String.class);

                String sBodyDescr = SingletonValues.getInstance().getSessionAEStoUseServerEncrypt().getAESDecrypt(s.getBody());
                 ProtocoloDTO pReturn = GsonFormated.get().fromJson(sBodyDescr, ProtocoloDTO.class);

                return new ResponseEntity<ProtocoloDTO>(pReturn, HttpStatus.OK);






            } catch (HttpClientErrorException e) {
                e.printStackTrace();

                if ("403".equals(e.getMessage().trim())){

                    ProtocoloDTO p = new ProtocoloDTO();
                    p.setCodigoRespuesta(ExceptionReturnCode.AUTH_SESSION_OUTOFSYNC.getCode());

                    return new ResponseEntity<ProtocoloDTO>(p, HttpStatus.OK);


                }
                protocoloDTO.setCodigoRespuesta(e.getMessage());
                return new ResponseEntity<ProtocoloDTO>(protocoloDTO, HttpStatus.OK);



            } catch (HttpMessageConversionException e) {

                e.printStackTrace();

                protocoloDTO.setCodigoRespuesta(e.getMessage());
                return new ResponseEntity<ProtocoloDTO>(protocoloDTO, HttpStatus.OK);

            } catch (RestClientException e) {
                e.printStackTrace();
                protocoloDTO.setCodigoRespuesta(e.getMessage());
                return new ResponseEntity<ProtocoloDTO>(protocoloDTO, HttpStatus.OK);

            } catch (Exception e) {
                e.printStackTrace();
                protocoloDTO.setCodigoRespuesta(e.getMessage());

                return new ResponseEntity<ProtocoloDTO>(protocoloDTO, HttpStatus.OK);

            }

            //return new ResponseEntity<ProtocoloDTO>(HttpStatus.BAD_REQUEST);
        }

    @Override
    protected void onPostExecute(ResponseEntity<ProtocoloDTO> response) {
        if (response.getBody() == null) return;
        if (response.getBody().getCodigoRespuesta() == null){



            this.callbackRest.response(response);
        }else{

            if(response.getBody().getCodigoRespuesta().equals(ExceptionReturnCode.AUTH_SESSION_OUTOFSYNC.getCode())){

                ErrorDialog.errorDialog(context,response, new CallbackRest(){
                    @Override
                    public void onError(ResponseEntity<ProtocoloDTO> response) {
                        try {
                            ReconnectionSyncSession.sync(context, null);
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            return;
                        }
                    }

                    @Override
                    public void response(ResponseEntity<ProtocoloDTO> response) {}
                    @Override
                    public void beforeShowErrorMessage(String msg) {}
                });

                return;
            }
            ErrorDialog.errorDialog(context,response,this.callbackRest );
        }

    }


    public  RestTemplate getRestTemplate() {
        final RestTemplate restTemplate = new RestTemplate();
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
