package pt.ipleiria.dei.iair.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Response<T> {

    public final static int ERROR_CODE_A = 10;

    private JSONObject jsonObject;
    private boolean success;
    private String message;

    private JSONArray jsonData;
    private List<Object> data;

    private JSONObject jsonError;
    private Integer errorStatus;

    public JSONObject getOriginalJsonObject() {
        return jsonObject;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public JSONArray getJsonData() {
        return jsonData;
    }

    public List<Object> getData() {
        return data;
    }

    public void setData(List<Object> data) {
        this.data = data;
    }

    public JSONObject getJsonError() {
        return jsonError;
    }

    public Integer getErrorStatus() {
        return errorStatus;
    }

    public Response() {
        throw new UnsupportedOperationException();
    }

    public Response(JSONObject jsonObject) throws JSONException {
        this.jsonData = null;
        this.message = null;
        this.jsonError = null;
        this.errorStatus = null;
        this.fromJson(jsonObject);
    }

    private void fromJson(JSONObject jsonObject) throws JSONException {
        this.jsonObject = jsonObject;
        this.success = jsonObject.getBoolean("status");
        if (success) {
            this.jsonData = jsonObject.optJSONArray("data");
        } else {
            this.message = jsonObject.getString("message");
            this.jsonError = jsonObject.optJSONObject("error");
            this.errorStatus = jsonError != null ? jsonError.optInt("status") : null;
        }
    }


}