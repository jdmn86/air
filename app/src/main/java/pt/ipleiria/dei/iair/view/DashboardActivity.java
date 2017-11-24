package pt.ipleiria.dei.iair.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.location.Location;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import pt.ipleiria.dei.iair.R;
import pt.ipleiria.dei.iair.Utils.HttpUtils;
import pt.ipleiria.dei.iair.controller.IAirManager;
import android.widget.EditText;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.channels.Channel;
import java.util.ArrayList;
import java.util.Map;

import pt.ipleiria.dei.iair.Utils.GPSActivity;
import pt.ipleiria.dei.iair.Utils.GPSUtils;
import pt.ipleiria.dei.iair.Utils.HttpCallBack;
import pt.ipleiria.dei.iair.Utils.ThinkSpeak;
import pt.ipleiria.dei.iair.model.Alerts;
import pt.ipleiria.dei.iair.model.CityAssociation;

import static junit.framework.Assert.fail;
import static pt.ipleiria.dei.iair.Utils.ThinkSpeak.getThingDataAlertsLast;

public class DashboardActivity extends GetVinicityActivity{
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
    private TextView txtView;

    private ServiceConnection connection;

    static final int PICK_LOCATION_REQUEST = 1;  // The request code
    private LatLng location;
    private String locationName;
    private ListView lista;
    private ArrayAdapter<String> adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        SharedPreferences sharedPref = this.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        IAirManager.INSTANCE.setSharedPreferences(sharedPref);
         txtView = this.findViewById(R.id.textViewFavoriteLocation);

         lista= this.findViewById(R.id.listViewInformativeMessage);

        txtView.setText(IAirManager.INSTANCE.getFavoriteLocationName());
        //ThinkSpeak.createNewChannel("Coimbra",40.200939, -8.407976,true,"Temperatura","Pressão","Humidade");
        bindTextViews();

        if (IAirManager.INSTANCE.getFavoriteLocationName()== "null") {

            textDialog();
        }

        if (IAirManager.INSTANCE.getUsername()==null) {

            openDialogName();
        }
        favouriteLocationTXT.setText(favLocation);
        userNameTXT.setText(txtUsername + IAirManager.INSTANCE.getUsername());


        ThinkSpeak.getThingDataAssociations(this);
         getDataLocation();
    }

    private void getDataLocation() {
        pt.ipleiria.dei.iair.model.Channel channel=null;
        // busca a localização
        GPSUtils gps = new GPSUtils(this);
        LatLng latLng= new LatLng(gps.getLatitude(),gps.getLongitude());
        // faz get vinicity
        getVicinity(latLng,4000);

        //poe dados
        temperatureFavLocationValue.setText("N/A");
        humidityFavLocationValue.setText("N/A");
        pressureFavLocationValue.setText("N/A");

        if(IAirManager.INSTANCE.getAllChannels().size()!=0){
             channel=IAirManager.INSTANCE.getAllChannels().get(IAirManager.INSTANCE.getCityIdLast());

        }

        if(channel!=null){
            if (temperatureFavLocationValue.getText().toString().contains("N/A") && !channel.getTemperature().contains("N/A"))
                temperatureFavLocationValue.setText(channel.getTemperature());
            if (pressureFavLocationValue.getText().toString().contains("N/A") && !channel.getPressure().contains( "N/A"))
                pressureFavLocationValue.setText(channel.getPressure());
            if (humidityFavLocationValue.getText().toString().contains("N/A") && !channel.getHumity().contains( "N/A"))
                humidityFavLocationValue.setText(channel.getHumity());
        }


        System.out.println("favorito"+IAirManager.INSTANCE.getFavoriteLocationName());

        CityAssociation city=IAirManager.INSTANCE.getCityAssociation(IAirManager.INSTANCE.getFavoriteLocationName());

        if(city!=null){
            getThingDataAlertsLast(city,this);

            System.out.println("number of alertas"+IAirManager.INSTANCE.getAllAlerts().size());
            if(IAirManager.INSTANCE.getAllAlerts().size()!=0){
                // Convert ArrayList to array


                ArrayList<String> strings = new ArrayList<>();

                adapter = new ArrayAdapter<String>(this,
                        android.R.layout.simple_list_item_1, strings);


                for (Alerts alert :IAirManager.INSTANCE.getAllAlerts()) {
                    //strings.add(alert.toString());
                    adapter.add(alert.toString());
                }



                lista.setAdapter(adapter);
               // adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item

            }
        }



    }

    private void bindTextViews() {
        favouriteLocationTXT = findViewById(R.id.textViewFavoriteLocation);
        temperatureFavLocationValue = findViewById(R.id.textViewValueTemperature);
        pressureFavLocationValue =  findViewById(R.id.textViewValuePressure);
        humidityFavLocationValue = findViewById(R.id.textViewValueHumidity);
        userNameTXT = findViewById(R.id.textViewUserName);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PICK_LOCATION_REQUEST) {
            if (resultCode == RESULT_OK) { // Activity.RESULT_OK

                String location = data.getStringExtra("location");
                String locationName = data.getStringExtra("locationName");
                LatLng latLng;
                if (location.isEmpty()) {
                    return;
                } else {
                    String[] strs = location.split(";");
                    latLng = new LatLng(Double.parseDouble(strs[0]), Double.parseDouble(strs[1]));
                }

                IAirManager.INSTANCE.saveFavoriteLocation(latLng, locationName);

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                System.out.println("fail  ");
            }
        }
    }//onActivityResult



    public void textDialog(){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Alert");
        alertDialogBuilder.setMessage("Your current favorite location isn't set, please choose one option.");

        alertDialogBuilder.setPositiveButton("Go To Map", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                Intent intent = null;
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
                enableGPS();
                GPSUtils locationTrack = new GPSUtils(getApplicationContext());;
                if (locationTrack.getLocation()!= null) {
                    double longitude = locationTrack.getLongitude();
                    double latitude = locationTrack.getLatitude();
                    LatLng latLng=new LatLng(latitude,longitude);

                    getVicinity(latLng,4000);

                  //  Toast.makeText(getApplicationContext(), "Longitude:" + Double.toString(longitude) + "\nLatitude:" + Double.toString(latitude), Toast.LENGTH_SHORT).show();
                }
                if (IAirManager.INSTANCE.getUsername() == "null") {
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
                IAirManager.INSTANCE.saveUsername(input.getText().toString());
                userNameTXT.setText(txtUsername + IAirManager.INSTANCE.getUsername());
                Toast.makeText(DashboardActivity.this, "Username Saved", Toast.LENGTH_LONG).show();
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
            //  ThinkSpeak.sendData(this, 39.749495, -8.807290, IAirManager.INSTANCE.getTemperature(), IAirManager.INSTANCE.getPresure(), IAirManager.INSTANCE.getHumity());
            //ThinkSpeak.sendData(this,location.getLatitude(), location.getLongitude(), IAirManager.INSTANCE.getTemperature(), IAirManager.INSTANCE.getPresure(), IAirManager.INSTANCE.getHumity());


            CityAssociation city = IAirManager.INSTANCE.getCityAssociation(locationName);

            String temp = IAirManager.INSTANCE.getTemperature();
            String press = IAirManager.INSTANCE.getPresure();
            String hum = IAirManager.INSTANCE.getHumity();

            pt.ipleiria.dei.iair.model.Channel channel = new pt.ipleiria.dei.iair.model.Channel(temp, press, hum, locationName);

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





        } else if (id == R.id.menu_gps) {
            enableGPS();
        }
        if(intent != null) {
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        txtView.setText(IAirManager.INSTANCE.getFavoriteLocationName());
        userNameTXT.setText(txtUsername + IAirManager.INSTANCE.getUsername());
    }


    public void getVicinity(LatLng latLng, int radius){

        HttpUtils.Get(new HttpCallBack() {

            @SuppressLint("ResourceType")
            @Override
            public void onResult(JSONObject response) throws JSONException {

                if(response.getJSONArray("results").length()>0){

                    double latitude=Double.parseDouble(response.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").get("lat").toString());
                    double longitude=Double.parseDouble(response.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").get("lng").toString());

                    location = new LatLng(latitude,longitude);
                    locationName = response.getJSONArray("results").getJSONObject(0).get("vicinity").toString();
                    IAirManager.INSTANCE.saveFavoriteLocation(location,locationName);
                    favouriteLocationTXT.setText(locationName);
                }

            }

            @Override
            public void onResult(String response) {

            }
        }, "https://maps.googleapis.com/maps/api/place/search/json?radius="+String.valueOf(radius)+"&sensor=false&type=locality&key=AIzaSyCel8hjaRHf6-DK0fe3KmIsXp1MMP-RYQk&location="+latLng.latitude+","+latLng.longitude, this);

    }

}
