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
import com.azweb.scheellarsen.adapters.ProductAdapter;
import com.azweb.scheellarsen.apis.DataTransferHandler;
import com.azweb.scheellarsen.apis.Response;
import com.azweb.scheellarsen.apis.RestClient;
import com.azweb.scheellarsen.listener.EndlessRecyclerOnScrollListener;
import com.azweb.scheellarsen.models.Product;

import java.util.ArrayList;

/**
 * Created by Thaind on 11/9/2015.
 */
public class ProductFragment extends BaseFragment {
    private MainActivity mActivity;
    private ProductAdapter mAdapter;
    private ProductDetailFragment mProductDetailFragment;
    private ArrayList<Product> listData;
    private View mView;
    private RecyclerView mRecyclerView;
    private int cateId;
    private int mPage = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_product_layout, container, false);
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (restoreStateFromArguments("product") == null) {
            setAdapter();
            mView.findViewById(R.id.progressbar).setVisibility(View.VISIBLE);
            Product.getInstance().getProduct(getActivity(), cateId, mPage, false, handler);
        } else {
            Bundle extras = restoreStateFromArguments("product");
            cateId = extras.getInt("catid");
            listData = extras.getParcelableArrayList("list_product");
            setAdapter();
        }
        ((SwipeRefreshLayout) mView.findViewById(R.id.swipe_refresh)).setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                listData.clear();
                Product.getInstance().getProduct(getActivity(), cateId, mPage, true, handler);
            }
        });
        mView.findViewById(R.id.reload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listData.clear();
                mView.findViewById(R.id.reload).setVisibility(View.GONE);
                mView.findViewById(R.id.progressbar).setVisibility(View.VISIBLE);
                Product.getInstance().getProduct(getActivity(), cateId, mPage, false, handler);
            }
        });

        mAdapter.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Product product = listData.get((Integer) v.getTag());
                Bundle extras = new Bundle();
                extras.putInt("productId", product.getId());
                mProductDetailFragment = new ProductDetailFragment();
                mProductDetailFragment.setArguments(extras);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                FragmentTransactionExtended fragmentTransactionExtended = new FragmentTransactionExtended(getActivity(), fragmentTransaction, CategoriesFragment.mProductFragment, mProductDetailFragment, R.id.container_framelayout);
                fragmentTransactionExtended.addTransition(7);
                fragmentTransactionExtended.commit();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mActivity.mBtnBack.setVisibility(View.VISIBLE);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Bundle outState = new Bundle();
        saveInstance(outState);
        RestClient.cancelRequest(getActivity());
    }

    private void saveInstance(Bundle outState) {
        outState.putParcelableArrayList("list_product", listData);
        outState.putInt("catid", cateId);
        saveStateToArguments(outState, "product");
    }

    private void setAdapter() {
        mAdapter = new ProductAdapter(getActivity(), listData);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnScrollListener(new EndlessRecyclerOnScrollListener(mLinearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                //mView.findViewById(R.id.progressbar).setVisibility(View.VISIBLE);
                Product.getInstance().getProduct(getActivity(), cateId, current_page, false, handler);
            }
        });
    }

    private DataTransferHandler handler = new DataTransferHandler() {
        @Override
        public void onDataTransferResultHandler(Response response) {
            mView.findViewById(R.id.progressbar).setVisibility(View.GONE);
            ((SwipeRefreshLayout) mView.findViewById(R.id.swipe_refresh)).setRefreshing(false);
            if (response.getObject() != null) {
                listData.addAll((ArrayList<Product>) response.getObject());
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
}
