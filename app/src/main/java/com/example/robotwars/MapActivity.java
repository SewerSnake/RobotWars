package com.example.robotwars;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MapActivity";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    private static final float DEFAULT_ZOOM = 15f;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));

    private static final int PLACE_PICKER_REQUEST = 1;
    private boolean locationPermissionsGranted;
    private static final int LOCATION_PERMISSION_CODE = 1234;

    private GoogleMap mMap;

    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;

    private GoogleApiClient mGoogleApiClient;

    private PlaceInfo mPlace = new PlaceInfo();

    private FusedLocationProviderClient fusedLocationProviderClient;
    private AutoCompleteTextView mSearchText;
    private ImageView gpsIcon;

    private DBHelper dbHelper;
    private int ID;
    private  Long robotIDFromIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        
        mSearchText = findViewById(R.id.searchFieldText);
        gpsIcon = findViewById(R.id.ic_gps);
        dbHelper = new DBHelper(this);

        Intent intent = getIntent();
        ID = intent.getIntExtra("ID", 0);
        robotIDFromIntent = intent.getLongExtra("robotId", 0);

        getLocationPermission();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /**
     * Initializes the search function and overrides the "enter button" to be a search button
     */
    private void initializeSearch() {
        Log.d(TAG, "initializeSearch: initializing the search function");

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        mSearchText.setOnItemClickListener(mAutocompleteClickListener);

        mPlaceAutocompleteAdapter =
                new PlaceAutocompleteAdapter(this, mGoogleApiClient, LAT_LNG_BOUNDS, null);

        mSearchText.setAdapter(mPlaceAutocompleteAdapter);

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){
                    geoLocate();
                }
                return false;
            }
        });

        gpsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked gps icon");
                getDeviceLocation();
            }
        });

        if (ID == 1) {
            placePicker();
        }
    }

    public void placePicker() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(MapActivity.this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            Log.e(TAG, "GooglePlayServicesRepairableException: " + e.getMessage());
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e(TAG, "GooglePlayServicesNotAvailableException: " + e.getMessage());
        }
    }

    /**
     * Makes the place picker (nearby suggestions) pop up when "check in map" is launched.
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);

                PendingResult<PlaceBuffer> placeResult =
                        Places.GeoDataApi.getPlaceById(mGoogleApiClient, place.getId());

                placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            }
        }
    }

    /**
     * Takes the string user entered in the search field and tries to find a location for it
     */
    private void geoLocate() {
        Log.d(TAG, "geoLocate: geolocating");
        String searchString = mSearchText.getText().toString();
        Geocoder geocoder = new Geocoder(MapActivity.this);
        List<Address> list = new ArrayList<>();

        try{
            list = geocoder.getFromLocationName(searchString, 1);
        }catch(IOException e){
            Log.e(TAG, "geoLocate: IOException: " + e.getMessage());
        }

        if(list.size() > 0){
            Address address = list.get(0);
            Log.d(TAG, "geoLocate: Found the location: " + address.toString());
            moveMapToLocation(new LatLng(address.getLatitude(), address.getLongitude()),
                    DEFAULT_ZOOM,
                    address.getAddressLine(0));
        }
    }

    /**
     * Getter method for the location of the device
     */
    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the current location of the device");
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (locationPermissionsGranted) {
                Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "getDeviceLocation: found device location");
                            Location currentLocation = (Location) task.getResult();
                            moveMapToLocation(new LatLng(currentLocation.getLatitude(),
                                    currentLocation.getLongitude()),
                                    DEFAULT_ZOOM, "My Location");
                        } else {
                            Log.d(TAG, "getDeviceLocation: couldn't find device location (null)");
                            Toast.makeText(MapActivity.this,
                                    getResources().getString(R.string.locationFailed),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException" + e.getMessage());
        }
    }

    /**
     * Moves the "camera" of the map to the right location
     * @param latLng - The latitude and longitude for the current location
     * @param zoom - Zooms in
     */
    private void moveMapToLocation(final LatLng latLng, float zoom, String title) {
        Log.d(TAG, "moveMapToLocation: moving the map to location. " + latLng);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if(ID == 1) {
            mMap.setInfoWindowAdapter(new CustomCheckinWindowAdapter(MapActivity.this));
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    Log.i(TAG, "onInfoWindowCLick: clicked check in button");

                    Intent intent = new Intent();
                    intent.putExtra("phoneNumber", mPlace.getPhoneNumber());
                    intent.putExtra("website", mPlace.getWebsiteUri());
                    intent.putExtra("address", mPlace.getAddress());
                    intent.putExtra("location", mPlace.getName());
                    intent.putExtra("lat", mPlace.getLatLng().latitude);
                    intent.putExtra("lng", mPlace.getLatLng().longitude);

                    Log.i(TAG, "onInfoWindowCLick: sets latitude to " + mPlace.getLatLng().latitude);
                    Log.i(TAG, "onInfoWindowCLick: sets longitude to " + mPlace.getLatLng().longitude);
                    Log.i(TAG, "onInfoWindowCLick: sets location to " + mPlace.getName());
                    Log.i(TAG, "onInfoWindowCLick: sets address to " + mPlace.getAddress());
                    Log.i(TAG, "onInfoWindowCLick: sets website to " + mPlace.getWebsiteUri());
                    Log.i(TAG, "onInfoWindowCLick: sets phone number to " + mPlace.getPhoneNumber());
                    setResult(1, intent);
                    finish();
                }
            });
        }

        if(ID != 3) {
            dropPin(latLng, zoom, title, mPlace);
        }
    }

    public LatLng getLatLngFromAddress(Context context, String strAddress) {

        Log.d(TAG, "getLatLngFromAddress: Receiving address:  " + strAddress);
        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng generatedLatLng = null;

        try {
            Log.d(TAG, "getLatLngFromAddress: try");
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                Log.d(TAG, "getLatLngFromAddress: address is null");
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();
            Log.d(TAG, "getLatLngFromAddress: got location");

            generatedLatLng = new LatLng(location.getLatitude(), location.getLongitude() );
            Log.d(TAG, "getLatLngFromAddress: created new latLng "+ generatedLatLng);

        } catch (IOException ex) {
            Log.d(TAG, "getLatLngFromAddress: caught IOEcxeption");
            ex.printStackTrace();

        }
        Log.d(TAG, "getLatLngFromAddress: returns "+ generatedLatLng);
        return generatedLatLng;
    }

    /**
     * Drops a pin on the location (everywhere but users actual position)
     */
    private void dropPin(LatLng latLng, float zoom, String title, PlaceInfo placeInfo) {
        Log.d(TAG, "dropPin: receiving placeinfo: " + placeInfo);

        if (ID == 1 || ID == 2){
            mMap.clear();
        }
        Log.d(TAG, "dropPin: dropping pin");
        if (title.equals("My Location")) {

            if (ID == 1) {
                MarkerOptions markerOptions =
                        new MarkerOptions().position(latLng).title(mPlace.getName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.brewlikes_pin));
                Marker marker = mMap.addMarker(markerOptions);
                marker.showInfoWindow();
            }
        }
    }

    /**
     * Drops pin and taking a robot as argument to print out the checked in info
     */
    private void dropPin(LatLng latLng, float zoom, String title, Robot robot){
        Log.d(TAG, "dropPin: receiving place info: " + robot.getLatLng());

        String snippet = "";
        if(!(robot.getAddress() == null)){
            snippet = snippet + getResources().getString(R.string.addressInSnippet)
                    + " " + robot.getAddress();
        }
        if(!(robot.getWeb() == null)){
            snippet = snippet + '\n' + getResources().getString(R.string.websiteInSnippet)
                    + " " + robot.getWeb();
        }
        if (robot.getTel().length() > 0) {
            snippet = snippet + '\n' + getResources().getString(R.string.telInSnippet)
                    + " " + robot.getTel();
        } else {
            Log.d(TAG, "dropPin: no phone number to print");
        }

        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title(robot.getLocation())
                .snippet(snippet)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.brewlikes_pin));

        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(MapActivity.this));
        Marker marker = mMap.addMarker(markerOptions);
        if (ID == 2) {
            marker.showInfoWindow();
        }
    }

    /**
     * Prepares the map.
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: Map is ready");
        mMap = googleMap;

        if (locationPermissionsGranted) {
            if (ID == 1) {
                Log.d(TAG, "Checking intent ID: 1");
                getDeviceLocation();
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                        (this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onMapReady: if if permissions granted");
                    return;
                }
                Log.d(TAG, "onMapReady: set my location enabled true");
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                initializeSearch();

            } else if (ID == 2) {
                Log.d(TAG, "Checking intent ID: 2");
                Robot b = dbHelper.getRobotById(robotIDFromIntent);
                String locationName = b.getLocation();
                Log.d(TAG, "Got location: "
                        + b.getLocation()
                        + "and lat: "
                        + b.getLat()
                        + ", lng: "
                        + b.getLng());
                LatLng latLng = new LatLng(b.getLat(), b.getLng());
                moveMapToLocation(latLng, DEFAULT_ZOOM, locationName);
                dropPin(new LatLng(b.getLat(), b.getLng()), DEFAULT_ZOOM, b.getLocation(), b);
                mSearchText.setVisibility(View.GONE);
                gpsIcon.setVisibility(View.GONE);
                View v = findViewById(R.id.relativeLayoutSearchBar);
                v.setVisibility(View.GONE);

            } else if (ID == 3) {
                Log.d(TAG, "Checking intent ID: 3");

                if (dbHelper.getAllRobots().isEmpty()) {
                    Log.d(TAG, "robot list is empty");
                    Toast.makeText(this,
                            getResources().getString(R.string.noCheckedInRobots),
                            Toast.LENGTH_SHORT).show();
                } else {
                    List<Robot> robots = dbHelper.getAllRobots();
                    for (Robot b : robots) {
                        if (b.getLat() != 0.0 && b.getLng() != 0.0) {
                            dropPin(new LatLng(b.getLat(), b.getLng()), DEFAULT_ZOOM, b.getLocation(), b);
                        }
                    }
                }
                initializeSearch();
            }
        }
    }

    /**
     * Initializes the map.
     */
    private void initializeMap() {
        Log.d(TAG, "initializeMap: initializing map");
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapActivity.this);
    }

    /**
     * Checks if the app obtains permission for using the user's location.
     */
    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {FINE_LOCATION, COURSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationPermissionsGranted = true;
                Log.d(TAG, "getLocationPermission: setting location permissions to true");
                initializeMap();
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_CODE);
                Log.d(TAG, "getLocationPermission: setting location permissions");
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_CODE);
        }
    }

    /**
     * Checks if app gets permission to use users location.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called");
        locationPermissionsGranted = false;

        switch(requestCode) {
            case LOCATION_PERMISSION_CODE: {
                if (grantResults.length > 0){
                    for (int i = 0; i < grantResults.length; i++) {
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            locationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionResult: Permission failed");
                            Toast.makeText(MapActivity.this,
                                    getResources().getString(R.string.permissionsFailed),
                                    Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionResult: Permission granted");
                    locationPermissionsGranted = true;
                    initializeMap();
                }
            }
        }
    }

    /**
     * Handles what happens when user clicks suggested location
     */
    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            hideSoftKeyboard(MapActivity.this);

            final AutocompletePrediction item = mPlaceAutocompleteAdapter.getItem(position);
            final String placeID = item.getPlaceId();
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeID);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };

    /**
     * Callback that gives us a place object with all the information about the location
     */
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.d(TAG, "onResultCallback: Place query did not complete successfully: " + places.getStatus().toString());
                places.release();
                return;
            }

            final Place place = places.get(0);

            Log.d(TAG, "onResultCallBack: preparing to set values to place info");
            try {

                if (place.getName().toString().contains("°") || place.getName() == null || place.getName().equals("")){

                    if (place.getAddress() == null || place.getAddress().equals("")) {
                        mPlace.setName(place.getLatLng().toString());
                        Log.d(TAG, "onResultCallBack: name is null and address is null, sets name to: " + mPlace.getName());
                    } else {
                        mPlace.setName(place.getAddress().toString());
                        Log.d(TAG, "onResultCallBack: name is latLng and sets it to address" + place.getAddress().toString());
                    }
                    Log.d(TAG, "onResultCallBack: Name was null so sets name to address instead: " + mPlace.getName());
                } else {
                    mPlace.setName(place.getName().toString());
                }

                if (place.getLatLng()== null) {
                    LatLng newLatLng = getLatLngFromAddress(MapActivity.this, mPlace.getAddress());
                    mPlace.setLatLng(newLatLng);
                    Log.d(TAG, "onResultCallBack: LatLng was null -> called method to set it to: " + newLatLng);
                } else {
                    mPlace.setLatLng(place.getLatLng());
                }
                Log.d(TAG, "onResultCallBack: Getting all info from the location: " + mPlace.toString());

                if (place.getAddress() == null) {
                    mPlace.setAddress(place.getLatLng().toString());
                } else {
                    mPlace.setAddress(place.getAddress().toString());
                }
                mPlace.setPhoneNumber(place.getPhoneNumber().toString());
                mPlace.setWebsiteUri(place.getWebsiteUri().toString());

            } catch (NullPointerException e) {
                Log.e(TAG, "onResultCallBack: NullPointerException: " + e.getMessage());
            }
            moveMapToLocation(new LatLng(place.getLatLng().latitude, place.getLatLng().longitude)
                    , DEFAULT_ZOOM, mPlace.getName());

            places.release();
        }
    };

    /**
     * Hides the keyboard to make more space on the screen
     * @param activity  the activity
     */
    public static void hideSoftKeyboard(Activity activity) {
        Log.d(TAG,"hideSoftKeyboard: hides the keyboard");
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

}
