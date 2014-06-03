package com.glennsayers.mapapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;



public class TreningType extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trening_type);
        Button btnNextScreenBeginner = (Button) findViewById(R.id.beginner);

        //Listening to button event
        btnNextScreenBeginner.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                //Starting a new Intent
                Intent nextScreen = new Intent(getApplicationContext(), MainActivity.class);
                AdvancedSettings.isAdvanced=false;
                //Sending data to another Activity
                //nextScreen.putExtra("name", inputName.getText().toString());
                //nextScreen.putExtra("email", inputEmail.getText().toString());

                // starting new activity
                startActivity(nextScreen);
                finish();
            }
        });

        Button btnNextScreenAdvanced = (Button) findViewById(R.id.advanced);
        //Listening to button event
        btnNextScreenAdvanced.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                //Starting a new Intent
                Intent nextScreen = new Intent(getApplicationContext(), AdvancedSettings.class);

                //Sending data to another Activity
                //nextScreen.putExtra("name", inputName.getText().toString());
                //nextScreen.putExtra("email", inputEmail.getText().toString());

                // starting new activity
                startActivity(nextScreen);
                finish();
            }
        });
        Button btnNextScreenResetDate = (Button) findViewById(R.id.resetDate);
        //Listening to button event
        btnNextScreenResetDate.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                android.content.SharedPreferences preferences = android.preference.PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                android.content.SharedPreferences.Editor editor = preferences.edit();
                editor.clear().commit();
                //Starting a new Intent
                Intent nextScreen = new Intent(getApplicationContext(), MainActivity.class);
                AdvancedSettings.isAdvanced=false;



                //Sending data to another Activity
                //nextScreen.putExtra("name", inputName.getText().toString());
                //nextScreen.putExtra("email", inputEmail.getText().toString());

                // starting new activity
                startActivity(nextScreen);
                finish();
            }
        });


}


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.trening_type, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
