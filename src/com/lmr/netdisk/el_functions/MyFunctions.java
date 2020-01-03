package com.lmr.netdisk.el_functions;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class MyFunctions {
	
	public static String decodeURI(String uri) throws UnsupportedEncodingException {
		return URLDecoder.decode(uri, "utf-8");
	}
	public static String encodeURI(String uri) throws UnsupportedEncodingException {
		return URLEncoder.encode(uri, "utf-8");
	}
}
