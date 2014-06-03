package com.glennsayers.mapapp;

import android.app.Service;
import android.content.Intent;
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
    int walkTimeMin = 0;
    int runTimeMin = 0;
    int iterationCounter = 0;
    public static int etapsAmount = 0;
    public static long etapTime=0;
    int[] weekShedule;

    android.content.Context appContext;
    boolean walk;
    boolean run;
    TextView tvTextView;
    android.app.Activity activity;
    double averageSpeed;
    int averageSpeedMin = 0;
    int averageSpeedSec = 0;


    public Stopwatch(android.app.Activity activity){
        this.activity = activity;
        run=false;
        walk=false;
        appContext = activity.getApplicationContext();
        if(MainActivity.weekNr==17||MainActivity.weekNr==18||MainActivity.weekNr==19)
            MainActivity.weekNr=20;
        if(MainActivity.weekNr>20)
            MainActivity.weekNr=20;
        weekShedule =  MainActivity.trainingSheduleMap.get(MainActivity.weekNr);
        walkTimeMin = weekShedule[0];
        runTimeMin = weekShedule[1];
        iterationCounter = weekShedule[2];
    }
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_START_TIMER:
                    //Toast.makeText(activity.getApplicationContext(), "StartTimer!!!!", Toast.LENGTH_LONG).show();
                    etapWalkNr = 0;
                    etapRunNr = 0;
                    MainActivity.resetMap();
                    start(); //start timer
                    etapsAmount=iterationCounter;
                    sendEmptyMessage(MSG_UPDATE_TIMER);
                    break;

                case MSG_UPDATE_TIMER:
                    if(running){
                        long elapsedMinutes = getElapsedTimeHour() * 60 + getElapsedTimeMin();
                        etapTime = elapsedMinutes - (etapWalkNr * walkTimeMin) - (etapRunNr * runTimeMin);
                        if(AdvancedSettings.isAdvanced && MainActivity.distance[0]>0.0 && getElapsedTimeSecs()%30==0)
                        {
                            long averegeSettingsTime = AdvancedSettings.minTimeValue*60+AdvancedSettings.secTimeValue;
                            long everageTimeInSec = averageSpeedMin*60 + averageSpeedSec;
                            if(averegeSettingsTime+30<everageTimeInSec){
                                Toast.makeText(activity.getApplicationContext(), "Szybciej!", Toast.LENGTH_LONG).show();
                                MainActivity.mpfaster.start();
                            }
                            else  if(averegeSettingsTime-30>everageTimeInSec) {
                                Toast.makeText(activity.getApplicationContext(), "Zwolnij!", Toast.LENGTH_LONG).show();
                                MainActivity.mpslower.start();
                            }
                        }
                            // start training
                        if (getElapsedTimeSecs() == 0 && getElapsedTimeMin() == 0) {
                            if(!AdvancedSettings.isAdvanced) {
                                Toast.makeText(activity.getApplicationContext(), "Idź!", Toast.LENGTH_LONG).show();
                                MainActivity.mpwalk.start();
                            }
                            walk=true;
                            run=false;
                        } else if (walk && etapTime >= walkTimeMin) {// <- tutaj jest problem bo jak się zatrzymuje to wpisuje straszne głupoty i wchodzi do teog warunku
                            ++etapWalkNr;
                            walk = false;
                            run = true;
                            MainActivity.mprun.start();
                            Toast.makeText(activity.getApplicationContext(), "Biegnij!", Toast.LENGTH_LONG).show();

                        } else if (run && etapTime >= runTimeMin) {
                            ++etapRunNr;
                            --etapsAmount;
                            if (etapsAmount >= 1) {
                                run = false;
                                walk = true;
                                MainActivity.mpwalk.start();
                                Toast.makeText(activity.getApplicationContext(), "Idź!", Toast.LENGTH_LONG).show();
                            }
                            else {
                                // "Koniec Treningu. Gratulacje!"

                                if(MainActivity.weekNr!=171 && MainActivity.weekNr!=181 && MainActivity.weekNr!=191)
                                {
                                    Toast.makeText(activity.getApplicationContext(), "Koniec treningu :), Gratulacje!!! ", Toast.LENGTH_LONG).show();
                                    MainActivity.mpEnd.start();
                                    stop();
                                }
                                /*
                                else
                                {
                                    if(MainActivity.weekNr==171)
                                        MainActivity.weekNr=172;
                                    else if(MainActivity.weekNr==181)
                                        MainActivity.weekNr=182;
                                    else if(MainActivity.weekNr==191)
                                        MainActivity.weekNr=192;
                                    String stringWeekNr = MainActivity.weekNr + "";
                                    int[] weekShedule =  MainActivity.trainingSheduleMap.get(stringWeekNr);
                                    walkTimeMin = weekShedule[0];
                                    runTimeMin = weekShedule[1];
                                    iterationCounter = weekShedule[2];
                                    etapsAmount=iterationCounter;

                                    MainActivity.mpwalk.start();
                                    Toast.makeText(activity.getApplicationContext(), "Idź!" + etapWalkNr, Toast.LENGTH_LONG).show();
                                    walk=true;
                                    run=false;
                                }
                                */
                            }
                        }

                        //koniec treningu

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
                            if(MainActivity.distance[0]>0.0 && 3600 * (getElapsedTimeHour() + 60 * getElapsedTimeMin() + getElapsedTimeSecs())>0)
                                averageSpeed = (3600 * getElapsedTimeHour() + 60 * getElapsedTimeMin() + getElapsedTimeSecs()) / 60.0 / MainActivity.distance[0] * 1000.0;
                            else
                                averageSpeed=0.0;

                            // odcinamy ułamek
                            averageSpeedMin = (int)averageSpeed;
                            // od całości odejmujemy minuty i resztę przeliczamy na sekundy
                            averageSpeedSec = (int) (((averageSpeed - (double) averageSpeedMin) )*0.6*100);

                            message = "Tempo:  " + averageSpeedMin + ":"+averageSpeedSec + " min/km";
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
    public static long elapsed = 0;
    public static long lastElapsedTimeMili = 0;
    public static long lastElapsedTimeSecs = 0;
    public static long lastElapsedTimeMin= 0;
    public static long lastElapsedTimeHour = 0;

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
            lastElapsedTimeMili = elapsed =((System.currentTimeMillis() - startTime)/100) % 1000 ;
            return elapsed;
        }
        return lastElapsedTimeMili;
    }

    //elaspsed time in seconds
    public long getElapsedTimeSecs() {
        if (running) {
            lastElapsedTimeSecs = elapsed = ((System.currentTimeMillis() - startTime) / 1000) % 60;
            return elapsed;
        }
        return lastElapsedTimeSecs;
    }

    //elaspsed time in minutes
    public long getElapsedTimeMin() {
        if (running) {
            lastElapsedTimeMin = elapsed = (((System.currentTimeMillis() - startTime) / 1000) / 60 ) % 60;
            return elapsed;
        }
        return lastElapsedTimeMin;
    }

    //elaspsed time in hours
    public long getElapsedTimeHour() {
        if (running) {
            lastElapsedTimeHour = elapsed = ((((System.currentTimeMillis() - startTime) / 1000) / 60 ) / 60);
            return elapsed;
        }
        return lastElapsedTimeHour;
    }
    public IBinder onBind(Intent arg0) {
        return null;
    }
}