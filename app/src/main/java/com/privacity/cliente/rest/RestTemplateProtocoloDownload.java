package com.privacity.cliente.rest;

import android.app.Activity;
import android.os.AsyncTask;

import com.privacity.cliente.rest.restcalls.CallbackRestDownload;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RestTemplateProtocoloDownload extends AsyncTask<Void, Void, ResponseEntity<byte[]>> {

    private CallbackRestDownload callbackRest;
    private ProtocoloDTO protocoloDTO;
    private Activity context;

    public RestTemplateProtocoloDownload(Activity context, ProtocoloDTO protocoloDTO, CallbackRestDownload callbackRest) {
        this.callbackRest = callbackRest;
        this.protocoloDTO = protocoloDTO;
        this.context = context;
    }


    @Override
    protected ResponseEntity<byte[]> doInBackground(Void... voids) {

            try {
                RestTemplate restTemplate = new RestTemplate();






                String url = SingletonServer.getInstance().getAppServer() + SystemGralConfURLs.CONSTANT_URL_PATH_PRIVATE_DOWNLOAD_DATA;

                HttpHeaders headers = new HttpHeaders();
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));
                String authHeader = SingletonValues.getInstance().getToken();
                headers.set( "Authorization", authHeader );

                String toSend="";
                toSend = SingletonValues.getInstance().getSessionAEStoUse().getAES(GsonFormated.get().toJson(protocoloDTO));

                HashMap<String, Object> parameters = new HashMap<String, Object>();
                parameters.put("request",toSend );

                HttpEntity<Map<String, Object>> entity;


                    entity = new HttpEntity<Map<String, Object>>(parameters, new HttpHeaders() {{
                        String authHeader = SingletonValues.getInstance().getToken();
                        set( "Authorization", authHeader );
                    }});
                ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.POST, entity, byte[].class);
                //System.out.println ( new String ( response.getBody(), Charset.defaultCharset()));






                return response;

            } catch (HttpClientErrorException e) {
                e.printStackTrace();

                if ("403".equals(e.getMessage().trim())){

                    ProtocoloDTO p = new ProtocoloDTO();
                    p.setCodigoRespuesta(ExceptionReturnCode.AUTH_SESSION_OUTOFSYNC.getCode());

                    return new ResponseEntity<byte[]>(p.toString().getBytes(), HttpStatus.OK);


                }
                protocoloDTO.setCodigoRespuesta(e.getMessage());
                return new ResponseEntity<byte[]>(protocoloDTO.toString().getBytes(), HttpStatus.OK);



            } catch (HttpMessageConversionException e) {

                e.printStackTrace();

                protocoloDTO.setCodigoRespuesta(e.getMessage());
                return new ResponseEntity<byte[]>(protocoloDTO.toString().getBytes(), HttpStatus.OK);

            } catch (RestClientException e) {
                e.printStackTrace();
                protocoloDTO.setCodigoRespuesta(e.getMessage());
                return new ResponseEntity<byte[]>(protocoloDTO.toString().getBytes(), HttpStatus.OK);

            } catch (Exception e) {
                e.printStackTrace();
                protocoloDTO.setCodigoRespuesta(e.getMessage());

                return new ResponseEntity<byte[]>(protocoloDTO.toString().getBytes(), HttpStatus.OK);

            }

            //return new ResponseEntity<ProtocoloDTO>(HttpStatus.BAD_REQUEST);


        }

    @Override
    protected void onPostExecute(ResponseEntity<byte[]> response) {




            this.callbackRest.response(response);


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
