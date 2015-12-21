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

/**
 * Created by Thaind on 12/11/2015.
 */
public class ProductDetail extends BaseData implements Parcelable {
    private int id;
    private String sku;
    private String name;
    private String price;
    private String desc;
    private String image;
    private static ProductDetail _instance;
    private ProductDetail() {
    }

    public synchronized static ProductDetail getInstance() {
        if (_instance == null) {
            _instance = new ProductDetail();
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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
    public static Parcelable.Creator<ProductDetail> getCreator() {
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
        dest.writeString(desc);
    }

    public ProductDetail(Parcel in) {
        id = in.readInt();
        name = in.readString();
        image = in.readString();
        price = in.readString();
        sku = in.readString();
        desc = in.readString();
    }

    public static final Parcelable.Creator<ProductDetail> CREATOR = new Parcelable.Creator<ProductDetail>() {
        public ProductDetail createFromParcel(Parcel in) {
            return new ProductDetail(in);
        }

        public ProductDetail[] newArray(int size) {
            return new ProductDetail[size];
        }
    };
    public void getProductDetail(final Context context, final int productId,final DataTransferHandler dataTransferHandler){
        boolean isNetwork = Utils.isOnline(context);
        if (isNetwork) {
            RequestParams rq = new RequestParams();
            rq.put("id", productId);
            setParam(rq, ServerConfig.API_PRODUCT_DETAIL);
            requestData(context, "post", new OnResult() {
                @Override
                public Object onResultSuccess(JSONObject jsResult) {
                    try {
                        String data = jsResult.getString("data");
                        Gson gson = new Gson();
                        ProductDetail products = gson.fromJson(data, ProductDetail.class);
                        return products;
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
