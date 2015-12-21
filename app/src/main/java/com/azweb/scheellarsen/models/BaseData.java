package com.azweb.scheellarsen.models;


import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.loopj.android.http.BuildConfig;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.azweb.scheellarsen.apis.DataTransferHandler;
import com.azweb.scheellarsen.apis.Response;
import com.azweb.scheellarsen.apis.RestClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class BaseData {

    private RequestParams param;
    private String TAG = "REQUEST DATA";
    private boolean DEBUG = true;

    protected void requestData(Context context, String type, final OnResult onResultSuccess, final DataTransferHandler dataHandler) {
        RestClient.post(context, type, TAG, param, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (DEBUG)
                    Log.i("REQUEST HTTP", TAG + " :" + response.toString());
                try {

                    if (response.getString("result").equals("1")) {
                        Object obj = onResultSuccess.onResultSuccess(response);
                        if (dataHandler != null)
                            dataHandler.onDataTransferResultHandler(new Response(true, null, obj,TAG));
                    } else {
                        if (dataHandler != null)
                            dataHandler.onDataTransferResultHandler(new Response(false, response.getString("error"), null, TAG));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (dataHandler != null)
                        dataHandler.onDataTransferResultHandler(new Response(false, "Can't get data", null, TAG));
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  Throwable throwable, JSONObject errorResponse) {
                if (DEBUG && errorResponse != null)
                    Log.e("REQUEST HTTP", TAG + " :" + errorResponse.toString());
                if (dataHandler != null)
                    dataHandler.onDataTransferResultHandler(new Response(false, "Can't get data", null, TAG));
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  String responseString, Throwable throwable) {
                if (DEBUG)
                    Log.e("REQUEST HTTP", TAG + " :" + responseString);
                if (dataHandler != null)
                    dataHandler.onDataTransferResultHandler(new Response(false, "Can't get data", null, TAG));
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  Throwable throwable, JSONArray errorResponse) {
                if (DEBUG)
                    Log.e("REQUEST HTTP", TAG + " :" + errorResponse.toString());
                if (dataHandler != null)
                    dataHandler.onDataTransferResultHandler(new Response(false, "Can't get data", null, TAG));
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

        });
    }

    protected void requestData(Context context, String type, final Object dataObject, final DataTransferHandler dataHandler) {
        RestClient.post(context, type, TAG, param, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (DEBUG)
                    Log.i("REQUEST HTTP", TAG + " :" + response.toString());
                try {
                    if (response.getString("result").equals("1")) {
                        Gson gson = new Gson();
                        Object obj = gson.fromJson(response.toString(), dataObject.getClass());
                        if (dataHandler != null)
                            dataHandler.onDataTransferResultHandler(new Response(true, null, obj, TAG));
                    } else {
                        if (dataHandler != null)
                            dataHandler.onDataTransferResultHandler(new Response(false, response.getString("error"), null, TAG));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (dataHandler != null)
                        dataHandler.onDataTransferResultHandler(new Response(false, "Can't get data", null, TAG));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  Throwable throwable, JSONObject errorResponse) {
                if (DEBUG)
                    Log.e("REQUEST HTTP", TAG + " :" + errorResponse.toString());
                if (dataHandler != null)
                    dataHandler.onDataTransferResultHandler(new Response(false, "Can't get data", null, TAG));
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  String responseString, Throwable throwable) {
                if (DEBUG)
                    Log.e("REQUEST HTTP", TAG + " :" + responseString);
                if (dataHandler != null)
                    dataHandler.onDataTransferResultHandler(new Response(false, "Can't get data", null, TAG));
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  Throwable throwable, JSONArray errorResponse) {
                if (DEBUG)
                    Log.e("REQUEST HTTP", TAG + " :" + errorResponse.toString());
                if (dataHandler != null)
                    dataHandler.onDataTransferResultHandler(new Response(false, "Can't get data", null, TAG));
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

        });
    }

    /**
     * @param requestParam request parameters
     * @param tag          : request URL
     */
    protected void setParam(RequestParams requestParam, String tag) {
        if (requestParam == null)
            this.param = new RequestParams();
        else
            this.param = requestParam;
        this.TAG = tag;
    }

    public interface OnResult {
        Object onResultSuccess(JSONObject jsResult);
    }

//    protected void onSuccess() {}
//    protected void onFailure() {}


    protected static String avoidMissingKey(JSONObject jo, String key) {
        try {
            return jo.getString(key);
        } catch (Exception e) {
            if (BuildConfig.DEBUG)
                e.printStackTrace();
            return null;
        }
    }

}
