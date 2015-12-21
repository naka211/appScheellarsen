package com.azweb.scheellarsen.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.azweb.scheellarsen.R;
import com.azweb.scheellarsen.models.Product;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Thaind on 11/9/2015.
 */
public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Product> mListData;
    private Context mContext;
    private DisplayImageOptions options;
    private AnimateFirstDisplayListener animateFirstListener;
    private View.OnClickListener mOnClickListener;
    public ProductAdapter(Context context,ArrayList<Product> listData){
        this.mContext = context;
        this.mListData = listData;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_stub)
                .showImageForEmptyUri(R.mipmap.ic_empty)
                .showImageOnFail(R.mipmap.ic_error)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();
        animateFirstListener = new AnimateFirstDisplayListener();
    }
    public void setOnItemClickListener(View.OnClickListener onItemClickListener){
        this.mOnClickListener = onItemClickListener;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.fragment_product_item_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Product product = mListData.get(position);
        ViewHolder v = (ViewHolder) holder;
        v.name.setText(product.getName().toUpperCase());
        v.sku.setText(product.getSku());
        v.price.setText(product.getPrice());
        ImageLoader.getInstance().displayImage(product.getImage(), v.icon, options, animateFirstListener);
        v.layout.setTag(position);
        v.layout.setOnClickListener(mOnClickListener);
    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView icon;
        private TextView name;
        private TextView price;
        private TextView sku;
        private RelativeLayout layout;

        public ViewHolder(View itemView) {
            super(itemView);
            this.icon = (ImageView) itemView.findViewById(R.id.iv_icon_product);
            this.name = (TextView) itemView.findViewById(R.id.tv_product_name);
            this.price = (TextView) itemView.findViewById(R.id.tv_price_buy);
            this.sku = (TextView) itemView.findViewById(R.id.tv_categories_name);
            this.layout = (RelativeLayout) itemView.findViewById(R.id.item_layout);

        }
    }

    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {
        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }
}
