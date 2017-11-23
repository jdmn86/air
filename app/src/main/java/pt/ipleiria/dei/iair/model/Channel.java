package pt.ipleiria.dei.iair.model;

/**
 * Created by joaonascimento on 23/11/2017.
 */

public class Channel {

    private String temperature;
    private String pressure;
    private String humity;

    public Channel(String temperature, String pressure, String humity) {
        this.temperature = temperature;
        this.pressure = pressure;
        this.humity = humity;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }
}
