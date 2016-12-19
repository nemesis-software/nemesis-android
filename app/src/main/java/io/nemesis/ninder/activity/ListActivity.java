package io.nemesis.ninder.activity;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
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
import android.widget.TextView;

import java.util.List;

import io.nemesis.ninder.NinderApplication;
import io.nemesis.ninder.R;
import io.nemesis.ninder.fragment.AccountFragment;
import io.nemesis.ninder.fragment.NinderFragment;
import io.nemesis.ninder.fragment.RecyclerViewFragment;
import io.nemesis.ninder.logic.ProductFacade;
import io.nemesis.ninder.model.Product;
import io.nemesis.ninder.services.LocationService;

public class ListActivity extends AppCompatActivity {
    //UI References
    private DrawerLayout mDrawer;
    private NavigationView navigationView;
    private Toolbar mToolbar;
    private RecyclerViewFragment recyclerViewFragment;
    private SearchView searchView;

    //Navigation drawer related
    private static final String MENU_ITEM = "MENU_ITEM";
    private int menuItemId;

    private static final int CODE_ACCESS_FINE_LOCATION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        if (savedInstanceState == null) {
            recyclerViewFragment = new RecyclerViewFragment();
            LoadFragment(recyclerViewFragment,false);
        }
        InitLocationService();
        InitializeToolbar();
        InitializeDrawer();
    }

    private void InitLocationService(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, CODE_ACCESS_FINE_LOCATION);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],@NonNull int[] grantResults) {
        if(requestCode == CODE_ACCESS_FINE_LOCATION && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            startService(new Intent(this,LocationService.class));
        }
    }

    private void InitializeToolbar(){
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.icon_burger);
        mToolbar.setTitle(R.string.nav_list);
        setSupportActionBar(mToolbar);
    }

    private void InitializeDrawer(){
        mDrawer = (DrawerLayout) findViewById(R.id.activity_list);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        //init header and set user email
        View header = navigationView.getHeaderView(0);
        TextView name = (TextView) header.findViewById(R.id.name);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        name.setText(sharedPref.getString(getString(R.string.email), getString(R.string.default_name)));
        //init menu items
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItemId != menuItem.getItemId()) {
                    menuItemId = menuItem.getItemId();
                    switch (menuItemId) {
                        case R.id.nav_list:
                            mToolbar.setTitle(menuItem.getTitle());
                            LoadFragment(new RecyclerViewFragment(), true);
                    break;
                    case R.id.nav_ninder:
                            mToolbar.setTitle(menuItem.getTitle());
                            LoadFragment(new NinderFragment(), true);
                        break;
                    case R.id.nav_account:
                            mToolbar.setTitle(menuItem.getTitle());
                            LoadFragment(new AccountFragment(), true);
                        break;
                    case R.id.nav_locator:
                        menuItemId = 0;
                        startActivity(new Intent(ListActivity.this, MapActivity.class));
                        break;
                    case R.id.nav_logout:
                        menuItemId = 0;
                        new AlertDialog.Builder(ListActivity.this)
                                .setTitle(R.string.title_logout)
                                .setMessage(R.string.message_logout)
                                .setPositiveButton(R.string.title_logout, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        startActivity(new Intent(ListActivity.this, LoginActivity.class));
                                        finish();
                                    }
                                })
                                .setNegativeButton(R.string.goback, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //stopService(new Intent(ListActivity.this,LocationService.class));
                                    }
                                })
                                .create()
                                .show();
                    default:
                        break;
                }
            }
                mDrawer.closeDrawers();
                return false;
            }
        });
    }

    private void LoadFragment(Fragment fragment, boolean backstackEnabled){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_placeholder, fragment);
        if(backstackEnabled)
            transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(MENU_ITEM, menuItemId);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.menuItemId = savedInstanceState.getInt(MENU_ITEM);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_list, menu);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        //init search view
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
                            ListActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    searchView.getSuggestionsAdapter().changeCursor(cursor);
                                }
                            });
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
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
