package com.example.robotwars;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

/**
 * Uses the built-in Global Positioning System of
 * the phone. Determines the longitude and latitude of
 * the delivered order.
 * @author Eric Groseclos
 */
public class GPS extends Service implements LocationListener {

    private final Context context;

    // Flag for the GPS status
    private boolean isGPSEnabled = false;

    // Flag for the status of the network
    private boolean isNetworkEnabled = false;

    private Location location;

    private double latitude;

    private double longitude;

    // The minimum distance to change updates. The unit is meters.
    private static final long MIN_DISTANCE = 0; // 0 meters

    // The minimum time between updates. The unit is milliseconds.
    private static final long MIN_TIME = 1000; // Every second

    // Necessary instance variable to determine the location.
    protected LocationManager locationManager;

    /**
     * Calls the getLocation method, to start
     * the process of using the GPS function of
     * the phone.
     * @param context   The context of the application
     */
    public GPS(Context context) {
        this.context = context;
        getLocation();
    }

    /**
     * A default constructor, in consideration for
     * the Manifest file.
     */
    public GPS() {
        context = null;
    }

    /**
     * Uses the GPS function of the phone to determine
     * longitude and latitude.
     * @return  an unused Location object
     */
    public Location getLocation() {
        try {
            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

            // Getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // Getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // No network provider is enabled...
            } else {
                // Get location from Network Provider!
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME,
                            MIN_DISTANCE, this);

                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // If GPS is enabled, get latitude and longitude using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME,
                                MIN_DISTANCE, this);

                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (SecurityException e) {}

        return location;
    }

    /**
     * The latitude of the delivered order.
     */
    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }
        return latitude;
    }

    /**
     * The longitude of the delivered order.
     */
    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }
        return longitude;
    }

    /**
     * Stops listening for GPS coordinates.
     */
    public void stopUsingGPS() {
        if(locationManager != null) {
            locationManager.removeUpdates(GPS.this);
        }
    }

    /**
     * Overrided method from LocationListener
     */
    @Override
    public void onLocationChanged(Location location) {}
    /**
     * Overrided method from LocationListener
     */
    @Override
    public void onProviderDisabled(String provider) {}
    /**
     * Overrided method from LocationListener
     */
    @Override
    public void onProviderEnabled(String provider) {}

    /**
     * Overrided method from LocationListener
     */
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    /**
     * Overrided method from LocationListener
     */
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}

