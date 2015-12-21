package com.azweb.scheellarsen.apis;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import com.loopj.android.http.RequestParams;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;


public class RestClient {

    private static AsyncHttpClient client = null;

    private static void configForClient(){
        if(client == null){
            client = new AsyncHttpClient();
            try {
                KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                trustStore.load(null, null);
                MySSLSocketFactory socketFactory = new MySSLSocketFactory(trustStore);
                socketFactory.setHostnameVerifier(MySSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
                client.setSSLSocketFactory(socketFactory);
            } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException | KeyManagementException | UnrecoverableKeyException e) {
                e.printStackTrace();
            }
            client.setResponseTimeout(60000);
        }
    }

    public static void post(Context context,String type,String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        configForClient();
        if(type.equals("post"))
            client.post(context,getAbsoluteUrl(url), params, responseHandler);
        else
            client.get(context,getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String url){
        return ServerConfig.BASE_URL+url;
    }

    public static void cancelRequest(Context context){
        if(client != null){
            client.cancelRequests(context, true);
        }
    }

}
