package pt.ipleiria.dei.iair.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import pt.ipleiria.dei.iair.model.IAirSensorListener;
import pt.ipleiria.dei.iair.view.MapActivity;
import pt.ipleiria.dei.iair.view.MySensorsActivity;

/**
 * Created by ricar on 06/11/2017.
 */

public enum IAirManager {
    INSTANCE;

    SensorManager sensorManager;

    MySensorsActivity mySensorsActivity;

    private String humity;
    private String presure;
    private String temperature;

    Place favoriteLocation;
    Place selectedPlace;
    SharedPreferences sharedPreferences;
    LatLng favoriteLocationLatLng;
    private String favoriteLocationName;
    private String username;


    public String getHumity() {
        return humity;
    }

    public String getPresure() {
        return presure;
    }

    public String getTemperature() {
        return temperature;
    }


    public void setSensorManager(SensorManager sensorManager) {
        this.sensorManager = sensorManager;
    }

    public void changeTemperatureValue(float[] eventValues) {
        mySensorsActivity.setTemperatureValue(eventValues[0]);
        temperature = String.valueOf(eventValues[0]);
    }

    public void changeHumidityValue(float[] eventValues) {
        mySensorsActivity.setHumidityValue(eventValues[0]);
        humity = String.valueOf(eventValues[0]);
    }

    public void changePressureValue(float[] eventValues) {
        mySensorsActivity.setPressureValue(eventValues[0]);
        presure = String.valueOf(eventValues[0]);

    }

    public void setMySensorsActivity(MySensorsActivity mySensorsActivity) {
        this.mySensorsActivity = mySensorsActivity;
    }

    public void setSensor(Sensor sensor) {

        sensorManager.registerListener(new IAirSensorListener(),sensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    public SensorManager getSensorManager() {
        return sensorManager;
    }

    public void setSelectedPlace(Place place) {
        this.selectedPlace = place;
    }

    public Place getFavoriteLocation() {
        return favoriteLocation;
    }





    public void saveFavoriteLocation(Place favoriteLocation) {
        this.favoriteLocation = favoriteLocation;
        this.favoriteLocationLatLng=favoriteLocation.getLatLng();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("favoriteLocation",favoriteLocation.getLatLng().latitude + ";" + favoriteLocation.getLatLng().longitude);
        //guardar também dados que sejam necessarios no dashboard como o nome, da localização favorita por ex
        editor.putString("favoriteLocationName",favoriteLocation.getName().toString());
        editor.commit();
    }

    public void setSharedPreferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        setFavoriteLocation(sharedPreferences.getString("favoriteLocation","null"));
        setFavoriteLocationName(sharedPreferences.getString("favoriteLocationName","null"));
        setUsername(sharedPreferences.getString("username","null"));
    }

    public LatLng getFavoriteLocationLatLng() {
        return favoriteLocationLatLng;
    }

    public void setFavoriteLocation(String string) {
        if(string.equals("null")) return;
        String[] strs = string.split(";");
        favoriteLocationLatLng=new LatLng(Double.parseDouble(strs[0]),Double.parseDouble(strs[1]));
    }

    public void setFavoriteLocationName(String favoriteLocationName) {
        this.favoriteLocationName = favoriteLocationName;
    }

    public String getFavoriteLocationName() {
        return favoriteLocationName;
    }

    public void saveUsername(String username) {
        this.username = username;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.commit();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
