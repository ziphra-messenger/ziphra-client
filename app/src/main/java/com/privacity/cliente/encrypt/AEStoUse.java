package com.privacity.cliente.encrypt;



import com.privacity.cliente.singleton.UtilsStringSingleton;
import com.privacity.common.exceptions.PrivacityException;
import com.privacity.common.interfaces.AESToUseInterface;
import com.privacity.common.util.UtilsStringAbstract;

import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;


public class AEStoUse  implements AESToUseInterface{


	private final Cipher decrypt;
	private final Cipher encrypt;
//
//	private final String secretKeyAES;
//	private final String saltAES;
//	private final int iteration;
//	private final int bits;

	//private AESDTO aesdto;

	public AEStoUse(String secretKeyAES, String saltAES, int iteration, int bits) throws Exception {
//		System.out.println("AEStoUse: "
//				+ " secretKeyAES- " + secretKeyAES
//		+ " saltAES- " + saltAES
//				+ " iteration- " + iteration
//				+ " bits- " + bits);
//		this.secretKeyAES=secretKeyAES;
//		this.saltAES=saltAES;
//		this.iteration=iteration;
//		this.bits=bits;

	//	this.aesdto.setBitsEncrypt(bits+"").setIteration(iteration+"").setSecretKeyAES(secretKeyAES).setSaltAES(saltAES);


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

	public String getAES(byte[] data) {
		try {
			return getAES(UtilsStringSingleton.getInstance().convertBytesToString(data));
		} catch (PrivacityException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getAES(String data) {
		//System.out.println("getAES data: " + data);
		try {
			if (data == null) return null;
			return Base64.getEncoder().encodeToString(encrypt.doFinal(data.getBytes()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}



	public String getAESDecrypt(byte[] data) throws Exception {
		return getAESDecrypt(
		UtilsStringSingleton.getInstance().convertBytesToString(data));

	}

	public String getAESDecrypt(String data) throws Exception {

		//System.out.println("Entrada getAESDecrypt : ///" + data + "///");
		if (data == null) return null;
		//if (data == "") return null;
		String r;
		if (data.charAt(data.length() - 1) == '"'){
			r = new String(decrypt.doFinal(Base64.getDecoder().decode(
					UtilsStringSingleton.getInstance().gson().fromJson(data,String.class).getBytes((UtilsStringAbstract.CONSTANT__DEFAULT_CHARSET)))));
		}else{
			byte[] b = Base64.getDecoder().decode(data.getBytes(UtilsStringAbstract.CONSTANT__DEFAULT_CHARSET ));
			//System.out.println("byy: ///" + shrinkString(new String(b)) + "///");
			r = new String(decrypt.doFinal(b));
		}

		//System.out.println("Salida getAESDecrypt : ///" + r + "///");
		//System.out.println("Salida getAESDecrypt : ///" + r + "///");
		return r;


	}


	private static final int MAX_LOG = 2000;
	private String shrinkString(String s) {
		String r = s.replace("\n", "").replace("\t", "").replace("\r", "").replace("\b", "").replace("\f", "")
				.replace("  ", " ").replace("  ", " ").replace("  ", " ").replace(", -" , "").replace(", " , "");

		return r.substring(0, (r.length()> MAX_LOG)? MAX_LOG : r.length()-1);
	}
	public String method(String str) {
		if (str != null && str.length() > 0 && str.charAt(str.length() - 1) == '\"') {
			str = str.substring(0, str.length() - 1);
		}
		return str;
	}

	public byte[] getAESDecryptData(byte[] data) throws Exception {

		//return data;
		if (data == null) return null;

		return decrypt.doFinal(data);
		//System.out.println("Entrada: " + data + " Salida: " + r);



	}

	public byte[] getAESData(byte[] data)  {

		try {
//return data;
			return encrypt.doFinal(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;




	}

}    

