package pt.ipleiria.dei.iair.Utils.InputStream;
import android.os.OperationCanceledException;

import java.io.InputStream;
import java.util.HashMap;

import pt.ipleiria.dei.iair.Utils.Converter;

/**
 * Created by kxtreme on 09-11-2017.
 */


public abstract class InputStreamConverter<O extends Object> implements Converter<InputStream, O> {

    protected OnBytesReadListener onBytesReadListener;

    public OnBytesReadListener getOnBytesReadListener() {
        return onBytesReadListener;
    }

    public void setOnBytesReadListener(OnBytesReadListener onBytesReadListener) {
        this.onBytesReadListener = onBytesReadListener;
    }

    public abstract O convert(InputStream inputStream, HashMap<String, Object> options) throws Exception;

    public static abstract class OnBytesReadListener {
        protected abstract void onBytesRead(double totalBytes, double bytes) throws OperationCanceledException;
    }

    protected void onBytesReaded(int totalBytes, int bytes) throws Exception {
        if (onBytesReadListener != null) {
            onBytesReadListener.onBytesRead(totalBytes, bytes);
        }
    }

}
