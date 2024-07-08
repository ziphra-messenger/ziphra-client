package com.privacity.cliente.encrypt;



import com.privacity.cliente.util.GsonFormated;

import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import lombok.Data;

@Data
public class AEStoUse {

	private Cipher decrypt;
	private Cipher encrypt;

	private String secretKeyAES;
	private String saltAES;
	private int iteration;
	private int bits;

	public AEStoUse(String secretKeyAES, String saltAES, int iteration, int bits) throws Exception {
		System.out.println("AEStoUse: "
				+ " secretKeyAES- " + secretKeyAES
		+ " saltAES- " + saltAES
				+ " iteration- " + iteration
				+ " bits- " + bits);
		this.secretKeyAES=secretKeyAES;
		this.saltAES=saltAES;
		this.iteration=iteration;
		this.bits=bits;



		{
			byte[] iv = new byte[16];
			IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
			SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
			KeySpec keySpec = new PBEKeySpec((secretKeyAES).toCharArray(), (saltAES).getBytes(), iteration, bits);
			SecretKey secretKeyTemp = secretKeyFactory.generateSecret(keySpec);
			SecretKeySpec secretKey = new SecretKeySpec(secretKeyTemp.getEncoded(), "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
			decrypt = cipher;
			
		}
		{
			byte[] iv = new byte[16];
			IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
			SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
			KeySpec keySpec = new PBEKeySpec((secretKeyAES).toCharArray(), (saltAES).getBytes(), iteration, bits);
			SecretKey secretKeyTemp = secretKeyFactory.generateSecret(keySpec);
			SecretKeySpec secretKey = new SecretKeySpec(secretKeyTemp.getEncoded(), "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
			encrypt=cipher;
		}
	}



	public String getAES(String data) {
		System.out.println("getAES data: " + data);
		try {
			if (data == null) return null;
			return Base64.getEncoder().encodeToString(encrypt.doFinal(data.getBytes()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getAESDecrypt(String data) throws Exception {

		System.out.println("Entrada getAESDecrypt : ///" + data + "///");
		if (data == null) return null;
		//if (data == "") return null;
		String r;
		if (data.charAt(data.length() - 1) == '"'){
			r = new String(decrypt.doFinal(Base64.getDecoder().decode(GsonFormated.get().fromJson(data,String.class).getBytes(("UTF-8")))));
		}else{
			byte[] b = Base64.getDecoder().decode(data.getBytes("UTF-8" ));
			System.out.println("byy: ///" + new String(b) + "///");
			r = new String(decrypt.doFinal(b));
		}

		System.out.println("Salida getAESDecrypt : ///" + r + "///");
		return r;


	}
	public String method(String str) {
		if (str != null && str.length() > 0 && str.charAt(str.length() - 1) == '\"') {
			str = str.substring(0, str.length() - 1);
		}
		return str;
	}

	public byte[] getAESDecrypt(byte[] data) throws Exception {

		//return data;
		if (data == null) return null;

		return decrypt.doFinal(data);
		//System.out.println("Entrada: " + data + " Salida: " + r);



	}

	public byte[] getAES(byte[] data)  {

		try {
//return data;
			return encrypt.doFinal(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;




	}

}    

