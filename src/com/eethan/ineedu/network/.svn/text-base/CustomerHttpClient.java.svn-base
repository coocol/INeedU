package com.eethan.ineedu.network;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.eethan.ineedu.constant.Constant;
import com.eethan.ineedu.mycontrol.MyToast;
import com.eethan.ineedu.primaryactivity.MainActivity;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class CustomerHttpClient {
    private static final String CHARSET = HTTP.UTF_8;
    private static HttpClient customerHttpClient;
    private static final String TAG = "CustomerHttpClient";
    
    private CustomerHttpClient() {
    }
    public static synchronized HttpClient getHttpClient() {
        if (null == customerHttpClient) {
            HttpParams params = new BasicHttpParams();
            // 设置一些基本参数
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params,
                    CHARSET);
            HttpProtocolParams.setUseExpectContinue(params, true);
            HttpProtocolParams
                    .setUserAgent(
                            params,
                            "HttpComponents/1.1");
            // 超时设置
            /* 从连接池中取连接的超时时间 */
            ConnManagerParams.setTimeout(params, 100000);
            /* 连接超时 */
            HttpConnectionParams.setConnectionTimeout(params, 20000);
            /* 请求超时 */
            HttpConnectionParams.setSoTimeout(params, 20000);
         
            // 设置我们的HttpClient支持HTTP和HTTPS两种模式
            SchemeRegistry schReg = new SchemeRegistry();
            schReg.register(new Scheme("http", PlainSocketFactory
                    .getSocketFactory(), 8080));
            schReg.register(new Scheme("https", SSLSocketFactory
                    .getSocketFactory(), 443));

            // 使用线程安全的连接管理来创建HttpClient
            ClientConnectionManager conMgr = new ThreadSafeClientConnManager(
                    params, schReg);
            customerHttpClient = new DefaultHttpClient(conMgr, params);
        }
        return customerHttpClient;
    }
    
    public static String post(String url, NameValuePair... params) {
        try {
        	Log.d("ineedu", url);
        	
            // 编码参数
            List<NameValuePair> formparams = new ArrayList<NameValuePair>(); // 请求参数
            for (NameValuePair p : params) {
                formparams.add(p);
            }
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams,
                    CHARSET);
            // 创建POST请求
            HttpPost request = new HttpPost(url);
            request.setEntity(entity);
            // 发送请求
            HttpClient client = getHttpClient();
            HttpResponse response = client.execute(request);
            if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
               // throw new RuntimeException("请求失败");
            		Log.i("NET","Http请求失败");
            }
            HttpEntity resEntity =  response.getEntity();
            
            return (resEntity == null) ? null : EntityUtils.toString(resEntity, CHARSET);
        } catch (ConnectTimeoutException cte) {
    			Log.i("NET","服务器连接超时!");
            cte.printStackTrace();
            return Constant.ConnectTimeoutException;
        } catch (SocketTimeoutException ste) {
    			Log.i("NET","服务器请求超时!");
            ste.printStackTrace();
            return Constant.SocketTimeoutException;
        }catch (HttpHostConnectException e) {
        		Log.i("NET","服务器连接失败,请检查网络后再试!");
            e.printStackTrace();
            return Constant.HttpHostConnectException;
		} catch (ConnectException e) {
			Log.i("NET","服务器连接失败,请检查网络后再试!");
            e.printStackTrace();
            return Constant.ConnectException;
		}catch (UnsupportedEncodingException e) {
            Log.w(TAG, e.getMessage());
            return null;
        }catch (ClientProtocolException e) {
            Log.w(TAG, e.getMessage());
            return null;
        } catch (IOException e) {
        	e.printStackTrace();
        	return null;
            //throw new RuntimeException("连接失败", e);
        }

    }
    
     public static String post(String url, Context context, NameValuePair... params) {
	    try {
	    	Log.d("ineedu", url);
    	
        // 编码参数
        List<NameValuePair> formparams = new ArrayList<NameValuePair>(); // 请求参数
        for (NameValuePair p : params) {
            formparams.add(p);
        }
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams,
                CHARSET);
        // 创建POST请求
        HttpPost request = new HttpPost(url);
        request.setEntity(entity);
        // 发送请求
        HttpClient client = getHttpClient();
        HttpResponse response = client.execute(request);
        if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
           // throw new RuntimeException("请求失败");
        	MyToast.showToast(context, "请求失败");
        }
        HttpEntity resEntity =  response.getEntity();
        
        return (resEntity == null) ? null : EntityUtils.toString(resEntity, CHARSET);
    } catch (UnsupportedEncodingException e) {
        Log.w(TAG, e.getMessage());
        return null;
    }  catch (ConnectTimeoutException cte) {
    		MyToast.showToast(context, "连接服务器超时,请稍后再试!");
        cte.printStackTrace();
        return null;
    } catch (SocketTimeoutException ste) {
    		MyToast.showToast(context, "获取数据超时,请稍后再试!");
            ste.printStackTrace();
            return null;
    }catch (ClientProtocolException e) {
        Log.w(TAG, e.getMessage());
        return null;
    } catch (IOException e) {
        	e.printStackTrace();
        	return null;
        //throw new RuntimeException("连接失败", e);
    }
     }
}
    

