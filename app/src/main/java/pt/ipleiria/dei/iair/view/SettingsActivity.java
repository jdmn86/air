package pt.ipleiria.dei.iair.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import pt.ipleiria.dei.iair.R;
import pt.ipleiria.dei.iair.Utils.GPSActivity;

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
