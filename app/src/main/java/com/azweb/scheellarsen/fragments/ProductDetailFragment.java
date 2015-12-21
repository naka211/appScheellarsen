package com.azweb.scheellarsen.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.azweb.scheellarsen.CameraActivity;
import com.azweb.scheellarsen.MainActivity;
import com.azweb.scheellarsen.R;
import com.azweb.scheellarsen.apis.DataTransferHandler;
import com.azweb.scheellarsen.apis.Response;
import com.azweb.scheellarsen.apis.RestClient;
import com.azweb.scheellarsen.models.ProductDetail;
import com.azweb.scheellarsen.widgets.SquareImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.ByteArrayOutputStream;

/**
 * Created by Thaind on 11/10/2015.
 */
public class ProductDetailFragment extends BaseFragment implements View.OnClickListener {
    private MainActivity mActivity;
    private RelativeLayout mBtnCamera;
    private ProductDetail mDetail;
    private int productId;
    private View mView;
    private DisplayImageOptions options;
    private SquareImageView mImageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_detail_product_layout, container, false);
        mBtnCamera = (RelativeLayout) mView.findViewById(R.id.btn_camera);
        mImageView = (SquareImageView) mView.findViewById(R.id.iv_icon_product);
        mBtnCamera.setOnClickListener(this);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_stub)
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(R.drawable.ic_error)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();
        return mView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        productId = getArguments().getInt("productId");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (restoreStateFromArguments("detail") == null) {
            mView.findViewById(R.id.progressbar).setVisibility(View.VISIBLE);
            ProductDetail.getInstance().getProductDetail(mActivity, productId, handler);
        } else {
            Bundle extras = restoreStateFromArguments("detail");
            mDetail = extras.getParcelable("product_detail");
            productId = extras.getInt("productId");
            setView();
        }
        mView.findViewById(R.id.reload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mView.findViewById(R.id.reload).setVisibility(View.GONE);
                mView.findViewById(R.id.progressbar).setVisibility(View.VISIBLE);
                ProductDetail.getInstance().getProductDetail(mActivity, productId, handler);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Bundle outState = new Bundle();
        saveInstance(outState);
        RestClient.cancelRequest(getActivity());
    }

    private void saveInstance(Bundle outState) {
        outState.putParcelable("product_detail", mDetail);
        outState.putInt("productId", productId);
        saveStateToArguments(outState, "detail");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveInstance(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mActivity.mBtnBack.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_camera:
                mImageView.buildDrawingCache();
                Bitmap image = mImageView.getDrawingCache();
                image = getResizedBitmap(image,382,510);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                Intent intent = new Intent(mActivity, CameraActivity.class);
                intent.putExtra("image", byteArray);
                startActivity(intent);
                mImageView.destroyDrawingCache();
                break;
        }
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {

        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);

        return resizedBitmap;

    }

    private DataTransferHandler handler = new DataTransferHandler() {
        @Override
        public void onDataTransferResultHandler(Response response) {
            mView.findViewById(R.id.progressbar).setVisibility(View.GONE);
            if (response.getObject() != null) {
                mDetail = (ProductDetail) response.getObject();
                setView();
            } else {
                mView.findViewById(R.id.reload).setVisibility(View.VISIBLE);
            }

        }

        @Override
        public void onError(String messageError) {
            mView.findViewById(R.id.progressbar).setVisibility(View.GONE);
            mView.findViewById(R.id.reload).setVisibility(View.VISIBLE);
        }
    };

    private void setView() {
        ((TextView) mView.findViewById(R.id.tv_product_name)).setText(mDetail.getName().toUpperCase());
        ((TextView) mView.findViewById(R.id.tv_categories_name)).setText(mDetail.getSku());
        ((TextView) mView.findViewById(R.id.tv_price_buy)).setText(mDetail.getPrice());
        ((TextView) mView.findViewById(R.id.tv_impresstion_content)).setText(Html.fromHtml(mDetail.getDesc()));
        ImageLoader.getInstance().displayImage(mDetail.getImage(), mImageView, options);
    }

}
