package com.privacity.cliente.model;

import android.app.Activity;

import com.privacity.cliente.common.constants.DeveloperConstant;
import com.privacity.cliente.encrypt.AESEncrypt;
import com.privacity.cliente.encrypt.AEStoUse;
import com.privacity.cliente.encrypt.EncryptUtil;
import com.privacity.cliente.singleton.SingletonValues;import com.privacity.cliente.singleton.Singletons;
import com.privacity.cliente.singleton.Singletons;
import com.privacity.cliente.singleton.countdown.WrittingCountDownTimer;
import com.privacity.cliente.util.notificacion.GrupoCountDownTimer;
import com.privacity.common.dto.AESDTO;
import com.privacity.common.dto.GrupoDTO;
import com.privacity.common.dto.GrupoGralConfDTO;
import com.privacity.common.dto.GrupoGralConfPasswordDTO;
import com.privacity.common.dto.GrupoInvitationDTO;
import com.privacity.common.dto.GrupoUserConfDTO;
import com.privacity.common.dto.LockDTO;
import com.privacity.common.dto.MembersQuantityDTO;
import com.privacity.common.dto.UserForGrupoDTO;
import com.privacity.common.enumeration.GrupoRolesEnum;

import java.io.IOException;
import java.security.GeneralSecurityException;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public class Grupo {



    @Getter
    @Setter
    private GrupoDTO dto;
    private final UserForGrupoDTO[] usersForGrupoDTO = new UserForGrupoDTO[1];
    private WrittingCountDownTimer writtingCountDownTimer;
    @Getter @Setter
    private boolean grupoLocked;

    private GrupoCountDownTimer countDownTimer;

    private AEStoUse AESToUse;
    @Getter
    private AEStoUse ExtraAESToUse;
    @Getter
    private AESDTO ExtraAES;

    private AESDTO AESDTOdesencrypt;
    @Getter
    private int reintentosPassword=0;
    @Getter @Setter
    private boolean iAmWritting=false;
    @Getter @Setter
    private boolean otherAreWritting=false;
    @Getter
    private final MessageInProcess messageInProcess= new MessageInProcess();

    @Getter @Setter
    private boolean secureFieldActivated;

    public Grupo(){
        this.dto= new GrupoDTO();
    }

    public Grupo(GrupoDTO dto){
        this.dto=dto;
    }

    public Grupo(String name){
        this.dto= new GrupoDTO(name);
    }

    public String getIdGrupo() {
        return dto.getIdGrupo();
    }

    public String getName() {
        return dto.getName();
    }
    public UserForGrupoDTO[] getUsersForGrupoDTO() {
        this.usersForGrupoDTO[0] = new UserForGrupoDTO();
        this.usersForGrupoDTO[0].setIdGrupo(this.getIdGrupo());
        this.usersForGrupoDTO[0].setUsuario(Singletons.usuario().getUsuario());

        return usersForGrupoDTO;
    }

    public WrittingCountDownTimer getWrittingCountDownTimer(Activity activity) {
        if (writtingCountDownTimer == null){
            writtingCountDownTimer = new WrittingCountDownTimer(activity, this);
        }
        return writtingCountDownTimer;
    }

    public GrupoCountDownTimer getCountDownTimer() {
        if (countDownTimer == null){
                countDownTimer = new GrupoCountDownTimer(getIdGrupo());
        }
        return countDownTimer;
    }

    public AEStoUse getAESToUse() throws Exception {
        if (AESToUse == null){
            AESToUse = EncryptUtil.iniciarEncrypt(this);
        }
        return AESToUse;
    }

    public AESDTO getAESDTOdesencrypt() throws IOException, GeneralSecurityException {

        if ( AESDTOdesencrypt == null){
            AESDTOdesencrypt = AESEncrypt.decrypt(this.dto.getUserForGrupoDTO().getAesDTO(),
                    SingletonValues.getInstance().getEncryptKeysToUse().getPrivateKey());
        }
        return AESDTOdesencrypt;
    }

    public boolean iAmAdmin(){
        if (DeveloperConstant.validateAdmin == false) return true;
        return getUserForGrupoDTO().getRole().equals(GrupoRolesEnum.ADMIN);
    }

    public boolean iAmMember(){
        return getUserForGrupoDTO().getRole().equals(GrupoRolesEnum.MEMBER);
    }

    public boolean iAmModerator(){
        return getUserForGrupoDTO().getRole().equals(GrupoRolesEnum.MODERATOR);
    }

    public boolean iAmReadOnly(){
        if (DeveloperConstant.validateReadOnly == false) return false;
        return getUserForGrupoDTO().getRole().equals(GrupoRolesEnum.READONLY);

    }


    public void reintentosPasswordSumar() {
        reintentosPassword ++;
    }
    public void reintentosPasswordReset() {
        reintentosPassword=0;
    }



    public void writtingCountDownTimerStop() {
        if (writtingCountDownTimer != null){
            writtingCountDownTimer.stop();
        }
    }




    public GrupoInvitationDTO getGrupoInvitationDTO() {
        return this.dto.getGrupoInvitationDTO();
    }

    public UserForGrupoDTO getUserForGrupoDTO() {

        if (this.dto.getUserForGrupoDTO() ==null ) this.setUserForGrupoDTO(new UserForGrupoDTO());
        return this.dto.getUserForGrupoDTO();
    }

    public GrupoGralConfDTO getGralConfDTO() {
        if (this.dto.getGralConfDTO() ==null ) this.setGralConfDTO(new GrupoGralConfDTO());
        return this.dto.getGralConfDTO();
        }

    public GrupoUserConfDTO getUserConfDTO(){
        if (this.dto.getUserConfDTO() ==null ) this.dto.setUserConfDTO(new GrupoUserConfDTO());
        return this.dto.getUserConfDTO();
    }

    public MembersQuantityDTO getMembersQuantityDTO(){
        if (this.dto.getMembersQuantityDTO() == null){
            this.dto.setMembersQuantityDTO(new MembersQuantityDTO());
        }
        return this.dto.getMembersQuantityDTO();
    }

    public String getAlias(){
        return this.dto.getAlias();
    }

    public LockDTO getLock(){

            if (this.dto.getLock() ==null ) this.setLock(new LockDTO());
            return this.dto.getLock();
        }


    public GrupoGralConfPasswordDTO getPassword() {
        if (this.dto.getPassword() ==null ) this.setPassword(new GrupoGralConfPasswordDTO());
        return this.dto.getPassword();
    }

    public Grupo setIdGrupo(final String idGrupo) {
        this.dto.setIdGrupo(idGrupo);
        return this;
    }

    public Grupo setName(final String name) {
        this.dto.setName(name);
        return this;
    }

    public Grupo setGrupoInvitationDTO(final GrupoInvitationDTO grupoInvitationDTO) {
        this.dto.setGrupoInvitationDTO(grupoInvitationDTO);
        return this;
    }

    public Grupo setUserForGrupoDTO(final UserForGrupoDTO userForGrupoDTO) {
        this.dto.setUserForGrupoDTO(userForGrupoDTO);
        return this;
    }

    public Grupo setGralConfDTO(final GrupoGralConfDTO gralConfDTO) {
        this.dto.setGralConfDTO(gralConfDTO);
        return this;
    }

    public Grupo setUserConfDTO(final GrupoUserConfDTO userConfDTO) {
        this.dto.setUserConfDTO(userConfDTO);
        return this;
    }

    public Grupo setMembersQuantityDTO(final MembersQuantityDTO membersQuantityDTO) {
        this.dto.setMembersQuantityDTO(membersQuantityDTO);
        return this;
    }

    public Grupo setAlias(final String alias) {
        this.dto.setAlias(alias);
        return this;
    }

    public Grupo setLock(final LockDTO lock) {
        this.dto.setLock(lock);
        return this;
    }

    public Grupo setPassword(final GrupoGralConfPasswordDTO password) {
        this.dto.setPassword(password);
        return this;
    }

    public boolean isGrupoInvitation() {
        return dto.isGrupoInvitation();
    }
}
