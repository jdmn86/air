package pt.ipleiria.dei.iair.view;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;

import pt.ipleiria.dei.iair.R;
import pt.ipleiria.dei.iair.Utils.GPSActivity;
import pt.ipleiria.dei.iair.Utils.GPSUtils;
import pt.ipleiria.dei.iair.Utils.ThinkSpeak;
import pt.ipleiria.dei.iair.controller.IAirManager;

public class SettingsActivity extends GPSActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        Intent intent = null;
        if (id == R.id.menu_dashboard) {
            intent = new Intent(this, DashboardActivity.class);

        } else if (id == R.id.menu_my_sensors) {
            intent = new Intent(this, MySensorsActivity.class);

        } else if (id == R.id.menu_create_message) {
            intent = new Intent(this, CreateInformativeMessageActivity.class);

        } else if (id == R.id.menu_map) {
            intent = new Intent(this, MapActivity.class);

        } else if (id == R.id.menu_locations) {
            intent = new Intent(this, LocationActivity.class);

        } else if (id == R.id.menu_send_data) {
            GPSUtils gpsUtils = new GPSUtils(this);
            Location location = gpsUtils.getLocation();
            //  ThinkSpeak.sendData(this, 39.749495, -8.807290, IAirManager.INSTANCE.getTemperature(), IAirManager.INSTANCE.getPresure(), IAirManager.INSTANCE.getHumity());
            ThinkSpeak.INSTANCE.sendData(this,location.getLatitude(), location.getLongitude(), IAirManager.INSTANCE.getTemperature(), IAirManager.INSTANCE.getPresure(), IAirManager.INSTANCE.getHumity());
            /*



            CityAssociation city = IAirManager.INSTANCE.getCityAssociation(locationName);


            pt.ipleiria.dei.iair.model.Channel channel = new pt.ipleiria.dei.iair.model.Channel(temperatureSensorValue.toString(), pressureSensorValue.toString(), humiditySensorValue.toString(), locationName);

            System.out.println("tamanho citys:" + IAirManager.INSTANCE.getAllCityAssociations().size());

            if (city == null) {
                ThinkSpeak.INSTANCE.createNewChannel(locationName, this);
                System.out.println("LOCAL :" + locationName);

                city = IAirManager.INSTANCE.getCityAssociation(locationName);

                System.out.println("tamanho citys:" + IAirManager.INSTANCE.getAllCityAssociations().size());
                if (city != null){

                    //ThinkSpeak.insertInChannel(channel,this);

                    //channel=IAirManager.INSTANCE.getChannel(local);
                    ThinkSpeak.insertInChannel(channel, this);
                }

            }else{
                //channel=IAirManager.INSTANCE.getChannel(local);
                ThinkSpeak.insertInChannel(channel, this);
            }

            */
        } else if (id == R.id.menu_gps) {
            enableGPS();

        }
        if (intent != null) {
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
