package com.a15490278.locationguardian;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.widget.Toast;

@SuppressLint("ApplySharedPref")
public class LocationReportService extends Service {

    private SharedPreferences sp;
    private Intent intent;
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    public LocationManager locationManager;
    public LocationListener listener;
    public Location previousBestLocation = null;


    private class LocationListener implements android.location.LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            if(isBetterLocation(location, previousBestLocation)) {
                location.getLatitude();
                location.getLongitude();
                /*
                intent.putExtra("Latitude", location.getLatitude());
                intent.putExtra("Longitude", location.getLongitude());
                intent.putExtra("Provider", location.getProvider());
                sendBroadcast(intent);
                */

                int currentTime = TimeCalculator.currentTime();
                int initTime = TimeCalculator.initTime(sp);
                int finalTime = TimeCalculator.finalTime(sp);

                if(currentTime >= initTime && currentTime <= finalTime){
                    String message = "Lat: " + location.getLatitude() + " Long: " + location.getLongitude() + " is ";
                    if(isInside(location)){
                        message += "inside. ";
                    }
                    else{
                        message += "outside. ";

                        int exit = whichExit(location);
                        String exitMessage;

                        switch (exit){
                            case 0:
                                message += "Used the firs exit.";
                                break;
                            case 1:
                                message += "Used the second exit.";
                                break;
                            default:
                                message += "Probably jumped the wall.";
                                break;
                        }

                        sendSMS(sp.getString(SharedKeys.Phone, null), message);
                        sendEmail();

                        Context context = getApplicationContext();

                        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                        PendingIntent event = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

                        long milliseconds = TimeCalculator.millisecondsTo(currentTime, initTime);

                        alarmManager.set(AlarmManager.RTC_WAKEUP, milliseconds, event);

                        stopSelf();
                    }

                    showToastLong(message);
                }
                else{
                    Context context = getApplicationContext();

                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    PendingIntent event = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

                    long milliseconds = TimeCalculator.millisecondsTo(currentTime, initTime);

                    alarmManager.set(AlarmManager.RTC_WAKEUP, milliseconds, event);
                    showToast("Not in time. Alarm manager set.");
                }

            }
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

        intent = new Intent("Intent");

        showToast("Location Guardian Service is enabled");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        super.onStartCommand(intent, flags, startId);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = new LocationListener();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            stopSelf();
            showToast("No permission");
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 4000, 0, (LocationListener) listener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 0, listener);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy(){
        //showToast("Location Guardian Service is disabled");
        locationManager.removeUpdates(listener);
        super.onDestroy();
    }

    //------------------------------------------------

    public void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void showToastLong(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private boolean isInside(Location location){
        Location upperLeft = new Location("");
        Location upperRight = new Location("");
        Location lowerRight = new Location("");
        Location lowerLeft = new Location("");

        upperLeft.setLatitude(sp.getFloat(SharedKeys.LimitLatitude(0), 0));
        upperLeft.setLongitude(sp.getFloat(SharedKeys.LimitLongitude(0),0));

        upperRight.setLatitude(sp.getFloat(SharedKeys.LimitLatitude(1), 0));
        upperRight.setLongitude(sp.getFloat(SharedKeys.LimitLongitude(1),0));

        lowerRight.setLatitude(sp.getFloat(SharedKeys.LimitLatitude(2), 0));
        lowerRight.setLongitude(sp.getFloat(SharedKeys.LimitLongitude(2),0));

        lowerLeft.setLatitude(sp.getFloat(SharedKeys.LimitLatitude(3), 0));
        lowerLeft.setLongitude(sp.getFloat(SharedKeys.LimitLongitude(3),0));


        //A lazy check. If distance to any corner is more than the distance from corner to corner, it's out
        if(location.distanceTo(upperLeft) > upperLeft.distanceTo(lowerRight) || location.distanceTo(upperRight) > upperRight.distanceTo(lowerLeft))
            return false;


        //Another lazy check...
        if((location.getLatitude() > upperLeft.getLatitude() && location.getLatitude() > upperLeft.getLatitude()) || //out up
                (location.getLongitude() > upperRight.getLongitude() && location.getLongitude() > lowerRight.getLongitude()) || //out right
                (location.getLatitude() < lowerRight.getLatitude() && location.getLatitude() < lowerLeft.getLatitude()) || //out down
                (location.getLongitude() < lowerLeft.getLongitude() && location.getLongitude() < upperLeft.getLongitude())  //out left
        ){
            return false;
        }

        return true;
    }

    private int whichExit(Location location){
        int maxDistance = 10;
        try{

            Location exit0 = new Location("");
            Location exit1 = new Location("");

            exit0.setLatitude(sp.getFloat(SharedKeys.ExitLatitude(0), 0));
            exit0.setLongitude(sp.getFloat(SharedKeys.ExitLongitude(0), 0));

            exit1.setLatitude(sp.getFloat(SharedKeys.ExitLatitude(1), 0));
            exit1.setLongitude(sp.getFloat(SharedKeys.ExitLongitude(1), 0));

            if(location.distanceTo(exit0) < maxDistance)
                return 0;

            else if(location.distanceTo(exit1) < maxDistance)
                return 1;

            else
                return 2;
        }
        catch (Exception ex){
            return 2;
        }

    }

    private void sendSMS(String number, String message){
        try{
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(number, null, message, null, null);
            showToast("SMS sent");
        }
        catch (Exception ex){
            showToast("Cant't send sms");
        }
    }

    private void sendEmail(){
        try {

        }
        catch (Exception ex){

        }
    }

    //-------------------------------------------------

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

}
