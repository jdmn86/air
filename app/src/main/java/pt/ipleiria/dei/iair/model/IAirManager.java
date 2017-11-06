package pt.ipleiria.dei.iair.model;

import android.hardware.Sensor;
import android.hardware.SensorManager;

import pt.ipleiria.dei.iair.view.MySensorsActivity;

/**
 * Created by ricar on 06/11/2017.
 */

public enum IAirManager {
    INSTANCE;

    SensorManager sensorManager;

    MySensorsActivity mySensorsActivity;



    public void setSensorManager(SensorManager sensorManager) {
        this.sensorManager = sensorManager;
    }

    public void changeTemperatureValue(float[] eventValues) {
        mySensorsActivity.setTemperatureValue(eventValues[0]);
    }

    public void changeHumidityValue(float[] eventValues) {
        mySensorsActivity.setHumidityValue(eventValues[0]);
    }

    public void changePressureValue(float[] eventValues) {
        mySensorsActivity.setPressureValue(eventValues[0]);
    }

    public void setMySensorsActivity(MySensorsActivity mySensorsActivity) {
        this.mySensorsActivity = mySensorsActivity;
    }

    public void setSensor(Sensor sensor) {
        sensorManager.registerListener(new IAirSensorListener(),sensor,SensorManager.SENSOR_DELAY_NORMAL);
    }
}
