package pt.ipleiria.dei.iair.Utils;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by kxtreme on 16-11-2017.
 */

public class InternetUtils {
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
}
