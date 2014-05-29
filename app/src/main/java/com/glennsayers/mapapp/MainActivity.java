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
import android.widget.TextView;
import android.widget.Toast;

import android.content.Intent;




public class MainActivity extends Activity {

    public static Stopwatch timer;
    public static float[] distance = new float[1];
    final int MSG_START_TIMER = 0;
    final int MSG_STOP_TIMER = 1;
    final int MSG_UPDATE_TIMER = 2;
    final int REFRESH_RATE = 100;
    public MapView myOpenMapView;
    public MapController myMapController;
    public Intent timerIntent;
    public PathOverlay myPath;
    public org.osmdroid.util.ResourceProxyImpl resProxyImp;
    public android.graphics.drawable.Drawable myMarker;
    public ItemizedIconOverlay markersOverlay;
    public GPS gps;

    Button btnStart,btnStop, btnNextScreen ;
    private android.os.Handler handler = new android.os.Handler();
    private Runnable runnable = new Runnable() {

        @Override
        public void run() {
            /* do what you need to do */
            //foobar();
            /* and here comes the "trick" */
            //Toast.makeText(getApplicationContext(), "STOP GPS", Toast.LENGTH_LONG).show();
            gps.stopUsingGPS();
            //Toast.makeText(getApplicationContext(), "START GPS", Toast.LENGTH_LONG).show();
            gps.getLocation();
            //Toast.makeText(getApplicationContext(), "RESTART!!!", Toast.LENGTH_LONG).show();
            handler.postDelayed(this, 30000);
        }
    };
    String message;
    android.widget.TextView tv;
    TextView tvTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // jak wraca ze stopera to przywrocic poprzedni widok a nie tworzyc nowego
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent i = getIntent();
        MainActivity prevActivity = (MainActivity)getLastNonConfigurationInstance();


        if(prevActivity!= null) {
            //Toast.makeText(getApplicationContext(), "Rotation!!!  prevActivity exists!\n", Toast.LENGTH_LONG).show();
            this.myOpenMapView = prevActivity.myOpenMapView;
            this.myPath = prevActivity.myPath;
            this.markersOverlay=prevActivity.markersOverlay;
            this.gps = prevActivity.gps;
            this.tvTextView = prevActivity.tvTextView;
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
            //String message = "Your distance is " + distance[0] + " meters";
            //android.widget.TextView tv = (android.widget.TextView) findViewById(R.id.distance);
            //tv.setText(message);
            //timer.mHandler.sendEmptyMessage(MSG_UPDATE_TIMER);
            String message = "Time is " + + timer.getElapsedTimeSecs() + " seconds";
            tv = (android.widget.TextView) findViewById(R.id.distance);
            tv.setText(message);
            tvTextView.setText(""+ timer.getElapsedTimeSecs());
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

            gps = new GPS(MainActivity.this, myOpenMapView, MainActivity.this);
            timer = new Stopwatch(MainActivity.this);
            // czemu handler?
            //http://www.mopri.de/2010/timertask-bad-do-it-the-android-way-use-a-handler/comment-page-1/
            handler.postDelayed(runnable, 10000);
            timerIntent = new Intent(this, Stopwatch.class);
            //startService(timerIntent);

        }
        String message = "Your distance is " + distance[0] + " meters";
        android.widget.TextView tv = (android.widget.TextView) findViewById(R.id.distance);
        tv.setText(message);
        Button startTimer;
        startTimer = (Button) findViewById(R.id.startButton);
        startTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //Toast.makeText(getApplicationContext(), "Start timer", Toast.LENGTH_LONG).show();
                timer.mHandler.sendEmptyMessage(MSG_START_TIMER);
            }
        });
        Button stopTimer;
        stopTimer = (Button) findViewById(R.id.stopButton);
        stopTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
               // Toast.makeText(getApplicationContext(), "STOP Timer", Toast.LENGTH_LONG).show();
                timer.mHandler.sendEmptyMessage(MSG_STOP_TIMER);
            }
        });
        Button stop;
        stop = (Button) findViewById(R.id.stopGPS);
        stop.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View arg0) {
             Toast.makeText(getApplicationContext(), "STOP GPS", Toast.LENGTH_LONG).show();
             gps.stopUsingGPS();
             //stopService(timerIntent);
             handler.removeCallbacks(runnable);
             finish();
             }
        });

            Button btnNextScreen = (Button) findViewById(R.id.stoperButton);
            btnNextScreen.setOnClickListener(new View.OnClickListener() {

                public void onClick(View arg0) {
                    //Starting a new Intent
                    Intent nextScreen = new Intent(getApplicationContext(), Stoper.class);
                    startActivity(nextScreen);
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
        //Toast.makeText(getApplicationContext(), "You have new location!", Toast.LENGTH_LONG).show();
    }

    void drawPath(GeoPoint gPt)
    {
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
        //Toast.makeText(getApplicationContext(), "First Localization", Toast.LENGTH_LONG).show();
    }
    public Object onRetainNonConfigurationInstance() {
        return this;
    }

}