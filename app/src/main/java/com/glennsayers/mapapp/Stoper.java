package com.glennsayers.mapapp;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Stoper extends Activity implements OnClickListener{

    final int MSG_START_TIMER = 0;
    final int MSG_STOP_TIMER = 1;
    final int MSG_UPDATE_TIMER = 2;
    public Stopwatch timer;
    Button btnStart,btnStop, btnNextScreen ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stoper);
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
        if(timer.running) timer.mHandler.sendEmptyMessage(MSG_UPDATE_TIMER);
        //else  android.widget.Toast.makeText(getApplicationContext(), "timer is not runnig!", android.widget.Toast.LENGTH_LONG).show();
    }

    public void onClick(View v) {
        if(btnStart == v)
        {
            timer.mHandler.sendEmptyMessage(MSG_START_TIMER);
        }else
        if(btnStop == v){
            timer.mHandler.sendEmptyMessage(MSG_STOP_TIMER);
        }
    }

}
