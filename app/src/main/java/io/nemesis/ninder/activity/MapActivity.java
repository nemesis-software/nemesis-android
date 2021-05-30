package io.nemesis.ninder.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.PlacePhotoResult;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

import io.nemesis.ninder.R;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener {
    private static final String TAG = MapActivity.class.getSimpleName();
    private MapFragment mapFragment;
    //Map related objects
    private GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;

    private boolean mLocationPermissionGranted;

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    // The desired interval for location updates. Inexact. Updates may be more or less frequent.
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    // The fastest rate for active location updates. Exact. Updates will never be more frequent
    // than this value.
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
        setContentView(R.layout.activity_map);
        mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.mapFragment);
        InitializeToolbar();
        buildGoogleApiClient();
        mGoogleApiClient.connect();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    private void InitializeToolbar(){
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        mToolbar.setTitle(R.string.nav_locator);
        setSupportActionBar(mToolbar);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();
        // Add markers for nearby places.
        updateMarkers();
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent new_intent = new Intent(MapActivity.this, StreetViewActivity.class);
                new_intent.putExtra(StreetViewActivity.STREET_VIEW_LOCATION, marker.getPosition());
                startActivity(new_intent);
            }
        });
        /*
         * Set the map's camera position to the current location of the device.
         * If the previous state was saved, set the position to the saved state.
         * If the current location is unknown, use a default position and zoom value.
         */
        if (mCameraPosition != null) {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
        } else if (mCurrentLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mCurrentLocation.getLatitude(),
                            mCurrentLocation.getLongitude()), DEFAULT_ZOOM));
        } else {
            Log.d(TAG, "Current location is null. Using defaults.");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }


    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        createLocationRequest();
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mCurrentLocation);
            super.onSaveInstanceState(outState);
        }
    }
    private void getDeviceLocation() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        if (mLocationPermissionGranted) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, this);
        }
    }

    @SuppressWarnings("MissingPermission")
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mCurrentLocation = null;
            }
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

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();

    /*
     * Sets the desired interval for active location updates. This interval is
     * inexact. You may not receive updates at all if no location sources are available, or
     * you may receive them slower than requested. You may also receive updates faster than
     * requested if other applications are requesting location at a faster interval.
     */
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

    /*
     * Sets the fastest rate for active location updates. This interval is exact, and your
     * application will never receive updates faster than this value.
     */
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void addPhotoToMarker(final HashMap<String,Bitmap> marker_images){
        if(mMap!=null)
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    return false;
                }
            });
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            // Return null here, so that getInfoContents() is called next.
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(final Marker marker) {
                // Inflate the layouts for the info window, title and snippet.
                View infoWindow = getLayoutInflater().inflate(R.layout.map_custom_info, null);
                TextView title = ((TextView) infoWindow.findViewById(R.id.title));
                title.setText(marker.getTitle());
                ImageView place_image = (ImageView) infoWindow.findViewById(R.id.place_image);
                place_image.setImageBitmap(marker_images.get(marker.getTag().toString()));
                TextView snippet = ((TextView) infoWindow.findViewById(R.id.body));
                snippet.setText(marker.getSnippet());
                return infoWindow;
            }
        });
    }
    private void updateMarkers() {
        if (mMap == null) {
            return;
        }

        if (mLocationPermissionGranted) {
            // Get the businesses and other points of interest located
            // nearest to the device's current location.
            @SuppressWarnings("MissingPermission")
            PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                    .getCurrentPlace(mGoogleApiClient, null);
            result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
                @Override
                public void onResult(@NonNull PlaceLikelihoodBuffer likelyPlaces) {
                    final HashMap<String,Bitmap> marker_images = new HashMap<>();
                    for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                        // Add a marker for each place near the device's current location, with an
                        // info window showing place information.
                        String attributions = (String) placeLikelihood.getPlace().getAttributions();
                        String snippet = (String) placeLikelihood.getPlace().getAddress();
                        if (attributions != null) {
                            snippet = snippet + "\n" + attributions;
                        }
                        final String id = placeLikelihood.getPlace().getId();
                        Places.GeoDataApi.getPlacePhotos(mGoogleApiClient,id).setResultCallback(new ResultCallback<PlacePhotoMetadataResult>() {
                            @Override
                            public void onResult(@NonNull PlacePhotoMetadataResult placePhotoMetadataResult) {
                                if(placePhotoMetadataResult.getStatus().isSuccess()) {
                                    PlacePhotoMetadataBuffer buffer = placePhotoMetadataResult.getPhotoMetadata();
                                    if(buffer.getCount()>0){
                                            buffer.get(0).getScaledPhoto(mGoogleApiClient,200,200).setResultCallback(new ResultCallback<PlacePhotoResult>() {
                                                @Override
                                                public void onResult(@NonNull PlacePhotoResult placePhotoResult) {
                                                    if(placePhotoResult.getStatus().isSuccess())
                                                        marker_images.put(id,placePhotoResult.getBitmap());
                                                    else Log.d(TAG,placePhotoResult.getStatus().getStatusMessage());
                                                }
                                            });

                                    }
                                    buffer.release();
                                }
                                else Log.d(TAG,placePhotoMetadataResult.getStatus().getStatusMessage());
                            }
                        });
                        mMap.addMarker(new MarkerOptions()
                                .position(placeLikelihood.getPlace().getLatLng())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_map_marker_main))
                                .title((String) placeLikelihood.getPlace().getName())
                                .snippet(snippet)).setTag(id);
                    }
                    // Release the place likelihood buffer.
                    likelyPlaces.release();
                    addPhotoToMarker(marker_images);
                }
            });
        } else {
            mMap.addMarker(new MarkerOptions()
                    .position(mDefaultLocation)
                    .title(getString(R.string.default_info_title))
                    .snippet(getString(R.string.default_info_snippet)));
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        updateMarkers();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }
    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            getDeviceLocation();
        }
        mGoogleApiClient.connect();
        updateMarkers();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient!=null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
            mGoogleApiClient.stopAutoManage(this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getDeviceLocation();
        if(mapFragment!=null)
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Play services connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // Refer to the reference doc for ConnectionResult to see what error codes might
        // be returned in onConnectionFailed.
        Log.d(TAG, "Play services connection failed: "
                + connectionResult.getErrorCode());
    }
}
