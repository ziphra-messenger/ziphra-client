package com.privacity.cliente.rest;

import android.app.Activity;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.privacity.cliente.encrypt.AEStoUse;
import com.privacity.cliente.model.dto.Protocolo;
import com.privacity.cliente.rest.restcalls.CallbackRestDownload;
import com.privacity.cliente.singleton.Singletons;
import com.privacity.cliente.singleton.UtilsStringSingleton;
import com.privacity.cliente.singleton.reconnect.SingletonReconnect;
import com.privacity.cliente.singleton.serverconfiguration.SingletonServerConfiguration;
import com.privacity.cliente.singleton.usuario.SingletonSessionClosing;
import com.privacity.common.dto.AESDTO;

import com.privacity.common.dto.ProtocoloWrapperDTO;
import com.privacity.common.dto.RequestIdDTO;
import com.privacity.common.enumeration.ExceptionReturnCode;
import com.privacity.common.enumeration.ProtocoloActionsEnum;
import com.privacity.common.enumeration.ProtocoloComponentsEnum;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Random;

public class RestExecute {
    private static final String TAG = "RestExecute";
    private static boolean checkInternet(Activity activity, CallbackRestDownload c){
        if (!SingletonReconnect.isOnline(activity)){
            Protocolo p = new Protocolo();
            p.setCodigoRespuesta(ExceptionReturnCode.GENERAL_NO_INTERNET_ACCESS.getCode());

            RestTemplateProtocolo t = new RestTemplateProtocolo(activity,
                    p,
                    new CallbackRest() {
                        @Override
                        public void response(ResponseEntity<Protocolo> response) {

                        }

                        @Override
                        public void onError(ResponseEntity<Protocolo> response) {
                            c.onError(null);
                        }

                        @Override
                        public void beforeShowErrorMessage(String msg) {

                        }
                    }, true);

            t.onPostExecutePublic(new ResponseEntity<Protocolo>(p,HttpStatus.OK));

            return false;
        }

        return true;
    }

    public static boolean checkInternet(Activity activity, CallbackRest c){
        if (!SingletonReconnect.isOnline(activity)){
            Protocolo p = new Protocolo();
            p.setCodigoRespuesta(ExceptionReturnCode.GENERAL_NO_INTERNET_ACCESS.getCode());

        RestTemplateProtocolo t = new RestTemplateProtocolo(activity,
                p,
                c, false);

        t.onPostExecutePublic(new ResponseEntity<Protocolo>(p,HttpStatus.OK));

            return true;
        }

        return false;
    }
    private final boolean secure = true;


    public static void doitSend(Activity context, Protocolo pOrigen, byte[] data, CallbackRest callbackRestOrigen) {
        if (checkInternet(context, callbackRestOrigen))return;


        String requestIdClientSide = requestIdClientSide();

        RequestIdDTO requestIdServerSide = new RequestIdDTO();
        requestIdServerSide.setRequestIdClientSide(requestIdClientSide);
        pOrigen.setRequestIdDTO(requestIdServerSide);

        RestTemplateProtocoloFile taskinner = new RestTemplateProtocoloFile(context,
                pOrigen,
                data,
                callbackRestOrigen);
        taskinner.execute();

    }

    public static void doitDownload(Activity context, Protocolo pOrigen, CallbackRestDownload callbackRestOrigen) {
        //if (!checkInternet(context,callbackRestOrigen))return;


        String requestIdClientSide = requestIdClientSide();

        RequestIdDTO requestIdServerSide = new RequestIdDTO();
        requestIdServerSide.setRequestIdClientSide(requestIdClientSide);
        pOrigen.setRequestIdDTO(requestIdServerSide);

        RestTemplateProtocoloDownload taskinner = new RestTemplateProtocoloDownload(context,
                pOrigen,
                       callbackRestOrigen);
        taskinner.execute();

    }

    public static void doit(
            Activity context
            , Protocolo pOrigen
            , CallbackRest callbackRestOrigen
    ) {

        doit(context
                , pOrigen
                , callbackRestOrigen
                , true);
    }

    public static void doitPublic(
            Activity context
            , Protocolo pOrigen
            , CallbackRest callbackRestOrigen
            , AEStoUse aeStoUse
            , AESDTO aesdtoToSend
    ) {
        System.out.println("AEStoUse: " + aeStoUse.toString());
        System.out.println("aesdtoToSend: " + aesdtoToSend.toString());
        String requestIdClientSide = requestIdClientSide();
        ProtocoloWrapperDTO wrapper = new ProtocoloWrapperDTO();

        Protocolo p = new Protocolo();
        p.setComponent(ProtocoloComponentsEnum.REQUEST_ID);
        p.setAction(ProtocoloActionsEnum.REQUEST_ID_PUBLIC_GET);

        RequestIdDTO requestIdClientSideDTO = new RequestIdDTO();
        requestIdClientSideDTO.setRequestIdClientSide(requestIdClientSide);


        Gson gson = UtilsStringSingleton.getInstance().gson();

        // tiene que tomar la fecha del servidor
        requestIdClientSideDTO.setDate(Singletons.serverTime().calculateServerTime());
        p.setObjectDTO(gson.toJson(requestIdClientSideDTO));
        System.out.println("protocoloNoEncrypt: " + p.toString());
        String protocoloNoEncrypt = gson.toJson(p);
        System.out.println("protocoloEncrypt: " + protocoloNoEncrypt);
        String protocoloEncrypt = aeStoUse.getAES(protocoloNoEncrypt);

        wrapper.setProtocoloDTO(protocoloEncrypt);
        wrapper.setAesEncripted(aesdtoToSend);

        RestTemplateProtocoloWrapper taskrest = new RestTemplateProtocoloWrapper(aeStoUse, context, wrapper,
                new CallbackRest() {

                    @Override
                    public void response(ResponseEntity<Protocolo> response) {

                        Gson gson = new GsonBuilder()
                                .setPrettyPrinting()
                                .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
                                .create();

                        Log.d(TAG, "Body entrada :" + response.getBody().getObjectDTO());
/*                        //String uncompressB64 = null;
                        try {
                            //uncompressB64 = UtilsString.uncompressB64(response.getBody().getObjectDTO());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG, "Body uncompressB64 :" + uncompressB64);

                        response.getBody().setObjectDTO(uncompressB64);*/

                        RequestIdDTO requestIdServerSide = gson.fromJson(response.getBody().getObjectDTO(), RequestIdDTO.class);
                        requestIdServerSide.setRequestIdClientSide(requestIdClientSide);
                        pOrigen.setRequestIdDTO(requestIdServerSide);

                        ProtocoloWrapperDTO wrapper = new ProtocoloWrapperDTO();

                        String OprotocoloEncrypt = aeStoUse.getAES(gson.toJson(pOrigen));
                        wrapper.setProtocoloDTO(OprotocoloEncrypt);
                        wrapper.setAesEncripted(aesdtoToSend);

                        RestTemplateProtocoloWrapper taskinner = new RestTemplateProtocoloWrapper(
                                aeStoUse,
                                context,
                                wrapper,
                                callbackRestOrigen, false);
                        taskinner.execute();


                    }

                    @Override
                    public void onError(ResponseEntity<Protocolo> response) {
                        callbackRestOrigen.onError(response);
                    }

                    @Override
                    public void beforeShowErrorMessage(String msg) {
                        callbackRestOrigen.beforeShowErrorMessage(msg);

                    }
                }, false);

        taskrest.execute();
    }


    public static void doit(
            Activity context
            , Protocolo pOrigen
            , CallbackRest callbackRestOrigen
            , boolean secure
    ) {
        if (SingletonSessionClosing.getInstance().isClosing() && !pOrigen.getAction().equals(ProtocoloActionsEnum.MY_ACCOUNT_CLOSE_SESSION))return;
        if (checkInternet(context, callbackRestOrigen))return;
        if (!secure) {
            RestTemplateProtocolo taskinner = new RestTemplateProtocolo(context,
                    pOrigen,
                    callbackRestOrigen, secure);
            taskinner.execute();
        } else {



            String requestIdClientSide = requestIdClientSide();

            RequestIdDTO requestIdServerSide = new RequestIdDTO();
            requestIdServerSide.setRequestIdClientSide(requestIdClientSide);
            pOrigen.setRequestIdDTO(requestIdServerSide);

            RestTemplateProtocolo taskinner = new RestTemplateProtocolo(context,
                    pOrigen,
                    callbackRestOrigen, secure);
            taskinner.execute();

        }
    }

    private static String requestIdClientSide() {
        if (SingletonSessionClosing.getInstance().isClosing() )return "";

        try {
            return alphMinMax(
                    SingletonServerConfiguration.getInstance().getSystemGralConf().getRequestId().getMinLenght(),
                    SingletonServerConfiguration.getInstance().getSystemGralConf().getRequestId().getMaxLenght());
        } catch (Exception e) {

        }
        /*+ "-" +
                SingletonValues.getInstance().getCounterNextValue();*/
        return "";
    }

    private static String alphMinMax(int min, int max) {
        return RandomStringUtils.randomAlphanumeric(new Random().nextInt(max - min) + min);
    }

    static class LocalDateAdapter extends TypeAdapter<LocalDateTime> {
        @Override
        public void write(final JsonWriter jsonWriter, final LocalDateTime localDate) throws IOException {
            if (localDate == null) {
                jsonWriter.nullValue();
            } else {
                jsonWriter.value(localDate.toString());
            }
        }

        @Override
        public LocalDateTime read(final JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull();
                return null;
            } else {
                return LocalDateTime.parse(jsonReader.nextString());
            }
        }
    }

}
