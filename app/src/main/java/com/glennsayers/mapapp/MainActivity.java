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
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.content.Intent;
import java.util.HashMap;


public class MainActivity extends Activity {
    public Stopwatch timer;
    public static float[] distance = new float[1];
    final int MSG_START_TIMER = 0;
    final int MSG_STOP_TIMER = 1;
    final int MSG_UPDATE_TIMER = 2;
    final int MSG_UPDATE_GPS = 20;
    final int MSG_STOP_GPS = 30;
    public MapView myOpenMapView;
    public MapController myMapController;
    public static PathOverlay myPath;
    public org.osmdroid.util.ResourceProxyImpl resProxyImp;
    public android.graphics.drawable.Drawable myMarker;
    public static ItemizedIconOverlay markersOverlay;
    public static GPS gps;
    public android.widget.TextView tv;
    public static     android.media.MediaPlayer mpwalk;
    public static  android.media.MediaPlayer mprun;
    public static     android.media.MediaPlayer mpfaster;
    public static  android.media.MediaPlayer mpslower;
    public static  android.media.MediaPlayer mpEnd;
    public static HashMap<Integer, int[]> trainingSheduleMap = new HashMap<Integer, int[]>();
    public static int weekNr;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        initTimeSchedule();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainActivity prevActivity = (MainActivity) getLastNonConfigurationInstance();
        if(AdvancedSettings.isAdvanced==true)
        {
            weekNr=20;
            Toast.makeText(getApplicationContext(), "Trening dla zaawansowanego! " + weekNr, Toast.LENGTH_LONG).show();
        }
        else {
            int weeks;
            long savedDateInMilis;
            android.content.SharedPreferences preferences2 = android.preference.PreferenceManager.getDefaultSharedPreferences(this);
            savedDateInMilis = preferences2.getLong("storedDateinMilis", 0);
            // save date of first training
            if (savedDateInMilis == 0) {
                android.content.SharedPreferences preferences = android.preference.PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                android.content.SharedPreferences.Editor editor = preferences.edit();
                editor.putLong("storedDateinMilis", new java.util.Date().getTime()); // value to store
                editor.commit();
            }
            long dateDiff = (new java.util.Date().getTime()) - savedDateInMilis;
            int days = (int) (dateDiff / 86400000);
            weeks = days / 7 + 1;
            weekNr = weeks;
            if(weekNr<1000)
                Toast.makeText(getApplicationContext(), "Trenujesz już: " + days + " dni." + " To jest tydzień " + weekNr, Toast.LENGTH_LONG).show();
        }

        if(timer==null )
            timer = new Stopwatch(MainActivity.this);
        if(prevActivity!= null) {
            this.myOpenMapView = prevActivity.myOpenMapView;
            //this.myPath = prevActivity.myPath;
            //this.markersOverlay=prevActivity.markersOverlay;
            //this.gps = prevActivity.gps;
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
            tv = (android.widget.TextView) findViewById(R.id.distance);
            tv.setText(message);
            //gps activity update
            gps.mainActivity = MainActivity.this;
        }
        else{
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
            gps = new GPS(MainActivity.this);
            String message = "Your distance is " + distance[0] + " meters";
            tv = (android.widget.TextView) findViewById(R.id.distance);
            tv.setText(message);
        }
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        mprun = android.media.MediaPlayer.create(getApplicationContext() , R.raw.run);
        mpwalk = android.media.MediaPlayer.create(getApplicationContext() , R.raw.walk);
        mpslower = android.media.MediaPlayer.create(getApplicationContext() , R.raw.slower);
        mpfaster = android.media.MediaPlayer.create(getApplicationContext() , R.raw.faster);
        mpEnd = android.media.MediaPlayer.create(getApplicationContext() , R.raw.end);


        if(timer.running) timer.mHandler.sendEmptyMessage(MSG_UPDATE_TIMER);
        gps.gpsHandler.sendEmptyMessage(MSG_UPDATE_GPS);
        Button startTimer;
        startTimer = (Button) findViewById(R.id.startButton);
        startTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //Toast.makeText(getApplicationContext(), "Start timer", Toast.LENGTH_LONG).show();
                if(!timer.running)
                    timer.mHandler.sendEmptyMessage(MSG_START_TIMER);
                else
                    Toast.makeText(getApplicationContext(), "Już włączony", Toast.LENGTH_LONG).show();
            }
        });
        /*
        Button stopTimer;
        stopTimer = (Button) findViewById(R.id.stopButton);
        stopTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                timer.mHandler.sendEmptyMessage(MSG_STOP_TIMER);
            }
        });
        */
        Button stop;
        stop = (Button) findViewById(R.id.stopGPS);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Toast.makeText(getApplicationContext(), "STOP GPS", Toast.LENGTH_LONG).show();
                gps.gpsHandler.sendEmptyMessage(MSG_STOP_GPS);
                gps.stopUsingGPS();
                timer.mHandler.sendEmptyMessage(MSG_STOP_TIMER);
                //handler.removeCallbacks(runnable);
                finish();
            }
        });

        Button btnNextScreen = (Button) findViewById(R.id.Timers);
        btnNextScreen.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
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

    void printIsFirstInfo()
    {
        //Toast.makeText(getApplicationContext(), "First Localization", Toast.LENGTH_LONG).show();
    }
    public Object onRetainNonConfigurationInstance() {
        return this;
    }

    public static void resetMap()
    {
        MainActivity.distance[0]=0;
        MainActivity.myPath.clearPath();
        MainActivity.markersOverlay.removeAllItems();
        MainActivity.gps.pointsNr = 0;
    }
    @Override
    protected void onDestroy() {
        if(null!=mprun){
            mprun.release();
        }
        if(null!=mpwalk){
            mpwalk.release();
        }
        super.onDestroy();
    }

    void initTimeSchedule(){
        int [] trainingWeek1 = {1,1,7};
        trainingSheduleMap.put(1,trainingWeek1);
        int [] trainingWeek2 = {1,2,5};
        trainingSheduleMap.put(2,trainingWeek2);
        int [] trainingWeek3 = {1,3,4};
        trainingSheduleMap.put(3,trainingWeek3);
        int [] trainingWeek4 = {1,4,4};
        trainingSheduleMap.put(4,trainingWeek4);
        int [] trainingWeek5 = {1,5,4};
        trainingSheduleMap.put(5,trainingWeek5);
        int [] trainingWeek6 = {1,6,4};
        trainingSheduleMap.put(6,trainingWeek6);
        int [] trainingWeek7 = {1,7,4};
        trainingSheduleMap.put(6,trainingWeek7);
        int [] trainingWeek8 = {1,8,4};
        trainingSheduleMap.put(7,trainingWeek8);
        int [] trainingWeek9 = {1,9,4};
        trainingSheduleMap.put(9,trainingWeek9);
        int [] trainingWeek10 = {1,10,4};
        trainingSheduleMap.put(10,trainingWeek10);
        int [] trainingWeek11 = {1,15,3};
        trainingSheduleMap.put(11,trainingWeek11);
        int [] trainingWeek12 = {1,15,3};
        trainingSheduleMap.put(12,trainingWeek12);
        int [] trainingWeek13 = {1,20,3};
        trainingSheduleMap.put(13,trainingWeek13);
        int [] trainingWeek14 = {1,20,3};
        trainingSheduleMap.put(14,trainingWeek14);
        int [] trainingWeek15 = {1,30,2};
        trainingSheduleMap.put(15,trainingWeek15);
        int [] trainingWeek16 = {1,30,2};
        trainingSheduleMap.put(16,trainingWeek16);//40,1,19;45,1,14;50,1,8
        /*
        int [] trainingWeek171 = {1,40,1};
        trainingSheduleMap.put("171",trainingWeek171);
        int [] trainingWeek172 = {1,19,1};
        trainingSheduleMap.put("171",trainingWeek172);
        int [] trainingWeek182 = {1,45,1};
        trainingSheduleMap.put("181",trainingWeek182);
        int [] trainingWeek181 = {1,14,1};
        trainingSheduleMap.put("181",trainingWeek181);
        int [] trainingWeek191 = {1,50,1};
        trainingSheduleMap.put("191",trainingWeek191);
        int [] trainingWeek192 = {1,8,1};
        trainingSheduleMap.put("191",trainingWeek192);
        */
        int [] trainingWeek20 = {1,60,1};
        trainingSheduleMap.put(20,trainingWeek20);
        int [] trainingWeek100 = {0,AdvancedSettings.fulltimeValue,1};
        trainingSheduleMap.put(20,trainingWeek100);
    }
}