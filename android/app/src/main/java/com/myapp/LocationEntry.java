package com.myapp;

import android.os.Parcel;
import android.os.Parcelable;

public class LocationEntry implements Parcelable {
    private float distance;
    private double lat;
    private double lng;
    private String date;
    private String batteryPercentage;
    private String isInOptimizedMode;
    private String isNetworkConnected;
    private String isGPSon;
    private String deviceModel;

    public LocationEntry(float distance, double lat, double lng, String date,
                         String batteryPercentage, String isInOptimizedMode,
                         String isNetworkConnected, String isGPSon, String deviceModel) {
        this.distance = distance;
        this.lat = lat;
        this.lng = lng;
        this.date = date;
        this.batteryPercentage = batteryPercentage;
        this.isInOptimizedMode = isInOptimizedMode;
        this.isNetworkConnected = isNetworkConnected;
        this.isGPSon = isGPSon;
        this.deviceModel = deviceModel;
    }

    protected LocationEntry(Parcel in) {
        distance = in.readFloat();
        lat = in.readDouble();
        lng = in.readDouble();
        date = in.readString();
        batteryPercentage = in.readString();
        isInOptimizedMode = in.readString();
        isNetworkConnected = in.readString();
        isGPSon = in.readString();
        deviceModel = in.readString();
    }

    public static final Creator<LocationEntry> CREATOR = new Creator<LocationEntry>() {
        @Override
        public LocationEntry createFromParcel(Parcel in) {
            return new LocationEntry(in);
        }

        @Override
        public LocationEntry[] newArray(int size) {
            return new LocationEntry[size];
        }
    };

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBatteryPercentage() {
        return batteryPercentage;
    }

    public void setBatteryPercentage(String batteryPercentage) {
        this.batteryPercentage = batteryPercentage;
    }

    public String getIsInOptimizedMode() {
        return isInOptimizedMode;
    }

    public void setIsInOptimizedMode(String isInOptimizedMode) {
        this.isInOptimizedMode = isInOptimizedMode;
    }

    public String getIsNetworkConnected() {
        return isNetworkConnected;
    }

    public void setIsNetworkConnected(String isNetworkConnected) {
        this.isNetworkConnected = isNetworkConnected;
    }

    public String getIsGPSon() {
        return isGPSon;
    }

    public void setIsGPSon(String isGPSon) {
        this.isGPSon = isGPSon;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeFloat(distance);
        parcel.writeDouble(lat);
        parcel.writeDouble(lng);
        parcel.writeString(date);
        parcel.writeString(batteryPercentage);
        parcel.writeString(isInOptimizedMode);
        parcel.writeString(isNetworkConnected);
        parcel.writeString(isGPSon);
        parcel.writeString(deviceModel);
    }
}