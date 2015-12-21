package com.azweb.scheellarsen.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.azweb.scheellarsen.MainActivity;
import com.azweb.scheellarsen.R;

/**
 * Created by Thaind on 12/15/2015.
 */
public class InstructionFragment extends BaseFragment{
    private View mView;
    private MainActivity mActivity;
    private WebView mWebView;
    private TabHomeFragment.BackListener backListener;
    public void setData(TabHomeFragment.BackListener listener){
        backListener = listener;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_instruction_layout, container, false);
        mWebView = (WebView) mView.findViewById(R.id.webview);
        return mView;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        mActivity.mBtnBack.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        backListener.onBack();
    }
}
