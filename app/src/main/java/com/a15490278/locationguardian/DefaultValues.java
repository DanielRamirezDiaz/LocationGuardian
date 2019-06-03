package com.a15490278.locationguardian;

public final class DefaultValues {

    public static float[] LimitsLatitudes(){
        float[] latitudes = new float[4];

        latitudes[0] = 32.623333f; //Upper left
        latitudes[1] = 32.623361f; //Upper right
        latitudes[2] = 32.619795f; //Bottom right
        latitudes[3] = 32.620437f; //Bottom left

        return latitudes;
    }

    public static float[] LimitsLongitudes(){
        float[] longitudes = new float[4];

        longitudes[0] = -115.400134f; //Upper left
        longitudes[1] = -115.392849f; //Upper right
        longitudes[2] = -115.392883f; //Bottom right
        longitudes[3] = -115.400147f; //Bottom left

        return longitudes;
    }

    public static float[] ExitsLatitudes(){
        float[] latitudes = new float[2];

        latitudes[0] = 32.619816f;
        latitudes[1] = 32.619739f;

        return latitudes;
    }

    public static float[] ExitsLongitudes(){
        float[] longitudes = new float[2];

        longitudes[0] = -115.397650f;
        longitudes[1] = -115.395133f;

        return longitudes;
    }

    public static int[] Hours(){
        int[] hours = new int[2];

        hours[0] = 800;
        hours[1] = 1200;

        return hours;
    }
}
