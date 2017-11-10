package pt.ipleiria.dei.iair.Utils;


import android.content.Context;
import android.util.Pair;

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

    public static boolean createNewChannel(final Context context, String channelName, double latitude, double longitude, boolean status, String... fields) {
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

            //HttpUtils.HttpRequest httpRequest = new HttpUtils.HttpRequest("https://api.thingspeak.com/channels", new HttpUtils.HttpPOST(), new InputStreamResponseConverter());
            HttpUtils.Post(new HttpCallBack() {
                @Override
                public void onResult(JSONObject response) throws JSONException {
                    ArrayList<Pair<String, String>> data = new ArrayList<>();
                    data.add(new Pair<>("API_KEY_CHANNEL", API_KEY_CREATE_ASSOCIATION));
                    data.add(new Pair<>("field1", new JSONObject(response.getJSONArray("api_keys").get(0).toString()).getString("api_key")));
                    data.add(new Pair<>("field2", response.getString("name")));
                    data.add(new Pair<>("field3", (String.valueOf(response.getInt("id")))));
                    HttpUtils.Post(new HttpCallBack() {
                        @Override
                        public void onResult(JSONObject response) throws JSONException {
                            MQTTHelper mqtt = new MQTTHelper(context, "channels/361937/subscribe/json/" + API_KEY_CREATE_ASSOCIATION);

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
