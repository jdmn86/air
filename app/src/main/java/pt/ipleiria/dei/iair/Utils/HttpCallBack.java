package pt.ipleiria.dei.iair.Utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kxtreme on 10-11-2017.
 */

public interface HttpCallBack {
    void onResult(JSONObject response) throws JSONException;
}
