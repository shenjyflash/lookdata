package com.digitalchina.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class HttpUtil {

	
	public static List<NameValuePair> getNameValue(Map<String, String> params){
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		for(String key : params.keySet()){
			formparams.add(new BasicNameValuePair(key, params.get(key)));
		}
		return formparams;
	}
	/**
	 * formparams.add(new BasicNameValuePair("type", "house"));
	 * @param postUurl
	 * @param formparams  参数队列  
	 */
	public static String post(String postUurl,List<NameValuePair> formparams) {  
        // 创建默认的httpClient实例.    
        CloseableHttpClient httpclient = HttpClients.createDefault();  
        // 创建httppost    
        HttpPost httppost = new HttpPost(postUurl);  
        UrlEncodedFormEntity uefEntity;
        String ret = null;
        HttpEntity entity = null;
        try {
            uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");  
            httppost.setEntity(uefEntity);  
            System.out.println("executing request " + httppost.getURI());  
            CloseableHttpResponse response = httpclient.execute(httppost);  
            entity = response.getEntity(); 
            if (entity != null) {
                System.out.println("--------------------------------------");  
                ret = EntityUtils.toString(entity, "UTF-8");
                System.out.println("Response content: " + ret);  
                System.out.println("--------------------------------------");  
            }
        } catch (ClientProtocolException e) {  
            e.printStackTrace();  
        } catch (UnsupportedEncodingException e1) {  
            e1.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            // 关闭连接,释放资源    
            try {  
                httpclient.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }
		return ret ;
    }  
	

	
	/**
	 * 超时时间 .
	 */
	private static int SOCKET_TIMEOUT = 120000;
	/**
	 * 连接超时时间 .
	 */
	private static int CONNECT_TIMEOUT = 120000;
	
	public static CloseableHttpResponse sendToOtherServer(String url,Map<String,String> dataMap){
		
		CloseableHttpClient client =  HttpClients.custom().build();
		RequestConfig config = RequestConfig.custom()
				.setSocketTimeout(SOCKET_TIMEOUT)
				.setConnectTimeout(CONNECT_TIMEOUT)
				.setAuthenticationEnabled(false)
				.build();
		
		
		HttpPost post = new HttpPost(url);
		post.setProtocolVersion(org.apache.http.HttpVersion.HTTP_1_1);
		post.setConfig(config);
		
		HttpEntity entity = null;
		List<NameValuePair> formpair = new ArrayList<NameValuePair>();
		{
			for (String str : dataMap.keySet().toArray(new String[dataMap.size()])) {
				formpair.add(new BasicNameValuePair(str,dataMap.get(str).toString()));
			}
		}

		entity = new UrlEncodedFormEntity(formpair, Consts.UTF_8);
		if (entity != null) {
			post.setEntity(entity);
		}
		try {
			return client.execute(post);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
