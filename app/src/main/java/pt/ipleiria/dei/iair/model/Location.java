package pt.ipleiria.dei.iair.model;

/**
 * Created by ricar on 23/11/2017.
 */

public class Location {
    private String locationName;
    private double latitude;
    private double longitude;

    public Location(String locationName, double latitude, double longitude) {
        this.locationName = locationName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Location() {

    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getLocationName() {
        return locationName.split(",", 2)[0];
    }
}
