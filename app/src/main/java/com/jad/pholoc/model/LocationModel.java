package com.jad.pholoc.model;

/**
 * Model Location
 *
 * @author Jorge Alvarado
 */
public class LocationModel {
    private int id;
    private String name;
    private String address;
    private String notes;
    private double latitude;
    private double longitude;
    private String path;

    public LocationModel(int id, String name, String address, String notes,
                         double latitude, double longitude, String path) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.notes = notes;
        this.latitude = latitude;
        this.longitude = longitude;
        this.path = path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
