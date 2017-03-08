package io.nemesis.ninder.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import io.nemesis.ninder.R;
import io.nemesis.ninder.fragment.AccountFragment;
import io.nemesis.ninder.fragment.NinderFragment;
import io.nemesis.ninder.fragment.RecyclerViewFragment;
import io.nemesis.ninder.services.LocationService;

public class ListActivity extends AppCompatActivity {
    //UI References
    private DrawerLayout mDrawer;
    private NavigationView navigationView;
    private Toolbar mToolbar;

    //Navigation drawer related
    private static final String MENU_ITEM = "MENU_ITEM";
    private int menuItemId;

    private static final int CODE_ACCESS_FINE_LOCATION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        if (savedInstanceState == null) {
            LoadFragment(new RecyclerViewFragment(),false);
        }
        InitializeToolbar();
        InitializeDrawer();
        initLocationService();
    }

    private void initLocationService(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, CODE_ACCESS_FINE_LOCATION);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
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
