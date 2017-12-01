package pt.ipleiria.dei.iair.controller;

import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;

import java.util.LinkedList;

import pt.ipleiria.dei.iair.model.Alerts;
import pt.ipleiria.dei.iair.model.Channel;
import pt.ipleiria.dei.iair.model.CityAssociation;
import pt.ipleiria.dei.iair.model.IAirSensorListener;
import pt.ipleiria.dei.iair.model.Location;
import pt.ipleiria.dei.iair.view.CreateInformativeMessageActivity;
import pt.ipleiria.dei.iair.view.MySensorsActivity;

/**
 * Created by ricar on 06/11/2017.
 */

public enum IAirManager {
    INSTANCE;

    SensorManager sensorManager;

    MySensorsActivity mySensorsActivity;

    CreateInformativeMessageActivity createInformativeMessageActivity;

    private String humity;
    private String presure;
    private String temperature;

    SharedPreferences sharedPreferences;

    private LatLng currentLocation;
    private String currentLocationName;

    private LatLng favoriteLocationLatLng;
    private String favoriteLocationName;

    private String username;

    private LinkedList<CityAssociation> listCityAssotiation=new LinkedList<>();

    private LinkedList<Alerts> listAlerts=new LinkedList<>();
    private LinkedList<Channel> listChannel=new LinkedList<>();
    private LinkedList<String> listaStrings;
    private int cityIdList;

    public CityAssociation getCityAssociation(String LocationName){

        for (CityAssociation city:listCityAssotiation) {
            if(city.getREGION_NAME().equalsIgnoreCase(LocationName)){
                return city;
            }
        }
        return null;
    }

    public void addCityAssociation(CityAssociation city){
        listCityAssotiation.add(city);
        System.out.println("City:" + city.getChannel());
    }

    public Alerts getAlerts(String alertName){

        for (Alerts alert:listAlerts) {
            if(alert.getName().equals(alertName)){
                return alert;
            }
        }
        return null;
    }

    public void addAlert(Alerts alert){
        listAlerts.add(alert);
    }

    public Channel getChannel(String channelname){

        for (Channel channel:listChannel) {
            if(channel.getName().equals(channelname)){
                return channel;
            }
        }
        return null;
    }

    public void addChannel(Channel channel){
        listChannel.add(channel);
    }




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
        setSensor(sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE));
        setSensor(sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE));
        setSensor(sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY));
    }

    public void changeTemperatureValue(float[] eventValues) {
        if(mySensorsActivity!=null)
            mySensorsActivity.setTemperatureValue(eventValues[0]);
        temperature = String.valueOf(eventValues[0]);
        System.out.println("TEmperature:" + String.valueOf(eventValues[0]));
    }

    public void changeHumidityValue(float[] eventValues) {
        if(mySensorsActivity!=null)
            mySensorsActivity.setHumidityValue(eventValues[0]);
        humity = String.valueOf(eventValues[0]);
    }

    public void changePressureValue(float[] eventValues) {
        if(mySensorsActivity!=null)
            mySensorsActivity.setPressureValue(eventValues[0]);
        presure = String.valueOf(eventValues[0]);

    }

    public void setMySensorsActivity(MySensorsActivity mySensorsActivity) {
        this.mySensorsActivity = mySensorsActivity;
    }
    public void setCreateInformativeMessageActivity(CreateInformativeMessageActivity createInformativeMessageActivity) {
        this.createInformativeMessageActivity = createInformativeMessageActivity;
    }

    public void setSensor(Sensor sensor) {
        sensorManager.registerListener(new IAirSensorListener(),sensor,SensorManager.SENSOR_DELAY_NORMAL);
    }


    public void saveFavoriteLocation(LatLng latLng, String name) {
        IAirManager.INSTANCE.setFavoriteLocationName(name);
        IAirManager.INSTANCE.setFavoriteLocation(latLng.latitude +";"+latLng.longitude);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("favoriteLocation",latLng.latitude+ ";" + latLng.longitude);
        //guardar também dados que sejam necessarios no dashboard como o nome, da localização favorita por ex
        editor.putString("favoriteLocationName",name.toString());
        editor.commit();

    }


    public void saveFavoriteLocation(Place favoriteLocation) {

        favoriteLocationName= favoriteLocation.getName().toString();
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

    public LinkedList<CityAssociation> getAllCityAssociations() {
        return listCityAssotiation;
    }

    public LinkedList<Alerts> getAllAlerts() {
        return listAlerts;
    }

    public LinkedList<Channel> getAllChannels() {
        return listChannel;
    }

    public int getCityIdFavoriteLocation(){
        if(listCityAssotiation.size()!=0) {
            for (int i = 0; i < listCityAssotiation.size(); i++) {
                if (listCityAssotiation.get(i).getREGION_NAME() == currentLocationName)
                    return i;
            }
        }
        return -1;
    }

    public int getCityIdLast() {
        return cityIdList;
    }

    public void setCityIdLast(int cityIdList) {
        this.cityIdList = cityIdList;
    }

    public void addToListStrings(String str){
        listaStrings.add(str);
    }

    public LinkedList<String> getListaStrings() {
        return listaStrings;
    }

    public void setListaStrings(LinkedList<String> listaStrings) {
        this.listaStrings = listaStrings;
    }

    public LatLng getCurrentLocation() {
        return currentLocation;
    }

    public  void setCurrentLocation(LatLng latLng) {
         this.currentLocation= latLng;
    }

    public String getCurrentLocationName() {
        return currentLocationName;
    }

    public void setCurrentLocationName(String currentLocationName) {
        this.currentLocationName = currentLocationName;
    }
}
