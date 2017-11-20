package pt.ipleiria.dei.iair.view;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;

import pt.ipleiria.dei.iair.R;
import pt.ipleiria.dei.iair.Utils.GPSActivity;
import pt.ipleiria.dei.iair.Utils.GPSUtils;
import pt.ipleiria.dei.iair.Utils.ThinkSpeak;
import pt.ipleiria.dei.iair.controller.IAirManager;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MapActivity extends GPSActivity implements OnMapReadyCallback {

    private ArrayList permissionsToRequest;
    private ArrayList permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();

    private final static int ALL_PERMISSIONS_RESULT = 101;
    GPSUtils locationTrack;

    private GoogleMap googleMap;

    private List<Marker> markers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
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

        } else if (id == R.id.menu_locations) {
            intent = new Intent(this, LocationActivity.class);

        } else if (id == R.id.menu_settings) {
            intent = new Intent(this, SettingsActivity.class);

        }else if (id == R.id.menu_send_data) {
            GPSUtils gpsUtils = new GPSUtils(this);
            Location location = gpsUtils.getLocation();
            //ThinkSpeak.sendData(this,39.749495, -8.807290, IAirManager.INSTANCE.getTemperature(), IAirManager.INSTANCE.getPresure(), IAirManager.INSTANCE.getHumity());
            ThinkSpeak.sendData(this,location.getLatitude(), location.getLongitude(), IAirManager.INSTANCE.getTemperature(), IAirManager.INSTANCE.getPresure(), IAirManager.INSTANCE.getHumity());
        }  else if (id == R.id.menu_gps) {
            enableGPS();

        }
        if(intent != null) {
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}
