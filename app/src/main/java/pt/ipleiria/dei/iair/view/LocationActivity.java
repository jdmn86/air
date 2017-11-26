package pt.ipleiria.dei.iair.view;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import pt.ipleiria.dei.iair.R;
import pt.ipleiria.dei.iair.Utils.GPSActivity;
import pt.ipleiria.dei.iair.Utils.GPSUtils;
import pt.ipleiria.dei.iair.Utils.ThinkSpeak;
import pt.ipleiria.dei.iair.controller.IAirManager;
import pt.ipleiria.dei.iair.model.CityAssociation;

public class LocationActivity extends GPSActivity {
    private ListView listViewLocations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        bindLayoutElements();
        populateList();

        //listener
        listViewLocations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(LocationActivity.this, LocationDetailsActivity.class);
                CityAssociation citiesAssociation = IAirManager.INSTANCE.getAllCityAssociations().get(position);
                intent.putExtra("locationName", citiesAssociation.getREGION_NAME());
                startActivity(intent);
            }
        });
    }

    private void bindLayoutElements() {
        listViewLocations = (ListView) findViewById(R.id.listViewLocations);
    }

    private void populateList() {
        List<String> cityNames = new ArrayList<String>();
        for (CityAssociation cityAssociation: IAirManager.INSTANCE.getAllCityAssociations())
        {
            cityNames.add(cityAssociation.getREGION_NAME());
        };
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, cityNames);
        listViewLocations.setAdapter(arrayAdapter);
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

        }  else if (id == R.id.menu_settings) {
            intent = new Intent(this, SettingsActivity.class);

        } else if (id == R.id.menu_send_data) {
            GPSUtils gpsUtils = new GPSUtils(getApplicationContext());
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

}
