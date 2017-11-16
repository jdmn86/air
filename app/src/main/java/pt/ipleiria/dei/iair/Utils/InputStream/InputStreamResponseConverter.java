package pt.ipleiria.dei.iair.Utils.InputStream;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pt.ipleiria.dei.iair.Utils.Converter;
import pt.ipleiria.dei.iair.Utils.InputStream.InputStreamConverter;
import pt.ipleiria.dei.iair.Utils.InputStream.InputStreamJSONObjectConverter;
import pt.ipleiria.dei.iair.Utils.Response;


public class InputStreamResponseConverter extends InputStreamConverter<Response> {

    private Converter<JSONObject, Object> converterData;

    public InputStreamResponseConverter(Converter<JSONObject, Object> converterData) {
        this.converterData = converterData;
    }

    public InputStreamResponseConverter() {
        this.converterData = null;
    }

    @Override
    public Response convert(InputStream inputStream, HashMap<String, Object> options) throws Exception {
        InputStreamJSONObjectConverter reader = new InputStreamJSONObjectConverter();
        reader.setOnBytesReadListener(this.onBytesReadListener);
        Response response = new Response(reader.convert(inputStream, options));

        if (response.getJsonData() != null && converterData != null) {
            List<Object> data = new ArrayList<>();
            for (int i = 0; i < response.getJsonData().length(); i++) {
                JSONObject jsonObject = response.getJsonData().getJSONObject(i);
                data.add(converterData.convert(jsonObject, null));
            }
            response.setData(data);
        }

        return response;
    }

}