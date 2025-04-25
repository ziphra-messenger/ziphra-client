package com.privacity.cliente.activity;

import org.springframework.core.io.ByteArrayResource;

import java.io.IOException;

public class MultipartFileResource extends ByteArrayResource {

	private final String filename;

	public MultipartFileResource(String multipartFile, String filename) throws IOException {
		super(multipartFile.getBytes());
		this.filename = "filename";
	}
	public MultipartFileResource(byte[] multipartFile, String filename) throws IOException {
		super(multipartFile);
		this.filename = "filename";
	}
	@Override
	public String getFilename() {
		return this.filename;
	}
}