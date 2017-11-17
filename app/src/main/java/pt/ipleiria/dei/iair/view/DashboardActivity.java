package pt.ipleiria.dei.iair.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.ipleiria.dei.iair.R;

import pt.ipleiria.dei.iair.Utils.GPSActivity;
import pt.ipleiria.dei.iair.Utils.GPSUtils;
import pt.ipleiria.dei.iair.Utils.HttpCallBack;
import pt.ipleiria.dei.iair.Utils.ThinkSpeak;
import pt.ipleiria.dei.iair.controller.IAirManager;
//import pt.ipleiria.dei.iair.Utils.ThinkSpeak;

public class DashboardActivity extends GPSActivity {
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
        //ThinkSpeak.createNewChannel("Coimbra",40.200939, -8.407976,true,"Temperatura","Pressão","Humidade");
        bindTextViews();
        favLocation = getLocationFavourite();
        if (favLocation == "") {

            openDialog();

        }
        //textDialog();

        favouriteLocationTXT.setText(favLocation);
        userNameTXT.setText(txtUsername + getUsername());

        getDataLocation();
    }

    private void getDataLocation() {
        ThinkSpeak.getData(new HttpCallBack() {
            @Override
            public void onResult(JSONObject response) throws JSONException {
                temperatureFavLocationValue.setText("N/A");
                humidityFavLocationValue.setText("N/A");
                pressureFavLocationValue.setText("N/A");
                JSONArray feeds = response.getJSONArray("feeds");
                if(feeds.length() == 0) {

                    Toast.makeText(DashboardActivity.this,"Don't have data in your location",Toast.LENGTH_LONG).show();
                } else {
                    //throw Exception;
                    //temperatureFavLocationValue.setText(String.valueOf(feeds.length()));

                    for (int i = feeds.length()-1; i >= 0; i--) {
                        JSONObject elem = (JSONObject) feeds.get(i);
                        if (temperatureFavLocationValue.getText().toString().contains("N/A") && !elem.getString("field1").contains("N/A"))
                            temperatureFavLocationValue.setText(String.valueOf(elem.getString("field1")));
                        if (pressureFavLocationValue.getText().toString().contains("N/A") && !elem.getString("field2").contains( "N/A"))
                            pressureFavLocationValue.setText(String.valueOf(elem.getString("field2")));
                        if (humidityFavLocationValue.getText().toString().contains("N/A") && !elem.getString("field3").contains( "N/A"))
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
        }, this, favLocation);
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

            alertDialogBuilder.setPositiveButton("Turn on location",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    enableGPS();
                    Toast.makeText(DashboardActivity.this,"GPS enabled",Toast.LENGTH_LONG).show();
                    textDialog();
                }
            });
            alertDialogBuilder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {

                    if (getUsername() == "") {
                        openDialogName();
                    }
                    favouriteLocationTXT.setText(getLocationFavourite());
                    Toast.makeText(DashboardActivity.this,"Location Favourite not Updated",Toast.LENGTH_LONG).show();

                }
            });
        }


        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void textDialog(){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Alert");
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
        String actualLocation = "null";
        try {
            actualLocation = preferencesRead.getString("locationText", GPSUtils.getLocationDetails(this, currentLocation.getLatitude(), currentLocation.getLongitude()).getLocality());
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
