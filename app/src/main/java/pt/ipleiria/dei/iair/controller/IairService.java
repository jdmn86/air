package pt.ipleiria.dei.iair.controller;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;


public class IairService extends Service {
    private SensorManager sensorManager;

    public IairService() {
    }


    @Override
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub

        super.onStart(intent, startId);
        IAirManager.INSTANCE.setMySensorsActivity(this);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        setSensorManager();
        this.stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void setSensorManager() {
        try {
            SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            IAirManager.INSTANCE.setSensorManager(sensorManager);
            setSensors(sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE), sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE),
                    sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY));
        } catch (Exception e) {
            return;
        }
    }

    public void setSensors(Sensor temperatureSensor, Sensor pressureSensor, Sensor humiditySensor) {

        IAirManager.INSTANCE.setSensor(temperatureSensor);
        IAirManager.INSTANCE.setSensor(pressureSensor);
        IAirManager.INSTANCE.setSensor(humiditySensor);

    }
}


    /*
    public IairService() {
        populateIairManager();
    }

    public void populateIairManager() {
        LinkedList<CityAssociation> cityAssociations = new LinkedList<>();
        ThinkSpeak.getThingDataAssociations(new AlertCallback() {
            @Override
            public void onResult(List<Alerts> alert) {

            }

            @Override
            public void onResult(LinkedList<CityAssociation> cityAssociations) {
                IAirManager.INSTANCE.CityAssociation(cityAssociations);
            }
        }, this);
    }*/
