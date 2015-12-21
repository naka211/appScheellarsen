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

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Thaind on 12/14/2015.
 */
public class Video extends BaseData implements Parcelable {
    private int id;
    private String title;
    private String content;
    private String urls;
    private String videoId;

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    private static Video _instance;

    private Video() {
    }

    public synchronized static Video getInstance() {
        if (_instance == null) {
            _instance = new Video();
        }
        return _instance;
    }

    public String getUrls() {
        return urls;
    }

    public void setUrls(String urls) {
        this.urls = urls;
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

    public static Parcelable.Creator<Video> getCreator() {
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
        dest.writeString(urls);
        dest.writeString(videoId);
    }

    public Video(Parcel in) {
        id = in.readInt();
        title = in.readString();
        content = in.readString();
        urls = in.readString();
        videoId = in.readString();
    }

    public static final Parcelable.Creator<Video> CREATOR = new Parcelable.Creator<Video>() {
        public Video createFromParcel(Parcel in) {
            return new Video(in);
        }

        public Video[] newArray(int size) {
            return new Video[size];
        }
    };

    public void getVideos(final Context context, final DataTransferHandler dataTransferHandler) {
        boolean isNetwork = Utils.isOnline(context);
        if (isNetwork) {
            if (DbManager.getInstance(context).getVideos() != null) {
                Object[] videos = DbManager.getInstance(context).getVideos();
                dataTransferHandler.onDataTransferResultHandler(new Response(true, null, new ArrayList<>(Arrays.asList(videos)), ServerConfig.API_VIDEOS));
                return;
            }
            RequestParams rq = new RequestParams();
            setParam(rq, ServerConfig.API_VIDEOS);
            requestData(context, "post", new OnResult() {
                @Override
                public Object onResultSuccess(JSONObject jsResult) {
                    try {
                        String data = jsResult.getString("data");
                        DbManager.getInstance(context).saveVideos(data);
                        Gson gson = new Gson();
                        Video[] videos = gson.fromJson(data, Video[].class);
                        return new ArrayList<>(Arrays.asList(videos));
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
