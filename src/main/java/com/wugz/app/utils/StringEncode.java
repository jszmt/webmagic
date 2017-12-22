package com.wugz.app.utils;

import java.io.UnsupportedEncodingException;

public class StringEncode {
	
	public static String toUTF8(String param) {  
        if (param == null) {  
            return null;  
        } else {  
            try {  
                param = new String(param.getBytes("ISO-8859-15"), "UTF-8");  
            } catch (UnsupportedEncodingException e) {  
                e.printStackTrace();  
                return param;  
            }  
        }  
        return param;  
    }  
	
	public static void main(String[] args) throws UnsupportedEncodingException {
		String myFileName = "ç»¼åˆ";
		System.out.println(new String(myFileName.getBytes("gbk")));   
		System.out.println(new String(myFileName.getBytes("utf-8"),"gbk"));
		System.out.println(new String(myFileName.getBytes("iso-8859-1"),"gbk")); 
		System.out.println(new String(myFileName.getBytes("gb2312"), "gbk"));

		System.out.println(new String(myFileName.getBytes("utf-8")));   
		System.out.println(new String(myFileName.getBytes("gbk"),"utf-8"));
		System.out.println(new String(myFileName.getBytes("iso-8859-15"),"utf-8")); 
		System.out.println(new String(myFileName.getBytes("gb2312"), "utf-8"));

		System.out.println(new String(myFileName.getBytes("iso-8859-1")));   
		System.out.println(new String(myFileName.getBytes("gbk"),"iso-8859-1"));
		System.out.println(new String(myFileName.getBytes("utf-8"),"iso-8859-1")); 
		System.out.println(new String(myFileName.getBytes("gb2312"), "iso-8859-1"));

		System.out.println(new String(myFileName.getBytes("gb2312")));   
		System.out.println(new String(myFileName.getBytes("gbk"),"gb2312"));
		System.out.println(new String(myFileName.getBytes("utf-8"),"gb2312")); 
		System.out.println(new String(myFileName.getBytes("iso-8859-1"), "gb2312"));
	}
}
