package com.sqbnet.expressassistant.mode;

/**
 * Created by virgil on 7/15/15.
 */
public class MyLocation {

    private double latitude;
    private double longitude;

    public MyLocation(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude(){
        return this.latitude;
    }

    public double getLongitude(){
        return this.longitude;
    }
}
