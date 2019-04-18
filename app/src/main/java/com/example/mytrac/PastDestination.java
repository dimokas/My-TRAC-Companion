package com.example.mytrac;

import java.util.Date;

public class PastDestination {

    private String address;
    private String area;
    private String date;

    public enum State {
        HOME,
        WORK,
        FAVORITE,
        NON_FAVORITE
    }

    private State state;

    public PastDestination(String address, String area, State state) {
        this.address = address;
        this.area = area;
        this.state = state;
    }

    public PastDestination(String address, String area, State state, String date) {
        this.address = address;
        this.area = area;
        this.date = date;
        this.state = state;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getArea() {
        return area;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setState(State state) {
        this.state = state;
    }

    public State getState() {
        return state;
    }
}
