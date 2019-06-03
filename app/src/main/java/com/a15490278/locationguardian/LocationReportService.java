package com.a15490278.locationguardian;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

public class LocationReportService extends Service {

    private SharedPreferences sp;

    private class LocationListener implements android.location.LocationListener {

        Location lastLocation;

        public LocationListener(String provider){
            lastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            lastLocation.set(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {
            showToast("Location provider " + provider + " is disabled");
        }
    }

    @Override
    public void onCreate(){
        super.onCreate();

        sp = getSharedPreferences(SharedKeys.Preferences, Context.MODE_PRIVATE);

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
