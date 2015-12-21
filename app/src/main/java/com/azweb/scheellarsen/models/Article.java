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
 * Created by Thaind on 12/8/2015.
 */
public class Article extends BaseData implements Parcelable {
    private String title;
    private String content;
    private String image;
    private static Article _instance;

    private Article() {
    }

    public synchronized static Article getInstance() {
        if (_instance == null) {
            _instance = new Article();
        }
        return _instance;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public static Parcelable.Creator<Article> getCreator() {
        return CREATOR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(image);
    }

    public Article(Parcel in) {
        title = in.readString();
        content = in.readString();
        image = in.readString();
    }

    public static final Parcelable.Creator<Article> CREATOR = new Parcelable.Creator<Article>() {
        public Article createFromParcel(Parcel in) {
            return new Article(in);
        }

        public Article[] newArray(int size) {
            return new Article[size];
        }
    };

    public void getArticle(final Context context, DataTransferHandler dataTransferHandler) {
        boolean isNetwork = Utils.isOnline(context);
        if (isNetwork) {
            if (DbManager.getInstance(context).getArticle() != null){
                Object article = DbManager.getInstance(context).getArticle();
                dataTransferHandler.onDataTransferResultHandler(new Response(true,null,article,ServerConfig.API_ARTICLE));
                return;
            }
            RequestParams rq = new RequestParams();
            setParam(rq, ServerConfig.API_ARTICLE);
            requestData(context, "post", new OnResult() {
                @Override
                public Object onResultSuccess(JSONObject jsResult) {
                    try {
                        String data = jsResult.getString("data");
                        DbManager.getInstance(context).saveArticle(data);
                        Gson gson = new Gson();
                        Article article = gson.fromJson(data, Article.class);
                        return article;
                    } catch (JSONException e) {

                    }
                    return null;
                }
            }, dataTransferHandler);
        }else{
            dataTransferHandler.onError(context.getString(R.string.title_network_error));
        }
    }
}
