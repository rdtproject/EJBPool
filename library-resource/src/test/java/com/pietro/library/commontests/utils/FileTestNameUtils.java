package com.pietro.library.commontests.utils;

import org.junit.Ignore;

@Ignore
public class FileTestNameUtils {
	private static final String PATH_REQUEST = "/request/";
	private static final String PATH_RESPONSE = "/response/";
	
	private FileTestNameUtils() {		
	}
	
	public static String getPathFileRequest(String mainFolder, String fileName) {
		return mainFolder + PATH_REQUEST + fileName;
	}
	
	public static String getPathFileResponse(String mainFolder, String fileName) {
		return mainFolder + PATH_RESPONSE + fileName;
	}
	
}
