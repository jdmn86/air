package pt.ipleiria.dei.iair.model;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import pt.ipleiria.dei.iair.controller.IAirManager;

/**
 * Created by ricar on 06/11/2017.
 */

public class IAirSensorListener implements SensorEventListener {

    private float[] eventValues;


    @Override
    public void onSensorChanged(SensorEvent event) {
        eventValues = event.values.clone();
        int sensorType = event.sensor.getType();
        if (sensorType == Sensor.TYPE_AMBIENT_TEMPERATURE){
            IAirManager.INSTANCE.changeTemperatureValue(eventValues);
        } else if (sensorType == Sensor.TYPE_RELATIVE_HUMIDITY){
            IAirManager.INSTANCE.changeHumidityValue(eventValues);
        } else if (sensorType == Sensor.TYPE_PRESSURE){
            IAirManager.INSTANCE.changePressureValue(eventValues);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
