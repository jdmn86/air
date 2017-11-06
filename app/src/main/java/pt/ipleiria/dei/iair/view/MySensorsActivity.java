package pt.ipleiria.dei.iair.view;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import pt.ipleiria.dei.iair.R;
import pt.ipleiria.dei.iair.model.IAirManager;

public class MySensorsActivity extends AppCompatActivity {

    private TextView temperatureSensorValue,humiditySensorValue,pressureSensorValue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_sensors);
        IAirManager.INSTANCE.setMySensorsActivity(this);
        bindTextViews();
        setSensors();


    }

    private void setSensors() {
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        IAirManager.INSTANCE.setSensorManager(sensorManager);

        try {
            Sensor temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
            IAirManager.INSTANCE.setSensor(temperatureSensor);
            temperatureSensorValue.setText("0.0");
        }catch (Exception e){
            temperatureSensorValue.setText("N/A");
            Toast.makeText(this, R.string.toast_no_temperature_sensor_available,Toast.LENGTH_SHORT).show();
        }
        try {
            Sensor pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
            IAirManager.INSTANCE.setSensor(pressureSensor);
            pressureSensorValue.setText("0.0");
        }catch (Exception e){
            pressureSensorValue.setText("N/A");
            Toast.makeText(this, R.string.toast_no_pressure_sensor_available,Toast.LENGTH_SHORT).show();
        }
        try {
            Sensor humiditySensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
            IAirManager.INSTANCE.setSensor(humiditySensor);
            humiditySensorValue.setText("0.0");
        }catch (Exception e){
            humiditySensorValue.setText("N/A");
            Toast.makeText(this, R.string.toast_no_humidity_sensor_available,Toast.LENGTH_SHORT).show();
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

}
