package pt.ipleiria.dei.iair.Utils.InputStream;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;

public class InputStreamJSONObjectConverter extends InputStreamConverter<JSONObject> {

    @Override
    public JSONObject convert(InputStream inputStream, HashMap<String, Object> options) throws Exception {
        InputStreamStringConverter reader = new InputStreamStringConverter();
        reader.setOnBytesReadListener(this.onBytesReadListener);
        String string = reader.convert(inputStream, options);
        return new JSONObject(string);
    }

}