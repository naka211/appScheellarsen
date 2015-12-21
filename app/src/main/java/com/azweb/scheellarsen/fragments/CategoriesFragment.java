package com.azweb.scheellarsen.fragments;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.azweb.library.scheellarsen.FragmentTransactionExtended;
import com.azweb.scheellarsen.MainActivity;
import com.azweb.scheellarsen.R;
import com.azweb.scheellarsen.adapters.CategoriesProductAdapter;
import com.azweb.scheellarsen.apis.DataTransferHandler;
import com.azweb.scheellarsen.apis.Response;
import com.azweb.scheellarsen.apis.RestClient;
import com.azweb.scheellarsen.models.Categories;
import com.azweb.scheellarsen.widgets.DividerItemDecoration;

import java.util.ArrayList;

/**
 * Created by Thaind on 11/6/2015.
 */
public class CategoriesFragment extends BaseFragment{
    private MainActivity mActivity;
    private CategoriesProductAdapter mAdapter;
    public static ProductFragment mProductFragment;
    private ArrayList<Categories> listData;
    private View mView;
    private RecyclerView mRecyclerView;
    private int cateId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_tab_product_layout, container, false);
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.recycler);
        listData = new ArrayList<>();
        return mView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cateId = getArguments().getInt("catId");
        mActivity = (MainActivity) getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        mActivity.mBtnBack.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (restoreStateFromArguments("category") == null) {
            setAdapter();
            mView.findViewById(R.id.progressbar).setVisibility(View.VISIBLE);
            Categories.getInstance().getListCategory(getActivity(), cateId, false, handler);
        }else{
            Bundle extras = restoreStateFromArguments("category");
            cateId = extras.getInt("catid");
            listData = extras.getParcelableArrayList("list_category");
            setAdapter();
        }

        mAdapter.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Categories categories = listData.get((Integer) v.getTag());
                Bundle extras = new Bundle();
                extras.putInt("catId", categories.getId());
                mProductFragment = new ProductFragment();
                mProductFragment.setArguments(extras);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                FragmentTransactionExtended fragmentTransactionExtended = new FragmentTransactionExtended(getActivity(), fragmentTransaction, TabProductFragment.mParentFragment, mProductFragment, R.id.container_framelayout);
                fragmentTransactionExtended.addTransition(7);
                fragmentTransactionExtended.commit();
            }
        });

        ((SwipeRefreshLayout) mView.findViewById(R.id.swipe_refresh)).setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                listData.clear();
                Categories.getInstance().getListCategory(getActivity(), cateId, true, handler);
            }
        });
        mView.findViewById(R.id.reload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listData.clear();
                mView.findViewById(R.id.reload).setVisibility(View.GONE);
                mView.findViewById(R.id.progressbar).setVisibility(View.VISIBLE);
                Categories.getInstance().getListCategory(getActivity(), cateId, false, handler);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Bundle outState = new Bundle();
        saveInstance(outState);
        RestClient.cancelRequest(getActivity());
        CategoriesProductAdapter.AnimateFirstDisplayListener.displayedImages.clear();
    }

    private void saveInstance(Bundle outState) {
        outState.putParcelableArrayList("list_category", listData);
        outState.putInt("catid", cateId);
        saveStateToArguments(outState,"category");
    }

    private DataTransferHandler handler = new DataTransferHandler() {
        @Override
        public void onDataTransferResultHandler(Response response) {
            mView.findViewById(R.id.progressbar).setVisibility(View.GONE);
            ((SwipeRefreshLayout) mView.findViewById(R.id.swipe_refresh)).setRefreshing(false);
            if (response.getObject() != null) {
                listData.addAll((ArrayList<Categories>) response.getObject());
                mAdapter.notifyDataSetChanged();
            }
            if (listData.size() == 0) {
                mView.findViewById(R.id.reload).setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onError(String messageError) {
            mView.findViewById(R.id.progressbar).setVisibility(View.GONE);
            ((SwipeRefreshLayout) mView.findViewById(R.id.swipe_refresh)).setRefreshing(false);
            mView.findViewById(R.id.reload).setVisibility(View.VISIBLE);
        }
    };

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveInstance(outState);
    }
    private void setAdapter() {
        mAdapter = new CategoriesProductAdapter(getActivity(), listData);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
    }


}
