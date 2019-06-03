package com.a15490278.locationguardian;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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


import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.SEND_SMS;

@SuppressLint("ApplySharedPref")
public class MainActivity extends AppCompatActivity {

    public static Activity fa;

    private static final int PERMISSION_REQUEST_CODE = 200;

    SharedPreferences sp;

    Switch aSwitch;
    Button buttonDefaults, buttonSave;
    EditText[] editTextsLimitsLat, editTextsLimitsLong, editTextsExitsLat, editTextsExitsLong, editTextsHour;

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
        startActivityForResult(new Intent(getApplicationContext(), UserActivity.class), 420);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        if(!checkPermission()){
            requestPermission();
        }
    }


    private void setSwitch(){
        aSwitch = findViewById(R.id.switchService);

        aSwitch.setChecked(sp.getBoolean(SharedKeys.ServiceState, false));

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(!checkPermission()){
                        requestPermission();
                        aSwitch.setChecked(false);
                        return;
                    }
                    try{
                        int currentTime = TimeCalculator.currentTime();

                        int initTime = TimeCalculator.initTime(sp);
                        int finalTime = TimeCalculator.finalTime(sp);


                        Context context = getApplicationContext();

                        Intent intent = new Intent(context, LocationReportService.class);

                        if(currentTime >= initTime && currentTime <= finalTime){
                            startService(intent);
                        }
                        else{
                            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                            PendingIntent event = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

                            long milliseconds = TimeCalculator.millisecondsTo(currentTime, initTime);

                            alarmManager.set(AlarmManager.RTC_WAKEUP, milliseconds, event);
                            showToast("Not in time. Alarm manager set.");
                        }
                    }catch (Exception ex){
                        showToast("Something went wrong. Service will not start.");
                        aSwitch.setChecked(false);
                    }
                    SharedPreferences.Editor e = sp.edit();
                    e.putBoolean(SharedKeys.ServiceState, true);
                    e.commit();
                }
                else{
                    stopService(new Intent(getApplicationContext(), LocationReportService.class));
                    SharedPreferences.Editor e = sp.edit();
                    e.putBoolean(SharedKeys.ServiceState, false);
                    e.commit();

                }
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

                if(!areCoordinatesValid()){
                    showToast("Invalid characters detected");
                    return;
                }

                if(!areHoursValid()){
                    showToast("Wrong time format. Use '00:00' - '23:59' and put them in order.");
                    return;
                }

                SharedPreferences.Editor e = sp.edit();

                for(int i = 0; i < 4; i++){
                    e.putFloat(SharedKeys.LimitLatitude(i), Float.valueOf(editTextsLimitsLat[i].getText().toString()));
                    e.putFloat(SharedKeys.LimitLongitude(i), Float.valueOf(editTextsLimitsLong[i].getText().toString()));
                }

                for(int i = 0; i < 2; i++){
                    //int aux = Integer.parseInt(editTextsHour[i].getText().toString().replace(":" , ""));
                    int aux = TimeCalculator.stringToIntTime(editTextsHour[i].toString());
                    e.putInt(SharedKeys.Hour(i), aux);
                }

                e.commit();

                showToast("Saved");
            }
        });

        buttonDefaults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                float[] limitsLatitudes = DefaultValues.LimitsLatitudes();
                float[] limitsLongitudes = DefaultValues.LimitsLongitudes();
                float[] exitsLatitudes = DefaultValues.ExitsLatitudes();
                float[] exitsLongitudes = DefaultValues.ExitsLongitudes();
                int[] hours = DefaultValues.Hours();

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

                    editTextsHour[i].setText(TimeCalculator.intToStringTime(hours[i]));

                    editor.putFloat(SharedKeys.ExitLatitude(i), exitsLatitudes[i]);
                    editor.putFloat(SharedKeys.ExitLongitude(i), exitsLongitudes[i]);

                    editor.putInt(SharedKeys.Hour(i), hours[i]);
                }

                editor.commit();

                showToast("Defaults loaded and saved");
            }
        });
    }

    private boolean areCoordinatesValid(){
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

    private boolean areHoursValid(){
            String regex24Hours = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$";

            boolean rightFormat = editTextsHour[0].getText().toString().matches(regex24Hours) && editTextsHour[1].getText().toString().matches(regex24Hours);

            boolean rightOrder = TimeCalculator.stringToIntTime(editTextsHour[0].getText().toString()) < TimeCalculator.stringToIntTime(editTextsHour[1].getText().toString());

            return rightFormat && rightOrder;
    }

    private boolean areAllFieldsFull(){
        for(int i = 0; i < 4; i++){
            if(editTextsLimitsLat[i].getText().toString().length() == 0 || editTextsLimitsLong[i].getText().toString().length() == 0)
                return false;
        }

        for(int i = 0; i < 2; i++){
            if(editTextsExitsLat[i].getText().toString().length() == 0 || editTextsExitsLong[i].getText().toString().length() == 0 ||
                editTextsHour[i].getText().toString().length() == 0)
                return false;
        }


        return true;
    }

    private void setEditTexts(){
        editTextsLimitsLat = new EditText[4];
        editTextsLimitsLong = new EditText[4];
        editTextsExitsLat = new EditText[2];
        editTextsExitsLong = new EditText[2];
        editTextsHour = new EditText[2];

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

        editTextsHour[0] = findViewById(R.id.editTextIH);
        editTextsHour[1] = findViewById(R.id.editTextFH);

        for(int i = 0; i < 4; i++){
            editTextsLimitsLat[i].setText(String.valueOf(sp.getFloat(SharedKeys.LimitLatitude(i), 0)));
            editTextsLimitsLong[i].setText(String.valueOf(sp.getFloat(SharedKeys.LimitLongitude(i), 0)));
        }

        for(int i = 0; i < 2; i++){
            editTextsExitsLat[i].setText(String.valueOf(sp.getFloat(SharedKeys.ExitLatitude(i),0)));
            editTextsExitsLong[i].setText(String.valueOf(sp.getFloat(SharedKeys.ExitLongitude(i),0)));
            editTextsHour[i].setText(TimeCalculator.intToStringTime(sp.getInt(SharedKeys.Hour(i), 0)));
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



    private boolean checkPermission() {
        int locationGranted = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int smsGranted = ContextCompat.checkSelfPermission(getApplicationContext(), SEND_SMS);


        return locationGranted == PackageManager.PERMISSION_GRANTED && smsGranted == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION, SEND_SMS}, PERMISSION_REQUEST_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {


                    if (checkPermission())
                        showToast("Permissions granted");
                    else {
                        showToast("Permissions denied");

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                                showMessageOKCancel("You need to grant all the requested permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{ACCESS_FINE_LOCATION, SEND_SMS},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }

                    }
                }

                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
}
