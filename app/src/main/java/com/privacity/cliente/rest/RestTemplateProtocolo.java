package com.privacity.cliente.rest;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.privacity.cliente.singleton.impl.SingletonServer;
import com.privacity.cliente.common.error.ErrorDialog;
import com.privacity.cliente.rest.restcalls.reconnect.ReconnectionSyncSession;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.util.GsonFormated;
import com.privacity.common.config.ConstantProtocolo;
import com.privacity.common.config.SystemGralConfURLs;
import com.privacity.common.dto.ProtocoloDTO;
import com.privacity.common.enumeration.ExceptionReturnCode;

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

public class RestTemplateProtocolo extends AsyncTask<Void, Void, ResponseEntity<ProtocoloDTO>> {

    private CallbackRest callbackRest;
    private ProtocoloDTO protocoloDTO;
    private Activity context;
    private boolean secure=true;
    public RestTemplateProtocolo(ProtocoloDTO ProtocoloDTO) {
        this.protocoloDTO = protocoloDTO;
    }

    public RestTemplateProtocolo(Activity context, ProtocoloDTO protocoloDTO, CallbackRest callbackRest) {
        this.callbackRest = callbackRest;
        this.protocoloDTO = protocoloDTO;
        this.context = context;
    }
    public RestTemplateProtocolo(Activity context, ProtocoloDTO protocoloDTO, CallbackRest callbackRest, boolean secure) {
        this.callbackRest = callbackRest;
        this.protocoloDTO = protocoloDTO;
        this.secure = secure;
        this.context = context;
    }

    @Override
    protected ResponseEntity<ProtocoloDTO> doInBackground(Void... voids) {

        final boolean  logOn;
        if ( protocoloDTO.getAction().equals(ConstantProtocolo.PROTOCOLO_ACTION_REQUEST_ID_PUBLIC_GET) ||
                protocoloDTO.getAction().equals(ConstantProtocolo.PROTOCOLO_ACTION_REQUEST_ID_PRIVATE_GET)
        ){
            logOn= true;
        }else{
            // ACA VA TRUE
            logOn= true;
        }

        if (logOn) Log.i("------------------- Action", protocoloDTO.getAction());


        RestTemplate rt = new RestTemplate();

        ResponseEntity<ProtocoloDTO> a = null;
        rt.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        try {

            Gson gson = GsonFormated.get();
            HashMap<String, Object> map = new HashMap<String, Object>();

            {
                String prettyJsonString = gson.toJson(protocoloDTO.getObjectDTO());
                if (logOn) Log.i(">> ProtocoloDTO >> ", prettyJsonString);
            }

            String toSend="";
            if (secure){
                /*
                if (
                        protocoloDTO.getAction().equals("/message/send") &&
                        protocoloDTO.getMessageDTO().getMediaDTO() != null){
                    protocoloDTO.getMessageDTO().getMediaDTO().setData(
                    ZipUtil.compress(protocoloDTO.getMessageDTO().getMediaDTO().getData())
                    );
                }*/


                toSend = SingletonValues.getInstance().getSessionAEStoUse().getAES(gson.toJson(protocoloDTO));


                map.put("request", toSend);

                //if (logOn)  Log.i(">> Rest", "Authorization: " + SingletonValues.getInstance().getToken());
            }else{
                map.put("component", protocoloDTO.getComponent());
                map.put("action", protocoloDTO.getAction());
                if ( protocoloDTO.getObjectDTO() != null) map.put("objectDTO", protocoloDTO.getObjectDTO());
                map.put("asyncId", protocoloDTO.getAsyncId());

            }


            HttpEntity<Map<String, Object>> entity;
            if (secure){

                entity = new HttpEntity<Map<String, Object>>(map, new HttpHeaders() {{
                    String authHeader = SingletonValues.getInstance().getToken();
                    set( "Authorization", authHeader );
                }});
                //if (logOn) Log.i(">> ProtocoloDTO ENCRIPTADO >>", toSend);

                String url;
                if (protocoloDTO.getMessageDTO() != null){
                    url = SingletonServer.getInstance().getAppServer() + SystemGralConfURLs.CONSTANT_URL_PATH_PRIVATE_SEND;
                }else{
                    url = SingletonServer.getInstance().getAppServer() + SystemGralConfURLs.CONSTANT_URL_PATH_PRIVATE;
                }
                ResponseEntity<String> s = null;
                s = rt.postForEntity(url, entity, String.class);

                //if (logOn) Log.i(">> ProtocoloDTO ENCRIPTADO ", s.getBody().toString());
                String sBodyDescr = SingletonValues.getInstance().getSessionAEStoUseServerEncrypt().getAESDecrypt(s.getBody());
                //if (logOn) Log.i("ProtocoloDTO  sBodyDescr <<", sBodyDescr);
                ProtocoloDTO pReturn = gson.fromJson(sBodyDescr, ProtocoloDTO.class);

                {
                    String prettyJsonString = gson.toJson(pReturn.getObjectDTO());
                    if (logOn) System.out.println("<< ProtocoloDTO << " + prettyJsonString);
                }
                a = new ResponseEntity<ProtocoloDTO>(pReturn, HttpStatus.OK);

            }else{
                entity = new HttpEntity<Map<String, Object>>(map, new HttpHeaders());
                a = rt.postForEntity(SingletonServer.getInstance().getAppServer() + SystemGralConfURLs.CONSTANT_URL_PATH_FREE, entity, ProtocoloDTO.class);
                if (logOn) Log.i("<< ProtocoloDTO << ", a.getBody().toString());
            }


            return a;

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

    public void onPostExecutePublic(ResponseEntity<ProtocoloDTO> response) {
        onPostExecute(response);
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
}
