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
 * Created by Thaind on 11/6/2015.
 */
public class Categories extends BaseData implements Parcelable {
    private int id;
    private String name;
    private String image;
    private static Categories _instance;

    private Categories() {
    }

    public synchronized static Categories getInstance() {
        if (_instance == null) {
            _instance = new Categories();
        }
        return _instance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public static Parcelable.Creator<Categories> getCreator() {
        return CREATOR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(image);
    }

    public Categories(Parcel in) {
        id = in.readInt();
        name = in.readString();
        image = in.readString();
    }

    public static final Parcelable.Creator<Categories> CREATOR = new Parcelable.Creator<Categories>() {
        public Categories createFromParcel(Parcel in) {
            return new Categories(in);
        }

        public Categories[] newArray(int size) {
            return new Categories[size];
        }
    };

    public void getListCategory(final Context context, final int catId, final boolean stateRefesh, final DataTransferHandler dataTransferHandler) {
        boolean isNetwork = Utils.isOnline(context);
        if (isNetwork) {
            if (DbManager.getInstance(context).getListCategory() != null && !stateRefesh && catId == 0) {
                Object[] category = DbManager.getInstance(context).getListCategory();
                dataTransferHandler.onDataTransferResultHandler(new Response(true, null, new ArrayList<>(Arrays.asList(category)), ServerConfig.API_ARTICLE));
                return;
            }
            RequestParams rq = new RequestParams();
            rq.put("catid", catId);
            setParam(rq, ServerConfig.API_CATEGORY);
            requestData(context, "post", new OnResult() {
                @Override
                public Object onResultSuccess(JSONObject jsResult) {
                    try {
                        String data = jsResult.getString("data");
                        if (catId == 0)
                            DbManager.getInstance(context).saveListCategory(data);
                        Gson gson = new Gson();
                        Categories[] categories = gson.fromJson(data, Categories[].class);

                        return new ArrayList<>(Arrays.asList(categories));
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
