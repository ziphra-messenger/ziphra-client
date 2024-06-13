package com.privacity.cliente.encrypt;

import java.security.PrivateKey;
import java.security.PublicKey;

import lombok.Data;

@Data
public class EncryptKeysToUse {
    private RSA rsa;
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private PublicKey publicKeyNoEncrypt;
}
