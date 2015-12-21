package com.azweb.scheellarsen.cache;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.gson.Gson;
import com.azweb.scheellarsen.models.Article;
import com.azweb.scheellarsen.models.Categories;
import com.azweb.scheellarsen.models.Guide;
import com.azweb.scheellarsen.models.Video;

public class DbManager {
    private static final String FD_PREFERENCE = "fd_cache";
    private static DbManager dbManager;
    private static SharedPreferences sharedpreferences;
    private Context mContext;

    private DbManager(Context context) {
        mContext = context;
    }

    public static synchronized DbManager getInstance(Context context) {

        if (dbManager == null) {
            dbManager = new DbManager(context);
            sharedpreferences = context.getSharedPreferences(FD_PREFERENCE,
                    Context.MODE_PRIVATE);
        }
        return dbManager;
    }

    public void saveArticle(String strArticle) {
        Editor editor = sharedpreferences.edit();
        editor.putString("data_article", strArticle);
        editor.putLong("time_article_current",System.currentTimeMillis());
        editor.commit();
    }

    public Object getArticle() {
        if (System.currentTimeMillis() - sharedpreferences.getLong("time_article_current",0) < 1200000) {
            String data = sharedpreferences.getString("data_article", null);
            if (data != null) {
                Gson gson = new Gson();
                return gson.fromJson(data, Article.class);
            } else {
                return null;
            }
        }
        return null;
    }
    public void saveListCategory(String strCategory) {
        Editor editor = sharedpreferences.edit();
        editor.putString("data_category", strCategory);
        editor.putLong("time_list_category_current",System.currentTimeMillis());
        editor.commit();
    }
    public Object[] getListCategory() {
        if (System.currentTimeMillis() - sharedpreferences.getLong("time_list_category_current",0) < 1200000) {
            String data = sharedpreferences.getString("data_category", null);
            if (data != null) {
                Gson gson = new Gson();
                return gson.fromJson(data, Categories[].class);
            } else {
                return null;
            }
        }
        return null;
    }
    public void saveVideos(String strVideo) {
        Editor editor = sharedpreferences.edit();
        editor.putString("data_video", strVideo);
        editor.putLong("time_video_current",System.currentTimeMillis());
        editor.commit();
    }
    public Object[] getVideos() {
        if (System.currentTimeMillis() - sharedpreferences.getLong("time_video_current",0) < 1800000) {
            String data = sharedpreferences.getString("data_video", null);
            if (data != null) {
                Gson gson = new Gson();
                return gson.fromJson(data, Video[].class);
            } else {
                return null;
            }
        }
        return null;
    }
    public void saveGuide(String strVideo) {
        Editor editor = sharedpreferences.edit();
        editor.putString("data_guide", strVideo);
        editor.putLong("time_guide_current",System.currentTimeMillis());
        editor.commit();
    }
    public Object getGuide() {
        if (System.currentTimeMillis() - sharedpreferences.getLong("time_guide_current",0) < 1800000) {
            String data = sharedpreferences.getString("data_guide", null);
            if (data != null) {
                Gson gson = new Gson();
                return gson.fromJson(data, Guide.class);
            } else {
                return null;
            }
        }
        return null;
    }
    public void saveInfomation(String strVideo) {
        Editor editor = sharedpreferences.edit();
        editor.putString("data_infomation", strVideo);
        editor.putLong("time_infomation_current",System.currentTimeMillis());
        editor.commit();
    }
    public Object getInfomation() {
        if (System.currentTimeMillis() - sharedpreferences.getLong("time_infomation_current",0) < 1800000) {
            String data = sharedpreferences.getString("data_infomation", null);
            if (data != null) {
                Gson gson = new Gson();
                return gson.fromJson(data, Guide.class);
            } else {
                return null;
            }
        }
        return null;
    }
}
