package pt.ipleiria.dei.iair.model;

/**
 * Created by joaonascimento on 23/11/2017.
 */

public class Alerts {

    private String type;
    private String message;

    public Alerts(String type, String message) {
        this.type = type;
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
