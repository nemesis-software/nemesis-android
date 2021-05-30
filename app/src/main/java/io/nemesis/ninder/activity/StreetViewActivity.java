package io.nemesis.ninder.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;

import io.nemesis.ninder.R;

public class StreetViewActivity extends AppCompatActivity {
    public static String STREET_VIEW_LOCATION;

    private SupportStreetViewPanoramaFragment mStreetView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_street_view);
        InitializeToolbar();
        final LatLng Location = getIntent().getParcelableExtra(STREET_VIEW_LOCATION);
        mStreetView = (SupportStreetViewPanoramaFragment) getSupportFragmentManager()
                .findFragmentById(R.id.streetView);
        mStreetView.getStreetViewPanoramaAsync(
                new OnStreetViewPanoramaReadyCallback() {
                    @Override
                    public void onStreetViewPanoramaReady(StreetViewPanorama panorama) {
                            panorama.setPosition(Location);
                    }
                });
    }

    private void InitializeToolbar(){
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        mToolbar.setTitle("StreetView");
        setSupportActionBar(mToolbar);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
