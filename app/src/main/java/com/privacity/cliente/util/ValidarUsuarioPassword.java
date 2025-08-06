package com.privacity.cliente.util;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.privacity.cliente.R;
import com.privacity.cliente.singleton.impl.SingletonServer;
import com.privacity.common.dto.servergralconf.PasswordRulesDTO;

import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RepeatCharacterRegexRule;
import org.passay.RuleResult;
import org.passay.UsernameRule;

import java.util.ArrayList;

public class ValidarUsuarioPassword {

    public static boolean validateNickname(Activity activity, TextView validationText, PasswordRulesDTO rulesConfig, String nickname, String nicknameOld, String username, String password, boolean mandatory) {
        if (nickname == null || nickname.equals("")) {
            validationText.setTextColor(Color.BLACK);
            validationText.setVisibility(View.VISIBLE);
            validationText.setText(activity.getString(R.string.register_activity__requerido));
            return !mandatory;
        }

        if (nickname.equals(nicknameOld)) {
            validationText.setVisibility(View.VISIBLE);
            validationText.setTextColor(Color.RED);
            validationText.setText(activity.getString(R.string.registro_validation_password2_no_change));
            return false;
        }

        validationText.setText("");
        boolean valid=true;
        ArrayList<String> mensajes=new ArrayList<>();
        ArrayList<String> sugerencias=new ArrayList<>();
        //List<Rule> rules = new ArrayList<>();

        nickname=nickname.trim();
        validarLenght(activity, rulesConfig, nickname, mensajes, sugerencias);
        validarWhiteSpace(activity, rulesConfig, nickname, mensajes, sugerencias);
        validarUppercase(activity, rulesConfig, nickname, mensajes, sugerencias);
        validarLowercase(activity, rulesConfig, nickname, mensajes, sugerencias);
        validarDigitNumber(activity, rulesConfig, nickname, mensajes, sugerencias);
        validarEspecialChar(activity, rulesConfig, nickname, mensajes, sugerencias);
        validarUsuarioRestrict(activity, rulesConfig,username, nickname, mensajes, sugerencias);
        validarPasswordRestrict(activity, rulesConfig,password, nickname, mensajes, sugerencias);
//        validarUsernameRestrict(activity, rulesConfig, username, username, mensajes, sugerencias);
        validarRepeatCharRestrict(activity, rulesConfig, nickname, mensajes, sugerencias);


        validationText.setVisibility(View.VISIBLE);
        if( mensajes.size()>0) {
            validationText.setVisibility(View.VISIBLE);
            valid = addErrores(validationText, mensajes);
        }else{
            validationText.setTextColor(Color.BLACK);
            addSugerencias(activity, validationText, sugerencias,false);

        }
       // if (SingletonServer.getInstance().isDeveloper()) return true;

        return valid;
    }

    public static boolean validateUsername(Activity activity, TextView validationText, PasswordRulesDTO rulesConfig, String username) {
        if (username == null || username.equals("")) {
            validationText.setTextColor(Color.BLACK);
            validationText.setText(activity.getString(R.string.register_activity__requerido));
            return false;
        }

        validationText.setText("");
        boolean valid=true;
        ArrayList<String> mensajes=new ArrayList<>();
        ArrayList<String> sugerencias=new ArrayList<>();
        //List<Rule> rules = new ArrayList<>();

        username=username.trim();
        validarLenght(activity, rulesConfig, username, mensajes, sugerencias);
        validarWhiteSpace(activity, rulesConfig, username, mensajes, sugerencias);
        validarUppercase(activity, rulesConfig, username, mensajes, sugerencias);
        validarLowercase(activity, rulesConfig, username, mensajes, sugerencias);
        validarDigitNumber(activity, rulesConfig, username, mensajes, sugerencias);
        validarEspecialChar(activity, rulesConfig, username, mensajes, sugerencias);
//        validarUsernameRestrict(activity, rulesConfig, username, username, mensajes, sugerencias);
//        validarUsernameRestrict(activity, rulesConfig, username, username, mensajes, sugerencias);
        validarRepeatCharRestrict(activity, rulesConfig, username, mensajes, sugerencias);


        validationText.setVisibility(View.VISIBLE);
        if( mensajes.size()>0) {
            valid = addErrores(validationText, mensajes);
        }else{
            validationText.setTextColor(Color.BLACK);
            addSugerencias(activity, validationText, sugerencias,true);

        }
        if (SingletonServer.getInstance().isDeveloper()) return true;
        return valid;
    }

    public static boolean validatePassword(Activity activity, TextView validationText, PasswordRulesDTO rulesConfig, String username, String password, String passwordConfirmacion, TextView validationTextPasswordConfirmacion, String nickname,boolean register) {
        if (passwordConfirmacion == null || passwordConfirmacion.equals("")) {
            validationTextPasswordConfirmacion.setVisibility(View.VISIBLE);
            validationTextPasswordConfirmacion.setTextColor(Color.BLACK);
            validationTextPasswordConfirmacion.setText(activity.getString(R.string.register_activity__requerido));

        }
        if (password == null || password.equals("")) {
            validationText.setVisibility(View.VISIBLE);
            validationText.setTextColor(Color.BLACK);
            validationText.setText(activity.getString(R.string.register_activity__requerido));
            return false;

        }

        boolean valid=true;
        ArrayList<String> mensajes=new ArrayList<>();
        ArrayList<String> sugerencias=new ArrayList<>();
        //List<Rule> rules = new ArrayList<>();

        password=password.trim();
        validarLenght(activity, rulesConfig, password, mensajes, sugerencias);
        validarWhiteSpace(activity, rulesConfig, password, mensajes, sugerencias);
        validarUppercase(activity, rulesConfig, password, mensajes, sugerencias);
        validarLowercase(activity, rulesConfig, password, mensajes, sugerencias);
        validarDigitNumber(activity, rulesConfig, password, mensajes, sugerencias);
        validarEspecialChar(activity, rulesConfig, password, mensajes, sugerencias);
        if (nickname != null){
            validarNicknameRestrict(activity, rulesConfig, nickname, password, mensajes, sugerencias);
        }
        validarUsuarioRestrict(activity, rulesConfig, username, password, mensajes, sugerencias);
         validarRepeatCharRestrict(activity, rulesConfig, password, mensajes, sugerencias);


        validationText.setVisibility(View.VISIBLE);
        if( mensajes.size()>0) {
            validationText.setVisibility(View.VISIBLE);
            valid = addErrores(validationText, mensajes);
        }else{
            validationText.setVisibility(View.VISIBLE);
            validationText.setTextColor(Color.BLACK);
            validationText.setText(R.string.registro_validation_password1_ok);
            addSugerencias(activity, validationText, sugerencias,true);
            valid = validarPasswordConfirmacion(password, passwordConfirmacion, validationTextPasswordConfirmacion, valid);
        }
        if (SingletonServer.getInstance().isDeveloper()&&register) return true;
        return valid;
    }

    private static boolean addErrores(TextView validationText, ArrayList<String> mensajes) {
        boolean valid;
        valid=false;
        validationText.setTextColor(Color.RED);
        String errores="";
        for(String er : mensajes) {
            errores = errores + er + "\n";
        }
        validationText.setText(errores);
        return valid;
    }

    private static void addSugerencias(Activity activity, TextView validationText, ArrayList<String> sugerencias,boolean muySeguro) {
        if( sugerencias.size()>0) {
            validationText.setVisibility(View.VISIBLE);
            String errores = "";
            for (String er : sugerencias) {
                errores = errores + er + "\n";
            }
            errores = activity.getString(R.string.registro_validation_sugerencia) + "\n" + errores;
            validationText.setText(validationText.getText().toString() + "\n" +errores);
        }else{
            if(muySeguro){
                validationText.setVisibility(View.VISIBLE);
                validationText.setText(validationText.getText().toString() + "\n" +activity.getString(R.string.registro_validation_sugerencia_muy_seguro));
            }
            else {validationText.setVisibility(View.GONE);}
        }
    }

    private static boolean validarPasswordConfirmacion(String password, String passwordConfirmacion, TextView validationTextPasswordConfirmacion, boolean valid) {
        validationTextPasswordConfirmacion.setVisibility(View.VISIBLE);
        if (passwordConfirmacion.trim().equals("")) {
            valid = false;
            validationTextPasswordConfirmacion.setTextColor(Color.BLACK);
            validationTextPasswordConfirmacion.setText(R.string.register_activity__requerido);

        }else {
            if (!password.equals(passwordConfirmacion)) {
                valid = false;
                validationTextPasswordConfirmacion.setTextColor(Color.RED);
                validationTextPasswordConfirmacion.setText(R.string.registro_validation_password2);

            } else {
                validationTextPasswordConfirmacion.setTextColor(Color.BLACK);
                validationTextPasswordConfirmacion.setText(R.string.registro_validation_password2_ok);

            }
        }
        return valid;
    }

    private static void validarUppercase(Activity activity, PasswordRulesDTO rulesConfig, String password, ArrayList<String> mensajes, ArrayList<String> sugerencias) {

        if (rulesConfig.isUppercaseMandatoryEnabled()) {
            {
                PasswordValidator validator = new PasswordValidator(new CharacterRule(EnglishCharacterData.UpperCase, rulesConfig.getEspecialcharMandatoryQuantity()));
                boolean isValid = validator.validate(new PasswordData(password)).isValid();
                String quantity = rulesConfig.getEspecialcharMandatoryQuantity() + "";
                if (!isValid) {
                    if (rulesConfig.getUppercaseMandatoryQuantity() > 1) {
                        mensajes.add(activity.getString(R.string.registro_validation_password1_must_contains_mayusculas, quantity));
                    } else {
                        mensajes.add(activity.getString(R.string.registro_validation_password1_must_contains_mayuscula, quantity));
                    }

                }else{
                    PasswordValidator validator2 = new PasswordValidator(new CharacterRule(EnglishCharacterData.UpperCase, rulesConfig.getUppercaseSugerenciaQuantity()));
                    boolean isValid2 = validator2.validate(new PasswordData(password)).isValid();
                    String quantity2 = rulesConfig.getUppercaseSugerenciaQuantity() + "";

                    if (!isValid2) {
                        if (rulesConfig.getUppercaseSugerenciaQuantity() > 1) {
                            sugerencias.add(activity.getString(R.string.registro_validation_sugerencia_password1_must_contains_mayusculas, quantity2));
                        } else {
                            sugerencias.add(activity.getString(R.string.registro_validation_sugerencia_password1_must_contains_mayuscula, quantity2));
                        }
                    }

                }
            }
        }}

    private static void validarLenght(Activity activity, PasswordRulesDTO rulesConfig, String password, ArrayList<String> mensajes, ArrayList<String> sugerencias) {
        if (password == null || password.equals("")) {
            mensajes.add(activity.getString(R.string.registro_validation_password1_empty));
        }else {

            if (password.length() > rulesConfig.getLenghtMandatoryMax()) {
                mensajes.add(activity.getString(R.string.registro_validation_password1_too_long, rulesConfig.getLenghtMandatoryMax()+""));
            }

            if (password.length() < rulesConfig.getLenghtMandatoryMin()) {
                mensajes.add(activity.getString(R.string.registro_validation_password1_too_short, rulesConfig.getLenghtMandatoryMin()+""));
            }else{
                if ( rulesConfig.isLenghtSugerenciaMinEnabled() &&  password.length() < rulesConfig.getLenghtSugerenciaMin()) {
                    sugerencias.add(activity.getString(R.string.registro_validation_sugerencia_password1_too_short, rulesConfig.getLenghtSugerenciaMin()+""));
                }
            }

        }
    }

    private static void validarLowercase(Activity activity, PasswordRulesDTO rulesConfig, String password, ArrayList<String> mensajes, ArrayList<String> sugerencias) {

        if (rulesConfig.isLowercaseMandatoryEnabled()) {
            {
                PasswordValidator validator = new PasswordValidator(new CharacterRule(EnglishCharacterData.LowerCase, rulesConfig.getEspecialcharMandatoryQuantity()));
                boolean isValid = validator.validate(new PasswordData(password)).isValid();
                String quantity = rulesConfig.getEspecialcharMandatoryQuantity() + "";
                if (!isValid) {
                    if (rulesConfig.getLowercaseMandatoryQuantity() > 1) {
                        mensajes.add(activity.getString(R.string.registro_validation_password1_must_contains_minusculas, quantity));
                    } else {
                        mensajes.add(activity.getString(R.string.registro_validation_password1_must_contains_minuscula, quantity));
                    }

                }else{
                    PasswordValidator validator2 = new PasswordValidator(new CharacterRule(EnglishCharacterData.LowerCase, rulesConfig.getLowercaseSugerenciaQuantity()));
                    boolean isValid2 = validator2.validate(new PasswordData(password)).isValid();
                    String quantity2 = rulesConfig.getLowercaseSugerenciaQuantity() + "";

                    if (!isValid2) {
                        if (rulesConfig.getLowercaseSugerenciaQuantity() > 1) {
                            sugerencias.add(activity.getString(R.string.registro_validation_sugerencia_password1_must_contains_minusculas, quantity2));
                        } else {
                            sugerencias.add(activity.getString(R.string.registro_validation_sugerencia_password1_must_contains_minuscula, quantity2));
                        }
                    }

                }
            }
        }}

    private static void validarWhiteSpace(Activity activity, PasswordRulesDTO rulesConfig, String password, ArrayList<String> mensajes, ArrayList<String> sugerencias) {
        if (rulesConfig.isWhitespaceRestrictEnabled() && password.contains(" ")) {
            mensajes.add(activity.getString(R.string.registro_validation_password1_contains_white_space));
        }else{
            if (rulesConfig.isWhitespaceSugerenciaEnabled() && password.contains(" ")) {
                sugerencias.add(activity.getString(R.string.registro_validation_sugerencia_password1_contains_white_space));
            }

        }
    }

    private static void validarRepeatCharRestrict(Activity activity, PasswordRulesDTO rulesConfig, String password, ArrayList<String> mensajes, ArrayList<String> sugerencias) {

        PasswordValidator validator = new PasswordValidator(new RepeatCharacterRegexRule(rulesConfig.getRepeatCharRestrictQuantity()));
        boolean isValid = validator.validate(new PasswordData(password)).isValid();

        if (rulesConfig.isRepeatCharRestrictEnabled() && !isValid) {
            mensajes.add(activity.getString(R.string.registro_validation_password1_must_contains_ilegal_match,rulesConfig.getRepeatCharRestrictQuantity()+""));
        }else{
            if (rulesConfig.isRepeatCharSugerenciaEnabled() && isValid) {
                PasswordValidator validator2 = new PasswordValidator(new RepeatCharacterRegexRule(rulesConfig.getRepeatCharSugerenciaQuantity()));
                boolean isValid2 = validator2.validate(new PasswordData(password)).isValid();

                if (!isValid2)
                sugerencias.add(activity.getString(R.string.registro_validation_password1_must_contains_sugerencia_ilegal_match,rulesConfig.getRepeatCharSugerenciaQuantity()+""));
            }

        }
    }

    private static void validarDigitNumber(Activity activity, PasswordRulesDTO rulesConfig, String password, ArrayList<String> mensajes, ArrayList<String> sugerencias) {

        if (rulesConfig.isDigitNumberMandatoryEnabled()) {
            {
                PasswordValidator validator = new PasswordValidator(new CharacterRule(EnglishCharacterData.Digit, rulesConfig.getEspecialcharMandatoryQuantity()));
                boolean isValid = validator.validate(new PasswordData(password)).isValid();
                String quantity = rulesConfig.getEspecialcharMandatoryQuantity() + "";
                if (!isValid) {
                    if (rulesConfig.getDigitNumberMandatoryQuantity() > 1) {
                        mensajes.add(activity.getString(R.string.registro_validation_password1_must_contains_numeros, quantity));
                    } else {
                        mensajes.add(activity.getString(R.string.registro_validation_password1_must_contains_numero, quantity));
                    }

                }else{
                    PasswordValidator validator2 = new PasswordValidator(new CharacterRule(EnglishCharacterData.Digit, rulesConfig.getDigitNumberSugerenciaQuantity()));
                    boolean isValid2 = validator2.validate(new PasswordData(password)).isValid();
                    String quantity2 = rulesConfig.getDigitNumberSugerenciaQuantity() + "";

                    if (!isValid2) {
                        if (rulesConfig.getDigitNumberSugerenciaQuantity() > 1) {
                            sugerencias.add(activity.getString(R.string.registro_validation_sugerencia_password1_must_contains_numeros, quantity2));
                        } else {
                            sugerencias.add(activity.getString(R.string.registro_validation_sugerencia_password1_must_contains_numero, quantity2));
                        }
                    }

                }
            }
        }}

    private static void validarPasswordRestrict(Activity activity, PasswordRulesDTO rulesConfig, String password, String nickname, ArrayList<String> mensajes, ArrayList<String> sugerencias) {
        if(rulesConfig.isUsernameRestrictEnabled()) {

            PasswordValidator passwordValidator = new PasswordValidator(new UsernameRule());

            PasswordData passwordData = new PasswordData(nickname);
            passwordData.setUsername(password);

            RuleResult validate = passwordValidator.validate(passwordData);
            if (!validate.isValid()) {
                mensajes.add(activity.getString(R.string.registro_validation_password1_contains_password));
            }
        }else{
            if (rulesConfig.isUsernameSugerenciaEnabled()) {
                PasswordValidator passwordValidator = new PasswordValidator(new UsernameRule());

                PasswordData passwordData = new PasswordData(nickname);
                passwordData.setUsername(password);

                RuleResult validate = passwordValidator.validate(passwordData);
                if (!validate.isValid()) {
                    sugerencias.add(activity.getString(R.string.registro_validation_password1_contains_sugerencia_password));
                }
            }

        }



    }

    private static void validarUsuarioRestrict(Activity activity, PasswordRulesDTO rulesConfig, String usuario, String contrasenia, ArrayList<String> mensajes, ArrayList<String> sugerencias) {
        if(rulesConfig.isUsernameRestrictEnabled()) {

            PasswordValidator passwordValidator = new PasswordValidator(new UsernameRule());

            PasswordData passwordData = new PasswordData(contrasenia);
            passwordData.setUsername(usuario);

            RuleResult validate = passwordValidator.validate(passwordData);
            if (!validate.isValid()) {
                mensajes.add(activity.getString(R.string.registro_validation_password1_contains_usuario));
            }
        }else{
            if (rulesConfig.isUsernameSugerenciaEnabled()) {
                PasswordValidator passwordValidator = new PasswordValidator(new UsernameRule());

                PasswordData passwordData = new PasswordData(contrasenia);
                passwordData.setUsername(usuario);

                RuleResult validate = passwordValidator.validate(passwordData);
                if (!validate.isValid()) {
                    sugerencias.add(activity.getString(R.string.registro_validation_password1_contains_sugerencia_usuario));
                }
            }

        }



    }

    private static void validarNicknameRestrict(Activity activity, PasswordRulesDTO rulesConfig, String nickname, String password, ArrayList<String> mensajes, ArrayList<String> sugerencias) {
        if(rulesConfig.isNicknameRestrictEnabled()) {

            PasswordValidator passwordValidator = new PasswordValidator(new UsernameRule());

            PasswordData passwordData = new PasswordData(password);
            passwordData.setUsername(nickname);

            RuleResult validate = passwordValidator.validate(passwordData);
            if (!validate.isValid()) {
                mensajes.add(activity.getString(R.string.registro_validation_password1_contains_nickname));
            }
        }else{
            if (rulesConfig.isNicknameSugerenciaEnabled()) {
                PasswordValidator passwordValidator = new PasswordValidator(new UsernameRule());

                PasswordData passwordData = new PasswordData(password);
                passwordData.setUsername(nickname);

                RuleResult validate = passwordValidator.validate(passwordData);
                if (!validate.isValid()) {
                    sugerencias.add(activity.getString(R.string.registro_validation_password1_contains_sugerencia_nickname));
                }
            }

        }



    }

    private static void validarEspecialChar(Activity activity, PasswordRulesDTO rulesConfig, String password, ArrayList<String> mensajes, ArrayList<String> sugerencias) {

        if (rulesConfig.isEspecialcharMandatoryEnabled()) {
            {
                PasswordValidator validator = new PasswordValidator(new CharacterRule(EnglishCharacterData.Special, rulesConfig.getEspecialcharMandatoryQuantity()));
                boolean isValid = validator.validate(new PasswordData(password)).isValid();
                String quantity = rulesConfig.getEspecialcharMandatoryQuantity() + "";
                if (!isValid) {
                    if (rulesConfig.getEspecialcharMandatoryQuantity() > 1) {
                        mensajes.add(activity.getString(R.string.registro_validation_password1_must_contains_specials, quantity));
                    } else {
                        mensajes.add(activity.getString(R.string.registro_validation_password1_must_contains_special, quantity));
                    }

            }else{
                PasswordValidator validator2 = new PasswordValidator(new CharacterRule(EnglishCharacterData.Special, rulesConfig.getEspecialcharSugerenciaQuantity()));
                boolean isValid2 = validator2.validate(new PasswordData(password)).isValid();
                String quantity2 = rulesConfig.getEspecialcharSugerenciaQuantity() + "";

                if (!isValid2) {
                    if (rulesConfig.getEspecialcharSugerenciaQuantity() > 1) {
                        sugerencias.add(activity.getString(R.string.registro_validation_sugerencia_password1_must_contains_specials, quantity2));
                    } else {
                        sugerencias.add(activity.getString(R.string.registro_validation_sugerencia_password1_must_contains_special, quantity2));
                    }
                }

            }
        }
    }}}
