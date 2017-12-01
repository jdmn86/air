package pt.ipleiria.dei.iair.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import pt.ipleiria.dei.iair.model.Alerts;

/**
 * Created by kxtreme on 10-11-2017.
 */

public interface HttpCallBack {
    void onResult(JSONObject response) throws JSONException;

    void onResult(String response);

}
