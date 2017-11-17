package pt.ipleiria.dei.iair.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.support.v7.app.AlertDialog;
import android.content.ServiceConnection;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.appindexing.internal.Thing;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.ipleiria.dei.iair.R;
import pt.ipleiria.dei.iair.Utils.GPSUtils;
import pt.ipleiria.dei.iair.Utils.HttpCallBack;
import pt.ipleiria.dei.iair.Utils.HttpUtils;
import pt.ipleiria.dei.iair.Utils.ThinkSpeak;
import pt.ipleiria.dei.iair.controller.IAirManager;
import pt.ipleiria.dei.iair.model.IAirSensorListener;
import pt.ipleiria.dei.iair.Utils.GPSUtils;
//import pt.ipleiria.dei.iair.Utils.ThinkSpeak;

public class DashboardActivity extends AppCompatActivity {
    public static final String SHARED_PREFERENCES = "Shared";
    SharedPreferences preferencesRead;
    SharedPreferences.Editor preferencesWrite;

    private String txtUsername = "Username: ";
    private TextView favouriteLocationTXT;
    private TextView temperatureFavLocationValue;
    private TextView pressureFavLocationValue;
    private TextView humidityFavLocationValue;
    private String favLocation;
    private TextView userNameTXT;


    private ServiceConnection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferencesRead =getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        preferencesWrite = preferencesRead.edit();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        bindTextViews();
        favLocation = getLocationFavourite();
        if (favLocation == "") {
            openDialog();
        }
        favouriteLocationTXT.setText(favLocation);
        userNameTXT.setText(txtUsername + getUsername());

        getDataLocation();
    }

    private void getDataLocation() {
        ThinkSpeak.getData(new HttpCallBack() {
            @Override
            public void onResult(JSONObject response) throws JSONException {
                JSONArray feeds = response.getJSONArray("feeds");
                if(feeds.length() == 0) {
                    temperatureFavLocationValue.setText("N/A");
                    humidityFavLocationValue.setText("N/A");
                    pressureFavLocationValue.setText("N/A");
                    Toast.makeText(DashboardActivity.this,"Don't have data in your location",Toast.LENGTH_LONG).show();
                } else {
                    //throw Exception;
                    temperatureFavLocationValue.setText(String.valueOf(feeds.length()));
                    humidityFavLocationValue.setText("N/A1");
                    pressureFavLocationValue.setText("N/A1");

                    for (int i = feeds.length(); i >= 1; i--) {
                        JSONObject elem = (JSONObject) feeds.get(i);
                        if (elem.getString("field1") != "N/A")
                            temperatureFavLocationValue.setText(String.valueOf(elem.getString("field1")));
                        if (elem.getString("field2") != "N/A")
                            pressureFavLocationValue.setText(String.valueOf(elem.getString("field2")));
                        if (elem.getString("field3") != "N/A")
                            humidityFavLocationValue.setText(elem.getString("field3"));
                        //if(!(elem.getString("field1").equals("23") && elem.getString("field2").equals("900") && elem.getString("field3").equals("0"))) {
                        //fail("not working because" + elem.toString());
                        //}

                    }
                }
            }

            @Override
            public void onResult(String response) {

            }
        }, this, "Leiria");
    }

    private void bindTextViews() {
        favouriteLocationTXT = findViewById(R.id.textViewFavoriteLocation);
        temperatureFavLocationValue = findViewById(R.id.textViewValueTemperature);
        pressureFavLocationValue =  findViewById(R.id.textViewValuePressure);
        humidityFavLocationValue = findViewById(R.id.textViewValueHumidity);
        userNameTXT = findViewById(R.id.textViewUserName);
    }




    public void openDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Alert");
        if(getActualLocation() == "") {
            alertDialogBuilder.setMessage("You don't have access to current location.");
            alertDialogBuilder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    if (getUsername() == "") {
                        openDialogName();
                    }
                    favouriteLocationTXT.setText(getLocationFavourite());
                    Toast.makeText(DashboardActivity.this,"Location Favourite don't Updated",Toast.LENGTH_LONG).show();
                }
            });
        } else {
            alertDialogBuilder.setMessage("Do you want choose current location " + getActualLocation() + " with favourite location?");
            alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    saveFavouriteLocation();
                    if (getUsername() == "") {
                        openDialogName();
                    }
                    favouriteLocationTXT.setText(getLocationFavourite());
                    Toast.makeText(DashboardActivity.this,"Location Favourite Updated to: " + getActualLocation(),Toast.LENGTH_LONG).show();
                }
            });

            alertDialogBuilder.setNegativeButton("NO",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (getUsername() == "") {
                        openDialogName();
                    }
                    Toast.makeText(DashboardActivity.this,"Your favourite location isn't choose",Toast.LENGTH_LONG).show();
                }
            });
        }


        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void openDialogName(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Insert Name");
        alertDialogBuilder.setMessage("Username:");

        final EditText input = new EditText(DashboardActivity.this);
        alertDialogBuilder.setView(input);

        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                saveUsername(input.getText().toString());
                userNameTXT.setText(txtUsername + getUsername());
                Toast.makeText(DashboardActivity.this,"Save your Username",Toast.LENGTH_LONG).show();

            }
        });
        alertDialogBuilder.setNegativeButton("CANCEL",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(DashboardActivity.this,"Your username isn't choose",Toast.LENGTH_LONG).show();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }



    private String getActualLocation() {
        GPSUtils gpsUtils = new GPSUtils(this);
        Location currentLocation = gpsUtils.getLocation();
        String actualLocation = "";
        try {
            actualLocation =  preferencesRead.getString("locationText", GPSUtils.getLocationDetails(this, currentLocation.getLatitude(), currentLocation.getLongitude()).getLocality());
        } catch (Exception e) {
            e.getMessage();
        }
        return actualLocation;
    }


    public void saveFavouriteLocation() {
        SharedPreferences sharedPreferences = getSharedPreferences("userData", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("locationFavourite", getActualLocation());
        editor.apply();
    }

    private void saveUsername(String username) {
        SharedPreferences sharedPreferences = getSharedPreferences("userData", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userName", username);
        editor.apply();
    }

    public String getLocationFavourite () {
        SharedPreferences sharedPreferences = getSharedPreferences("userData", Context.MODE_PRIVATE);
        String favLocation = sharedPreferences.getString("locationFavourite", "");
        return  favLocation;
    }

    public String getUsername() {
        SharedPreferences sharedPreferences = getSharedPreferences("userData", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("userName", "");
        return  username;
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
            //Location location = GPSUtils.getLocation();
            ThinkSpeak.sendData(this,39.749495, -8.807290, IAirManager.INSTANCE.getTemperature(), IAirManager.INSTANCE.getPresure(), IAirManager.INSTANCE.getHumity());
            //ThinkSpeak.sendData(this,location.getLatitude(), location.getLongitude(), IAirManager.INSTANCE.getTemperature(), IAirManager.INSTANCE.getPresure(), IAirManager.INSTANCE.getHumity());
        }
        if(intent != null) {
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
