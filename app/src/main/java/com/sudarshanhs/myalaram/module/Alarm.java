package com.sudarshanhs.myalaram.module;

/**
 * Created by Sudarshan on 18-12-2016.
 */

public class Alarm {


    String timeHr="",timeMin="",repeat="",state="",id="0";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTimeHr() {
        return timeHr;
    }

    public void setTimeHr(String timeHr) {
        this.timeHr = timeHr;
    }

    public String getTimeMin() {
        return timeMin;
    }

    public void setTimeMin(String timeMin) {
        this.timeMin = timeMin;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
