package com.privacity.cliente.rest.restcalls.serverconf;

import android.app.Activity;

import com.privacity.cliente.singleton.SingletonLoginValues;
import com.privacity.cliente.encrypt.AESEncrypt;
import com.privacity.cliente.encrypt.AESFactory;
import com.privacity.cliente.encrypt.AEStoUse;
import com.privacity.cliente.encrypt.AEStoUseFactory;
import com.privacity.cliente.encrypt.PGPKeyBuilder;
import com.privacity.cliente.rest.CallbackRest;
import com.privacity.cliente.rest.InnerCallbackRest;
import com.privacity.cliente.rest.RestExecute;
import com.privacity.cliente.util.GsonFormated;
import com.privacity.common.enumeration.ProtocoloComponentsEnum;import com.privacity.common.enumeration.ProtocoloActionsEnum;

import com.privacity.common.dto.AESDTO;
import com.privacity.common.dto.ProtocoloDTO;

import org.springframework.http.ResponseEntity;

import java.security.PublicKey;

public class GetServerPublicKey {

    public static void getServerPublicKey(Activity context, CallbackRest callbackRest, InnerCallbackRest innerCallbackRest) {



        ProtocoloDTO p = new ProtocoloDTO();
        p.setComponent(ProtocoloComponentsEnum.PRIVACITY_RSA);
        p.setAction(ProtocoloActionsEnum.PRIVACITY_RSA_GET_PUBLIC_KEY);

        RestExecute.doit(context, p, new CallbackRest() {
            @Override
            public void response(ResponseEntity<ProtocoloDTO> response) {

                try {

                    byte[] publicKeyByte = GsonFormated.get().fromJson(response.getBody().getObjectDTO().replaceAll("\"",""), byte[].class);

                    PublicKey publicKey = PGPKeyBuilder.publicKey(publicKeyByte);

                    AESDTO aesdto = AESFactory.getAESPublic();
                    AEStoUse aestoUse = AEStoUseFactory.getAEStoUsePublic(aesdto);

                    AESDTO aesdtoEncrypt = AESEncrypt.encrypt(aesdto, publicKey);

                    SingletonLoginValues.getInstance().setAEStoUse(aestoUse);
                    SingletonLoginValues.getInstance().setAEStoSend(aesdtoEncrypt);

                    innerCallbackRest.action(context);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ResponseEntity<ProtocoloDTO> response) {
                if (callbackRest!= null) callbackRest.onError(response);
            }

            @Override
            public void beforeShowErrorMessage(String msg) {

            }
        }, false);



    }
}
