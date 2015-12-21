package com.azweb.scheellarsen.fragments;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.azweb.scheellarsen.R;

/**
 * Created by Thaind on 11/5/2015.
 */
public class TabProductFragment extends BaseContainerFragment {

    private FragmentManager mFragmentManager;
    public static CategoriesParentFragment mParentFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_product_container_layout, null);
        if (savedInstanceState == null) {
            mParentFragment = new CategoriesParentFragment();
            mFragmentManager = getActivity().getFragmentManager();
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            ft.replace(R.id.container_framelayout, mParentFragment);
            ft.commit();
        }
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
}
