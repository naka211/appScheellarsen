package com.azweb.scheellarsen.models;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.azweb.scheellarsen.R;
import com.azweb.scheellarsen.apis.DataTransferHandler;
import com.azweb.scheellarsen.apis.ServerConfig;
import com.azweb.scheellarsen.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Thaind on 12/10/2015.
 */
public class Product extends BaseData implements Parcelable {
    private int id;
    private String sku;
    private String name;
    private String price;
    private String image;
    private static Product _instance;

    private Product() {
    }

    public synchronized static Product getInstance() {
        if (_instance == null) {
            _instance = new Product();
        }
        return _instance;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public static Parcelable.Creator<Product> getCreator() {
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
        dest.writeString(price);
        dest.writeString(sku);
    }

    public Product(Parcel in) {
        id = in.readInt();
        name = in.readString();
        image = in.readString();
        price = in.readString();
        sku = in.readString();
    }

    public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>() {
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
    public void getProduct(final Context context, final int catId,final int page, final boolean stateRefesh, final DataTransferHandler dataTransferHandler) {
        boolean isNetwork = Utils.isOnline(context);
        if (isNetwork) {
            RequestParams rq = new RequestParams();
            rq.put("catid", catId);
            rq.put("page", page);
            setParam(rq, ServerConfig.API_PRODUCT);
            requestData(context, "post", new OnResult() {
                @Override
                public Object onResultSuccess(JSONObject jsResult) {
                    try {
                        String data = jsResult.getString("data");
                        Gson gson = new Gson();
                        Product[] products = gson.fromJson(data, Product[].class);

                        return new ArrayList<>(Arrays.asList(products));
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
