package com.wugz.app.utils;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Set;


public class HttpAccessServletUtil {
	
	public static void main(String[] args) {
		System.out.println(httpURLConntJson("http://data.stats.gov.cn/easyquery.htm?cn=C01", ""));
	}
	public static void getHttpResponse() throws Exception {
		URL url = new URL("http://data.stats.gov.cn/easyquery.htm?cn=C01");
		URLConnection conn = url.openConnection();
		Map headers = conn.getHeaderFields();
		Set<String> keys = headers.keySet();
		for( String key : keys ){
		String val = conn.getHeaderField(key);
		System.out.println(key+"    "+val);
		System.out.println( conn.getLastModified() );
		}
	}
	
	
	
	/**
	  * @ClassName(类名)      : httpURLConnection
	  * @Description(描述)    : 发送HTTP请求
	  * @author(作者)         ：
	  * @date (开发日期)      ：2016-3-30 下午2:15:54
	 */	
	public static  String httpURLConnection(String url, String param) {
	    PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)"); 
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setConnectTimeout(1000*60);
            conn.setReadTimeout(1000*60);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(),"utf-8"));
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
   }
	
	/**
	  * @ClassName(类名)      : httpURLConnection
	  * @Description(描述)    : 发送HTTP请求
	  * @author(作者)         ：
	  * @date (开发日期)      ：2016-3-30 下午2:15:54
	 */	
	public static  String httpURLConnt(String URIP, String param) {
	   StringBuffer sb = new StringBuffer();
	   HttpURLConnection httpURLConnection=null;
	   InputStream iin=null;
      try {
			URL url = new URL(URIP);
			httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setDoInput(true);
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setConnectTimeout(1000*60);
			httpURLConnection.setReadTimeout(1000*60);
			httpURLConnection.setRequestProperty("content-type",
					"text/xml; charset=UTF-8");
			httpURLConnection.getOutputStream().write(param.getBytes("utf-8"));
			httpURLConnection.getOutputStream().flush();
			httpURLConnection.getOutputStream().close();

			httpURLConnection.connect();
			iin = httpURLConnection.getInputStream();
			byte[] buffer = new byte[2048];
			int length = 0;

			while ((length = iin.read(buffer, 0, buffer.length)) != -1) {
				sb.append(new String(buffer, 0, length, "utf-8"));
			}
			System.out.println("[==repMsg==]" + sb.toString());         
      } catch (Exception e) {
          System.out.println("发送 POST 请求出现异常！"+e);
          e.printStackTrace();
      }finally{
          try{
              if(iin!=null){
           	   iin.close();
              }
          }
          catch(IOException ex){
              ex.printStackTrace();
          }
      }
      return sb.toString();
  }	
	/**
	  * @ClassName(类名)      : HttpAccessServletUtil
	  * @Description(描述)    : 发送HTTP请求
	  * @author(作者)         ：
	  * @date (开发日期)      ：2016-3-30 下午2:15:54
	 */	
	public static  String sendPostHttl(String url, String param) {	
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setConnectTimeout(1000*60);
            conn.setReadTimeout(1000*60);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }
	
	/**
	  * @ClassName(类名)      : handleResponse
	  * @Description(描述)    : 发送HTTP请求,返回请求报文
	  * @author(作者)         ：
	  * @date (开发日期)      ：2016-3-30 下午2:15:54
	 */
	public static void handleResponse(HttpServletRequest request,
			HttpServletResponse response, String retMsg, String encoding) {
		try {
			response.setCharacterEncoding(encoding);
			response.setContentType("text/xml; charset=" + encoding);		
			PrintWriter writer = response.getWriter();
			writer.write(retMsg);
		} catch (Exception ee) {	
           System.out.println("Failed to handle response!"); 
		}
	}
	
	/**
	 * 
	 * @Description(功能描述)    :  发送Get请求
	 * @author(作者)             ：  
	 * @date (开发日期)          :  2016-6-22 下午2:10:13 
	 */
	public static String sendGet(String url, String param) {  
        String result = "";  
        BufferedReader in = null;  
        try {  
            String urlName = url + "?" + param;  
            URL realUrl = new URL(urlName);  
            // 打开和URL之间的连接  
            URLConnection conn = realUrl.openConnection();  
            // 设置通用的请求属性  
            conn.setRequestProperty("accept", "*/*");  
            conn.setRequestProperty("connection", "Keep-Alive");  
            conn.setRequestProperty("user-agent",  
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");  
            // 建立实际的连接  
            conn.connect();  
//            // 获取所有响应头字段  
//            Map<String, List<String>> map = conn.getHeaderFields();  
//            // 遍历所有的响应头字段  
//            for (String key : map.keySet()) {  
//                System.out.println(key + "--->" + map.get(key));  
//            }  
            // 定义BufferedReader输入流来读取URL的响应  
            in = new BufferedReader(  
                    new InputStreamReader(conn.getInputStream()));
            String line;  
            while ((line = in.readLine()) != null) {  
                result += line;  
            }  
        } catch (Exception e) {  
            System.out.println("发送GET请求出现异常！" + e);  
        }  
        // 使用finally块来关闭输入流  
        finally {  
            try {  
                if (in != null) {  
                    in.close();  
                }  
            } catch (IOException ex) {  
                ex.printStackTrace();  
            }  
        }  
        return result;  
    }  
	

	/**
	  * @ClassName(类名)      : httpURLConnection
	  * @Description(描述)    : 发送HTTP请求
	  * @author(作者)         ：
	  * @date (开发日期)      ：2016-3-30 下午2:15:54
	 */	
	public static  String httpURLConntJson(String URIP, String param) {
	   StringBuffer sb = new StringBuffer();
	   HttpURLConnection httpURLConnection=null;
	   InputStream iin=null;
      try {
			URL url = new URL(URIP);
			httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setDoInput(true);
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setConnectTimeout(1000*10);
			httpURLConnection.setReadTimeout(1000*10);
			httpURLConnection.setRequestProperty("content-type",
					"application/json; charset=UTF-8");
			httpURLConnection.getOutputStream().write(param.getBytes("utf-8"));
			httpURLConnection.getOutputStream().flush();
			httpURLConnection.getOutputStream().close();

			httpURLConnection.connect();
			iin = httpURLConnection.getInputStream();
			byte[] buffer = new byte[2048];
			int length = 0;

			while ((length = iin.read(buffer, 0, buffer.length)) != -1) {
				sb.append(new String(buffer, 0, length, "utf-8"));
			}
      } catch (Exception e) {
          System.out.println("发送 POST 请求出现异常！"+e);
          e.printStackTrace();
      }finally{
          try{
              if(iin!=null){
           	   iin.close();
              }
          }
          catch(IOException ex){
              ex.printStackTrace();
          }
      }
      return sb.toString();
  }
}
