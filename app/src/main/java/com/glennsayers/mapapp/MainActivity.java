package com.glennsayers.mapapp;

import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import java.util.Vector;
import org.osmdroid.views.overlay.PathOverlay;
import android.graphics.Color;
import android.graphics.Paint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

public class MainActivity extends Activity implements LocationListener{
   /* private class GPSTracker extends Service implements LocationListener {
*/
        //private final Context mContext;

        // flag for GPS status
        boolean isGPSEnabled = false;

        boolean isFirstPoint = true;
        // flag for network status
        boolean isNetworkEnabled = false;

        // flag for GPS status
        boolean canGetLocation = false;

        Location location; // location
        double latitude; // latitude
        double longitude; // longitude
        double oldLat=0, oldLng=0;

        // The minimum distance to change Updates in meters
        private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES =1;//1; // 1 meters

        // The minimum time between updates in milliseconds
        private static final long MIN_TIME_BW_UPDATES = 5;//1000 * 1 *5; // 30 sec [ms]

        private MainActivity mainActivity;
        // Declaring a Location Manager
        protected LocationManager locationManager;

        private MapView myOpenMapView;
        private MapController myMapController;
        float[] distance = new float[1];
        PathOverlay myPath;
        org.osmdroid.util.ResourceProxyImpl resProxyImp;
        android.graphics.drawable.Drawable myMarker;
        ItemizedIconOverlay markersOverlay;

        //public GPSTracker(Context context) {
         //   this.mContext = context;

         //   isFirstPoint=true;
       // }

        /**
         * Stop using GPS listener
         * Calling this function will stop using GPS in your app
         * */
        public void stopUsingGPS(){
            if(locationManager != null){
                locationManager.removeUpdates(this);
            }
        }

        public void updateGPSandNetwork()
        {
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
        /*public void showSettingsAlert(){
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

            // Setting Dialog Title
            alertDialog.setTitle("GPS is settings");

            // Setting Dialog Message
            alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

            // On pressing Settings button
            alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    mContext.startActivity(intent);
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
        */
        @Override
        public void onLocationChanged(Location location) {
           // if (gps.canGetLocation()) {
                GeoPoint gPt = new GeoPoint((int) (this.getLatitude() * 1E6), (int) (this.getLongitude() * 1E6));
                printInfo();
                setMarker(gPt, myMarker, markersOverlay);
                String message = "Your actual localization is " + ((int) (this.getLatitude() * 1E6)) + "\t"+ ((int) (this.getLongitude() * 1E6));
                android.widget.TextView tv = (android.widget.TextView) findViewById(R.id.distance);
                tv.setText(message);
                drawPath(gPt);
                if (!isFirstPoint) {

                    //distance[0] += countDistance(oldLat, oldLng, (int) (this.getLatitude() * 1E6), (int) (this.getLongitude() * 1E6));

                } else {
                    printIsFirstInfo();
                 //   distance[0] = 0;
                    isFirstPoint = false;
                }
            //}
            //else {
            //    gps.showSettingsAlert();
            //}
            oldLat=(int)(this.getLatitude() * 1E6);
            oldLng=(int)(this.getLatitude() * 1E6);


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

    //}
        public Location getLocation() {


            try {
                locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

                // getting GPS status
                isGPSEnabled = locationManager
                        .isProviderEnabled(LocationManager.GPS_PROVIDER);

                // getting network status
                /*isNetworkEnabled = locationManager
                        .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                */


                if (!isGPSEnabled /*&& isNetworkEnabled*/) {
                    // no network provider is enabled
                } else {
                    this.canGetLocation = true;
                    // First get location from Network Provider
                    /*
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
                    */
                    // if GPS Enabled get lat/long using GPS Services

                    if (isGPSEnabled) {
                        if (location == null) {
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
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

    //GPSTracker gps=null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //gps = new GPSTracker(MainActivity.this);
        distance[0]=0.0f;
        resProxyImp = new
                org.osmdroid.util.ResourceProxyImpl(this);
        myMarker = this.getResources().getDrawable(R.drawable.markeractual);
        markersOverlay = new
                ItemizedIconOverlay<OverlayItem>(new java.util.LinkedList<OverlayItem>(), myMarker, null, resProxyImp);
        myPath = new PathOverlay(Color.RED, this);
        myOpenMapView = (MapView)findViewById(R.id.openmapview);
        myOpenMapView.getOverlays().add(myPath);
        myOpenMapView.getOverlays().add(markersOverlay);
        myOpenMapView.setTileSource(TileSourceFactory.MAPNIK);
        myOpenMapView.setBuiltInZoomControls(true);
        myOpenMapView.setMultiTouchControls(true);
        myMapController = (MapController) myOpenMapView.getController();
        myMapController.setZoom(12);
        GeoPoint gPtCenter = new GeoPoint(52233000 + 100000, 21016700 - 100000);
        myMapController.setCenter(gPtCenter);

        //double latitude = gps.getLatitude();
        //double longitude = gps.getLongitude();
        //GeoPoint gPt = new GeoPoint((int) (latitude* 1E6), (int) (longitude* 1E6));
        //setMarker(gPt, myMarker, markersOverlay);

        //Toast.makeText(getApplicationContext(), "Your distance is " + distance[0] / 1000 + " km", Toast.LENGTH_LONG).show();

        String message = "Your distance is " + 0 + " meters";
        android.widget.TextView tv = (android.widget.TextView) findViewById(R.id.distance);
        tv.setText(message);

        Button btnShowLocation;

        btnShowLocation = (Button) findViewById(R.id.btnShowLocation);

        btnShowLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //gps.getLocation();
                //if (gps.canGetLocation()) {
                    double latitude = getLatitude();
                    double longitude = getLongitude();
                    Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + (int) (latitude* 1E6) + "\nLong: " + (int) (longitude* 1E6), Toast.LENGTH_LONG).show();
                    GeoPoint gPt = new GeoPoint((int) (latitude * 1E6), (int) (longitude* 1E6));
                    if(gPt.getLatitudeE6()!=0 && gPt.getLongitudeE6()!=0)
                    {
                        setMarker(gPt, myMarker, markersOverlay);
                        drawPath(gPt);
                    }
                //} else {
                //    gps.showSettingsAlert();
               // }
            }

        });
        Button reset;

        reset = (Button) findViewById(R.id.reset);

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                    Toast.makeText(getApplicationContext(), "STOP GPS", Toast.LENGTH_LONG).show();
                    stopUsingGPS();
                    Toast.makeText(getApplicationContext(), "START GPS", Toast.LENGTH_LONG).show();
                    getLocation();
                    //updateGPSandNetwork();
            }
        });
        Button stop;
        stop = (Button) findViewById(R.id.stopGPS);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Toast.makeText(getApplicationContext(), "STOP GPS", Toast.LENGTH_LONG).show();
                //gps.stopUsingGPS();
            }
        });
    }

    void setMarker(GeoPoint gPt, android.graphics.drawable.Drawable myMarker,  ItemizedIconOverlay markersOverlay)
    {
        OverlayItem ovm = new OverlayItem("title", "description", gPt);
        ovm.setMarker(myMarker);
        markersOverlay.addItem(ovm);
    }
    void printInfo()
    {
        Toast.makeText(getApplicationContext(), "You have new location!", Toast.LENGTH_LONG).show();
    }

    void drawPath(GeoPoint gPt)
    {
        //myPath = new PathOverlay(Color.RED, this);
        //myOpenMapView.getOverlays().add(myPath);
        myPath.addPoint(gPt);
    }

    float countDistance(double oldLat, double oldLng, double newLat, double newLng)
    {
        float[] newDistance = new float[1];
        android.location.Location.distanceBetween(oldLat,oldLng,newLat,newLng,newDistance);
        return newDistance[0];
    }
    void printIsFirstInfo()
    {
        Toast.makeText(getApplicationContext(), "First Localization", Toast.LENGTH_LONG).show();

    }

}