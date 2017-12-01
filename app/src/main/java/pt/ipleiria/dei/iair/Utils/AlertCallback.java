package pt.ipleiria.dei.iair.Utils;

import java.util.LinkedList;
import java.util.List;

import pt.ipleiria.dei.iair.model.Alerts;
import pt.ipleiria.dei.iair.model.CityAssociation;

/**
 * Created by kxtreme on 25-11-2017.
 */

public interface AlertCallback {
    void onResult(List<Alerts> alert);

    void onResult(LinkedList<CityAssociation> cityAssociations);
}
