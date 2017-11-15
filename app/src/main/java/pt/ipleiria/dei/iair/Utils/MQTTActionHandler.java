package pt.ipleiria.dei.iair.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class MQTTActionHandler {
    private final List<String> actions;

    MQTTActionHandler(String action) {
        this.actions = Collections.singletonList(action);
    }

    MQTTActionHandler(List<String> actions) {
        this.actions = new ArrayList<>(actions);
    }

    public abstract boolean onAction(String action, JSONObject json) throws Exception;

    public boolean hasAction(String action) {
        return actions.contains(action);
    }
}
