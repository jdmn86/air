package pt.ipleiria.dei.iair.Utils;


import android.content.Context;
import android.location.LocationManager;
import android.util.Pair;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import pt.ipleiria.dei.iair.R;

/**
 * Created by kxtreme on 08-11-2017.
 */

public class ThinkSpeak {
    private final static String API_KEY_CREATE_CHANNEL = "6T4V93KT9K3ZVOWV";
    private final static String API_KEY_CREATE_ASSOCIATION = "BAFPV9ZE40IW6C6G";
    private final static String CHANNEL_NUMBER_CREATE_ASSOCIATION = "BAFPV9ZE40IW6C6G";
    public static String location;
    public static String temperature;
    public static String pressure;
    public static String humity;
    public static double latitude;
    public static double longitude;
    public static Context context;
    private static HttpCallBack callback;


    public static boolean sendData(Context context, double latitude, double longitude, String temperature, String pressure, String humity) {

        ThinkSpeak.humity = humity;
        ThinkSpeak.pressure = pressure;
        ThinkSpeak.temperature = temperature;
        ThinkSpeak.latitude = latitude;
        ThinkSpeak.longitude = longitude;
        ThinkSpeak.context = context;
        LocationManager manager = (LocationManager) context.getSystemService( Context.LOCATION_SERVICE );
        if(temperature == null && pressure == null && humity == null) {
            Toast.makeText(context, R.string.No_data_message, Toast.LENGTH_SHORT).show();
            return false;
        }else if(!InternetUtils.isNetworkConnected(context)) {
            Toast.makeText(context, R.string.No_internet_message, Toast.LENGTH_SHORT).show();
            return false;
        } else if( manager.isProviderEnabled( LocationManager.GPS_PROVIDER )) {
            Toast.makeText(context, R.string.No_gps_message, Toast.LENGTH_SHORT).show();
            return false;
        }

          location = GPSUtils.getLocationDetails(context,latitude, longitude).getLocality();
        HttpUtils.Get(new HttpCallBack() {
            @Override
            public void onResult(JSONObject response) throws JSONException {
                System.out.println(response.toString());
                JSONArray feeds = response.getJSONArray("feeds");
                if(feeds.length() != 0) {

                    for (int i = 0; i < feeds.length(); i++) {
                        JSONObject elem = new JSONObject(feeds.get(i).toString());
                        if (elem.getString("field2").equals(location)) {
                            //insert if already exists
                            ArrayList<Pair<String, String>> data = new ArrayList<>();
                            data.add(new Pair<>("api_key", elem.getString("field1")));
                            data.add(new Pair<>("name", location));
                            data.add(new Pair<>("field1", ThinkSpeak.temperature == null? "N/A": ThinkSpeak.temperature));
                            data.add(new Pair<>("field2", ThinkSpeak.pressure == null? "N/A": ThinkSpeak.pressure));
                            data.add(new Pair<>("field3", ThinkSpeak.humity == null? "N/A": ThinkSpeak.humity));

                            HttpUtils.Post(null, "https://api.thingspeak.com/update.json?api_key=" + elem.getString("field1") + "&field1=" + (ThinkSpeak.temperature == null ? "N/A" : ThinkSpeak.temperature), data, ThinkSpeak.context);
                        return;
                        }

                    }
                }
                    createNewChannel(new CallBack() {
                        @Override
                        public void onFinish(String... messages) {
                            ArrayList<Pair<String, String>> data = new ArrayList<>();
                            data.add(new Pair<>("api_key", messages[0]));
                            data.add(new Pair<>("name", messages[1]));
                            data.add(new Pair<>("field1", ThinkSpeak.temperature == null? "N/A": ThinkSpeak.temperature));
                            data.add(new Pair<>("field2", ThinkSpeak.pressure == null? "N/A": ThinkSpeak.pressure));
                            data.add(new Pair<>("field3", ThinkSpeak.humity == null? "N/A": ThinkSpeak.humity));

                            HttpUtils.Post(null, "https://api.thingspeak.com/update.json?api_key=" + messages[0] + "&field1=" + (ThinkSpeak.temperature == null? "N/A": ThinkSpeak.temperature), data, ThinkSpeak.context);
                        }

                    }, ThinkSpeak.context,location, ThinkSpeak.latitude, ThinkSpeak.longitude,true, "temperature", "pressure", "humity");


            }

            @Override
            public void onResult(String response) {

            }
        }, "https://api.thingspeak.com/channels/361937/feeds.json?api_key=XI56ZFE2HQM85U8H&results=2", context);
        return true;
    }

    public static boolean createNewChannel(final CallBack callBack, final Context context, String channelName, double latitude, double longitude, boolean status, String... fields) {
        try {
            ArrayList<Pair<String, String>> data = new ArrayList<>();
            data.add(new Pair<>("api_key", API_KEY_CREATE_CHANNEL));
            data.add(new Pair<>("name", channelName));
            data.add(new Pair<>("latitude", String.valueOf(latitude)));
            data.add(new Pair<>("longitude", String.valueOf(longitude)));

            int i = 1;
            for (String field : fields) {

                data.add(new Pair<>("field" + i, field));
                i += 1;
            }

            MQTTHandler mqtt = new MQTTHandler(context, "channels/361937/subscribe/json/" + API_KEY_CREATE_ASSOCIATION, "pl12taes217", "0DT1756US8QLAZUK".toCharArray());
            mqtt.connect();
            mqtt.addActionListener(new MQTTThinkSpeakHandler() {

            });

            HttpUtils.Post(new HttpCallBack() {
                @Override
                public void onResult(JSONObject response) throws JSONException {
                    callBack.onFinish(new JSONObject(response.getJSONArray("api_keys").get(0).toString()).getString("api_key"), response.getString("name"));

                    ArrayList<Pair<String, String>> data = new ArrayList<>();
                    data.add(new Pair<>("API_KEY_CHANNEL", API_KEY_CREATE_ASSOCIATION));
                    data.add(new Pair<>("field1", new JSONObject(response.getJSONArray("api_keys").get(0).toString()).getString("api_key")));
                    data.add(new Pair<>("field2", response.getString("name")));
                    data.add(new Pair<>("field3", (String.valueOf(response.getInt("id")))));
                    HttpUtils.Post(new HttpCallBack() {
                        @Override
                        public void onResult(JSONObject response) throws JSONException {
                            //Inserted into API_Table

                        }

                        @Override
                        public void onResult(String response) {
                            System.out.println(response);
                        }
                    }, "https://api.thingspeak.com/update.json?api_key=" + API_KEY_CREATE_ASSOCIATION + "&field1=" + new JSONObject(response.getJSONArray("api_keys").get(0).toString()).getString("api_key"), data, context);

                }

                @Override
                public void onResult(String response) {


                }
            }, "https://api.thingspeak.com/channels.json", data, context);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return true;
    }
    public static void getData( HttpCallBack callback,Context context, double latitude, double longitude) {
        getData(callback, context, GPSUtils.getLocationDetails(context, latitude,longitude).getLocality());
    }

    public static void getData(HttpCallBack callback, Context context, String location) {
        ThinkSpeak.location = location;
        ThinkSpeak.context = context;
        ThinkSpeak.callback = callback;
        HttpUtils.Get(new HttpCallBack() {
            @Override
            public void onResult(JSONObject response) throws JSONException {
                JSONArray feeds = response.getJSONArray("feeds");
                if (feeds.length() != 0) {

                    for (int i = 0; i < feeds.length(); i++) {
                        JSONObject elem = new JSONObject( feeds.get(i).toString());
                        if (elem.get("field2").equals(ThinkSpeak.location)) {
                            HttpUtils.Get(ThinkSpeak.callback,"https://api.thingspeak.com/channels/" + elem.get("field3") + "/feeds.json?api_key=" + elem.get("field1") + "&results=2" , ThinkSpeak.context);

                        }
                    }
                }
            }

            @Override
            public void onResult(String response) {

            }
        }, "https://api.thingspeak.com/channels/361937/feeds.json?api_key=XI56ZFE2HQM85U8H&results=2", context);
    }
}
