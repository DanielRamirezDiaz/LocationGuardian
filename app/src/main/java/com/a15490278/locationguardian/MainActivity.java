package com.a15490278.locationguardian;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

@SuppressLint("ApplySharedPref")
public class MainActivity extends AppCompatActivity {

    public static Activity fa;

    SharedPreferences sp;

    Switch aSwitch;
    Button buttonDefaults, buttonSave;
    EditText[] editTextsLimitsLat, editTextsLimitsLong, editTextsExitsLat, editTextsExitsLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("Location Manager");

        sp = getSharedPreferences(SharedKeys.Preferences, MODE_PRIVATE);
        SharedPreferences.Editor e = sp.edit();
        e.putBoolean(SharedKeys.Logged, false);
        e.commit();

        fa = this;

        login();

        setSwitch();

        setButtons();

        setEditTexts();

        setFab();
    }

    private void login(){
        startActivity(new Intent(getApplicationContext(), UserActivity.class));
    }

    private void setSwitch(){
        aSwitch = findViewById(R.id.switchService);

        aSwitch.setChecked(sp.getBoolean(SharedKeys.ServiceState, false));

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor e = sp.edit();
                if(isChecked){
                    startService(new Intent(getApplicationContext(), LocationReportService.class));
                    e.putBoolean(SharedKeys.ServiceState, true);
                }
                else{
                    stopService(new Intent(getApplicationContext(), LocationReportService.class));
                    e.putBoolean(SharedKeys.ServiceState, false);
                }

                e.commit();
            }
        });
    }

    private void setButtons(){
        buttonSave = findViewById(R.id.buttonSave);
        buttonDefaults = findViewById(R.id.buttonDefaults);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!areAllFieldsFull()){
                    showToast("All fields must be filled");
                    return;
                }

                if(!areAllFieldsValid()){
                    showToast("Invalid characters detected");
                    return;
                }

                SharedPreferences.Editor e = sp.edit();

                for(int i = 0; i < 4; i++){
                    e.putFloat(SharedKeys.LimitLatitude(i), Float.valueOf(editTextsLimitsLat[i].getText().toString()));
                    e.putFloat(SharedKeys.LimitLongitude(i), Float.valueOf(editTextsLimitsLong[i].getText().toString()));
                }

                e.commit();

                showToast("Saved");
            }
        });

        buttonDefaults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                float[] limitsLatitudes = DefaultLocations.LimitsLatitudes();
                float[] limitsLongitudes = DefaultLocations.LimitsLongitudes();
                float[] exitsLatitudes = DefaultLocations.ExitsLatitudes();
                float[] exitsLongitudes = DefaultLocations.ExitsLongitudes();

                SharedPreferences.Editor editor = sp.edit();

                for(int i = 0; i < 4; i++){
                    editTextsLimitsLat[i].setText(String.valueOf(limitsLatitudes[i]));
                    editTextsLimitsLong[i].setText(String.valueOf(limitsLongitudes[i]));

                    editor.putFloat(SharedKeys.LimitLatitude(i), limitsLatitudes[i]);
                    editor.putFloat(SharedKeys.LimitLongitude(i), limitsLongitudes[i]);
                }

                for(int i = 0; i < 2; i++){
                    editTextsExitsLat[i].setText(String.valueOf(exitsLatitudes[i]));
                    editTextsExitsLong[i].setText(String.valueOf(exitsLongitudes[i]));

                    editor.putFloat(SharedKeys.ExitLatitude(i), exitsLatitudes[i]);
                    editor.putFloat(SharedKeys.ExitLongitude(i), exitsLongitudes[i]);
                }

                editor.commit();

                showToast("Defaults loaded and saved");
            }
        });
    }

    private boolean areAllFieldsValid(){
        try{
            for(int i = 0; i < 4; i++){
                Float.valueOf(editTextsLimitsLat[i].getText().toString());
                Float.valueOf(editTextsLimitsLong[i].getText().toString());
            }

            for(int i = 0; i < 2; i++){
                Float.valueOf(editTextsExitsLat[i].getText().toString());
                Float.valueOf(editTextsExitsLong[i].getText().toString());
            }

            return true;
        }
        catch (Exception ex){
            return false;
        }
    }

    private boolean areAllFieldsFull(){
        for(int i = 0; i < 4; i++){
            if(editTextsLimitsLat[i].getText().toString().length() == 0 || editTextsLimitsLong[i].getText().toString().length() == 0)
                return false;
        }

        for(int i = 0; i < 2; i++){
            if(editTextsExitsLat[i].getText().toString().length() == 0 || editTextsExitsLong[i].getText().toString().length() == 0)
                return false;
        }

        return true;
    }

    private void setEditTexts(){
        editTextsLimitsLat = new EditText[4];
        editTextsLimitsLong = new EditText[4];
        editTextsExitsLat = new EditText[2];
        editTextsExitsLong = new EditText[2];

        editTextsLimitsLat[0] = findViewById(R.id.editTextLat0);
        editTextsLimitsLat[1] = findViewById(R.id.editTextLat1);
        editTextsLimitsLat[2] = findViewById(R.id.editTextLat2);
        editTextsLimitsLat[3] = findViewById(R.id.editTextLat3);

        editTextsLimitsLong[0] = findViewById(R.id.editTextLong0);
        editTextsLimitsLong[1] = findViewById(R.id.editTextLong1);
        editTextsLimitsLong[2] = findViewById(R.id.editTextLong2);
        editTextsLimitsLong[3] = findViewById(R.id.editTextLong3);

        editTextsExitsLat[0] = findViewById(R.id.editTextLatEx0);
        editTextsExitsLat[1] = findViewById(R.id.editTextLatEx1);

        editTextsExitsLong[0] = findViewById(R.id.editTextLongEx0);
        editTextsExitsLong[1] = findViewById(R.id.editTextLongEx1);

        for(int i = 0; i < 4; i++){
            editTextsLimitsLat[i].setText(String.valueOf(sp.getFloat(SharedKeys.LimitLatitude(i), 0)));
            editTextsLimitsLong[i].setText(String.valueOf(sp.getFloat(SharedKeys.LimitLongitude(i), 0)));
        }

        for(int i = 0; i < 2; i++){
            editTextsExitsLat[i].setText(String.valueOf(sp.getFloat(SharedKeys.ExitLatitude(i),0)));
            editTextsExitsLong[i].setText(String.valueOf(sp.getFloat(SharedKeys.ExitLongitude(i),0)));
        }
    }

    private void setFab(){
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setImageResource(android.R.drawable.ic_lock_idle_lock);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(getApplicationContext(), UserActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showToast(String text){
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }
}
