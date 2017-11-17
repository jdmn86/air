package pt.ipleiria.dei.iair.view;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import pt.ipleiria.dei.iair.R;
import pt.ipleiria.dei.iair.controller.IAirManager;

import static android.app.PendingIntent.getActivity;


public class DashboardActivity extends AppCompatActivity {

    private ServiceConnection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        SharedPreferences sharedPref = this.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        IAirManager.INSTANCE.setSharedPreferences(sharedPref);
        TextView txtView = this.findViewById(R.id.textViewFavoriteLocation);
        //Descomentar apenas para limpar as sharedpreferences
        //SharedPreferences.Editor editor = sharedPref.edit();
        //editor.clear();
        //editor.commit();
        IAirManager.INSTANCE.setFavoriteLocation(sharedPref.getString("favoriteLocation","null"));
        txtView.setText(sharedPref.getString("favoriteLocation","null"));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        Intent intent = null;
        if (id == R.id.menu_create_message) {
            intent = new Intent(this, CreateInformativeMessageActivity.class);

        } else if (id == R.id.menu_my_sensors) {
            intent = new Intent(this, MySensorsActivity.class);

        } else if (id == R.id.menu_map) {
            intent = new Intent(this, MapActivity.class);

        } else if (id == R.id.menu_locations) {
            intent = new Intent(this, LocationActivity.class);

        } else if (id == R.id.menu_settings) {
            intent = new Intent(this, SettingsActivity.class);

        } else if (id == R.id.menu_send_data) {
           // ThinkSpeak.createNewChannel(this, "teste", 39.749495, -8.807290,false, "temp", "pressure", "humity");
        }

        if(intent != null) {
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
