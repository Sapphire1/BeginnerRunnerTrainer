package com.glennsayers.mapapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Stoper extends Activity implements OnClickListener{

    final int MSG_START_TIMER = 0;
    final int MSG_STOP_TIMER = 1;
    final int MSG_UPDATE_TIMER = 2;
    public Stopwatch timer;
    Button btnStart,btnStop, btnNextScreen ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.glennsayers.mapapp.R.layout.activity_stoper);
        timer = new Stopwatch(Stoper.this);
        btnStart = (Button)findViewById(R.id.startButton);
        btnStop= (Button)findViewById(R.id.stopButton);
        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        btnNextScreen = (Button) findViewById(R.id.mapButton);
        btnNextScreen.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                finish();
            }
        });
        double averageSpeed;
        String message;
        if(timer.running)
        {
                timer.mHandler.sendEmptyMessage(MSG_UPDATE_TIMER);
                //else  android.widget.Toast.makeText(getApplicationContext(), "timer is not runnig!", android.widget.Toast.LENGTH_LONG).show();
        }

        TextView tvTextView = (TextView) findViewById(R.id.etapTime);
        if (tvTextView != null)
            tvTextView.setText("Czas etapu: " + timer.etapTime + ":" + timer.getElapsedTimeSecs());

        tvTextView = (TextView) findViewById(R.id.TimerText);
        if (tvTextView != null)
            tvTextView.setText("Czas: " + timer.getElapsedTimeHour() + ":" + timer.getElapsedTimeMin() + ":" + timer.getElapsedTimeSecs());

        if(MainActivity.distance[0]>0.0 && 3600 * (timer.getElapsedTimeHour() + 60 * timer.getElapsedTimeMin() + timer.getElapsedTimeSecs())>0)
            averageSpeed = (3600*timer.getElapsedTimeHour()+60*timer.getElapsedTimeMin()+timer.getElapsedTimeSecs())/60.0/MainActivity.distance[0]*1000.0;
        else
            averageSpeed =0;
        // odcinamy ułamek
        int averageSpeedMin = (int)averageSpeed;
        // od całości odejmujemy minuty i resztę przeliczamy na sekundy
        int averageSpeedSec = (int) (((averageSpeed - (double) averageSpeedMin) )*0.6*100);

        message = "Tempo:  " + averageSpeedMin + ":"+averageSpeedSec + " min/km";
        android.widget.TextView avSpeed = (android.widget.TextView) findViewById(R.id.AverageSpeed);
        if (avSpeed!=null) avSpeed.setText(message);
        message = "Dystans:  " + String.format("%.1f", MainActivity.distance[0]) + " m";
        android.widget.TextView distanceStopper = (android.widget.TextView) findViewById(R.id.distanceStopper);
        if (distanceStopper !=null) distanceStopper.setText(message);
    }


    public void onClick(View v) {
        if(btnStart == v)
        {
            if(!timer.running)
                timer.mHandler.sendEmptyMessage(MSG_START_TIMER);
            else
                Toast.makeText(getApplicationContext(), "Już włączone!", Toast.LENGTH_LONG).show();
        }else
        if(btnStop == v){
            timer.mHandler.sendEmptyMessage(MSG_STOP_TIMER);
        }
    }

}
