package pt.ipleiria.dei.iair.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import pt.ipleiria.dei.iair.R;
import pt.ipleiria.dei.iair.Utils.GPSActivity;
import pt.ipleiria.dei.iair.Utils.GPSUtils;
import pt.ipleiria.dei.iair.Utils.HttpCallBack;
import pt.ipleiria.dei.iair.Utils.HttpUtils;
import pt.ipleiria.dei.iair.Utils.ThinkSpeak;
import pt.ipleiria.dei.iair.controller.IAirManager;
import pt.ipleiria.dei.iair.model.CityAssociation;

public class MySensorsActivity extends GPSActivity {

    private TextView temperatureSensorValue,humiditySensorValue,pressureSensorValue;
    private SensorManager sensorManager;
    private String locationName;
    private LatLng location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_sensors);
        IAirManager.INSTANCE.setMySensorsActivity(this);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        bindTextViews();
        setSensorManager();


    }

    private void setSensorManager() {
        try {
            SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            IAirManager.INSTANCE.setSensorManager(sensorManager);
            setSensors(sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE),sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE),
                    sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY));
        }catch (Exception e){
            return;
        }


    }

    public void setSensors(Sensor temperatureSensor, Sensor pressureSensor, Sensor humiditySensor) {
        if(temperatureSensor==null){
            temperatureSensorValue.setText("N/A");
        }else{
            IAirManager.INSTANCE.setSensor(temperatureSensor);
            temperatureSensorValue.setText("0.0");
        }
        if(pressureSensor==null){
            pressureSensorValue.setText("N/A");
        }else{
            IAirManager.INSTANCE.setSensor(pressureSensor);
            pressureSensorValue.setText("0.0");
        }
        if(humiditySensor==null){
            humiditySensorValue.setText("N/A");
        }else{
            IAirManager.INSTANCE.setSensor(humiditySensor);
            humiditySensorValue.setText("0.0");
        }

    }

    private void bindTextViews() {
        temperatureSensorValue = (TextView) findViewById(R.id.textViewTemperatureSensorValue);
        pressureSensorValue = (TextView) findViewById(R.id.textViewPressureSensorValue);
        humiditySensorValue = (TextView) findViewById(R.id.textViewHumiditySensorValue);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        Intent intent = null;
        if (id == R.id.menu_dashboard) {
            intent = new Intent(this, DashboardActivity.class);

        }  else if (id == R.id.menu_my_sensors) {
            intent = new Intent(this, MySensorsActivity.class);

        } else if (id == R.id.menu_create_message) {
            intent = new Intent(this, CreateInformativeMessageActivity.class);

        } else if (id == R.id.menu_map) {
            intent = new Intent(this, MapActivity.class);

        } else if (id == R.id.menu_locations) {
            intent = new Intent(this, LocationActivity.class);

        } else if (id == R.id.menu_settings) {
            intent = new Intent(this, SettingsActivity.class);

        }else if (id == R.id.menu_send_data) {
            GPSUtils gpsUtils = new GPSUtils(this);
            Location location = gpsUtils.getLocation();
            //  ThinkSpeak.sendData(this, 39.749495, -8.807290, IAirManager.INSTANCE.getTemperature(), IAirManager.INSTANCE.getPresure(), IAirManager.INSTANCE.getHumity());
            //ThinkSpeak.INSTANCE.sendData(this,location.getLatitude(), location.getLongitude(), IAirManager.INSTANCE.getTemperature(), IAirManager.INSTANCE.getPresure(), IAirManager.INSTANCE.getHumity());

            CityAssociation city = IAirManager.INSTANCE.getCityAssociation(IAirManager.INSTANCE.getCurrentLocationName().toString());

            String temp = IAirManager.INSTANCE.getTemperature();
            String press = IAirManager.INSTANCE.getPresure();
            String hum = IAirManager.INSTANCE.getHumity();

            System.out.println("tamanho citys:" + IAirManager.INSTANCE.getAllCityAssociations().size());

            if (city != null) {

                pt.ipleiria.dei.iair.model.Channel channel = new pt.ipleiria.dei.iair.model.Channel(temp, press, hum, city.getREGION_NAME(),String.valueOf(IAirManager.INSTANCE.getCurrentLocation().latitude),String.valueOf(IAirManager.INSTANCE.getCurrentLocation().longitude));
                //channel=IAirManager.INSTANCE.getChannel(local);
                ThinkSpeak.INSTANCE.insertInChannel(channel, this);

            }
        } else if (id == R.id.menu_gps) {
            enableGPS();

        }
        if(intent != null) {
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setTemperatureValue(float eventValue) {
        temperatureSensorValue.setText(String.valueOf(eventValue) + " ºC");
    }

    public void setHumidityValue(float eventValue) {
        humiditySensorValue.setText(String.valueOf(eventValue) + " %");
    }

    public void setPressureValue(float eventValue) {
        pressureSensorValue.setText(String.valueOf(eventValue) + " hpa");
    }

    public String getTemperatureValue() {
        return temperatureSensorValue.getText().toString();
    }

    public String getPressureValue() {
        return pressureSensorValue.getText().toString();
    }

    public String getHumidityValue() {
        return humiditySensorValue.getText().toString();
    }

    public void getVicinity(LatLng latLng, int radius){

        HttpUtils.Get(new HttpCallBack() {

            @SuppressLint("ResourceType")
            @Override
            public void onResult(JSONObject response) throws JSONException {

                if(response.getJSONArray("results").length()>0){

                    double latitude=Double.parseDouble(response.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").get("lat").toString());
                    double longitude=Double.parseDouble(response.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").get("lng").toString());

                     location = new LatLng(latitude, longitude);
                    locationName = response.getJSONArray("results").getJSONObject(0).get("vicinity").toString();

                    CityAssociation city = IAirManager.INSTANCE.getCityAssociation(locationName);

                    pt.ipleiria.dei.iair.model.Channel channel = new pt.ipleiria.dei.iair.model.Channel(IAirManager.INSTANCE.getTemperature(),
                            IAirManager.INSTANCE.getPresure(), IAirManager.INSTANCE.getHumity(), locationName,String.valueOf(location.latitude)
                            ,String.valueOf(location.longitude));


                    if (city == null) {

                        ThinkSpeak.INSTANCE.createNewChannel(locationName, String.valueOf(location.latitude),String.valueOf(location.longitude),getApplicationContext());
                        System.out.println("LOCAL :" + locationName);
                        city = IAirManager.INSTANCE.getCityAssociation(locationName);

                        System.out.println("tamanho citys:" + IAirManager.INSTANCE.getAllCityAssociations().size());
                        if (city != null){

                            //ThinkSpeak.insertInChannel(channel,this);

                            //channel=IAirManager.INSTANCE.getChannel(local);
                            ThinkSpeak.INSTANCE.insertInChannel(channel, getApplicationContext());
                        }

                    }else{
                        //channel=IAirManager.INSTANCE.getChannel(local);
                        ThinkSpeak.INSTANCE.insertInChannel(channel, getApplicationContext());
                    }



                }

            }

            @Override
            public void onResult(String response) {

            }
        }, "https://maps.googleapis.com/maps/api/place/search/json?radius="+String.valueOf(radius)+"&sensor=false&type=locality&key=AIzaSyCel8hjaRHf6-DK0fe3KmIsXp1MMP-RYQk&location="+latLng.latitude+","+latLng.longitude, this);

    }

}
