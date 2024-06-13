package com.privacity.cliente.encrypt;

public class GrupoEncryptToUse {
    private AEStoUse aestoUse;

    public AEStoUse getAestoUse() {
        return aestoUse;
    }

    public void setAestoUse(AEStoUse aestoUse) {
        this.aestoUse = aestoUse;
    }

    public EncryptKeysToUse getEncryptKeysToUse() {
        return encryptKeysToUse;
    }

    public void setEncryptKeysToUse(EncryptKeysToUse encryptKeysToUse) {
        this.encryptKeysToUse = encryptKeysToUse;
    }

    private EncryptKeysToUse encryptKeysToUse;
}
