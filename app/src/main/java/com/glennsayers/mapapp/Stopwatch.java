package com.glennsayers.mapapp;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;

/**
 * Created by lzmuda on 5/28/14.
 */

public class Stopwatch extends Service {
    final int MSG_START_TIMER = 0;
    final int MSG_STOP_TIMER = 1;
    final int MSG_UPDATE_TIMER = 2;
    final int REFRESH_RATE = 1000;
    public static int etapWalkNr = 0;
    public static int etapRunNr = 0;
    int walkTimeMin = 1;
    int runTimeMin = 2;
    public static int etapsAmount = 0;
    public static long etapTime=0;

    android.content.Context appContext;
    boolean walk;
    boolean run;
    TextView tvTextView;
    android.app.Activity activity;
    double averageSpeed;
    int iterationCounter = 1;

    public Stopwatch(android.app.Activity activity){
        this.activity = activity;
        run=false;
        walk=false;
        appContext = activity.getApplicationContext();
    }
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_START_TIMER:
                    Toast.makeText(activity.getApplicationContext(), "StartTimer!!!!", Toast.LENGTH_LONG).show();
                    MainActivity.resetMap();
                    start(); //start timer
                    etapsAmount=iterationCounter;
                    sendEmptyMessage(MSG_UPDATE_TIMER);
                    break;

                case MSG_UPDATE_TIMER:
                    if(running){
                    long elapsedMinutes = getElapsedTimeHour() * 60 + getElapsedTimeMin();
                    etapTime = elapsedMinutes - (etapWalkNr * walkTimeMin) - (etapRunNr * runTimeMin);
                        // start training
                    if (getElapsedTimeSecs() == 0 && getElapsedTimeMin() == 0) {
                        // moze nie idz tylko "Szybkie chodzenie!"?
                        MainActivity.mpwalk.start();
                        Toast.makeText(activity.getApplicationContext(), "Idź!", Toast.LENGTH_LONG).show();
                        walk=true;
                        run=false;
                    } else if (walk && etapTime >= walkTimeMin) {// <- tutaj jest problem bo jak się zatrzymuje to wpisuje straszne głupoty i wchodzi do teog warunku
                        ++etapWalkNr;
                        walk = false;
                        run = true;
                        MainActivity.mprun.start();
                        Toast.makeText(activity.getApplicationContext(), "Biegnij!" + etapWalkNr, Toast.LENGTH_LONG).show();
                    } else if (run && etapTime >= runTimeMin) {
                        ++etapRunNr;
                        run = false;
                        walk = true;
                        //Toast.makeText(activity.getApplicationContext(), "222222", Toast.LENGTH_LONG).show();
                        --etapsAmount;
                        if (etapsAmount >= 1) {
                            MainActivity.mpwalk.start();
                            Toast.makeText(activity.getApplicationContext(), "Idź!", Toast.LENGTH_LONG).show();
                        }
                    }

                    //koniec treningu!!!!
                    if (etapsAmount < 1) {
                        // "Koniec Treningu. Gratulacje!"
                        // MainActivity.mpEnd.start();
                        Toast.makeText(activity.getApplicationContext(), "Koniec treningu :), Gratulacje!!! " +etapsAmount, Toast.LENGTH_LONG).show();
                        stop();
                    }
                    tvTextView = (TextView) activity.findViewById(R.id.etapTime);
                    if (tvTextView != null)
                        tvTextView.setText("Czas etapu: " + etapTime + ":" + getElapsedTimeSecs());

                        tvTextView = (TextView) activity.findViewById(R.id.TimerText);
                    if (tvTextView != null)
                        tvTextView.setText("Czas: " + getElapsedTimeHour() + ":" + getElapsedTimeMin() + ":" + getElapsedTimeSecs());


                    // np 30s/60s/200m*1000 = 1/400*1000 = 1000/400 = 2,5 min/km, drukujemy co 10 sekund średnią prędkość
                    long elapsedTimeSec = getElapsedTimeSecs();
                    if (elapsedTimeSec % 10 == 0) {
                        String message = "Dystans:  " + String.format("%.1f", MainActivity.distance[0]) + " m";
                        android.widget.TextView distanceStopper = (android.widget.TextView) activity.findViewById(R.id.distanceStopper);
                        if (distanceStopper != null) distanceStopper.setText(message);

                        averageSpeed = (3600 * getElapsedTimeHour() + 60 * getElapsedTimeMin() + getElapsedTimeSecs()) / 60.0 / MainActivity.distance[0] * 1000.0;
                        message = "Tempo:  " + String.format("%.2f", averageSpeed) + " min/km";
                        android.widget.TextView avSpeed = (android.widget.TextView) activity.findViewById(R.id.AverageSpeed);
                        if (avSpeed != null) avSpeed.setText(message);
                    }

                    mHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIMER, REFRESH_RATE); //text view is updated every second,
                    }
                    break;
                case MSG_STOP_TIMER:
                    mHandler.removeMessages(MSG_UPDATE_TIMER);
                    stop();//stop timer
                    break;
                default:
                    break;
            }

        }
    };

    public static long startTime = 0;
    public static boolean running = false;
    public static long currentTime = 0;
    public static long elapsed = 0;


    public void start() {
        startTime = System.currentTimeMillis();
        running = true;
    }

    public void stop() {
        running = false;
        //save elapsed time, and distance or refresh activity fields
    }

    //elaspsed time in milliseconds
    public long getElapsedTimeMili() {

        if (running) {
            elapsed =((System.currentTimeMillis() - startTime)/100) % 1000 ;
        }
        return elapsed;
    }

    //elaspsed time in seconds
    public long getElapsedTimeSecs() {
        if (running) {
            elapsed = ((System.currentTimeMillis() - startTime) / 1000) % 60;
        }
        return elapsed;
    }

    //elaspsed time in minutes
    public long getElapsedTimeMin() {
        if (running) {
            elapsed = (((System.currentTimeMillis() - startTime) / 1000) / 60 ) % 60;
        }
        return elapsed;
    }

    //elaspsed time in hours
    public long getElapsedTimeHour() {
        if (running) {
            elapsed = ((((System.currentTimeMillis() - startTime) / 1000) / 60 ) / 60);
        }
        return elapsed;
    }
    public IBinder onBind(Intent arg0) {
        return null;
    }
}