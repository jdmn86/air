package pt.ipleiria.dei.iair.Utils;


import android.util.Pair;

import java.util.ArrayList;

import pt.ipleiria.dei.iair.Utils.InputStream.InputStreamResponseConverter;

/**
 * Created by kxtreme on 08-11-2017.
 */

public class ThinkSpeak {
    private final static String API_KEY = "6T4V93KT9K3ZVOWV";
    public static boolean createNewChannel(String channelName, int latitude, int longitude, boolean status, String... fields) {
        try {
            ArrayList<Pair<String, String>> data = new ArrayList<>();
            data.add(new Pair<>("api_key", API_KEY));
            data.add(new Pair<>("name", channelName));
            data.add(new Pair<>("latitude", String.valueOf(latitude)));
            data.add(new Pair<>("longitude", String.valueOf(longitude)));

            int i = 1;
            for (String field: fields) {

                data.add(new Pair<>("field" + i, field));
                i+=1;
            }

            //HttpUtils.HttpRequest httpRequest = new HttpUtils.HttpRequest("https://api.thingspeak.com/channels", new HttpUtils.HttpPOST(), new InputStreamResponseConverter());
            HttpUtils.doRequest("https://api.thingspeak.com/channels", new HttpUtils.HttpPOSTUrlEnconded(), data, new InputStreamResponseConverter(), null);
        } catch (Exception e) {
           System.out.println(e.getMessage());
        }

        return true;
    }
}
