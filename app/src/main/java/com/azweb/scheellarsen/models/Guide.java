package com.azweb.scheellarsen.models;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.azweb.scheellarsen.R;
import com.azweb.scheellarsen.apis.DataTransferHandler;
import com.azweb.scheellarsen.apis.Response;
import com.azweb.scheellarsen.apis.ServerConfig;
import com.azweb.scheellarsen.cache.DbManager;
import com.azweb.scheellarsen.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Thaind on 12/15/2015.
 */
public class Guide extends BaseData implements Parcelable {
    private int id;
    private String title;
    private String content;
    private static Guide _instance;

    private Guide() {
    }

    public synchronized static Guide getInstance() {
        if (_instance == null) {
            _instance = new Guide();
        }
        return _instance;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public static Parcelable.Creator<Guide> getCreator() {
        return CREATOR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(content);
    }

    public Guide(Parcel in) {
        id = in.readInt();
        title = in.readString();
        content = in.readString();
    }

    public static final Parcelable.Creator<Guide> CREATOR = new Parcelable.Creator<Guide>() {
        public Guide createFromParcel(Parcel in) {
            return new Guide(in);
        }

        public Guide[] newArray(int size) {
            return new Guide[size];
        }
    };

    public void getGuide(final Context context,final boolean refersh, final DataTransferHandler dataTransferHandler) {
        boolean isNetwork = Utils.isOnline(context);
        if (isNetwork) {
            if (DbManager.getInstance(context).getGuide() != null && !refersh) {
                Object guide = DbManager.getInstance(context).getGuide();
                dataTransferHandler.onDataTransferResultHandler(new Response(true, null, guide, ServerConfig.API_GUIDE));
                return;
            }
            RequestParams rq = new RequestParams();
            setParam(rq, ServerConfig.API_GUIDE);
            requestData(context, "post", new OnResult() {
                @Override
                public Object onResultSuccess(JSONObject jsResult) {
                    try {
                        String data = jsResult.getString("data");
                        DbManager.getInstance(context).saveGuide(data);
                        Gson gson = new Gson();
                        Guide guide = gson.fromJson(data, Guide.class);
                        return guide;
                    } catch (JSONException e) {

                    }
                    return null;
                }
            }, dataTransferHandler);
        } else {
            dataTransferHandler.onError(context.getString(R.string.title_network_error));
        }
    }
    public void getInfomation(final Context context,final boolean refersh, final DataTransferHandler dataTransferHandler) {
        boolean isNetwork = Utils.isOnline(context);
        if (isNetwork) {
            if (DbManager.getInstance(context).getInfomation() != null && !refersh) {
                Object guide = DbManager.getInstance(context).getInfomation();
                dataTransferHandler.onDataTransferResultHandler(new Response(true, null, guide, ServerConfig.API_INFOMATION));
                return;
            }
            RequestParams rq = new RequestParams();
            setParam(rq, ServerConfig.API_INFOMATION);
            requestData(context, "post", new OnResult() {
                @Override
                public Object onResultSuccess(JSONObject jsResult) {
                    try {
                        String data = jsResult.getString("data");
                        DbManager.getInstance(context).saveInfomation(data);
                        Gson gson = new Gson();
                        Guide guide = gson.fromJson(data, Guide.class);
                        return guide;
                    } catch (JSONException e) {

                    }
                    return null;
                }
            }, dataTransferHandler);
        } else {
            dataTransferHandler.onError(context.getString(R.string.title_network_error));
        }
    }
}
