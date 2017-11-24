package pt.ipleiria.dei.iair.view;

import android.annotation.SuppressLint;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import pt.ipleiria.dei.iair.Utils.GPSActivity;
import pt.ipleiria.dei.iair.Utils.HttpCallBack;
import pt.ipleiria.dei.iair.Utils.HttpUtils;
import pt.ipleiria.dei.iair.controller.IAirManager;
import pt.ipleiria.dei.iair.model.Location;

/**
 * Created by ricar on 23/11/2017.
 */

abstract class GetVinicityActivity extends GPSActivity {


    public void getVicinity(LatLng latLng, int radius){

        HttpUtils.Get(new HttpCallBack() {

            @SuppressLint("ResourceType")
            @Override
            public void onResult(JSONObject response) throws JSONException {

                if(response.getJSONArray("results").length()>0){
                    Location location = new Location();
                    location.setLatitude(Double.parseDouble(response.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").get("lat").toString()));
                    location.setLongitude(Double.parseDouble(response.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").get("lng").toString()));
                    location.setLocationName(response.getJSONArray("results").getJSONObject(0).get("vicinity").toString());
                    IAirManager.INSTANCE.setCurrentLocation(location);
                }

            }

            @Override
            public void onResult(String response) {

            }
        }, "https://maps.googleapis.com/maps/api/place/search/json?radius="+String.valueOf(radius)+"&sensor=false&type=locality&key=AIzaSyCel8hjaRHf6-DK0fe3KmIsXp1MMP-RYQk&location="+latLng.latitude+","+latLng.longitude, this);

    }
}
