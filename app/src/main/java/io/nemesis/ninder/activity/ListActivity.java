package io.nemesis.ninder.activity;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

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
        setContentView(R.layout.activity_list);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Ninder");

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_list, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView =
                (SearchView) MenuItemCompat.getActionView(searchItem);

        // Configure the search info and add any event listeners...

        // Get the MenuItem for the action item
        MenuItem actionMenuItem = menu.findItem(R.id.action_settings);

        //  Assign the listener to that action item
        MenuItemCompat.setOnActionExpandListener(actionMenuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Do something when action item collapses
                return true;  // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Do something when expanded
                return true;  // Return true to expand action view
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.action_search:
                // User chose the "Search" action
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
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
