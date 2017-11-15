package pt.ipleiria.dei.iair.Utils.InputStream;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;

import pt.ipleiria.dei.iair.Utils.InputStream.InputStreamConverter;

public class InputStreamStringConverter extends InputStreamConverter<String> {

    private String charsetName = "UTF8";

    public InputStreamStringConverter() {
    }

    public InputStreamStringConverter(String charsetName) {
        this.charsetName = charsetName;
    }

    @Override
    public String convert(InputStream inputStream, HashMap<String, Object> options) throws Exception {

        char[] buffer = new char[1024 * 4];
        int totalBytes = 0;
        int bytes;

        java.io.InputStreamReader reader = new java.io.InputStreamReader(inputStream, charsetName);
        StringWriter writer = new StringWriter();

        try {
            while ((bytes = reader.read(buffer)) != -1) {
                totalBytes += bytes;
                super.onBytesReaded(totalBytes, bytes);
                writer.write(buffer, 0, bytes);
            }
        } finally {
            reader.close();
            writer.close();
            inputStream.close();
        }

        return writer.toString();
    }


}