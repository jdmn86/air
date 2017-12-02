package pt.ipleiria.dei.iair.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

import pt.ipleiria.dei.iair.R;
import pt.ipleiria.dei.iair.Utils.GPSActivity;
import pt.ipleiria.dei.iair.Utils.GPSUtils;
import pt.ipleiria.dei.iair.Utils.ThinkSpeak;
import pt.ipleiria.dei.iair.controller.IAirManager;

import static pt.ipleiria.dei.iair.view.DashboardActivity.SHARED_PREFERENCES;

public class SettingsActivity extends GPSActivity {

    SharedPreferences preferencesRead;
    SharedPreferences.Editor preferencesWrite;
    private SeekBar graphicsTimeInterval;
    private SeekBar alarmsTimeInterval;
    SeekBar alarmsRadius;
    TextView progressgraphicsTimeInterval;
    TextView progressAlarmsTimeInterval;
    TextView progressAlarmsRadius;
    EditText location;
    private static ArrayList<String> timeIntervals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferencesRead =getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        preferencesWrite = preferencesRead.edit();
        timeIntervals = new ArrayList<String>() {{
            add(getString(R.string.oneDay));
            add(getString(R.string.oneWeek));
            add(getString(R.string.oneMonth));
            add(getString(R.string.oneYear));
        }};


        //initialize components
        graphicsTimeInterval = (SeekBar) findViewById(R.id.seekBar_settings_graphics_interval);
        alarmsTimeInterval = (SeekBar) findViewById(R.id.seekBar_settings_alarms_interval);
        alarmsRadius = (SeekBar) findViewById(R.id.seekBar_settings_alarms_radius);
        location = (EditText) findViewById(R.id.editText_settings_location);
        progressgraphicsTimeInterval = (TextView) findViewById(R.id.textView_settings_graphics_interval);
        progressAlarmsTimeInterval = (TextView) findViewById(R.id.textView_settings_alarms_interval);
        progressAlarmsRadius = (TextView) findViewById(R.id.textView_settings_alarms_radius_interval);

        //setListeners for seekbar
        //setSeekbarListeners();


        //load default settings
        //LoadSavedSettings();
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
    private void LoadSavedSettings() {
        GPSUtils gpsTracker = new GPSUtils(this);
        Location currentLocation = gpsTracker.getLocation();
        graphicsTimeInterval.setProgress(preferencesRead.getInt("graphicsTimeInterval", 1));
        progressgraphicsTimeInterval.setText(timeIntervals.get(graphicsTimeInterval.getProgress()));
        alarmsTimeInterval.setProgress(preferencesRead.getInt("alarmsTimeInterval", 3));
        progressAlarmsTimeInterval.setText(timeIntervals.get(alarmsTimeInterval.getProgress()));
        alarmsRadius.setProgress(preferencesRead.getInt("alarmsRadius", 1));
        progressAlarmsRadius.setText(timeIntervals.get(alarmsRadius.getProgress()));
        GPSUtils gpsUtils = new GPSUtils(this);
        location.setText(preferencesRead.getString("locationText", gpsUtils.getLocationName(currentLocation.getLatitude(), currentLocation.getLongitude())));
}
    private void setSeekbarListeners() {
        graphicsTimeInterval.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                this.progress = progress;
                progressgraphicsTimeInterval.setText(timeIntervals.get(progress));


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                preferencesWrite.putInt("graphicsTimeInterval", progress);

            }
        });

        alarmsTimeInterval.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                this.progress = progress;
                progressAlarmsTimeInterval.setText(timeIntervals.get(progress));


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                preferencesWrite.putInt("graphicsTimeInterval", progress);

            }
        });
        alarmsRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                this.progress = progress;
                progressAlarmsRadius.setText(timeIntervals.get(progress));


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                preferencesWrite.putInt("graphicsTimeInterval", progress);

            }
        });
    }
}
