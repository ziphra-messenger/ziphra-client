package com.privacity.cliente.rest;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.privacity.cliente.singleton.impl.SingletonServer;
import com.privacity.cliente.common.error.ErrorDialog;
import com.privacity.cliente.encrypt.AEStoUse;
import com.privacity.cliente.util.GsonFormated;
import com.privacity.common.config.SystemGralConfURLs;
import com.privacity.common.dto.ProtocoloDTO;
import com.privacity.common.dto.ProtocoloWrapperDTO;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public class RestTemplateProtocoloWrapper extends AsyncTask<Void, Void, ResponseEntity<ProtocoloDTO>> {

    private CallbackRest callbackRest;
    private ProtocoloWrapperDTO protocoloWrapperDTO;
    private Context context;
    private boolean secure=false;
    private AEStoUse aeStoUse;


    public RestTemplateProtocoloWrapper(AEStoUse aeStoUse, Context context, ProtocoloWrapperDTO protocoloWrapperDTO, CallbackRest callbackRest) {
        this.callbackRest = callbackRest;
        this.protocoloWrapperDTO = protocoloWrapperDTO;
        this.context = context;
        this.aeStoUse = aeStoUse;
    }
    public RestTemplateProtocoloWrapper(AEStoUse aeStoUse, Context context, ProtocoloWrapperDTO protocoloWrapperDTO, CallbackRest callbackRest, boolean secure) {
        this.callbackRest = callbackRest;
        this.protocoloWrapperDTO = protocoloWrapperDTO;
        this.secure = secure;
        this.context = context;
        this.aeStoUse = aeStoUse;
    }


    @Override
    protected ResponseEntity<ProtocoloDTO> doInBackground(Void... voids) {



        RestTemplate rt = new RestTemplate();

        ResponseEntity<String> a = null;
        rt.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        try {

            Gson gson = GsonFormated.get();

            HashMap<String, Object> map = new HashMap<String, Object>();

            {
                String prettyJsonString = gson.toJson(protocoloWrapperDTO);
                Log.i("ProtocoloWrapperDTO  prettyJsonString <<", prettyJsonString);
            }

            String toSend="";


           map.put("aesEncripted", protocoloWrapperDTO.getAesEncripted());
            map.put("protocoloDTO", protocoloWrapperDTO.getProtocoloDTO());





            HttpEntity<Map<String, Object>> entity;
                  entity = new HttpEntity<Map<String, Object>>(map, new HttpHeaders());
                a = rt.postForEntity(SingletonServer.getInstance().getAppServer() + SystemGralConfURLs.CONSTANT_URL_PATH_PUBLIC, entity, String.class);

                String protocoloJson = aeStoUse.getAESDecrypt(a.getBody());


            return new ResponseEntity<ProtocoloDTO>(gson.fromJson(protocoloJson,ProtocoloDTO.class), HttpStatus.OK);

            //Log.i("ProtocoloDTO <<", a.getBody().toString());
//            return a;

        } catch (HttpClientErrorException e) {
            e.printStackTrace();

            ProtocoloDTO p = new ProtocoloDTO();
            p.setCodigoRespuesta(e.getMessage());

            return new ResponseEntity<ProtocoloDTO>(p, HttpStatus.OK);


        } catch (HttpMessageConversionException e) {

            e.printStackTrace();

            ProtocoloDTO p = new ProtocoloDTO();
            p.setCodigoRespuesta(e.getMessage());
            return new ResponseEntity<ProtocoloDTO>(p, HttpStatus.OK);

        } catch (RestClientException e) {
            e.printStackTrace();
            ProtocoloDTO p = new ProtocoloDTO();
            p.setCodigoRespuesta(e.getMessage());
            return new ResponseEntity<ProtocoloDTO>(p, HttpStatus.OK);


        } catch (Exception e) {
            e.printStackTrace();
            ProtocoloDTO p = new ProtocoloDTO();
            p.setCodigoRespuesta(e.getMessage());
            return new ResponseEntity<ProtocoloDTO>(p, HttpStatus.OK);
        }

        //return new ResponseEntity<ProtocoloWrapperDTO>(HttpStatus.BAD_REQUEST);
    }

    @Override
    protected void onPostExecute(ResponseEntity<ProtocoloDTO> response) {
        if (response.getBody() == null) return;
        if (response.getBody().getCodigoRespuesta() == null){
            this.callbackRest.response(response);
        }else{
            ErrorDialog.errorDialog(context,response,this.callbackRest );
        }

    }
}
