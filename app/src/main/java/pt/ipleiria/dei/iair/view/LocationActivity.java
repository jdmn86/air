package pt.ipleiria.dei.iair.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.multidex.MultiDex;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import pt.ipleiria.dei.iair.R;
import pt.ipleiria.dei.iair.Utils.GPSActivity;
import pt.ipleiria.dei.iair.Utils.GPSUtils;
import pt.ipleiria.dei.iair.Utils.HttpCallBack;
import pt.ipleiria.dei.iair.Utils.ThinkSpeak;
import pt.ipleiria.dei.iair.controller.IAirManager;
import pt.ipleiria.dei.iair.model.CityAssociation;

public class LocationActivity extends GPSActivity {
    private ListView listViewLocations;
    private GraphView graph;
    private TabLayout tabLayout;
    private ArrayList<LinearLayout> linearLayouts = new ArrayList();
    private LinearLayout loadingScreen;
    private Spinner locationsSpinner;
    private ListView listViewData;
    public List<String> cityNames;
    public Context context;

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
               showLoading(true);
                ThinkSpeak.INSTANCE.getGraphURL(new HttpCallBack() {
                   @Override
                   public void onResult(JSONObject response) throws JSONException {
                       /*
                       //data incoming
                       System.out.println(response.toString());
                       JSONArray feeds = response.getJSONArray("feeds");
                       LineGraphSeries<DataPoint> seriesTemperature = new LineGraphSeries<>(new DataPoint[]{});
                       LineGraphSeries<DataPoint> seriesPressure = new LineGraphSeries<>(new DataPoint[]{});
                       LineGraphSeries<DataPoint> seriesHumity = new LineGraphSeries<>(new DataPoint[]{});
                       if(feeds.length() != 0) {
                           Date startDate = parseDate(feeds.getJSONObject(0).getString("created_at"));
                           Date endDate = parseDate(feeds.getJSONObject(feeds.length()-1).getString("created_at"));


                           for (int i = 0; i < feeds.length(); i++) {
                               JSONObject feed = feeds.getJSONObject(i);
                               if (!feed.getString("field1").equals("N/A"))
                                   seriesTemperature.appendData(new DataPoint(parseDate(feed.getString("created_at")), Double.parseDouble(feed.getString("field1"))), true, 3);
                               if (!feed.getString("field2").equals("N/A"))
                                   seriesPressure.appendData(new DataPoint(parseDate(feed.getString("created_at")), Double.parseDouble(feed.getString("field2"))), true, 3);
                               if (!feed.getString("field3").equals("N/A"))
                                   seriesHumity.appendData(new DataPoint(parseDate(feed.getString("created_at")), Double.parseDouble(feed.getString("field3"))), true, 3);
                           }
                           graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(context));
                           graph.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space
                           graph.getViewport().setYAxisBoundsManual(true);
                           graph.getViewport().setMinY(0);
                           graph.getViewport().setMaxY(100);
                           graph.getViewport().setXAxisBoundsManual(true);
                           graph.getViewport().setMinX(startDate.getTime());
                           graph.getViewport().setMaxX(endDate.getTime());
                           graph.getGridLabelRenderer().setHumanRounding(true);
                           seriesTemperature.setColor(Color.RED);
                           seriesPressure.setColor(Color.GRAY);
                           seriesHumity.setColor(Color.BLUE);
                           graph.addSeries(seriesTemperature);
                           graph.addSeries(seriesPressure);
                           graph.addSeries(seriesHumity);

                       }*/

                       showLoading(false);
                   }

                   @Override
                   public void onResult(String response) {

                   }
               }, context, cityNames.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                linearLayouts.get(tab.getPosition()).setVisibility(View.VISIBLE);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                linearLayouts.get(tab.getPosition()).setVisibility(View.INVISIBLE);

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

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

        //graph = (GraphView) findViewById(R.id.graphAirQuality);
        tabLayout = (TabLayout) findViewById(R.id.tabLayoutLocationActivity);
        linearLayouts.add((LinearLayout) findViewById(R.id.linearLayoutlocationActivityGraphical));
        linearLayouts.add((LinearLayout) findViewById(R.id.linearLayoutlocationActivityList));
        locationsSpinner = (Spinner) findViewById(R.id.spinnerLocationList);
        listViewData = (ListView) findViewById(R.id.listViewData);
        loadingScreen = (LinearLayout) findViewById(R.id.linearLayoutLoadingLocationActivity);

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
        }
        if(intent != null) {
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
