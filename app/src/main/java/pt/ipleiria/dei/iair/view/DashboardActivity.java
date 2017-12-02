package pt.ipleiria.dei.iair.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import pt.ipleiria.dei.iair.R;
import pt.ipleiria.dei.iair.Utils.AlertCallBack;
import pt.ipleiria.dei.iair.Utils.HttpUtils;
import pt.ipleiria.dei.iair.controller.IAirManager;
import android.widget.EditText;
import com.google.android.gms.maps.model.LatLng;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import pt.ipleiria.dei.iair.Utils.GPSUtils;
import pt.ipleiria.dei.iair.Utils.HttpCallBack;
import pt.ipleiria.dei.iair.Utils.HttpUtils;
import pt.ipleiria.dei.iair.Utils.ThinkSpeak;
import pt.ipleiria.dei.iair.controller.IAirManager;
import pt.ipleiria.dei.iair.model.Alerts;
import pt.ipleiria.dei.iair.model.Channel;
import pt.ipleiria.dei.iair.model.CityAssociation;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class DashboardActivity extends GetVinicityActivity implements LocationListener{
    public static final String SHARED_PREFERENCES = "Shared";
    public static final String GOOGLE_API_KEY = "AIzaSyDw9LzBVitGvG3jPApKgFCwvwoFuUpyet8";
    SharedPreferences preferencesRead;
    SharedPreferences.Editor preferencesWrite;


    private TextView favouriteLocationTXT;
    private static TextView temperatureFavLocationValue;
    private static TextView pressureFavLocationValue;
    private static TextView humidityFavLocationValue;
    private TextView textViewUserName;
    private TextView txtView;

    private ServiceConnection connection;

    static final int PICK_LOCATION_REQUEST = 1;  // The request code

    private static ListView lista;
    private static ArrayAdapter<String> adapter;

    private ArrayList permissionsToRequest;
    private ArrayList permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();

    private final static int ALL_PERMISSIONS_RESULT = 101;
    private DashboardActivity context;

    LocationManager mLocationManager;
    private long lastTimestamp;


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        setSensorManager();
        context = this;

        SharedPreferences sharedPref = this.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        IAirManager.INSTANCE.setSharedPreferences(sharedPref);

        bindTextViews();

        lastTimestamp =  0;

        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);

        permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (permissionsToRequest.size() > 0) {
                requestPermissions((String[]) permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
            } else {
                enableGPS();
                mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

                Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(location != null && location.getTime() > Calendar.getInstance().getTimeInMillis() - 2 * 60 * 1000) {
                    // Do something with the recent location fix
                    //  if it is less than two minutes old,
                    //  otherwise wait for the update below
                }

                setCurrentLocation();

            }
            //startService(new Intent(this, IairService.class));

            }
        }
    }
    private void setSensorManager() {
        try {
            SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            IAirManager.INSTANCE.setSensorManager(sensorManager);
        }catch (Exception e){
            return;
        }

    }


    private void bindTextViews() {
        favouriteLocationTXT = findViewById(R.id.textViewFavoriteLocation);
        temperatureFavLocationValue = findViewById(R.id.textViewValueTemperature);
        pressureFavLocationValue =  findViewById(R.id.textViewValuePressure);
        humidityFavLocationValue = findViewById(R.id.textViewValueHumidity);
        textViewUserName = findViewById(R.id.textViewUsernamedescription);
        txtView = this.findViewById(R.id.textViewFavoriteLocation);
        lista= this.findViewById(R.id.listViewInformativeMessage);

        temperatureFavLocationValue.setText("N/A");
        humidityFavLocationValue.setText("N/A");
        pressureFavLocationValue.setText("N/A");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PICK_LOCATION_REQUEST) {
            if (resultCode == RESULT_OK) { // Activity.RESULT_OK

                double latitude = data.getDoubleExtra("latitude",0.0);
                double longitude = data.getDoubleExtra("longitude", 0.0);
                String locationName = data.getStringExtra("locationName");
                LatLng latLng;
                if (latitude==0.0||longitude==0.0||locationName.isEmpty()) {
                    return;
                } else {
                    latLng = new LatLng(latitude, longitude);
                }

                IAirManager.INSTANCE.saveFavoriteLocation(latLng, locationName);

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                System.out.println("fail  ");
            }
        }
    }//onActivityResult



    public void dialogFavoriteLocation(){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Alert");
        alertDialogBuilder.setMessage("Your current favorite location isn't set, please choose one option.");

        alertDialogBuilder.setPositiveButton("Map", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                Intent intent;
                intent = new Intent(getApplicationContext(), MapActivity.class);
                if (intent != null) {
                    Intent pickLocation = new Intent( getApplicationContext() , MapActivity.class );
                    //pickLocation.setType(Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
                    pickLocation.putExtra("SEND_LOCATION_REQUEST", 2);
                    startActivityForResult(pickLocation, PICK_LOCATION_REQUEST);
                }

                favouriteLocationTXT.setText(IAirManager.INSTANCE.getFavoriteLocationName());
                Toast.makeText(DashboardActivity.this,"Location Favourite Updated to: " + IAirManager.INSTANCE.getFavoriteLocationName(),Toast.LENGTH_LONG).show();
            }
        });

        alertDialogBuilder.setNegativeButton("My Current Location",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
/*                enableGPS();
                GPSUtils locationTrack = new GPSUtils(getApplicationContext());;
                if (locationTrack.getLocation()!= null) {
                    double longitude = locationTrack.getLongitude();
                    double latitude = locationTrack.getLatitude();
                    LatLng latLng=new LatLng(latitude,longitude);*/

                    IAirManager.INSTANCE.saveFavoriteLocation(IAirManager.INSTANCE.getCurrentLocation(),IAirManager.INSTANCE.getCurrentLocationName().toString());
                    favouriteLocationTXT.setText(IAirManager.INSTANCE.getCurrentLocationName().toString());


                  //  Toast.makeText(getApplicationContext(), "Longitude:" + Double.toString(longitude) + "\nLatitude:" + Double.toString(latitude), Toast.LENGTH_SHORT).show();
                }
               // Toast.makeText(DashboardActivity.this,"Your favourite location isn't choose",Toast.LENGTH_LONG).show();
            //}
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void dialogUsername(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Insert Name");
        alertDialogBuilder.setMessage("Username:");

        final EditText input = new EditText(DashboardActivity.this);
        alertDialogBuilder.setView(input);

        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                IAirManager.INSTANCE.saveUsername(input.getText().toString());
                textViewUserName.setText(IAirManager.INSTANCE.getUsername());

                Toast.makeText(DashboardActivity.this, "Username Saved", Toast.LENGTH_LONG).show();
            }
        });
        alertDialogBuilder.setNegativeButton("CANCEL",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogUsername();
                Toast.makeText(DashboardActivity.this,"Your username isn't choosen",Toast.LENGTH_LONG).show();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
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
            //  ThinkSpeak.sendData(this, 39.749495, -8.807290, IAirManager.INSTANCE.getTemperature(), IAirManager.INSTANCE.getPresure(), IAirManager.INSTANCE.getHumity());
            //ThinkSpeak.sendData(this,location.getLatitude(), location.getLongitude(), IAirManager.INSTANCE.getTemperature(), IAirManager.INSTANCE.getPresure(), IAirManager.INSTANCE.getHumity());

            CityAssociation city = IAirManager.INSTANCE.getCityAssociation(IAirManager.INSTANCE.getCurrentLocationName().toString());

            String temp = IAirManager.INSTANCE.getTemperature();
            String press = IAirManager.INSTANCE.getPresure();
            String hum = IAirManager.INSTANCE.getHumity();

            if (city != null) {

                pt.ipleiria.dei.iair.model.Channel channel = new pt.ipleiria.dei.iair.model.Channel(temp, press, hum, city.getREGION_NAME(),String.valueOf(IAirManager.INSTANCE.getCurrentLocation().latitude),String.valueOf(IAirManager.INSTANCE.getCurrentLocation().longitude));
                //channel=IAirManager.INSTANCE.getChannel(local);
                ThinkSpeak.INSTANCE.insertInChannel(channel, this);

                putDataOnDashboard(this);
            }


        } else if (id == R.id.menu_gps) {
            enableGPS();
        }
        if(intent != null) {
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void getVicinity(LatLng latLng, int radius){

        HttpUtils.Get(new HttpCallBack() {

            @SuppressLint("ResourceType")
            @Override
            public void onResult(JSONObject response) throws JSONException {

                if(response.getJSONArray("results").length()>0){

                    double latitude=Double.parseDouble(response.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").get("lat").toString());
                    double longitude=Double.parseDouble(response.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").get("lng").toString());

                    IAirManager.INSTANCE.setCurrentLocation(new LatLng(latitude,longitude));
                    IAirManager.INSTANCE.setCurrentLocationName(response.getJSONArray("results").getJSONObject(0).get("vicinity").toString());

                    if (IAirManager.INSTANCE.getFavoriteLocationName()== "null") {

                        dialogFavoriteLocation();
                    }
                    populate();
                }

            }

            @Override
            public void onResult(String response) {

            }
        }, "https://maps.googleapis.com/maps/api/place/search/json?radius="+String.valueOf(radius)+ "&sensor=false&type=locality&key=" + GOOGLE_API_KEY + "&location=" +latLng.latitude+","+latLng.longitude, this);

    }

    public void setCurrentLocation(){

        GPSUtils locationTrack = new GPSUtils(this);

      //  if (
            Location loc =locationTrack.getLocation();
            if(loc!=null){
                double longitude = loc.getLongitude();
                double latitude = loc.getLatitude();
                LatLng latLng = new LatLng(latitude, longitude);
                getVicinity(latLng,4000);
            }

    }


    private ArrayList findUnAskedPermissions(ArrayList wanted) {
        ArrayList result = new ArrayList();

        for (Object perm : wanted) {
            if (!hasPermission((String) perm)) {
                result.add(perm);
            }
        }
        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (Object perms : permissionsToRequest) {
                    if (!hasPermission((String) perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale((String) permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions((String[]) permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
        }

        enableGPS();

        setCurrentLocation();

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new android.app.AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public void populate(){


        if (IAirManager.INSTANCE.getUsername()==null || IAirManager.INSTANCE.getUsername()=="null") {

            dialogUsername();
        }

        textViewUserName.setText(IAirManager.INSTANCE.getUsername());

        pt.ipleiria.dei.iair.model.Channel channel=null;

        //carrega dados
        ThinkSpeak.INSTANCE.getThingDataAssociations(this);

    }


    public static void putDataOnDashboard(Context context) {
        Channel channel = null;

        if (IAirManager.INSTANCE.getAllChannels().size() != 0) {

            channel = IAirManager.INSTANCE.getChannel(IAirManager.INSTANCE.getFavoriteLocationName());
        }

        if (channel != null) {
            if (channel.getTemperature()!=null&&!channel.getTemperature().contains("N/A"))
                temperatureFavLocationValue.setText(channel.getTemperature());
            if (channel.getPressure()!=null&&!channel.getPressure().contains("N/A"))
                pressureFavLocationValue.setText(channel.getPressure());
            if (channel.getHumity()!=null&&!channel.getHumity().contains("N/A"))
                humidityFavLocationValue.setText(channel.getHumity());
        }

        CityAssociation city = IAirManager.INSTANCE.getCityAssociation(IAirManager.INSTANCE.getFavoriteLocationName());

        if(city!=null){
            getThingDataAlertsLast(new AlertCallBack() {

                @Override
                public void onResult(List<Alerts> response) {

                    ArrayList<String> strings = new ArrayList<>();

                    adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, strings);

                    if(IAirManager.INSTANCE.getAllAlerts().size()!=0){
                        // Convert ArrayList to array

                        for (Alerts alert :response) {
                            adapter.clear();
                            adapter.add(alert.toString());
                        }



                        lista.setAdapter(adapter);
                        // adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item

                    }
                    else{
                        adapter.clear();
                        adapter.add("No alerts available!");
                        lista.setAdapter(adapter);
                    }
                }
            }, city,context);


        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        txtView.setText(IAirManager.INSTANCE.getFavoriteLocationName());
        textViewUserName.setText(IAirManager.INSTANCE.getUsername());

        putDataOnDashboard(this);
    }


    @Override
    public void onLocationChanged(Location location) {
        if(System.currentTimeMillis()> lastTimestamp +(1000*60*20)) {
            lastTimestamp = System.currentTimeMillis();
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            IAirManager.INSTANCE.setCurrentLocation(latLng);
            GPSUtils gpsUtils = new GPSUtils(this);
            IAirManager.INSTANCE.setCurrentLocationName(gpsUtils.getLocationName(location.getLatitude(), location.getLongitude()));
            getVicinity(latLng,4000);
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
