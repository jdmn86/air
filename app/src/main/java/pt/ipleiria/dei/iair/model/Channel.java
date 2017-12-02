package pt.ipleiria.dei.iair.model;

import java.util.Date;

/**
 * Created by joaonascimento on 23/11/2017.
 */

public class Channel {
    private Date date;
    private String name;
    private String temperature;
    private String pressure;
    private String humity;
    private String latitude;
    private String longitude;


    public Channel(Date date, String temperature, String pressure, String humity, String name, String latitude, String longitude) {
        this.date = date;
        this.name = name;
        this.temperature = temperature;
        this.pressure = pressure;
        this.humity = humity;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Date getDate() {
        return date;
    }

    public Channel(String temperature, String pressure, String humity, String name, String latitude, String longitude) {
        this.temperature = temperature;
        this.pressure = pressure;
        this.humity = humity;
        this.name=name;
        this.latitude=latitude;
        this.longitude=longitude;

    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getHumity() {
        return humity;
    }

    public void setHumity(String humity) {
        this.humity = humity;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
