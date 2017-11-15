package pt.ipleiria.dei.iair.Utils;


import android.content.Context;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by kxtreme on 08-11-2017.
 */

public class ThinkSpeak {
    private final static String API_KEY_CREATE_CHANNEL = "6T4V93KT9K3ZVOWV";
    private final static String API_KEY_CREATE_ASSOCIATION = "BAFPV9ZE40IW6C6G";
    private final static String CHANNEL_NUMBER_CREATE_ASSOCIATION = "BAFPV9ZE40IW6C6G";
    public static String location;

    public static boolean sendData(final Context context, final double latitude, final double longitude, final String temperature, final String pressure, final String humity) {
          location = GPSUtils.getLocationDetails(context,latitude, longitude).getLocality();
        HttpUtils.Get(new HttpCallBack() {
            @Override
            public void onResult(JSONObject response) throws JSONException {
                JSONArray feeds = response.getJSONArray("feeds");

                for (int i = 0; i < feeds.length();i++ ) {
                    JSONObject elem = new JSONObject((String) feeds.get(i));
                    if (elem.get("field2").equals(location)) {
                        //insert if already exists
                        ArrayList<Pair<String, String>> data = new ArrayList<>();
                        data.add(new Pair<>("api_key", elem.getString("field1")));
                        data.add(new Pair<>("name", location));
                        data.add(new Pair<>("field1", temperature));
                        data.add(new Pair<>("field2", pressure));
                        data.add(new Pair<>("field3", humity));

                        HttpUtils.Post(null, "https://api.thingspeak.com/update.json?api_key=" + elem.getString("API_KEY_CHANNEL") + "&field1=" + temperature, data, context);
                      return;
                    }
                    createNewChannel(new CallBack() {
                        @Override
                        public void onFinish(String... messages) {
                            ArrayList<Pair<String, String>> data = new ArrayList<>();
                            data.add(new Pair<>("api_key", messages[0]));
                            data.add(new Pair<>("name", messages[1]));
                            data.add(new Pair<>("field1", temperature));
                            data.add(new Pair<>("field2", pressure));
                            data.add(new Pair<>("field3", humity));

                            HttpUtils.Post(null, "https://api.thingspeak.com/update.json?api_key=" + messages[0] + "&field1=" + messages[1], data, context);
                        }

                    }, context,location, latitude,longitude,true, "temperature", "pressure", "humity");

                }
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
}
