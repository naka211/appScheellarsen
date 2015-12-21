package com.azweb.scheellarsen.fragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.azweb.scheellarsen.CameraActivity;
import com.azweb.scheellarsen.R;

/**
 * Created by Thaind on 11/18/2015.
 */
public class ShareFragmentDialog extends DialogFragment {
    private CameraActivity.ShareFacebookListener mListener;

    public static ShareFragmentDialog newInstance() {
        ShareFragmentDialog frag = new ShareFragmentDialog();
        return frag;
    }

    public void setData(CameraActivity.ShareFacebookListener listener){
        mListener = listener;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.share_facebook_dialog, container);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        TextView shareButton = (TextView) view.findViewById(R.id.dialog_ok);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.accept();
                dismiss();
            }
        });
        TextView cancelBtn = (TextView) view.findViewById(R.id.dialog_cancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }

}
