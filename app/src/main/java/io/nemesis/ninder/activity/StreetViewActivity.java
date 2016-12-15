package io.nemesis.ninder.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaView;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;

import io.nemesis.ninder.R;

public class StreetViewActivity extends AppCompatActivity {
    public static String STREET_VIEW_LOCATION;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_street_view);
        final LatLng Location = getIntent().getParcelableExtra(STREET_VIEW_LOCATION);

        StreetViewPanoramaView streetViewPanoramaView = (StreetViewPanoramaView) findViewById(R.id.streetView);
        streetViewPanoramaView.getStreetViewPanoramaAsync(
                new OnStreetViewPanoramaReadyCallback() {
                    @Override
                    public void onStreetViewPanoramaReady(StreetViewPanorama panorama) {
                            panorama.setPosition(Location);
                    }
                });
    }
}
