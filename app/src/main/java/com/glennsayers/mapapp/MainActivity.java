package com.glennsayers.mapapp;

import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import org.osmdroid.views.overlay.PathOverlay;
import android.graphics.Color;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import android.content.Intent;
import android.content.Context;


public class MainActivity extends Activity {
        public MapView myOpenMapView;
        public MapController myMapController;
        float[] distance = new float[1];
        public PathOverlay myPath;
        public org.osmdroid.util.ResourceProxyImpl resProxyImp;
        public android.graphics.drawable.Drawable myMarker;
        public ItemizedIconOverlay markersOverlay;
        public GPS gps;
        private android.os.Handler handler = new android.os.Handler();
        private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            /* do what you need to do */
            //foobar();
            /* and here comes the "trick" */
            Toast.makeText(getApplicationContext(), "STOP GPS", Toast.LENGTH_LONG).show();
            gps.stopUsingGPS();
            Toast.makeText(getApplicationContext(), "START GPS", Toast.LENGTH_LONG).show();
            gps.getLocation();
            //Toast.makeText(getApplicationContext(), "RESTART!!!", Toast.LENGTH_LONG).show();
            handler.postDelayed(this, 30000);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent i = getIntent();
        MainActivity prevActivity = (MainActivity)getLastNonConfigurationInstance();
        if(prevActivity!= null) {
            Toast.makeText(getApplicationContext(), "Rotation!!!  prevActivity exists!\n", Toast.LENGTH_LONG).show();
            this.distance=prevActivity.distance;
            this.myOpenMapView = prevActivity.myOpenMapView;
            this.myPath = prevActivity.myPath;
            this.distance = prevActivity.distance;
            this.markersOverlay=prevActivity.markersOverlay;
            this.gps = prevActivity.gps;
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
            String message = "Your distance is " + distance[0] + " meters";
            android.widget.TextView tv = (android.widget.TextView) findViewById(R.id.distance);
            tv.setText(message);
        }
        else{
            distance[0]=12.0f;
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
            String message = "Your distance is " + distance[0] + " meters";
            android.widget.TextView tv = (android.widget.TextView) findViewById(R.id.distance);
            tv.setText(message);
            gps = new GPS(MainActivity.this, myOpenMapView, MainActivity.this);



            handler.postDelayed(runnable, 10000);

        }
        //double latitude = gps.getLatitude();
            //double longitude = gps.getLongitude();
            //GeoPoint gPt = new GeoPoint((int) (latitude* 1E6), (int) (longitude* 1E6));
            //setMarker(gPt, myMarker, markersOverlay);

            //Toast.makeText(getApplicationContext(), "Your distance is " + distance[0] / 1000 + " km", Toast.LENGTH_LONG).show();
                /*
                Button btnShowLocation;

                btnShowLocation = (Button) findViewById(R.id.btnShowLocation);

                btnShowLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        //gps.getLocation();
                        if (gps.canGetLocation()) {
                            double latitude = gps.getLatitude();
                            double longitude = gps.getLongitude();
                            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + (int) (latitude* 1E6) + "\nLong: " + (int) (longitude* 1E6), Toast.LENGTH_LONG).show();
                            GeoPoint gPt = new GeoPoint((int) (latitude * 1E6), (int) (longitude* 1E6));
                            if(gPt.getLatitudeE6()!=0 && gPt.getLongitudeE6()!=0)
                            {
                                setMarker(gPt, myMarker, markersOverlay);
                                drawPath(gPt);
                            }
                        } else {
                            gps.showSettingsAlert();
                        }
                    }
                });

                Button reset;

                reset = (Button) findViewById(R.id.reset);

                reset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                            Toast.makeText(getApplicationContext(), "STOP GPS", Toast.LENGTH_LONG).show();
                            gps.stopUsingGPS();
                            Toast.makeText(getApplicationContext(), "START GPS", Toast.LENGTH_LONG).show();
                            gps.getLocation();
                            //updateGPSandNetwork();
                    }
                });
                */
                Button stop;
                stop = (Button) findViewById(R.id.stopGPS);
                stop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        Toast.makeText(getApplicationContext(), "STOP GPS", Toast.LENGTH_LONG).show();
                        gps.stopUsingGPS();
                        handler.removeCallbacks(runnable);
                        finish();
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
    public Object onRetainNonConfigurationInstance() {
        return this;
    }

}