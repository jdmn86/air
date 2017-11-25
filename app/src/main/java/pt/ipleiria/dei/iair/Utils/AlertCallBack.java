package pt.ipleiria.dei.iair.Utils;

import java.util.List;

import pt.ipleiria.dei.iair.model.Alerts;

/**
 * Created by kxtreme on 25-11-2017.
 */

public interface AlertCallBack {
    void onResult(List<Alerts> alert);
}
