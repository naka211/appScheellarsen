package com.azweb.scheellarsen.fragments;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.azweb.library.scheellarsen.FragmentTransactionExtended;
import com.azweb.scheellarsen.MainActivity;
import com.azweb.scheellarsen.R;
import com.azweb.scheellarsen.apis.DataTransferHandler;
import com.azweb.scheellarsen.apis.Response;
import com.azweb.scheellarsen.models.Article;
import com.azweb.scheellarsen.widgets.RectangleImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

/**
 * Created by Thaind on 11/5/2015.
 */
public class TabHomeFragment extends BaseContainerFragment implements View.OnClickListener {


    private MainActivity mActivity;
    private Article mArticle;
    private View mView;
    private RectangleImageView mImageView;
    private DisplayImageOptions options;
    private static InstructionFragment fragment;
    private BackListener backListener = new BackListener() {
        @Override
        public void onBack() {
            mActivity.mBtnBack.setVisibility(View.GONE);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_tab_home_layout, null);
        mImageView = (RectangleImageView) mView.findViewById(R.id.imageView);
        mView.findViewById(R.id.btn_infomation).setOnClickListener(this);
        mView.findViewById(R.id.btn_using_app).setOnClickListener(this);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.banner)
                .showImageForEmptyUri(R.mipmap.banner)
                .showImageOnFail(R.mipmap.banner)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .displayer(new RoundedBitmapDisplayer(10))
                .considerExifParams(true)
                .build();
        return mView;
    }
    @Override
    public void onResume() {
        super.onResume();
        mActivity.mBtnBack.setVisibility(View.GONE);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState == null) {
            mView.findViewById(R.id.progressbar).setVisibility(View.VISIBLE);
            Article.getInstance().getArticle(mActivity, handler);
        } else {
            mArticle = savedInstanceState.getParcelable("data_arctile");
            ImageLoader.getInstance().displayImage(mArticle.getImage(), mImageView, options);
            ((TextView) mView.findViewById(R.id.tv_abount_title)).setText(mArticle.getTitle());
            ((TextView) mView.findViewById(R.id.tv_abount_content)).setText(Html.fromHtml(mArticle.getContent()));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("data_arctile", mArticle);
    }

    private DataTransferHandler handler = new DataTransferHandler() {
        @Override
        public void onDataTransferResultHandler(Response response) {
            mView.findViewById(R.id.progressbar).setVisibility(View.GONE);
            if (response.getObject() != null) {
                mArticle = (Article) response.getObject();
                ImageLoader.getInstance().displayImage(mArticle.getImage(), mImageView, options);
                ((TextView) mView.findViewById(R.id.tv_abount_title)).setText(mArticle.getTitle().toUpperCase());
                ((TextView) mView.findViewById(R.id.tv_abount_content)).setText(Html.fromHtml(mArticle.getContent()));
            }
        }

        @Override
        public void onError(String messageError) {
            mView.findViewById(R.id.progressbar).setVisibility(View.GONE);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_using_app:
                fragment = new InstructionFragment();
                fragment.setData(backListener);
                FragmentTransaction fragmentTransaction = mActivity.getFragmentManager().beginTransaction();
                FragmentTransactionExtended fragmentTransactionExtended = new FragmentTransactionExtended(getActivity(), fragmentTransaction, TabHomeFragment.fragment, fragment, R.id.container_framelayout);
                fragmentTransactionExtended.addTransition(7);
                fragmentTransactionExtended.commit();
                break;
            case R.id.btn_infomation:
                String url = "http://www.scheel-larsen.dk/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                break;
        }
    }
    public interface BackListener{
        public void onBack();
    }
}
