package com.privacity.cliente.model;

import android.app.Activity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.privacity.cliente.encrypt.AESEncrypt;
import com.privacity.cliente.encrypt.AEStoUse;
import com.privacity.cliente.encrypt.EncryptUtil;
import com.privacity.cliente.singleton.SingletonValues;
import com.privacity.cliente.singleton.countdown.WrittingCountDownTimer;
import com.privacity.cliente.util.notificacion.GrupoCountDownTimer;
import com.privacity.common.dto.AESDTO;
import com.privacity.common.dto.GrupoDTO;
import com.privacity.common.dto.UserForGrupoDTO;

import java.io.IOException;
import java.security.GeneralSecurityException;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
public class Grupo extends GrupoDTO {

    public UserForGrupoDTO[] getUsersForGrupoDTO() {

        this.usersForGrupoDTO[0] = new UserForGrupoDTO();
        this.usersForGrupoDTO[0].setIdGrupo(this.getIdGrupo());
        this.usersForGrupoDTO[0].setUsuario(SingletonValues.getInstance().getUsuario());

        return usersForGrupoDTO;
    }

    public void setUsersForGrupoDTO(UserForGrupoDTO[] usersForGrupoDTO) {
        this.usersForGrupoDTO = usersForGrupoDTO;
    }
@Getter
@Setter
    private boolean grupoLocked;

    private UserForGrupoDTO[] usersForGrupoDTO = new UserForGrupoDTO[1];

    private WrittingCountDownTimer writtingCountDownTimer;

    public WrittingCountDownTimer getWrittingCountDownTimer(Activity activity) {
        if (writtingCountDownTimer == null){

            writtingCountDownTimer = new WrittingCountDownTimer(activity, this);

        }
        return writtingCountDownTimer;
    }

    public void setWrittingCountDownTimer(WrittingCountDownTimer writtingCountDownTimer) {
        this.writtingCountDownTimer = writtingCountDownTimer;
    }

    private GrupoCountDownTimer countDownTimer;

    public GrupoCountDownTimer getCountDownTimer() {
        if (countDownTimer == null){
            //if (this.getPassword().isEnabled()){
                countDownTimer = new GrupoCountDownTimer(idGrupo);
            //}
        }
        return countDownTimer;
    }

    public void setCountDownTimer(GrupoCountDownTimer countDownTimer) {
        this.countDownTimer = countDownTimer;
    }

    public AEStoUse getAESToUse() throws Exception {

        if (AESToUse == null){
            AESToUse = EncryptUtil.iniciarEncrypt(this);
        }
        return AESToUse;
    }

    public void setAESToUse(AEStoUse AESToUse) {
        this.AESToUse = AESToUse;
    }

    @JsonIgnore
    private AEStoUse AESToUse;

    public Grupo(String name) {
        super(name);
    }

    public AEStoUse getExtraAESToUse() {
        return ExtraAESToUse;
    }

    public void setExtraAESToUse(AEStoUse extraAESToUse) {
        ExtraAESToUse = extraAESToUse;
    }

    public AESDTO getExtraAES() {
        return ExtraAES;
    }

    public void setExtraAES(AESDTO extraAES) {
        ExtraAES = extraAES;
    }

    @JsonIgnore
    private AEStoUse ExtraAESToUse;

    @JsonIgnore
    private AESDTO ExtraAES;

    public AESDTO getAESDTOdesencrypt() throws IOException, GeneralSecurityException {

        if ( AESDTOdesencrypt == null){
            AESDTOdesencrypt = AESEncrypt.desencrypt(this.getUserForGrupoDTO().getAesDTO(),
                    SingletonValues.getInstance().getEncryptKeysToUse().getPrivateKey());
        }
        return AESDTOdesencrypt;
    }

    public void setAESDTOdesencrypt(AESDTO AESDTOdesencrypt) {
        this.AESDTOdesencrypt = AESDTOdesencrypt;
    }

    @JsonIgnore
    private AESDTO AESDTOdesencrypt;

    public int getReintentosPassword() {
        return reintentosPassword;
    }

    public void reintentosPasswordSumar() {
        reintentosPassword ++;
    }
    public void reintentosPasswordReset() {
        reintentosPassword=0;
    }
    private int reintentosPassword=0;

    public void setIAmWritting(boolean iAmWritting) {
        this.iAmWritting = iAmWritting;
    }

    public boolean isIamWritting() {
        return iAmWritting;
    }

    public void setIamWritting(boolean writting) {
        this.iAmWritting = writting;
    }
    private boolean otherAreWritting=false;

    public boolean isOtherAreWritting() {
        return otherAreWritting;
    }

    public void setOtherAreWritting(boolean otherAreWritting) {
        this.otherAreWritting = otherAreWritting;
    }

    private boolean iAmWritting=false;


    public void writtingCountDownTimerStop() {
        if (writtingCountDownTimer != null){
            writtingCountDownTimer.stop();
        }
    }
    @Getter @Setter
    private MessageInProcess messageInProcess= new MessageInProcess();


}
