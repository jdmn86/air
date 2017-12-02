package pt.ipleiria.dei.iair.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import pt.ipleiria.dei.iair.R;
import pt.ipleiria.dei.iair.Utils.AlarmsDataAdapter;
import pt.ipleiria.dei.iair.Utils.AlertCallback;
import pt.ipleiria.dei.iair.Utils.GPSActivity;
import pt.ipleiria.dei.iair.Utils.GPSUtils;
import pt.ipleiria.dei.iair.Utils.HttpCallBack;
import pt.ipleiria.dei.iair.Utils.SensorDataAdapter;
import pt.ipleiria.dei.iair.Utils.ThinkSpeak;
import pt.ipleiria.dei.iair.controller.IAirManager;
import pt.ipleiria.dei.iair.model.Alerts;
import pt.ipleiria.dei.iair.model.Channel;
import pt.ipleiria.dei.iair.model.CityAssociation;

public class LocationActivity extends GPSActivity {
    private ListView listViewLocations;
    private WebView graphtemperature;
    private WebView graphHumity;
    private WebView graphPressure;
    private TabLayout tabLayout;
    private ArrayList<LinearLayout> linearLayouts = new ArrayList();
    private LinearLayout loadingScreen;
    private Spinner locationsSpinner;
    private ListView listViewData;
    private ImageView imageSetLocationWithFavorite;
    private ImageView imageSendInformativeMessage;
    public List<String> cityNames;
    public Context context;
    private ListView listViewAlarms;
    public AlarmsDataAdapter customAdapter;
    public SensorDataAdapter sensorDataAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPref = this.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        IAirManager.INSTANCE.setSharedPreferences(sharedPref);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        context = this;
        bindLayoutElements();
        populateList();
        setListeners();
    }

    private void setListeners() {
        locationsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!locationsSpinner.getSelectedItem().equals(IAirManager.INSTANCE.getFavoriteLocationName())) {
                    imageSetLocationWithFavorite.setVisibility(View.VISIBLE);
                } else {
                    imageSetLocationWithFavorite.setVisibility(View.INVISIBLE);
                }
               showLoading(true);
                ThinkSpeak.INSTANCE.getGraphURL(new HttpCallBack() {
                   @Override
                   public void onResult(JSONObject response) {
                   }

                   @Override
                   public void onResult(String response) {
                       String[] URLparts = response.split("<key>");
                       Display display = getWindowManager().getDefaultDisplay();
                       Point size = new Point();
                       display.getSize(size);
                       //String html = URLparts[0] + "1" + URLparts[1];
                       graphtemperature.loadUrl("about:blank");
                       graphHumity.loadUrl("about:blank");
                       graphPressure.loadUrl("about:blank");
                       graphtemperature.setWebChromeClient(new WebChromeClient());
                       graphtemperature.setWebViewClient(new WebViewClient());
                       graphtemperature.getSettings().setJavaScriptEnabled(true);
                       graphtemperature.loadData("<iframe width=\"" + (size.x -200) + "\" height=\"250\" style=\"border: 1px solid #cccccc;\" src=\"" + URLparts[0] + "1" + URLparts[1] + "\" ></iframe>\"", "text/html", null);
                       graphtemperature.setWebViewClient(new WebViewClient(){
                           public boolean shouldOverrideUrlLoading(WebView view, String url) {
                               return true;
                           }
                       });
                       graphHumity.setWebChromeClient(new WebChromeClient());
                       graphHumity.setWebViewClient(new WebViewClient());
                       graphHumity.getSettings().setJavaScriptEnabled(true);
                       graphHumity.loadData("<iframe width=\""+ (size.x -20) + "\" height=\"250\" style=\"border: 1px solid #cccccc;\" src=\"" + URLparts[0] + "2" + URLparts[1] + "\" ></iframe>\"", "text/html", null);
                       graphHumity.setWebViewClient(new WebViewClient(){
                           public boolean shouldOverrideUrlLoading(WebView view, String url) {
                               return true;
                           }
                       });
                       graphPressure.setWebChromeClient(new WebChromeClient());
                       graphPressure.setWebViewClient(new WebViewClient());
                       graphPressure.getSettings().setJavaScriptEnabled(true);
                       graphPressure.loadData("<iframe width=\""+ (size.x -100) + "\" height=\"250\" style=\"border: 1px solid #cccccc;\" src=\"" + URLparts[0] + "3" + URLparts[1] + "\" ></iframe>\"", "text/html", null);
                       graphPressure.setWebViewClient(new WebViewClient(){
                           public boolean shouldOverrideUrlLoading(WebView view, String url) {
                               return true;
                           }
                       });

                       showLoading(false);

                   }
               }, context, cityNames.get(position));
                ThinkSpeak.INSTANCE.getData(new HttpCallBack() {
                    @Override
                    public void onResult(JSONObject response) throws JSONException {
                        List<Channel> channels = new LinkedList<>();
                        JSONArray feeds = response.getJSONArray("feeds");
                        if(feeds.length()!=0) {
                            for (int i = 0; i <feeds.length(); i++) {
                                if(!(feeds.getJSONObject(i).getString("field1").equals("N/A") && feeds.getJSONObject(i).getString("field2").equals("N/A") && feeds.getJSONObject(i).getString("field3").equals("N/A"))) {
                                    channels.add(new Channel(parseDate(feeds.getJSONObject(i).getString("created_at")),feeds.getJSONObject(i).getString("field1"), feeds.getJSONObject(i).getString("field2"), feeds.getJSONObject(i).getString("field3"), response.getJSONObject("channel").getString("name"), response.getJSONObject("channel").getString("latitude"), response.getJSONObject("channel").getString("longitude")));
                                System.out.println(channels.get(0));
                                }
                            }
                        }
                        if(sensorDataAdapter == null) {
                            sensorDataAdapter = new SensorDataAdapter(context, R.layout.list_item_sensors_data, channels);
                            listViewData.setAdapter(sensorDataAdapter);
                        }
                        else {
                            sensorDataAdapter.clear();
                            sensorDataAdapter.addAll(channels);
                            sensorDataAdapter.notifyDataSetChanged();
                        }
                        if(locationsSpinner.getSelectedItemPosition() !=1)
                            linearLayouts.get(1).setVisibility(View.INVISIBLE);
                        else
                            linearLayouts.get(1).setVisibility(View.VISIBLE);


                    }

                    @Override
                    public void onResult(String response) {

                    }
                }, context, cityNames.get(position));

                CityAssociation cityAssociation = IAirManager.INSTANCE.getCityAssociationsByName(cityNames.get(position));
                ThinkSpeak.INSTANCE.getThingDataAlerts(new AlertCallback() {
                    @Override
                    public void onResult(List<Alerts> alert) {

                        if(customAdapter == null) {
                            customAdapter = new AlarmsDataAdapter(context, R.layout.list_item_alerts_data);
                            listViewAlarms.setAdapter(customAdapter);
                        }
                            customAdapter.clear();
                            customAdapter.addAll(alert);
                            customAdapter.notifyDataSetChanged();
                            if(locationsSpinner.getSelectedItemPosition() !=2)
                                linearLayouts.get(2).setVisibility(View.INVISIBLE);
                            else
                                linearLayouts.get(2).setVisibility(View.VISIBLE);

                    }

                    @Override
                    public void onResult(LinkedList<CityAssociation> cityAssociations) {

                    }
                }, context, cityAssociation);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                linearLayouts.get(tab.getPosition()).setVisibility(View.VISIBLE);
                System.out.println(tab.getPosition() + "IN");
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                linearLayouts.get(tab.getPosition()).setVisibility(View.INVISIBLE);
                System.out.println(tab.getPosition() + "OUT");

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        imageSetLocationWithFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Double latitudeLocationSpinner = 0.0;
                Double longitudeLocationSpinner = 0.0;

                LatLng latLng = new LatLng(latitudeLocationSpinner, longitudeLocationSpinner);

                for (CityAssociation cityAssociation: IAirManager.INSTANCE.getAllCityAssociations())
                {
                    if(locationsSpinner.getSelectedItem().toString().equals(cityAssociation.getREGION_NAME())){
                        latitudeLocationSpinner = Double.valueOf(cityAssociation.getLatitude());
                        longitudeLocationSpinner= Double.valueOf(cityAssociation.getLongitude());
                        latLng = new LatLng(latitudeLocationSpinner, longitudeLocationSpinner);
                    }
                }
                IAirManager.INSTANCE.saveFavoriteLocation(latLng, locationsSpinner.getSelectedItem().toString());

                Log.d("testeImg", locationsSpinner.getSelectedItem().toString() + " =>> " + latitudeLocationSpinner+";"+longitudeLocationSpinner);
                imageSetLocationWithFavorite.setVisibility(View.INVISIBLE);
                Toast.makeText(context, "Location Favourite Updated to: " + IAirManager.INSTANCE.getFavoriteLocationName(), Toast.LENGTH_SHORT).show();
            }
        });
        imageSendInformativeMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CreateInformativeMessageActivity.class);
                intent.putExtra("listPosition", locationsSpinner.getSelectedItemPosition());
                context.startActivity(intent);
            }
        });
    }

    private Date parseDate(String created_at) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);


        try {
            Date date = format.parse(created_at);
       return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void showLoading(boolean b) {
        loadingScreen.setVisibility(b ? View.VISIBLE : View.INVISIBLE);
    }

    private void bindLayoutElements()
    {

        graphtemperature = (WebView) findViewById(R.id.graph_temperature);
        graphHumity = (WebView) findViewById(R.id.graph_humity);
        graphPressure = (WebView) findViewById(R.id.graph_pressure);
        tabLayout = (TabLayout) findViewById(R.id.tabLayoutLocationActivity);
        linearLayouts.add((LinearLayout) findViewById(R.id.linearLayoutlocationActivityGraphical));
        linearLayouts.add((LinearLayout) findViewById(R.id.linearLayoutlocationActivityList));
        linearLayouts.add((LinearLayout) findViewById(R.id.linearLayoutlocationActivityAlerts));
        locationsSpinner = (Spinner) findViewById(R.id.spinnerLocationList);
        listViewData = (ListView) findViewById(R.id.listViewData_location);
        listViewAlarms = (ListView) findViewById(R.id.listViewAlerts_location);
        loadingScreen = (LinearLayout) findViewById(R.id.linearLayoutLoadingLocationActivity);
        imageSetLocationWithFavorite = findViewById(R.id.imageSetLocationWithFavoriteLocation);
        imageSendInformativeMessage = (ImageView) findViewById(R.id.imageView_send_alert_for_location);
    }

    private void populateList() {
        cityNames = new ArrayList<String>();
        for (CityAssociation cityAssociation: IAirManager.INSTANCE.getAllCityAssociations())
        {
            cityNames.add(cityAssociation.getREGION_NAME());
        }
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

        }else if (id == R.id.menu_send_data) {
            CityAssociation city = IAirManager.INSTANCE.getCityAssociation(IAirManager.INSTANCE.getCurrentLocationName().toString());

            String temp = IAirManager.INSTANCE.getTemperature();
            String press = IAirManager.INSTANCE.getPresure();
            String hum = IAirManager.INSTANCE.getHumity();

            if (city != null) {

                pt.ipleiria.dei.iair.model.Channel channel = new pt.ipleiria.dei.iair.model.Channel(temp, press, hum, city.getREGION_NAME(),String.valueOf(IAirManager.INSTANCE.getCurrentLocation().latitude),String.valueOf(IAirManager.INSTANCE.getCurrentLocation().longitude));
                //channel=IAirManager.INSTANCE.getChannel(local);
                ThinkSpeak.INSTANCE.insertInChannel(channel, this);
                Toast.makeText(this, "The sensors data was send", Toast.LENGTH_LONG).show();
            }
        }
        if(intent != null) {
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
