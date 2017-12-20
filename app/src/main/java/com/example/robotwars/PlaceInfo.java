package com.example.robotwars;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

/**
 * A class for storing information about the user's location.
 */
public class PlaceInfo {

    private String name;
    private String address;
    private String id;
    private LatLng latLng;
    private String websiteUri;
    private String phoneNumber;

    /**
     * Constructor with some info about the location
     */
    public PlaceInfo(String name, String address, String id, LatLng latLng) {
        this.name = name;
        this.address = address;
        this.id = id;
        this.latLng = latLng;
    }

    /**
     * Constructor with all the info about the location
     */
    public PlaceInfo(String name, String address, String id,
                     LatLng latLng, String phoneNumber, String websiteUri) {
        this.name = name;
        this.address = address;
        this.id = id;
        this.latLng = latLng;
        this.phoneNumber = phoneNumber;
        this.websiteUri = websiteUri;
    }

    /**
     * Empty constructor
     */
    public PlaceInfo() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getWebsiteUri() {
        return websiteUri;
    }

    public void setWebsiteUri(String websiteUri) {
        this.websiteUri = websiteUri;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "PlaceInfo{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", id='" + id + '\'' +
                ", websiteUri='" + websiteUri + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", latLng=" + latLng +
                '}';
    }

}

