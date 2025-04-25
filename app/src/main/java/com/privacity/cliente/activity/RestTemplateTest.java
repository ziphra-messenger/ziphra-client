package com.privacity.cliente.activity;

import android.app.Activity;
import android.os.AsyncTask;

import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.model.dto.Protocolo;
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
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

public class RestTemplateTest extends AsyncTask<Void, Void, ResponseEntity<Object>> {

    private CallbackRest callbackRest;
    private Protocolo protocolo;
    private Activity context;
    private final boolean secure=true;
    public RestTemplateTest(Protocolo Protocolo) {
        this.protocolo = protocolo;
    }

    public RestTemplateTest(Activity context) {

        this.context = context;
    }

    @Override
    protected ResponseEntity<Object> doInBackground(Void... voids) {



       try {



            final boolean  logOn;

            logOn= true;


            RestTemplate template = getRestTemplate();


            String url = "http://192.168.0.176:8080" + "/free/arch/upload";


            final HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);


            MultipartFileResource e = new MultipartFileResource("ssssssssssssssss","filename");


            MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<String, Object>();
            parameters.set("Content-Type","multipart/form-data");
            parameters.add("file", e);
            parameters.add("p2", "p2");
            parameters.add("p1", "1");

            final HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<MultiValueMap<String, Object>>(
                    parameters, headers);
            final ResponseEntity<String> a = template.exchange(url,
                    HttpMethod.POST, httpEntity, String.class);


            System.out.println (a.getBody());






    } catch (HttpClientErrorException e) {
            e.printStackTrace();

            if ("403".equals(e.getMessage().trim())){

                Protocolo p = new Protocolo();
                p.setCodigoRespuesta(ExceptionReturnCode.AUTH_SESSION_OUTOFSYNC.getCode());

                return new ResponseEntity<Object>(p, HttpStatus.OK);


            }
            protocolo.setCodigoRespuesta(e.getMessage());
            return new ResponseEntity<Object>(protocolo, HttpStatus.OK);


        } catch (HttpMessageConversionException e) {

            e.printStackTrace();

            protocolo.setCodigoRespuesta(e.getMessage());
            return new ResponseEntity<Object>(protocolo, HttpStatus.OK);

        } catch (RestClientException e) {
            e.printStackTrace();
            protocolo.setCodigoRespuesta(e.getMessage());
            return new ResponseEntity<Object>(protocolo, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            protocolo.setCodigoRespuesta(e.getMessage());

            return new ResponseEntity<Object>(protocolo, HttpStatus.OK);

        }

        return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
    }




    @Data
    public class FileUploadRequest {
        private String fileName="jorge";
        private InputStream fileStream = new ByteArrayInputStream("jorge".getBytes());
        private boolean enabled = true;

    }

/*    public ClientHttpRequestFactory httpRequestFactory() {

        HttpClient client = HttpClients.custom()
                .setSSLHostnameVerifier(new AllowAllHostnameVerifier())
                .build();

        final HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(client);
        //requestFactory.setBufferRequestBody(false);
        return requestFactory;
    }*/


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


        MappingJackson2HttpMessageConverter jsonHttpMessageConverter = new MappingJackson2HttpMessageConverter();
        //jsonHttpMessageConverter.getObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        e.addPartConverter(jsonHttpMessageConverter);
        converters.add(e);
        return converters;
    }


    public CommonsMultipartResolver multipartResolver() {
        final CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver();
        return commonsMultipartResolver;
    }

}
