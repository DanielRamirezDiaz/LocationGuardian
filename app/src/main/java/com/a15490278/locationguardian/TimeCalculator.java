package com.a15490278.locationguardian;

import android.content.SharedPreferences;

import java.util.Calendar;

public class TimeCalculator {
    public static String intToStringTime(int intTime){
        String time = String.valueOf(intTime);

        if(time.length() == 1){
            return "00:0" + intTime;
        }
        else if(time.length() == 2){
            return "00:" + intTime;
        }
        else if (time.length() == 3){
            int hours = intTime / 100;
            String aux = "0" + hours + ":" + (intTime - (hours * 100));
            if(aux.length() == 4)
                aux = aux.concat("0");
            return aux;
        }
        else{
            int hours = intTime / 100;
            String aux = hours + ":" + (intTime - (hours*100));
            if(aux.length() == 4)
                aux = aux.concat("0");
            return aux;
        }
    }

    public static int stringToIntTime(String time){
        return Integer.parseInt(time.replace(":" , ""));
    }

    public static int currentTime(){
        String currentTimeS = Calendar.getInstance().getTime().toString().split(" ")[3].substring(0, 5);

        String[] splitCurrentTime = currentTimeS.split(":");

        int currentTime = (Integer.parseInt(splitCurrentTime[0]) * 100) + (Integer.parseInt(splitCurrentTime[1]));

        return currentTime;
    }

    public static int initTime(SharedPreferences sp){
       return sp.getInt(SharedKeys.Hour(0), 0);
    }

    public static int finalTime(SharedPreferences sp){
        return sp.getInt(SharedKeys.Hour(1), 0);
    }

    public static long millisecondsTo(int currentTime, int initTime){
        long milliseconds;

        if (currentTime() < initTime)
            milliseconds = (initTime - currentTime) * 60000;
        else
            milliseconds = (initTime - currentTime + 2359) * 60000;

        return milliseconds;
    }
}
