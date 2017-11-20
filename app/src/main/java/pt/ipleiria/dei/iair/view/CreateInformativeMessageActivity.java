package pt.ipleiria.dei.iair.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import pt.ipleiria.dei.iair.R;
import pt.ipleiria.dei.iair.Utils.GPSActivity;

public class CreateInformativeMessageActivity extends GPSActivity {

    EditText editTextTimestampCreateInformativeMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_informative_message);

        final Button buttonNow = findViewById(R.id.buttonNowCreateInformativeMessage);
        editTextTimestampCreateInformativeMessage= (EditText)findViewById(R.id.editTextTimestampCreateInformativeMessage);
        buttonNow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SimpleDateFormat s = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                String format = s.format(new Date());
                editTextTimestampCreateInformativeMessage.setText(format);
            }
        });
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

        }else if (id == R.id.menu_settings) {
            intent = new Intent(this, SettingsActivity.class);

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
