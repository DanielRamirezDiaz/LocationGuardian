package com.a15490278.locationguardian;

public final class SharedKeys {
    public static final String Preferences = "preferencesKey";

    public static final String ServiceState = "serviceStateKey";

    public static final String User = "userKey";
    public static final String Password = "passwordKey";
    public static final String Email = "emailKey";
    public static final String Phone = "phoneKey";
    public static final String Logged = "loggedKey";

    public static final String LimitLatitude(int index){
        return "limitLatitude" + index + "Key";
    }

    public static final String LimitLongitude(int index){ return "limitLongitude" + index + "Key"; }

    public static final String ExitLatitude(int index){
        return "exitLatitude" + index + "Key";
    }

    public static final String ExitLongitude(int index){
        return "exitLongitude" + index + "Key";
    }

    public static final String Hour(int index) { return  "hour" + index + "Key"; }
}
