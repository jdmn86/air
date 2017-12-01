package pt.ipleiria.dei.iair.Utils.InputStream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;

import pt.ipleiria.dei.iair.Utils.InputStream.InputStreamConverter;

/**
 * Created by kxtreme on 09-11-2017.
 */

public class InputStreamFileConverter extends InputStreamConverter<File> {

    private File file;

    public InputStreamFileConverter(File file) {
        this.file = file;
    }


    @Override
    public File convert(InputStream inputStream, HashMap<String, Object> options) throws Exception {

        FileOutputStream fileOutputStream = new FileOutputStream(this.file);

        byte[] buffer = new byte[1024];
        int totalBytes = 0;
        int bytes;

        try {
            while ((bytes = inputStream.read(buffer)) != -1) {
                totalBytes += bytes;
            /*if (onBytesReadListener != null) {
                onBytesReadListener.onBytesRead(totalBytes, bytes);
            }*/
                super.onBytesReaded(totalBytes, bytes);
                fileOutputStream.write(buffer, 0, bytes);
            }
        } finally {
            fileOutputStream.close();
            inputStream.close();
        }


        return file;
    }


}
