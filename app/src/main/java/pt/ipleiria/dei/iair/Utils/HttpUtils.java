package pt.ipleiria.dei.iair.Utils;
import android.content.Context;
import android.util.Log;
import android.util.Pair;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.android.volley.VolleyLog.TAG;

/**
 * Created by kxtreme on 08-11-2017.
 */

public class HttpUtils {

    public  static void Post(final HttpCallBack callBack, String url, final List<Pair<String, String>> params, Context context) {

            System.out.println(url);
        final RequestQueue MyRequestQueue = Volley.newRequestQueue(context);
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    if(callBack!= null)
                     callBack.onResult(new JSONObject(response));
                } catch (JSONException e) {
                    callBack.onResult(response);
                }
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
            System.out.println(error.getMessage());

                //This code is executed if there is an error.
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                for (Pair<String, String>param: params) {
                    MyData.put(param.first, param.second);
                }
                return MyData;
            }
        };
        //System.out.println(MyStringRequest.getHeaders().toString());

MyRequestQueue.add(MyStringRequest);

    }
    public static void Get(final HttpCallBack callBack, String url, Context context) {
        final RequestQueue MyRequestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            callBack.onResult(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // display response
                        Log.d("Response", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.getMessage());
                    }
                }
        );

// add it to the RequestQueue
        MyRequestQueue.add(getRequest);
    }


    public static void Put(final HttpCallBack callBack, String url, final ArrayList<Pair<String, String>> params, Context context) {
        final RequestQueue MyRequestQueue = Volley.newRequestQueue(context);
        StringRequest MyStringRequest = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    callBack.onResult(new JSONObject(response));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {

                final int status = error.networkResponse.statusCode;
                // Handle 30x
                if(HttpURLConnection.HTTP_MOVED_PERM == status || status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_SEE_OTHER) {
                    final String location = error.networkResponse.headers.get("Location");
                    Log.d(TAG, "Location: " + location);
                }

                //This code is executed if there is an error.
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                for (Pair<String, String>param: params) {
                    MyData.put(param.first, param.second);
                }
                return MyData;
            }
        };
        //System.out.println(MyStringRequest.getHeaders().toString());

        MyRequestQueue.add(MyStringRequest);
    }

}
