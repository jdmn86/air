package pt.ipleiria.dei.iair.controller;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.LinkedList;
import java.util.List;

import pt.ipleiria.dei.iair.Utils.AlertCallback;
import pt.ipleiria.dei.iair.Utils.ThinkSpeak;
import pt.ipleiria.dei.iair.model.Alerts;
import pt.ipleiria.dei.iair.model.CityAssociation;

public class IairService extends Service {
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
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
