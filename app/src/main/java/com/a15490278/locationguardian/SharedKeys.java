package com.a15490278.locationguardian;

public final class SharedKeys {
    public static String Preferences = "preferencesKey";

    public static String ServiceState = "serviceStateKey";

    public static String User = "userKey";
    public static String Password = "passwordKey";
    public static String Email = "emailKey";
    public static String Phone = "phoneKey";
    public static String Logged = "loggedKey";

    public static String LimitLatitude(int index){
        return "limitLatitude" + index + "Key";
    }

    public static String LimitLongitude(int index){
        return "limitLongitude" + index + "Key";
    }

    public static String ExitLatitude(int index){
        return "exitLatitude" + index + "Key";
    }

    public static String ExitLongitude(int index){
        return "exitLongitude" + index + "Key";
    }


}
