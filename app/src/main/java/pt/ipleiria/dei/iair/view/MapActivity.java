package pt.ipleiria.dei.iair.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import pt.ipleiria.dei.iair.R;
import pt.ipleiria.dei.iair.Utils.GPSActivity;
import pt.ipleiria.dei.iair.Utils.GPSUtils;
import pt.ipleiria.dei.iair.Utils.HttpCallBack;
import pt.ipleiria.dei.iair.Utils.HttpUtils;
import pt.ipleiria.dei.iair.Utils.ThinkSpeak;
import pt.ipleiria.dei.iair.controller.IAirManager;
import pt.ipleiria.dei.iair.model.Channel;
import pt.ipleiria.dei.iair.model.CityAssociation;

public class MapActivity extends GPSActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {


    private List<Marker> markers;
    GPSUtils locationTrack;

    private GoogleMap googleMap;


    private LatLng location;
    private String locationName = null;
    private int sendLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            sendLocation = extras.getInt("SEND_LOCATION_REQUEST");
            //The key argument here must match that used in the other activity
        } else {
            sendLocation = 1;
        }

        markers = new ArrayList<>();


        locationTrack = new GPSUtils(this);

        if (locationTrack.getLocation() != null) {

            double longitude = locationTrack.getLongitude();
            double latitude = locationTrack.getLatitude();

            Toast.makeText(getApplicationContext(), "Longitude:" + Double.toString(longitude) + "\nLatitude:" + Double.toString(latitude), Toast.LENGTH_SHORT).show();
        } else {

            locationTrack.showSettingsAlert();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_REGIONS).setCountry("PT")
                .build();
        autocompleteFragment.setFilter(typeFilter);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(final Place place) {
                // TODO: obter informações sobre o local selecionado.
                //Log.i(TAG, "Place: " + place.getName());
                if (sendLocation == 1) {
                    LatLng chosenLocation = place.getLatLng();
                    Marker marker = googleMap.addMarker(new MarkerOptions().position(chosenLocation).title(place.getAddress().toString()));
                    markers.add(marker);
                    googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                        @Override
                        public void onMapLongClick(LatLng latLng) {
                            for (Marker marker : markers) {
                                if (Math.abs(marker.getPosition().latitude - latLng.latitude) < 0.05 && Math.abs(marker.getPosition().longitude - latLng.longitude) < 0.05) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
                                    // Add the buttons
                                    builder.setPositiveButton(R.string.set_as_favorite_location, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            IAirManager.INSTANCE.saveFavoriteLocation(place);
                                            googleMap.clear();
                                            googleMap.addMarker(new MarkerOptions().position(place.getLatLng())
                                                    .title(IAirManager.INSTANCE.getFavoriteLocationName() + " This Is Your Favorite Location")
                                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_name)));
                                            Toast.makeText(MapActivity.this, place.getName() + " is now your favorite location!", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    builder.setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // User cancelled the dialog
                                        }
                                    });
                                    // Set other dialog properties
                                    builder.setTitle(place.getAddress());
                                    builder.setMessage(place.getLatLng().toString());

                                    // Create the AlertDialog
                                    AlertDialog dialog = builder.create();
                                    dialog.show();

                                    break;
                                }
                            }
                        }
                    });
                    googleMap.clear();
                    googleMap.addMarker(new MarkerOptions().position(chosenLocation).title(place.getAddress().toString()));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(chosenLocation));
                    googleMap.moveCamera(CameraUpdateFactory.zoomTo(10));
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
                    // Add the buttons
                    builder.setPositiveButton(R.string.choose_location, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (sendLocation != 2 && location != null && !locationName.isEmpty()) {

                                IAirManager.INSTANCE.saveFavoriteLocation(location, locationName);
                            }
                            if (sendLocation == 2 && location != null && !locationName.isEmpty()) {

                                Intent intent = new Intent();
                                intent.putExtra("latitude", place.getLatLng().latitude);
                                intent.putExtra("longitude", place.getLatLng().longitude);
                                intent.putExtra("locationName", place.getName());
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                            googleMap.clear();
                            googleMap.addMarker(new MarkerOptions().position(location)
                                    .title(IAirManager.INSTANCE.getFavoriteLocationName() + "\n This Is Yor Favorite Location")
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_name)));
                            Toast.makeText(MapActivity.this, location.toString() + " is now your favorite location!", Toast.LENGTH_SHORT).show();
                            //location=null;
                            finish();
                        }
                    });

                    builder.setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
                    // Set other dialog properties
                    builder.setTitle(place.getName());
                    builder.setMessage(place.getAddress());
                    // Create the AlertDialog
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }

            @Override
            public void onError(Status status) {
                // TODO: Solucionar o erro.
                //Log.i(TAG, "Ocorreu um erro: " + status);
            }
        });
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
        if (id == R.id.menu_dashboard) {
            intent = new Intent(this, DashboardActivity.class);
            finish();

        } else if (id == R.id.menu_my_sensors) {
            intent = new Intent(this, MySensorsActivity.class);
            finish();

        } else if (id == R.id.menu_create_message) {
            intent = new Intent(this, CreateInformativeMessageActivity.class);
            finish();

        } else if (id == R.id.menu_locations) {
            intent = new Intent(this, LocationActivity.class);
            finish();

        } else if (id == R.id.menu_settings) {
            intent = new Intent(this, SettingsActivity.class);
            finish();

        } else if (id == R.id.menu_send_data) {
            //Location location = GPSUtils.getLocation();
            //  ThinkSpeak.sendData(this, 39.749495, -8.807290, IAirManager.INSTANCE.getTemperature(), IAirManager.INSTANCE.getPresure(), IAirManager.INSTANCE.getHumity());
            //ThinkSpeak.sendData(this,location.getLatitude(), location.getLongitude(), IAirManager.INSTANCE.getTemperature(), IAirManager.INSTANCE.getPresure(), IAirManager.INSTANCE.getHumity());

            CityAssociation city = IAirManager.INSTANCE.getCityAssociation(IAirManager.INSTANCE.getCurrentLocationName());

            String temp = IAirManager.INSTANCE.getTemperature();
            String press = IAirManager.INSTANCE.getPresure();
            String hum = IAirManager.INSTANCE.getHumity();

            System.out.println("tamanho citys:" + IAirManager.INSTANCE.getAllCityAssociations().size());

            if (city != null) {

                pt.ipleiria.dei.iair.model.Channel channel = new pt.ipleiria.dei.iair.model.Channel(temp, press, hum, city.getREGION_NAME(), String.valueOf(IAirManager.INSTANCE.getCurrentLocation().latitude), String.valueOf(IAirManager.INSTANCE.getCurrentLocation().longitude));
                //channel=IAirManager.INSTANCE.getChannel(local);
                ThinkSpeak.INSTANCE.insertInChannel(channel, this);

            }


        } else if (id == R.id.menu_gps) {


        }
        if (intent != null) {
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        this.googleMap.setMyLocationEnabled(true);
        this.googleMap.setOnMapLongClickListener(this);

        if (IAirManager.INSTANCE.getFavoriteLocationLatLng() != null) {

            Marker marker = googleMap.addMarker(new MarkerOptions().position(IAirManager.INSTANCE.getFavoriteLocationLatLng())
                    .title(IAirManager.INSTANCE.getFavoriteLocationName()).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_name)));
            markers.add(marker);

            this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(IAirManager.INSTANCE.getFavoriteLocationLatLng()));
            this.googleMap.moveCamera(CameraUpdateFactory.zoomTo(6));

        }

        if (IAirManager.INSTANCE.getAllCityAssociations().size() != 0 && IAirManager.INSTANCE.getAllChannels().size()!=0) {

            for (CityAssociation city : IAirManager.INSTANCE.getAllCityAssociations()) {

                location = new LatLng(Double.parseDouble(city.getLatitude()), Double.parseDouble(city.getLongitude()));

                if (location != IAirManager.INSTANCE.getFavoriteLocationLatLng()) {

                    Channel channel = IAirManager.INSTANCE.getAllChannels().get(city.getChannel());

                    System.out.println("CHANNEL" + channel.toString());

                    markericon m = new markericon(this, channel.getTemperature().toString(), channel.getPressure().toString(), channel.getHumity().toString());

                    m.setDrawingCacheEnabled(true);
                    m.buildDrawingCache();
                    Bitmap bm = m.getDrawingCache();

                    googleMap.addMarker(new MarkerOptions()
                            .position(location)
                            .title(city.getREGION_NAME())
                            .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, m)))

                    );
                }
                //          }
            }
        }
    }

    public static Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationTrack.stopListener();
    }


    @Override
    public void onMapLongClick(final LatLng latLng) {


            getVicinity( latLng,4500,this);

            if(location!=null) {
                Toast.makeText(this, location.toString(), Toast.LENGTH_LONG).show();

            }


    }

    public void getVicinity(LatLng latLng, int radius, final MapActivity m) {

        HttpUtils.Get(new HttpCallBack() {

            @SuppressLint("ResourceType")
            @Override
            public void onResult(JSONObject response) throws JSONException {

                if (response.getJSONArray("results").length() > 0) {

                    double latitude = Double.parseDouble(response.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").get("lat").toString());
                    double longitude = Double.parseDouble(response.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").get("lng").toString());
                    location = new LatLng(latitude, longitude);
                    locationName = response.getJSONArray("results").getJSONObject(0).get("vicinity").toString();

                    if (sendLocation == 1) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
                        // Add the buttons
                        builder.setPositiveButton(R.string.set_as_favorite_location, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (sendLocation != 2 && location != null && !locationName.isEmpty()) {

                                    IAirManager.INSTANCE.saveFavoriteLocation(location, locationName);

                                }
                                googleMap.addMarker(new MarkerOptions().position(location)
                                        .title(IAirManager.INSTANCE.getFavoriteLocationName() + "\n This Is Yor Favorite Location")
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_name)));
                                Toast.makeText(MapActivity.this, location.toString() + " is now your favorite location!", Toast.LENGTH_SHORT).show();
                                //location=null;
                                m.finish();
                            }
                        });

                        builder.setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
                        // Set other dialog properties
                        builder.setTitle(locationName.toString());
                        builder.setMessage(location.toString());

                        // Create the AlertDialog
                        AlertDialog dialog = builder.create();
                        dialog.show();

                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
                        // Add the buttons
                        builder.setPositiveButton(R.string.choose_location, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (sendLocation == 2 && location != null && !locationName.isEmpty()) {

                                    Intent intent = new Intent();
                                    intent.putExtra("latitude", location.latitude);
                                    intent.putExtra("longitude", location.longitude);
                                    intent.putExtra("locationName", locationName);
                                    setResult(RESULT_OK, intent);
                                    finish();

                                }
                                googleMap.clear();
                                googleMap.addMarker(new MarkerOptions().position(location)
                                        .title(IAirManager.INSTANCE.getFavoriteLocationName() + "\n This Is Yor Favorite Location")
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_name)));
                                Toast.makeText(MapActivity.this, location.toString() + " is now your favorite location!", Toast.LENGTH_SHORT).show();
                                //location=null;
                                m.finish();
                            }
                        });

                        builder.setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
                        // Set other dialog properties
                        builder.setTitle(locationName.toString());
                        builder.setMessage(location.toString());

                        // Create the AlertDialog
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }

                }

            }
            @Override
            public void onResult(String response) {

            }
        }, "https://maps.googleapis.com/maps/api/place/search/json?radius=" + String.valueOf(radius) + "&sensor=false&type=locality&key=AIzaSyBdxSk1cxVRbL5xc_s4pEWZDVbeAMNVzEs&location=" + latLng.latitude + "," + latLng.longitude, this);

    }
}
