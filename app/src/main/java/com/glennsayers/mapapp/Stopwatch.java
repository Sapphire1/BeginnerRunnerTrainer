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
    final int REFRESH_RATE = 100;
    TextView tvTextView;
    android.app.Activity activity;

    public Stopwatch(android.app.Activity activity){
        this.activity = activity;
    }
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_START_TIMER:
                    start(); //start timer
                    sendEmptyMessage(MSG_UPDATE_TIMER);
                    break;
                case MSG_UPDATE_TIMER:
                    tvTextView = (TextView) activity.findViewById(R.id.TextViewTimer);
                    if (tvTextView!=null)tvTextView.setText("" + getElapsedTimeSecs());
                    tvTextView = (TextView) activity.findViewById(R.id.TimerText);
                    if (tvTextView!=null)tvTextView.setText("" + getElapsedTimeSecs());
                    mHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIMER, REFRESH_RATE); //text view is updated every second,
                    break;                                  //though the timer is still running
                case MSG_STOP_TIMER:
                    mHandler.removeMessages(MSG_UPDATE_TIMER); // no more updates.
                    stop();//stop timer
//                    tvTextView = (TextView) activity.findViewById(R.id.TextViewTimer);
//                    tvTextView.setText("" + getElapsedTimeSecs());
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