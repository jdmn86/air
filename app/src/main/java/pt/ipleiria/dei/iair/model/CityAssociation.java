package pt.ipleiria.dei.iair.model;

/**
 * Created by joaonascimento on 23/11/2017.
 */

public class CityAssociation {

    private String API_KEY_CHANNEL;
    private String API_KEY_ALERTS;
    private String REGION_NAME;
    private String CHANNEL_ID;
    private String ALERTS_ID;
    private String latitude;
    private String longitude;
    private int id;

    public CityAssociation(String API_KEY_CHANNEL, String API_KEY_ALERTS, String REGION_NAME, String CHANNEL_ID, String ALERTS_ID, String latitude, String longitude) {
        this.API_KEY_CHANNEL = API_KEY_CHANNEL;
        this.API_KEY_ALERTS = API_KEY_ALERTS;
        this.REGION_NAME = REGION_NAME;
        this.CHANNEL_ID = CHANNEL_ID;
        this.ALERTS_ID = ALERTS_ID;
        this.latitude = latitude;
        this.longitude = longitude;


    }


    public String getAPI_KEY_CHANNEL() {
        return API_KEY_CHANNEL;
    }

    public void setAPI_KEY_CHANNEL(String API_KEY_CHANNEL) {
        this.API_KEY_CHANNEL = API_KEY_CHANNEL;
    }

    public String getAPI_KEY_ALERTS() {
        return API_KEY_ALERTS;
    }

    public void setAPI_KEY_ALERTS(String API_KEY_ALERTS) {
        this.API_KEY_ALERTS = API_KEY_ALERTS;
    }

    public String getREGION_NAME() {
        return REGION_NAME;
    }

    public void setREGION_NAME(String REGION_NAME) {
        this.REGION_NAME = REGION_NAME;
    }

    public String getCHANNEL_ID() {
        return CHANNEL_ID;
    }

    public void setCHANNEL_ID(String CHANNEL_ID) {
        this.CHANNEL_ID = CHANNEL_ID;
    }

    public String getALERTS_ID() {
        return ALERTS_ID;
    }

    public void setALERTS_ID(String ALERTS_ID) {
        this.ALERTS_ID = ALERTS_ID;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public int getChannel() {
        return id;
    }

    public void setChannel(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "CityAssociation{" +
                "API_KEY_CHANNEL='" + API_KEY_CHANNEL + '\'' +
                ", API_KEY_ALERTS='" + API_KEY_ALERTS + '\'' +
                ", REGION_NAME='" + REGION_NAME + '\'' +
                ", CHANNEL_ID='" + CHANNEL_ID + '\'' +
                ", ALERTS_ID='" + ALERTS_ID + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                '}';
    }
}
