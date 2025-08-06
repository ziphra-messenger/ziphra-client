package com.privacity.cliente.activity.message;

import com.privacity.cliente.model.Grupo;
import com.privacity.cliente.singleton.Observers;
import com.privacity.cliente.singleton.SingletonValues;import com.privacity.cliente.singleton.Singletons;
import com.privacity.common.dto.GrupoGralConfDTO;
import com.privacity.common.dto.GrupoUserConfDTO;
import com.privacity.common.dto.MyAccountConfDTO;
import com.privacity.common.enumeration.ConfigurationStateEnum;
import com.privacity.common.enumeration.RulesConfEnum;

public class MasterGeneralConfiguration {

    public static ConfType buildSiempreTemporalConfigurationByGrupo(String idGrupo){

        Grupo grupo = Observers.grupo().getGrupoById(idGrupo);

        GrupoUserConfDTO grupoUser = Observers.grupo().getGrupoById(idGrupo).getUserConfDTO();
        GrupoGralConfDTO grupoGeneral = grupo.getGralConfDTO();
        MyAccountConfDTO myAccount = SingletonValues.getInstance().getMyAccountConfDTO();
        //
        ConfType conf = new ConfType();

        if (grupoGeneral.isTimeMessageMandatory()){
            conf.setValue(true);
            conf.setSuperiorConf(true);
            conf.setSuperiorValue(true);
            conf.setGanaGrupo(true);
        }else{
            conf.setGanaGrupo(false);
            conf.setSuperiorConf(false);
            conf.setSuperiorValue(false);
            if ( grupoUser.getTimeMessageAlways().equals(RulesConfEnum.ON) ){
                    conf.setValue(true);
                if ( grupoUser.getTimeMessageAlways().equals(RulesConfEnum.OFF) ){
                    conf.setValue(false);
            }else if ( grupoUser.getTimeMessageAlways().equals(RulesConfEnum.NULL) ){
                conf.setSuperiorConf(true);
                    conf.setSuperiorValue(myAccount.isTimeMessageAlways());
            }
                if (myAccount.isTimeMessageAlways()){
                    conf.setSuperiorValue(true);
                }
            }
        }

        return conf;
    }


    public static ConfType buildSiempreBlackReceptionConfigurationByGrupo(String idGrupo) {

        GrupoUserConfDTO grupoUser = Observers.grupo().getGrupoById(idGrupo).getUserConfDTO();

        if ( RulesConfEnum.ON.equals(grupoUser.getBlackMessageAttachMandatoryReceived()) ) {
            ConfType conf = new ConfType();
            conf.setValue(true);
            return conf;
        }else{
            ConfType conf = new ConfType();
            conf.setValue(false);
            return conf;
        }
    }

    public static ConfType buildSiempreAnonimoReceptionConfigurationByGrupo(String idGrupo) {

        GrupoUserConfDTO grupoUser = Observers.grupo().getGrupoById(idGrupo).getUserConfDTO();

        if ( grupoUser.isAnonimoRecived() ) {
            ConfType conf = new ConfType();
            conf.setValue(true); //parche
            return conf;
        }else{
            ConfType conf = new ConfType();
            conf.setValue(false);
            return conf;
        }
    }

    public static ConfType buildHideNicknameConfigurationByGrupo(String idGrupo) {
        Grupo grupo = Observers.grupo().getGrupoById(idGrupo);
        GrupoUserConfDTO grupoUser = Observers.grupo().getGrupoById(idGrupo).getUserConfDTO();
        GrupoGralConfDTO grupoGeneral = grupo.getGralConfDTO();
        MyAccountConfDTO myAccount = SingletonValues.getInstance().getMyAccountConfDTO();


        if ( grupoGeneral.isRandomNickname()) {
            ConfType conf = new ConfType();
            conf.setSuperiorValue(true);
            conf.setGanaGrupo(true);
            conf.setValue(true);
            return conf;
//        }else if ( grupoUser.getChangeNicknameToNumber().equals(RulesConfEnum.ON) ) {
//                ConfType conf = new ConfType();
//                conf.setValue(true);
//                return conf;
        }else{
            ConfType conf = new ConfType();
            conf.setValue(false);
            return conf;
        }
    }

    public static ConfType buildBlockResendConfigurationByGrupo(String idGrupo){

        Grupo grupo = Observers.grupo().getGrupoById(idGrupo);

        GrupoUserConfDTO grupoUser = Observers.grupo().getGrupoById(idGrupo).getUserConfDTO();
        GrupoGralConfDTO grupoGeneral = grupo.getGralConfDTO();
        MyAccountConfDTO myAccount = SingletonValues.getInstance().getMyAccountConfDTO();
        //
        ConfType conf = new ConfType();

        conf.setValue(false);
        if (grupoGeneral.isBlockResend()){
            conf.setValue(true);
            conf.setSuperiorConf(false);
            conf.setSuperiorValue(false);
            conf.setGanaGrupo(true);
        }else{
            if ( grupoUser.getBlockResend().equals(RulesConfEnum.ON) ) {
                conf.setValue(true);
            }else  if ( grupoUser.getBlockResend().equals(RulesConfEnum.OFF) ){
                    conf.setValue(false);
            }else if ( grupoUser.getBlockResend().equals(RulesConfEnum.NULL) ){
                conf.setSuperiorConf(true);
                if (myAccount.isBlockResend()){
                    conf.setValue(true);
                    conf.setSuperiorValue(false);
                }else{
                    conf.setValue(false);
                    conf.setSuperiorValue(false);

                }
            }

            if (myAccount.isBlockResend()){
                conf.setSuperiorValue(true);
            }
        }

        return conf;
    }

    public static ConfType  buildSiempreAnonimoConfigurationByGrupo(String idGrupo){

        Grupo grupo = Observers.grupo().getGrupoById(idGrupo);

        GrupoUserConfDTO grupoUser = Observers.grupo().getGrupoById(idGrupo).getUserConfDTO();
        GrupoGralConfDTO grupoGeneral = grupo.getGralConfDTO();
        //MyAccountConfDTO myAccount = SingletonValues.getInstance().getMyAccountConfDTO();
        //
        ConfType conf = new ConfType();

        if (grupoGeneral.getAnonimo().equals(ConfigurationStateEnum.BLOCK)){
            conf.setValue(false);
            conf.setSuperiorConf(false);
            conf.setSuperiorValue(false);
            conf.setGanaGrupo(true);
        }if (grupoGeneral.getAnonimo().equals(ConfigurationStateEnum.MANDATORY)){
            conf.setValue(true);
            conf.setSuperiorConf(true);
            conf.setSuperiorValue(true);
            conf.setGanaGrupo(true);
        }else{
            if ( grupoUser.getAnonimoAlways().equals(RulesConfEnum.ON) ){
                conf.setValue(true);
            }
            if ( grupoUser.getAnonimoAlways().equals(RulesConfEnum.OFF) ){
                conf.setValue(false);
            }

            conf.setSuperiorValue(false);

        }

        return conf;
    }

    public static ConfType buildSiempreExtraEncryptConfigurationByGrupo(String idGrupo){

        Grupo grupo = Observers.grupo().getGrupoById(idGrupo);

        GrupoUserConfDTO grupoUser = Observers.grupo().getGrupoById(idGrupo).getUserConfDTO();
        GrupoGralConfDTO grupoGeneral = grupo.getGralConfDTO();
        //MyAccountConfDTO myAccount = SingletonValues.getInstance().getMyAccountConfDTO();
        //
        ConfType conf = new ConfType();

        if (grupoGeneral.getExtraEncrypt().equals(ConfigurationStateEnum.BLOCK)){
            conf.setValue(false);
            conf.setSuperiorConf(true);
            conf.setSuperiorValue(false);
            conf.setGanaGrupo(true);
        }if (grupoGeneral.getExtraEncrypt().equals(ConfigurationStateEnum.MANDATORY)){
            conf.setValue(true);
            conf.setSuperiorConf(true);
            conf.setSuperiorValue(true);
            conf.setGanaGrupo(true);
        }else{
            if ( grupoUser.getExtraAesAlways().equals(RulesConfEnum.ON) ){
                conf.setValue(true);
            }
            if ( grupoUser.getExtraAesAlways().equals(RulesConfEnum.OFF) ){
                conf.setValue(false);
            }
            conf.setSuperiorConf(false);
            conf.setSuperiorValue(false);
            conf.setGanaGrupo(false);

        }

        return conf;
    }



}
