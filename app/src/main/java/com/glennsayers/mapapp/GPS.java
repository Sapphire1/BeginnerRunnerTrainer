package com.glennsayers.mapapp;

/**
 * Created by lzmuda on 5/28/14.
 */

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

public class GPS extends Service implements LocationListener {

    private final Context mContext;

    // flag for GPS status
    boolean isGPSEnabled = false;

    boolean isFirstPoint = true;
    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS status
    boolean canGetLocation = false;

    protected LocationManager locationManager;
    final int MSG_UPDATE_GPS = 20;
    final int MSG_STOP_GPS = 30;
    Location location; // location
    double latitude; // latitude
    double longitude; // longitude
    double oldLat=0, oldLng=0;
    //MapView myOpenMapView;
    MainActivity mainActivity;
    TextView tvTextView;
    Handler gpsHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_GPS:

                    stopUsingGPS();
                    getLocation();
                    if(mainActivity.timer.running)
                    {
                        String message = "MSG_UPDATE_GPS Your distance is " + MainActivity.distance[0] + " meters";
                        tvTextView = (android.widget.TextView) mainActivity.findViewById(R.id.distance);
                        tvTextView.setText(message);
                        // np 30s/60s/200m*1000 = 1/400*1000 = 1000/400 = 2,5 min/km
                        double averageSpeed = mainActivity.timer.getElapsedTimeSecs()/60.0/MainActivity.distance[0]*1000.0;
                        message = "Tempo:  " + String.format("%.2f", averageSpeed)+ " min/km";
                        android.widget.TextView avSpeed = (android.widget.TextView) mainActivity.findViewById(R.id.AverageSpeed);
                        avSpeed.setText(message);
                    }
                    gpsHandler.sendEmptyMessageDelayed(MSG_UPDATE_GPS, 10000); //text view is updated every second,
                    break;                                  //though the timer is still running
                case MSG_STOP_GPS:
                      gpsHandler.removeMessages(MSG_UPDATE_GPS);
                      break;
                default:
                    break;
            }
        }
    };

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES =1;//1; // 1 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 5;//1000 * 1 *5; // 5 sec [ms]

    public GPS(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.mContext = mainActivity.getApplicationContext();
        getLocation();
    }


    public void stopUsingGPS(){
        if(locationManager != null){
            locationManager.removeUpdates(this);
        }
    }

    /**
     * Function to get latitude
     * */
    public double getLatitude(){
        if(location != null){
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     * */
    public double getLongitude(){
        if(location != null){
            longitude = location.getLongitude();
        }
        // return longitude
        return longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     * @return boolean
     * */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     * */

    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                //mContext.startActivity(intent);
                mainActivity.getApplicationContext().startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (canGetLocation()) {
            GeoPoint gPt = new GeoPoint((int) (this.getLatitude() * 1E6), (int) (this.getLongitude() * 1E6));
            mainActivity.drawPath(gPt);
            if (!isFirstPoint) {
                float[] result=new float[5];
                location.distanceBetween(this.getLatitude(), this.getLongitude(), oldLat,  oldLng,result);
                mainActivity.distance[0] +=result[0];
            } else {
                mainActivity.setMarker(gPt, mainActivity.myMarker, mainActivity.markersOverlay);
                mainActivity.printIsFirstInfo();
                mainActivity.distance[0] = 0;
                isFirstPoint = false;
            }
        }
        else {
            showSettingsAlert();
        }
        oldLat=this.getLatitude();
        oldLng=this.getLongitude();
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    public IBinder onBind(Intent arg0) {
        return null;
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
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

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }
}