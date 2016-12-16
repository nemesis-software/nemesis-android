package io.nemesis.ninder.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.mugen.Mugen;
import com.mugen.MugenCallbacks;
import com.mugen.attachers.BaseAttacher;

import java.util.ArrayList;
import java.util.List;

import io.nemesis.ninder.NinderApplication;
import io.nemesis.ninder.R;
import io.nemesis.ninder.adapter.RecyclerViewAdapter;
import io.nemesis.ninder.logic.ProductFacade;
import io.nemesis.ninder.logic.ProductWrapper;


public class RecyclerViewFragment extends Fragment {
    private static final int PAGE_SIZE = 12;
    private static final int PRODUCTS_PER_PAGE = 2;
    private static int OFFSET = 0;
    private boolean endOfQueueReached = false;

    private ProgressBar mProgressBar;

    protected RecyclerView recyclerView;
    protected RecyclerViewAdapter adapter;
    protected StaggeredGridLayoutManager layoutManager;


    private static List<ProductWrapper> products;

    public RecyclerViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        products = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_recycler_view, container, false);
        recyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

        layoutManager = new StaggeredGridLayoutManager(PRODUCTS_PER_PAGE, OrientationHelper.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerViewAdapter(getContext(), products);
        recyclerView.setAdapter(adapter);
        BaseAttacher attacher = Mugen.with(recyclerView, new MugenCallbacks() {
            @Override
            public void onLoadMore() {
                    if(!endOfQueueReached){
                        OFFSET++;
                        getData();
                    }
            }

            @Override
            public boolean isLoading() {
                return false;
            }

            @Override
            public boolean hasLoadedAllItems() {
                return false;
            }
        }).start();
        if(products.size()==0) {
            mProgressBar.setVisibility(View.VISIBLE);
            getData();
        }
        return rootView;
    }

    public RecyclerViewAdapter getAdapter(){
        return adapter;
    }
    private void getData(){
        ((NinderApplication) getActivity().getApplication()).getProductFacade().getProductsAsync(PAGE_SIZE, OFFSET, new ProductFacade.AsyncCallback<ProductWrapper>() {
            @Override
            public void onSuccess(final List<ProductWrapper> new_products) {
                FragmentActivity activity = getActivity();
                if(activity!=null)
                    activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (new_products != null) {
                        products.addAll(new_products);
                        endOfQueueReached = products.isEmpty();
                        adapter.notifyDataSetChanged();
                        if(mProgressBar!=null)
                            mProgressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
            @Override
            public void onFail(Exception e) {
                endOfQueueReached = true;
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(mProgressBar!=null)
                        mProgressBar.setVisibility(View.GONE);
                    }
                });
            }
        });
    }
}
