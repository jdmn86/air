package pt.ipleiria.dei.iair.view;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.multidex.MultiDex;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;

import com.jjoe64.graphview.GraphView;

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
    private GraphView graph;
    private TabLayout tabLayout;
    private ArrayList<LinearLayout> linearLayouts = new ArrayList();
    private Spinner locationsSpinner;
    private ListView listViewData;

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        bindLayoutElements();
        populateList();
        setListeners();
    }

    private void setListeners() {
        locationsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("selected" + position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                linearLayouts.get(tab.getPosition()).setVisibility(View.VISIBLE);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                linearLayouts.get(tab.getPosition()).setVisibility(View.INVISIBLE);

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void bindLayoutElements()
    {

        graph = (GraphView) findViewById(R.id.graphAirQuality);
        tabLayout = (TabLayout) findViewById(R.id.tabLayoutLocationActivity);
        linearLayouts.add((LinearLayout) findViewById(R.id.linearLayoutlocationActivityGraphical));
        linearLayouts.add((LinearLayout) findViewById(R.id.linearLayoutlocationActivityList));
        locationsSpinner = (Spinner) findViewById(R.id.spinnerLocationList);
        listViewData = (ListView) findViewById(R.id.listViewData);
    }

    private void populateList() {
        List<String> cityNames = new ArrayList<String>();
        for (CityAssociation cityAssociation: IAirManager.INSTANCE.getAllCityAssociations())
        {
            cityNames.add(cityAssociation.getREGION_NAME());
        };
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, cityNames);
        locationsSpinner.setAdapter(arrayAdapter);
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
