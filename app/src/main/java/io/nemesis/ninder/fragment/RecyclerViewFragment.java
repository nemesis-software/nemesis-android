package io.nemesis.ninder.fragment;

import android.app.SearchManager;
import android.content.Context;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
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
import io.nemesis.ninder.model.Product;


public class RecyclerViewFragment extends Fragment {
    private static final int PAGE_SIZE = 12;
    private static final int PRODUCTS_PER_PAGE = 2;
    private static int OFFSET;
    private boolean endOfQueueReached;

    private ProgressBar mProgressBar;

    protected RecyclerView recyclerView;
    protected RecyclerViewAdapter adapter;
    protected StaggeredGridLayoutManager layoutManager;
    private SearchView searchView;
    private TextView error_text;
    private List<ProductWrapper> products;

    public RecyclerViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        products = new ArrayList<>();
        OFFSET = 0;
        endOfQueueReached = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_recycler_view, container, false);
        recyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        error_text = (TextView) rootView.findViewById(R.id.error_text);

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

    @Override
    public void  onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_activity_list, menu);
        super.onCreateOptionsMenu(menu,inflater);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        SearchManager searchManager = (SearchManager)getContext().getSystemService(Context.SEARCH_SERVICE);
        //init search view
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setSuggestionsAdapter(new SimpleCursorAdapter(
                getContext(), android.R.layout.simple_list_item_1, null,
                new String[] { SearchManager.SUGGEST_COLUMN_TEXT_1 },
                new int[] { android.R.id.text1 }, 0));
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                MatrixCursor cursor = (MatrixCursor) searchView.getSuggestionsAdapter().getItem(position);
                String term = cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1));
                cursor.close();
                searchView.setQuery(term,true);
                //recyclerViewFragment.getAdapter().getFilter().filter(term);
                return true;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                return onSuggestionSelect(position);
            }
        });
//        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
//            @Override
//            public boolean onClose() {
//                adapter.getFilter().filter("");
//                adapter.ClearFilter();
//                return false;
//            }
//        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ((NinderApplication) getActivity().getApplication()).getProductFacade().autoComplete(newText, new ProductFacade.AsyncCallback<List<Product>>() {
                    @Override
                    public void onSuccess(List<Product> items) {
                        if(items!=null){
                            String[] sAutocompleteColNames = new String[] {
                                    BaseColumns._ID,                         // necessary for adapter
                                    SearchManager.SUGGEST_COLUMN_TEXT_1      // the full search term
                            };
                            final MatrixCursor cursor = new MatrixCursor(sAutocompleteColNames);
                            for (int i = 0; i < items.size(); i++) {
                                Product item = items.get(i);

                                Object[] row = new Object[] { i, item.getName()};
                                cursor.addRow(row);
                            }
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    searchView.getSuggestionsAdapter().changeCursor(cursor);
                                }
                            });
                        }
                    }

                    @Override
                    public void onFail(Throwable t) {
                        t.printStackTrace();
                    }
                });

                return false;
            }
        });
        searchView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View view) {

            }

            @Override
            public void onViewDetachedFromWindow(View view) {
                adapter.ClearFilter();
            }
        });
    }


    private void getData(){
        ((NinderApplication) getActivity().getApplication()).getProductFacade()
                .getProductsAsync(PAGE_SIZE, OFFSET, new ProductFacade.AsyncCallback<List<ProductWrapper>>() {
            @Override
            public void onSuccess(final List<ProductWrapper> new_products) {
                FragmentActivity activity = getActivity();
                if(activity!=null)
                    activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        error_text.setVisibility(View.INVISIBLE);
                        if (new_products != null) {
                            products.addAll(new_products);
                            endOfQueueReached = products.isEmpty();
                            adapter.notifyDataSetChanged();
                            if(mProgressBar!=null) mProgressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
            @Override
            public void onFail(Throwable t) {
                endOfQueueReached = true;
                t.printStackTrace();
            getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(mProgressBar!=null)
                        mProgressBar.setVisibility(View.GONE);
                        error_text.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
    }
}
