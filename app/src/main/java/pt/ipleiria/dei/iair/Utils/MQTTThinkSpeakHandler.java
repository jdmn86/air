package pt.ipleiria.dei.iair.Utils;

import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by kxtreme on 11-11-2017.
 */

public class MQTTThinkSpeakHandler extends MQTTActionHandler{
    public MQTTThinkSpeakHandler() {
        super(Arrays.asList("fire", "rain", "wind", "gas_leak", "other"));
    }

    @Override
    public boolean onAction(String action, JSONObject json) throws Exception {
        System.out.println(action + json.toString());
        return false;
    }
}
