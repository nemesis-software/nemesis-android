package io.nemesis.ninder.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import io.nemesis.ninder.NinderApplication;
import io.nemesis.ninder.R;
import io.nemesis.ninder.fragment.AccountFragment;
import io.nemesis.ninder.fragment.NinderFragment;
import io.nemesis.ninder.fragment.RecyclerViewFragment;
import io.nemesis.ninder.logic.ProductFacade;
import io.nemesis.ninder.logic.model.Product;

public class ListActivity extends AppCompatActivity implements RecyclerViewFragment.OnFragmentInteractionListener {
    private DrawerLayout mDrawer;
    private NavigationView navigationView;
    private Toolbar mToolbar;
    private RecyclerViewFragment recyclerViewFragment;
    private SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        if (savedInstanceState == null) {
            recyclerViewFragment = new RecyclerViewFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_placeholder, recyclerViewFragment)
                    .commit();
        }
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.icon_burger);
        setSupportActionBar(mToolbar);

        mDrawer = (DrawerLayout) findViewById(R.id.activity_list);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        TextView name = (TextView) header.findViewById(R.id.name);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        name.setText(sharedPref.getString(getString(R.string.email),getString(R.string.default_name)));
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if(menuItem.getItemId()!=R.id.nav_logout)mToolbar.setTitle(menuItem.getTitle());
                switch(menuItem.getItemId()){
                    case R.id.nav_list:
                        recyclerViewFragment = new RecyclerViewFragment();
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_placeholder, recyclerViewFragment)
                                .commit();
                        break;
                    case R.id.nav_ninder:
                        NinderFragment ninderFragment = new NinderFragment();
                        FragmentTransaction transaction = getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_placeholder, ninderFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                        break;
                    case R.id.nav_account:
                        AccountFragment accountFragment = new AccountFragment();
                        transaction = getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_placeholder, accountFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                        break;
                    case R.id.nav_logout:
                        new AlertDialog.Builder(ListActivity.this)
                                .setTitle("Logout")
                                .setMessage("Are you sure you want to logout?")
                                .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        startActivity(new Intent(ListActivity.this,LoginActivity.class));
                                        finish();
                                    }
                                })
                                .setNegativeButton("Go Back",null)
                                .create()
                                .show();
                        break;

                    default:
                        break;
                }
                mDrawer.closeDrawers();
                return false;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_list, menu);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setSuggestionsAdapter(new SimpleCursorAdapter(
                this, android.R.layout.simple_list_item_1, null,
                new String[] { SearchManager.SUGGEST_COLUMN_TEXT_1 },
                new int[] { android.R.id.text1 },CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER));
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
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                if(recyclerViewFragment!=null)
                    recyclerViewFragment.getAdapter().getFilter().filter("");
                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(recyclerViewFragment!=null)
                    recyclerViewFragment.getAdapter().getFilter().filter(query);
                else Log.d("Fragment","Null");
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(recyclerViewFragment!=null)
                recyclerViewFragment.getAdapter().getFilter().filter("");
                ((NinderApplication) getApplication()).getProductFacade().autoComplete(newText, new ProductFacade.AsyncCallback<Product>() {
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
                            Log.d("Test",cursor.getCount()+""+cursor.getColumnNames());
                            ListActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    searchView.getSuggestionsAdapter().changeCursor(cursor);
                                }
                            });
                            if(items.size()>0)
                            Log.d("Test",items.get(items.size()-1).getImageUrl());
                        }
                    }

                    @Override
                    public void onFail(Exception e) {
                        e.printStackTrace();
                    }
                });

                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawers();
            return;
        }
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStackImmediate();
            return;
        }
        super.onBackPressed();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
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

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
