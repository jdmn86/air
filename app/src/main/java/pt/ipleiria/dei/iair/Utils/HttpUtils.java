package pt.ipleiria.dei.iair.Utils;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.OperationCanceledException;
import android.util.Log;
import android.util.Pair;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.ipleiria.dei.iair.Utils.InputStream.InputStreamConverter;
import pt.ipleiria.dei.iair.Utils.InputStream.InputStreamFileConverter;
import pt.ipleiria.dei.iair.Utils.InputStream.InputStreamResponseConverter;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import static com.android.volley.VolleyLog.TAG;

/**
 * Created by kxtreme on 08-11-2017.
 */

public class HttpUtils {

    public  static void Post(final HttpCallBack callBack, String url, final List<Pair<String, String>> params, Context context) {


        final RequestQueue MyRequestQueue = Volley.newRequestQueue(context);
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                     callBack.onResult(new JSONObject(response));
                } catch (JSONException e) {
                    callBack.onResult(response);
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
    public static void Get(final HttpCallBack callBack, String url, Context context) {
        final RequestQueue MyRequestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
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
    /*public static File downloadFile(File file, String url, final HttpRequestListener httpRequestListener) throws Exception {
        InputStreamFileConverter fileInputStreamReader = new InputStreamFileConverter(file);
        return (File) doRequest(url, new HttpGET(), null, fileInputStreamReader, httpRequestListener);
    }

    private static Object doRequest(HttpRequest httpRequest) throws Exception {
        return HttpUtils.doRequest(
                httpRequest.getUrl(),
                httpRequest.getHttpMethod(),
                httpRequest.getParams(),
                httpRequest.getInputStreamConverter(),
                httpRequest.getHttpRequestListener());
    }


    //Mandatory -> url, method, inputStreamConverter
    //Optional -> params, httpRequestListener, onHttpRequestListener
    public static <PARAMS> Object doRequest(String url, HttpMethod<PARAMS> httpMethod, PARAMS params,
                                            InputStreamConverter inputStreamReader, final HttpRequestListener httpRequestListener) throws Exception {
        HttpURLConnection conn = null;
        int responseCode;
        try {
            //Open Connection
            conn = httpMethod.openConnection(url, params);

            if (httpRequestListener != null)
                httpRequestListener.onConnectionOpened(conn);
            conn.connect();

            //Write on Request Body
            httpMethod.writeToOuputStream(conn, params);

            responseCode = conn.getResponseCode();

            //Read from Response body
            Object object = readFromInputStream(conn, inputStreamReader, httpRequestListener);

            //Check http code
            if (responseCode < 200 || responseCode >= 300)
                throw new InvalidHttpCodeException("The response code is invalid: " + conn.getResponseCode(), conn.getResponseCode(), object);

            return object;
        } finally {
            if (conn != null)
                conn.disconnect();
        }
    }

    public static RetryOnException<HttpRequest, Object> createRetryOnExceptionToDoRequest(RetryOnException.RetryParameters<HttpRequest> retryParameters) throws Exception {
        return new RetryOnException<HttpRequest, Object>(retryParameters) {
            @Override
            public Object doInTry(HttpRequest httpRequest, final int tryNumber, Exception e) throws Exception {
                return doRequest(httpRequest);
            }
        };
    }

    public static Object doRequest(final HttpRequest httpRequest, RetryOnException.RetryParameters<HttpRequest> retryParameters) throws Exception {
        if (retryParameters != null) {
            RetryOnException<HttpRequest, Object> retryOnException = createRetryOnExceptionToDoRequest(retryParameters);
            return retryOnException.execute(httpRequest);
        } else {
            return doRequest(httpRequest);
        }

    }

    public static Object readFromInputStream(
            URLConnection conn, InputStreamConverter inputStreamReader,
            final HttpRequestListener httpRequestListener) throws Exception {

        if (inputStreamReader.getOnBytesReadListener() != null)
            throw new Exception("The field onBytesReadListener of inputStreamConverter should be null");

        final double totalBytes = conn.getContentLength();
        final boolean totalBytesAvailable = totalBytes != -1;
        if (httpRequestListener != null) {
            httpRequestListener.onDownloadStarted(totalBytes);
        }
        inputStreamReader.setOnBytesReadListener(new InputStreamConverter.OnBytesReadListener() {
            @Override
            public void onBytesRead(double totalBytesRead, double bytesRead) {
                if (httpRequestListener != null) {
                    int progressPercentage = -1;
                    if (totalBytesAvailable) {
                        progressPercentage = (int) (totalBytesRead * 100 / totalBytes);
                        progressPercentage = Math.max(0, Math.min(100, progressPercentage));
                    }
                    httpRequestListener.onDownloadProgress(totalBytesRead, bytesRead, totalBytes, progressPercentage);
                }
            }
        });
        return inputStreamReader.convert(conn.getInputStream(), null);
    }

    public static void Post(String url, ArrayList<Pair<String, String>> data) {
    }

    public static abstract class HttpMethod<PARAMS> implements Serializable {
        protected String method;

        public HttpMethod(String method) {
            this.method = method;
        }

        public HttpURLConnection openConnection(String url, PARAMS params) throws Exception {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
            httpURLConnection.setRequestMethod(method);
            httpURLConnection.setDoInput(true);
            return httpURLConnection;

        }

        public void writeToOuputStream(HttpURLConnection conn, PARAMS params) throws Exception {
        }


        protected String encondeParameters(List<Pair<String, String>> params) {
            if (params == null || params.size() == 0)
                return null;

            Uri.Builder uriParamsBuilder = new Uri.Builder();
            for (Pair<String, String> param : params) {
                if (param.first == null || param.first.isEmpty())
                    continue;
                if (param.second == null || param.second.isEmpty())
                    continue;
                uriParamsBuilder.appendQueryParameter(param.first, param.second);
            }
            return uriParamsBuilder.build().getEncodedQuery();
        }
    }

    public static class HttpGET extends HttpMethod<ArrayList<Pair<String, String>>> {

        public final static String METHOD = "GET";

        public HttpGET() {
            super(METHOD);
        }

        public HttpURLConnection openConnection(String url, ArrayList<Pair<String, String>> params) throws Exception {
            String urlWithParams = appendParamsToUrl(url, params);
            return super.openConnection(urlWithParams, params);
        }

        private String appendParamsToUrl(String url, ArrayList<Pair<String, String>> params) {
            String encondedParameters = encondeParameters(params);
            return encondedParameters == null ? url : (url + "?" + encondedParameters);
        }

    }

    public static abstract class HttpPOST<PARAMS> extends HttpMethod<PARAMS> {

        public final static String METHOD = "POST";

        public HttpPOST() {
            super(METHOD);
        }

        public HttpURLConnection openConnection(String url, PARAMS params) throws Exception {
            HttpURLConnection conn = super.openConnection(url, params);
            conn.setDoOutput(true);
            return conn;
        }

        protected void writeBytesOnOutputStream(HttpURLConnection conn, byte[] bytes) throws IOException {

            OutputStream outputStream = null;
            OutputStream writer = null;
            try {
                outputStream = conn.getOutputStream();
                writer = new BufferedOutputStream(outputStream);
                writer.write(bytes);
                writer.flush();
                writer.close();
            } finally {
                if (outputStream != null)
                    outputStream.close();
                if (writer != null)
                    writer.close();
            }
        }

        protected void writeStringOnOutputStream(HttpURLConnection conn, String string) throws IOException {
            writeBytesOnOutputStream(conn, string.getBytes());
        }

    }

    public static class HttpPOSTJSON extends HttpPOST<String> {

        public HttpURLConnection openConnection(String url, String params) throws Exception {
            HttpURLConnection conn = super.openConnection(url, params);
            conn.setRequestProperty("Content-Type", "application/json");
            return conn;
        }

        @Override
        public void writeToOuputStream(HttpURLConnection conn, String jsonString) throws Exception {
            writeStringOnOutputStream(conn, jsonString);
        }
    }


    public static class HttpPOSTUrlEnconded extends HttpPOST<ArrayList<Pair<String, String>>> {

        public HttpURLConnection openConnection(String url, ArrayList<Pair<String, String>> params) throws Exception {
            HttpURLConnection conn = super.openConnection(url, params);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            return conn;
        }

        @Override
        public void writeToOuputStream(HttpURLConnection conn, ArrayList<Pair<String, String>> params) throws Exception {
            String encondedParameters = encondeParameters(params);
            writeStringOnOutputStream(conn, encondedParameters != null ? encondedParameters : "");
        }
    }

    public static boolean isConnectedToNetwork(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static class HttpRequest<PARAMS> implements Serializable {
        private String url;
        private HttpMethod<PARAMS> httpMethod;
        private InputStreamConverter inputStreamConverter;
        private PARAMS params;
        private HttpRequestListener httpRequestListener;

        public String getUrl() {
            return url;
        }

        public HttpMethod<PARAMS> getHttpMethod() {
            return httpMethod;
        }

        public InputStreamConverter getInputStreamConverter() {
            return inputStreamConverter;
        }

        public PARAMS getParams() {
            return params;
        }

        public HttpRequestListener getHttpRequestListener() {
            return httpRequestListener;
        }

        public void setHttpRequestListener(HttpRequestListener httpRequestListener) {
            this.httpRequestListener = httpRequestListener;
        }

        public HttpRequest(String url, HttpMethod<PARAMS> httpMethod, InputStreamConverter inputStreamConverter) {
            this(url, httpMethod, inputStreamConverter, null, null);
        }

        public HttpRequest(String url, HttpMethod<PARAMS> httpMethod, InputStreamConverter inputStreamConverter, PARAMS params) {
            this(url, httpMethod, inputStreamConverter, params, null);
        }

        public HttpRequest(String url, HttpMethod<PARAMS> httpMethod, InputStreamConverter inputStreamConverter, PARAMS params, HttpRequestListener httpRequestListener) {
            this.url = url;
            this.httpMethod = httpMethod;
            this.inputStreamConverter = inputStreamConverter;
            this.params = params;
            this.httpRequestListener = httpRequestListener;
        }
    }

    public static class InvalidHttpCodeException extends Exception {
        private int code;
        private Object object;

        public int getCode() {
            return code;
        }

        public InvalidHttpCodeException(String message, int code) {
            super(message);
            this.code = code;
        }

        public InvalidHttpCodeException(String message, int code, Object object) {
            super(message);
            this.code = code;
            this.object = object;
        }
    }


    public abstract static class HttpRequestListener implements Serializable {
        public void onConnectionOpened(HttpURLConnection conn) throws Exception {
        }

        public void onDownloadStarted(double totalBytes) {
        }

        public void onDownloadProgress(double totalBytesRead, double bytesRead, double totalBytes, int progressPercentage) throws OperationCanceledException {
        }
    }

    public static class HttpRequestListenerGroup extends HttpRequestListener {
        public List<HttpRequestListener> listeners;

        public HttpRequestListenerGroup(List<HttpRequestListener> listeners) {
            this.listeners = listeners;
        }

        public void onConnectionOpened(HttpURLConnection conn) throws Exception {
            for (HttpRequestListener listener : listeners) {
                listener.onConnectionOpened(conn);
            }
        }

        public void onDownloadStarted(double totalBytes) {
            for (HttpRequestListener listener : listeners) {
                listener.onDownloadStarted(totalBytes);
            }
        }

        public void onDownloadProgress(double totalBytesRead, double bytesRead, double totalBytes, int progressPercentage) throws OperationCanceledException {
            for (HttpRequestListener listener : listeners) {
                listener.onDownloadProgress(totalBytesRead, bytesRead, totalBytes, progressPercentage);
            }
        }

    }*/

}
