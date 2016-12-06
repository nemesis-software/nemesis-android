package io.nemesis.ninder.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

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

public class ListActivity extends AppCompatActivity {

    private StaggeredGridLayoutManager layoutManager;
    RecyclerView recyclerView;
    RecyclerViewAdapter adapter;
    private final int page_size = 12;
    private int offset = 0;
    private List<ProductWrapper> products;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        products = new ArrayList<>();
        layoutManager = new StaggeredGridLayoutManager(3,1);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerViewAdapter(ListActivity.this, products);
        recyclerView.setAdapter(adapter);
        BaseAttacher attacher = Mugen.with(recyclerView, new MugenCallbacks() {
            @Override
            public void onLoadMore() {
                offset++;
                getData();
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
        attacher.setLoadMoreOffset(12);
//        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager) {
//            @Override
//            public void onLoadMore(int current_page) {
//                Log.d("Page",current_page+"");
//                getData(current_page);
//            }
//        });
        getData();
    }

    private void getData(){
        ((NinderApplication) getApplication()).getProductFacade().getProductsAsync(page_size, offset, new ProductFacade.AsyncCallback() {
            @Override
            public void onSuccess(final List<ProductWrapper> new_products) {
                ListActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for(ProductWrapper product : new_products) {
                            products.add(product);
                        }
                        adapter.notifyDataSetChanged();
                        }
                });
            }
            @Override
            public void onFail(Exception e) {
                e.printStackTrace();
            }
        });

    }
}
