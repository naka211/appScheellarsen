package com.azweb.scheellarsen.fragments;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.azweb.scheellarsen.R;
import com.azweb.scheellarsen.apis.DataTransferHandler;
import com.azweb.scheellarsen.apis.Response;
import com.azweb.scheellarsen.models.Guide;

/**
 * Created by Thaind on 11/5/2015.
 */
public class TabInfomationFragment extends BaseContainerFragment {
    private View mView;
    private Guide mGuide;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_tab_infomation_layout, null);

        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState == null){
            mView.findViewById(R.id.progressbar).setVisibility(View.VISIBLE);
            Guide.getInstance().getInfomation(getActivity(), false, handler);
        }else{
            mGuide = savedInstanceState.getParcelable("data_infomation");
            setUI();
        }
        mView.findViewById(R.id.reload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mView.findViewById(R.id.reload).setVisibility(View.GONE);
                mView.findViewById(R.id.progressbar).setVisibility(View.VISIBLE);
                Guide.getInstance().getInfomation(getActivity(), true, handler);
            }
        });
    }
    private void setUI(){
        ((TextView)mView.findViewById(R.id.tv_infomation_title)).setText(mGuide.getTitle().toUpperCase());
        ((TextView)mView.findViewById(R.id.tv_infomation_content)).setText(Html.fromHtml(mGuide.getContent()));
    }
    private DataTransferHandler handler = new DataTransferHandler() {
        @Override
        public void onDataTransferResultHandler(Response response) {
            mView.findViewById(R.id.progressbar).setVisibility(View.GONE);
            if (response.getObject() != null) {
                mGuide = (Guide) response.getObject();
                setUI();
            }else{
                mView.findViewById(R.id.reload).setVisibility(View.VISIBLE);
            }

        }

        @Override
        public void onError(String messageError) {
            mView.findViewById(R.id.progressbar).setVisibility(View.GONE);
            mView.findViewById(R.id.reload).setVisibility(View.VISIBLE);
        }
    };
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("data_infomation", mGuide);
    }
}
