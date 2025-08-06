package com.privacity.cliente.util;

import com.privacity.common.util.UtilsStringAbstract;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import lombok.Data;



@Data
public class ZipUtilService {

	public static byte[] compress(String data) {
		byte[] compressed=null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length());
			GZIPOutputStream gzip = new GZIPOutputStream(bos);
			gzip.write(data.getBytes());
			gzip.close();
			compressed = bos.toByteArray();
		
			bos.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//throw new ProcessException( ExceptionReturnCode.ZIP_COMPRESS);
		}
		return compressed;
	}
	
	public static String decompress(byte[] compressed)  {
		StringBuilder sb=null;
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(compressed);
			GZIPInputStream gis = new GZIPInputStream(bis);
			BufferedReader br = new BufferedReader(new InputStreamReader(gis, UtilsStringAbstract.CONSTANT__DEFAULT_CHARSET));
			sb = new StringBuilder();
			String line;
			while((line = br.readLine()) != null) {
				sb.append(line);
			}
			br.close();
			gis.close();
			bis.close();
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//throw new ProcessException( ExceptionReturnCode.ZIP_DESCOMPRESS);
		}
		return sb.toString();
	}
}