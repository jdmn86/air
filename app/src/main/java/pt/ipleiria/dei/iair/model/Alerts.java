package pt.ipleiria.dei.iair.model;

import java.util.Date;

/**
 * Created by joaonascimento on 23/11/2017.
 */

public class Alerts {

    private String name ;
    private String type;
    private String message;
    private String timeStamp;
    private Date date;

    public Alerts(String name,String type, String message,String timeStamp) {
        this.name=name;
        this.type = type;
        this.message = message;
        this.timeStamp=timeStamp;
    }public Alerts(String name,String type, String message,Date timeStamp) {
        this.name=name;
        this.type = type;
        this.message = message;
        this.date=timeStamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public String toString() {
        return "Alerts{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", message='" + message + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                '}';
    }

    public Date getDate() {
        return date;
    }
}
