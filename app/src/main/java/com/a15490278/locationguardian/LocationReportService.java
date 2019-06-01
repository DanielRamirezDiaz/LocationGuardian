package com.a15490278.locationguardian;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.widget.Toast;

public class LocationReportService extends Service {

    SharedPreferences sp;

    float[] latitudes;
    float[] longitudes;


    public LocationReportService() {
    }

    @Override
    public void onCreate(){
        super.onCreate();

        sp = getSharedPreferences(SharedKeys.Preferences, Context.MODE_PRIVATE);

        latitudes = new float[4];
        longitudes = new float[4];

        for (int i = 0; i < 4; i++){
            latitudes[i] = sp.getFloat(SharedKeys.LimitLatitude(i), 0);
            longitudes[i] = sp.getFloat(SharedKeys.LimitLongitude(i),0);
        }

        showToast("Location Guardian Service is enabled");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        super.onStartCommand(intent, flags, startId);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy(){
        showToast("Location Guardian Service is disabled");
        super.onDestroy();
    }

    //------------------------------------------------

    public void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void showToastLong(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

}
