package pt.ipleiria.dei.iair.Utils;


import android.content.Context;
import android.location.LocationManager;
import android.util.Pair;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;

import pt.ipleiria.dei.iair.R;
import pt.ipleiria.dei.iair.controller.IAirManager;
import pt.ipleiria.dei.iair.model.Alerts;
import pt.ipleiria.dei.iair.model.Channel;
import pt.ipleiria.dei.iair.model.CityAssociation;
import pt.ipleiria.dei.iair.view.DashboardActivity;

/**
 * Created by kxtreme on 08-11-2017.
 */

public class ThinkSpeak {
    private final static String API_KEY_CREATE_CHANNEL = "6T4V93KT9K3ZVOWV";
    private final static String API_KEY_CREATE_ASSOCIATION = "BAFPV9ZE40IW6C6G";
   // private final static String CHANNEL_NUMBER_CREATE_ASSOCIATION = "BAFPV9ZE40IW6C6G";
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
        } else if( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER )) {
            Toast.makeText(context, R.string.No_gps_message, Toast.LENGTH_SHORT).show();
            return false;
        }

          location = GPSUtils.getLocationDetails(context,latitude, longitude).getLocality();
        System.out.println(location);
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

    public static boolean createNewChannel(final CallBack callBack, final Context context, final String channelName, double latitude, double longitude, boolean status, String... fields) {
        try {
            ArrayList<Pair<String, String>> data = new ArrayList<>();
            data.add(new Pair<>("api_key", API_KEY_CREATE_CHANNEL));
            data.add(new Pair<>("name", ThinkSpeak.location));
            data.add(new Pair<>("latitude", String.valueOf(latitude)));
            data.add(new Pair<>("longitude", String.valueOf(longitude)));

            int i = 1;
            for (String field : fields) {

                data.add(new Pair<>("field" + i, field));
                i += 1;
            }

            //MQTTHandler mqtt = new MQTTHandler(context, "channels/361937/subscribe/json/" + API_KEY_CREATE_ASSOCIATION, "pl12taes217", "0DT1756US8QLAZUK".toCharArray());
            //mqtt.connect();
            //mqtt.addActionListener(new MQTTThinkSpeakHandler() {

            //});

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
                System.out.println(feeds.length());
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

    public static void getAllCitysAssociation(HttpCallBack callback, Context context, String location){

            ThinkSpeak.location = location;
            ThinkSpeak.context = context;
            ThinkSpeak.callback = callback;
            HttpUtils.Get(new HttpCallBack() {
                @Override
                public void onResult(JSONObject response) throws JSONException {
                    JSONArray feeds = response.getJSONArray("feeds");
                    System.out.println(feeds.length());
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



    public static void createNewChannel(final String name, final String latitude, final String longitude , final Context context) {

        CityAssociation city=IAirManager.INSTANCE.getCityAssociation(name);
        if(city!=null){
            System.out.println("RETURN in create channel");
            return;
        }
        System.out.println("CREATE in create channel");

        ArrayList<Pair<String, String>> data = new ArrayList<>();
        //data.add(new Pair<>("API_KEY_CHANNEL", API_KEY_CREATE_ASSOCIATION));
        data.add(new Pair<>("field1", "TEMPERATURE"));
        data.add(new Pair<>("field2", "PRESSURE"));
        data.add(new Pair<>("field3", "HUMITY"));
        data.add(new Pair<>("latitude",  latitude.toString()));
        data.add(new Pair<>("longitude", longitude.toString()));

        HttpUtils.Post(new HttpCallBack() {
            @Override
            public void onResult(JSONObject response) throws JSONException {

                String key =response.getJSONArray("api_keys").getJSONObject(0).get("api_key").toString();
                String id = response.get("id").toString();
               String latitude = response.get("latitude").toString();
                String longitude = response.get("longitude").toString();

                CityAssociation city=new CityAssociation(key,"",name,id,"",latitude,longitude);

                createNewAlert(city,context);


            }

            @Override
            public void onResult(String response) {


            }
        }, "https://api.thingspeak.com/channels.json?api_key=" + API_KEY_CREATE_CHANNEL + "&name=" + name, data, context);


    }


    public static void createNewAlert(final CityAssociation city, final Context context) {

        ArrayList<Pair<String, String>> data = new ArrayList<>();
        //data.add(new Pair<>("API_KEY_CHANNEL", API_KEY_CREATE_ASSOCIATION));
        data.add(new Pair<>("field1", "TYPE"));
        data.add(new Pair<>("field2", "MESSAGE"));
        data.add(new Pair<>("field3", "TIMESTAMP"));


        HttpUtils.Post(new HttpCallBack() {
            @Override
            public void onResult(JSONObject response) throws JSONException {

                String key = response.getJSONArray("api_keys").getJSONObject(0).get("api_key").toString();
                String id = response.get("id").toString();

                city.setALERTS_ID(id);
                city.setAPI_KEY_ALERTS(key);

                IAirManager.INSTANCE.addCityAssociation(city);

                insertInAssociation(city,context);


            }

            @Override
            public void onResult(String response) {


            }
        }, "https://api.thingspeak.com/channels.json?api_key=" + API_KEY_CREATE_CHANNEL + "&name=A_" + city.getREGION_NAME(), data, context);

    }

    public static void insertInAssociation(CityAssociation city, Context context) {

           // IAirManager.INSTANCE.addCityAssociation(city);

            ArrayList<Pair<String, String>> data = new ArrayList<>();
            data.add(new Pair<>("field1", city.getREGION_NAME() ));
            data.add(new Pair<>("field2",city.getAPI_KEY_CHANNEL() ));
            data.add(new Pair<>("field3", String.valueOf(city.getCHANNEL_ID()) ));
            data.add(new Pair<>("field4", city.getAPI_KEY_ALERTS() ));
            data.add(new Pair<>("field5",String.valueOf(city.getALERTS_ID()) ));
            data.add(new Pair<>("field6", city.getLatitude() ));
            data.add(new Pair<>("field7",city.getLongitude() ));

            HttpUtils.Post(new HttpCallBack() {
                @Override
                public void onResult(JSONObject response) throws JSONException {

                    System.out.println(response.toString());

                }

                @Override
                public void onResult(String response) {

                }
            }, "https://api.thingspeak.com/update?api_key=" + API_KEY_CREATE_ASSOCIATION, data, context);
            return;
        }



    public static void insertInAlerts(Alerts alert, Context context) {

        //get api key for alert to add

        CityAssociation city=IAirManager.INSTANCE.getCityAssociation(alert.getName());

        IAirManager.INSTANCE.addAlert(alert);

        System.out.println("HEREEEEE alert"+city.toString());

        ArrayList<Pair<String, String>> data = new ArrayList<>();
        data.add(new Pair<>("field1", alert.getType() ));
        data.add(new Pair<>("field2",alert.getMessage() ));
        data.add(new Pair<>("field3", alert.getTimeStamp() ));


        HttpUtils.Post(new HttpCallBack() {
            @Override
            public void onResult(JSONObject response) throws JSONException {

                System.out.println(response.toString());
            }

            @Override
            public void onResult(String response) {

            }
        }, "https://api.thingspeak.com/update?api_key=" + city.getAPI_KEY_ALERTS().toString(), data, context);

        return;

    }

    public static void insertInChannel(Channel channel, Context context) {

        //get api key for alert to add

        CityAssociation city=IAirManager.INSTANCE.getCityAssociation(channel.getName());
        System.out.println("tamanho:"+IAirManager.INSTANCE.getAllCityAssociations());
        IAirManager.INSTANCE.addChannel(channel);

        System.out.println("HEREEEEE city"+city.toString());

        ArrayList<Pair<String, String>> data = new ArrayList<>();
        data.add(new Pair<>("field1", channel.getTemperature() == null? "N/A":channel.getTemperature() ));
        data.add(new Pair<>("field2",channel.getPressure() == null? "N/A": channel.getPressure()));
        data.add(new Pair<>("field3", channel.getHumity() == null? "N/A": channel.getHumity()));
        data.add(new Pair<>("latitude",city.getLatitude()));
        data.add(new Pair<>("longitude", city.getLongitude()));

        System.out.println("getAPI_KEY "+city.getAPI_KEY_CHANNEL().toString());

        HttpUtils.Post(new HttpCallBack() {
            @Override
            public void onResult(JSONObject response) throws JSONException {

                System.out.println(response.toString());
            }

            @Override
            public void onResult(String response) {

            }
        }, "https://api.thingspeak.com/update?api_key=" + city.getAPI_KEY_CHANNEL().toString(), data, context);

        return;

    }

    public static void getThingDataAssociations(final Context context) {

        HttpUtils.Get(new HttpCallBack() {
            @Override
            public void onResult(JSONObject response) throws JSONException {
                JSONArray feeds = response.getJSONArray("feeds");
                System.out.println(feeds.length());
                if (feeds.length() != 0) {

                    for (int i = 0; i < feeds.length(); i++) {
                        //int id=feeds.getJSONObject(i).getInt("entry_id");
                        String nome=feeds.getJSONObject(i).getString("field1");
                        String KEY_CHANNEL=feeds.getJSONObject(i).getString("field2");
                        String id_CHANNEL=feeds.getJSONObject(i).getString("field3");
                        String KEY_ALERT=feeds.getJSONObject(i).getString("field4");
                        String id_ALERT=feeds.getJSONObject(i).getString("field5");
                        String Latitude=feeds.getJSONObject(i).getString("field6");
                        String longitude=feeds.getJSONObject(i).getString("field7");

                        CityAssociation city = new CityAssociation(KEY_CHANNEL,KEY_ALERT,nome,id_CHANNEL,id_ALERT,Latitude,longitude);
                        IAirManager.INSTANCE.addCityAssociation(city);
                    }
                }


                    getThingDataChannels(IAirManager.INSTANCE.getAllCityAssociations(),context);
                    getThingDataAlerts(IAirManager.INSTANCE.getAllCityAssociations(),context);

                    verificaLocationFavourite(context);
            }

            @Override
            public void onResult(String response) {

            }
        }, "https://api.thingspeak.com/channels/361937/feeds.json?api_key="+API_KEY_CREATE_ASSOCIATION, context);
    }

    private static void verificaLocationFavourite(Context context) {
        CityAssociation city=IAirManager.INSTANCE.getCityAssociation(IAirManager.INSTANCE.getFavoriteLocationName());

        if(city!=null){

            DashboardActivity.putDataOnDashboard(context);


        }else{
            System.out.println("TESTE:" +IAirManager.INSTANCE.getCurrentLocationName());
            ThinkSpeak.createNewChannel(IAirManager.INSTANCE.getCurrentLocationName().toString(),String.valueOf(IAirManager.INSTANCE.getCurrentLocation().latitude),String.valueOf(IAirManager.INSTANCE.getCurrentLocation().longitude), context);

            System.out.println("tamanho citys:" + IAirManager.INSTANCE.getAllCityAssociations().size());
            if (city != null){

                //ThinkSpeak.insertInChannel(channel,this);
                pt.ipleiria.dei.iair.model.Channel channel2 = new pt.ipleiria.dei.iair.model.Channel(IAirManager.INSTANCE.getTemperature(), IAirManager.INSTANCE.getPresure(), IAirManager.INSTANCE.getHumity(), city.getREGION_NAME(),city.getLatitude().toString(),city.getLongitude().toString());
                //channel=IAirManager.INSTANCE.getChannel(local);
                ThinkSpeak.insertInChannel(channel2, context);
            }

        }

    }

    public static void getThingDataAlerts(LinkedList<CityAssociation> listaCitys, Context context) {
        for (CityAssociation city:listaCitys) {

            //CityAssociation city = IAirManager.INSTANCE.getCityAssociation(alert.getName());

        HttpUtils.Get(new HttpCallBack() {
            @Override
            public void onResult(JSONObject response) throws JSONException {
                JSONArray feeds = response.getJSONArray("feeds");
                System.out.println(feeds.length());
                if (feeds.length() != 0) {

                    for (int i = 0; i < feeds.length(); i++) {
                        String name=response.getJSONObject("channel").getString("name");
                        String type=feeds.getJSONObject(i).getString("field1");
                        String message=feeds.getJSONObject(i).getString("field2");
                        String timestamp=feeds.getJSONObject(i).getString("field3");

                        Alerts alert = new Alerts(name,type,message,timestamp);
                        IAirManager.INSTANCE.addAlert(alert);
                    }
                }

                for (Alerts alert:IAirManager.INSTANCE.getAllAlerts()) {
                    System.out.println("alert : "+alert.toString());
                }
            }

            @Override
            public void onResult(String response) {

            }
        }, "https://api.thingspeak.com/channels/"+city.getALERTS_ID()+"/feeds.json?api_key="+city.getAPI_KEY_ALERTS().toString(), context);
        }
    }

    public static void getThingDataChannels(LinkedList<CityAssociation> listaCitys, Context context) {
        for (CityAssociation city:listaCitys) {

            //CityAssociation city = IAirManager.INSTANCE.getCityAssociation(alert.getName());

            HttpUtils.Get(new HttpCallBack() {
                @Override
                public void onResult(JSONObject response) throws JSONException {
                    JSONArray feeds = response.getJSONArray("feeds");
                    System.out.println(feeds.length());
                    if (feeds.length() != 0) {

                        for (int i = 0; i < feeds.length(); i++) {
                            //int id=response.getJSONArray("channel").getJSONObject(i).getInt("entry_id");
                            String name=response.getJSONObject("channel").getString("name");
                            String temperature=feeds.getJSONObject(i).getString("field1");
                            String PRESSURE=feeds.getJSONObject(i).getString("field2");
                            String HUMITY=feeds.getJSONObject(i).getString("field3");
                            String latitude=response.getJSONObject("channel").getString("latitude");
                            String longitude=response.getJSONObject("channel").getString("longitude");

                            Channel channel = new Channel(temperature,PRESSURE,HUMITY,name,latitude,longitude);
                            IAirManager.INSTANCE.addChannel(channel);
                        }
                    }

                    for (Channel channel:IAirManager.INSTANCE.getAllChannels()) {
                        System.out.println("channel : "+channel.toString());
                    }
                }

                @Override
                public void onResult(String response) {

                }
            }, "https://api.thingspeak.com/channels/"+city.getCHANNEL_ID()+"/feeds.json?api_key="+city.getAPI_KEY_CHANNEL().toString(), context);
        }
    }

    public static void getThingDataChannelLastData(CityAssociation city) {
        HttpUtils.Get(new HttpCallBack() {
            @Override
            public void onResult(JSONObject response) throws JSONException {
                JSONArray feeds = response.getJSONArray("feeds");
              //  int id=response.getInt("entry_id");
                String name=response.getJSONObject("channel").getString("name");
                String temperature=feeds.getJSONObject(0).getString("field1");
                String PRESSURE=feeds.getJSONObject(0).getString("field2");
                String HUMITY=feeds.getJSONObject(0).getString("field3");
                String latitude=response.getJSONObject("channel").getString("latitude");
                String longitude=response.getJSONObject("channel").getString("longitude");

               // Channel channel = new Channel(temperature,PRESSURE,HUMITY,name,id,location);
                //IAirManager.INSTANCE.addChannel(channel);
                System.out.println("Last channel : "+name + temperature + "etc" );
                LatLng latLng= new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude));

                Channel channel = new Channel(temperature,PRESSURE,HUMITY,name,latitude,longitude);
                IAirManager.INSTANCE.addChannel(channel);
                IAirManager.INSTANCE.setCityIdLast(IAirManager.INSTANCE.getAllChannels().lastIndexOf(channel));
            }

            @Override
            public void onResult(String response) {

            }
        }, "https://api.thingspeak.com/channels/"+city.getCHANNEL_ID()+"/feeds/last.json?api_key="+city.getAPI_KEY_CHANNEL().toString()+"&last", context);
    }


    public static void getThingDataAlertsLast(CityAssociation city, Context context) {

            //CityAssociation city = IAirManager.INSTANCE.getCityAssociation(alert.getName());

            HttpUtils.Get(new HttpCallBack() {
                @Override
                public void onResult(JSONObject response) throws JSONException {
                    JSONArray feeds = response.getJSONArray("channel");
                    System.out.println(feeds.length());
                    if (feeds.length() != 0) {

                        for (int i = 0; i < feeds.length(); i++) {
                            String name=feeds.getJSONObject(i).getString("name");
                            String type=feeds.getJSONObject(i).getString("field2");
                            String message=feeds.getJSONObject(i).getString("field3");
                            String timestamp=feeds.getJSONObject(i).getString("field4");

                            Alerts alert = new Alerts(name,type,message,timestamp);
                            IAirManager.INSTANCE.addAlert(alert);
                        }
                    }

                    for (Alerts alert:IAirManager.INSTANCE.getAllAlerts()) {
                        System.out.println("alert : "+alert.toString());
                    }
                }

                @Override
                public void onResult(String response) {

                }
            }, "https://api.thingspeak.com/channels/"+city.getALERTS_ID()+"/feeds.json?api_key="+city.getAPI_KEY_ALERTS().toString(), context);
        }
    }




