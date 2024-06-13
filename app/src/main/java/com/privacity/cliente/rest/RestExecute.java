package com.privacity.cliente.rest;

import android.app.Activity;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.privacity.cliente.encrypt.AEStoUse;
import com.privacity.cliente.rest.restcalls.CallbackRestDownload;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.singleton.reconnect.SingletonReconnect;
import com.privacity.cliente.util.GsonFormated;
import com.privacity.common.config.ConstantProtocolo;
import com.privacity.common.dto.AESDTO;
import com.privacity.common.dto.ProtocoloDTO;
import com.privacity.common.dto.ProtocoloWrapperDTO;
import com.privacity.common.dto.RequestIdDTO;
import com.privacity.common.enumeration.ExceptionReturnCode;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Random;

public class RestExecute {

    private static boolean checkInternet(Activity activity, CallbackRestDownload c){
        if (!SingletonReconnect.isOnline(activity)){
            ProtocoloDTO p = new ProtocoloDTO();
            p.setCodigoRespuesta(ExceptionReturnCode.GENERAL_NO_INTERNET_ACCESS.getCode());

            RestTemplateProtocolo t = new RestTemplateProtocolo(activity,
                    p,
                    new CallbackRest() {
                        @Override
                        public void response(ResponseEntity<ProtocoloDTO> response) {

                        }

                        @Override
                        public void onError(ResponseEntity<ProtocoloDTO> response) {
                            c.onError(null);
                        }

                        @Override
                        public void beforeShowErrorMessage(String msg) {

                        }
                    }, true);

            t.onPostExecutePublic(new ResponseEntity<ProtocoloDTO>(p,HttpStatus.OK));

            return false;
        }

        return true;
    }

    public static boolean checkInternet(Activity activity, CallbackRest c){
        if (!SingletonReconnect.isOnline(activity)){
            ProtocoloDTO p = new ProtocoloDTO();
            p.setCodigoRespuesta(ExceptionReturnCode.GENERAL_NO_INTERNET_ACCESS.getCode());

        RestTemplateProtocolo t = new RestTemplateProtocolo(activity,
                p,
                c, false);

        t.onPostExecutePublic(new ResponseEntity<ProtocoloDTO>(p,HttpStatus.OK));

            return false;
        }

        return true;
    }
    private boolean secure = true;


    public static void doitSend(Activity context, ProtocoloDTO pOrigen, byte[] data,  CallbackRest callbackRestOrigen) {
        if (!checkInternet(context,callbackRestOrigen))return;

        SingletonValues.getInstance().passwordCountDownTimerRestart();



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

    public static void doitDownload(Activity context, ProtocoloDTO pOrigen, CallbackRestDownload callbackRestOrigen) {
        //if (!checkInternet(context,callbackRestOrigen))return;
        SingletonValues.getInstance().passwordCountDownTimerRestart();



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
            , ProtocoloDTO pOrigen
            , CallbackRest callbackRestOrigen
    ) {

        doit(context
                , pOrigen
                , callbackRestOrigen
                , true);
    }

    public static void doitPublic(
            Context context
            , ProtocoloDTO pOrigen
            , CallbackRest callbackRestOrigen
            , AEStoUse aeStoUse
            , AESDTO aesdtoToSend
    ) {

        String requestIdClientSide = requestIdClientSide();
        ProtocoloWrapperDTO wrapper = new ProtocoloWrapperDTO();

        ProtocoloDTO p = new ProtocoloDTO();
        p.setComponent(ConstantProtocolo.PROTOCOLO_COMPONENT_REQUEST_ID);
        p.setAction(ConstantProtocolo.PROTOCOLO_ACTION_REQUEST_ID_PUBLIC_GET);

        RequestIdDTO requestIdClientSideDTO = new RequestIdDTO();
        requestIdClientSideDTO.setRequestIdClientSide(requestIdClientSide);

        Gson gson = GsonFormated.get();

        // tiene que tomar la fecha del servidor
        requestIdClientSideDTO.setDate(SingletonValues.getInstance().calculateServerTime());
        p.setObjectDTO(gson.toJson(requestIdClientSideDTO));

        String protocoloNoEncrypt = gson.toJson(p);
        String protocoloEncrypt = aeStoUse.getAES(protocoloNoEncrypt);

        wrapper.setProtocoloDTO(protocoloEncrypt);
        wrapper.setAesEncripted(aesdtoToSend);

        RestTemplateProtocoloWrapper taskrest = new RestTemplateProtocoloWrapper(aeStoUse, context, wrapper,
                new CallbackRest() {

                    @Override
                    public void response(ResponseEntity<ProtocoloDTO> response) {

                        Gson gson = new GsonBuilder()
                                .setPrettyPrinting()
                                .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
                                .create();

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
                    public void onError(ResponseEntity<ProtocoloDTO> response) {
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
            , ProtocoloDTO pOrigen
            , CallbackRest callbackRestOrigen
            , boolean secure
    ) {
        if (!checkInternet(context,callbackRestOrigen))return;
        if (!secure) {
            RestTemplateProtocolo taskinner = new RestTemplateProtocolo(context,
                    pOrigen,
                    callbackRestOrigen, secure);
            taskinner.execute();
        } else {

            SingletonValues.getInstance().passwordCountDownTimerRestart();



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

        return alphMinMax(
                SingletonValues.getInstance().getSystemGralConf().getRequestId().getMinLenght(),
                SingletonValues.getInstance().getSystemGralConf().getRequestId().getMaxLenght()) + "-" +
                SingletonValues.getInstance().getCounterNextValue();

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
