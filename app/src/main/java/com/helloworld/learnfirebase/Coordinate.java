package com.helloworld.learnfirebase;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
class Coordinate {

    private String longitude;
    private String latitude;

    public Coordinate() {
    }

    public Coordinate(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }
}