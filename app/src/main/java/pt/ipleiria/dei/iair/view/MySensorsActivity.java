package pt.ipleiria.dei.iair.view;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import pt.ipleiria.dei.iair.R;
import pt.ipleiria.dei.iair.Utils.GPSActivity;
import pt.ipleiria.dei.iair.Utils.GPSUtils;
import pt.ipleiria.dei.iair.Utils.ThinkSpeak;
import pt.ipleiria.dei.iair.controller.IAirManager;

public class MySensorsActivity extends GPSActivity {

    private TextView temperatureSensorValue,humiditySensorValue,pressureSensorValue;
    private SensorManager sensorManager;

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


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
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
            //ThinkSpeak.sendData(this,39.749495, -8.807290, IAirManager.INSTANCE.getTemperature(), IAirManager.INSTANCE.getPresure(), IAirManager.INSTANCE.getHumity());
            ThinkSpeak.INSTANCE.sendData(this,location.getLatitude(), location.getLongitude(), IAirManager.INSTANCE.getTemperature(), IAirManager.INSTANCE.getPresure(), IAirManager.INSTANCE.getHumity());
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
}
