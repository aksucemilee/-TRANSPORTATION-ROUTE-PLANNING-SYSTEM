package org.prolab2_1.prolab2_1.sistem;

import java.util.List;

public class Durak {
    private String id;
    private String name;
    private String type;
    private double lat;
    private double lon;
    private boolean sonDurak;
    private List<NextStop> nextStops;
    private Transfer transfer;


    public Durak () {}

    public Durak(String id, String name, String type, double lat, double lon, boolean sonDurak, List<NextStop> nextStops, Transfer transfer) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.lat = lat;
        this.lon = lon;
        this.sonDurak = sonDurak;
        this.nextStops = nextStops;
        this.transfer = transfer;
    }


    public Transfer getTransfer() {
        return transfer;
    }
    public void setTransfer(Transfer transfer) {
        this.transfer = transfer;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public double getLat() {
        return lat;
    }
    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }
    public void setLon(double lon) {
        this.lon = lon;
    }

    public boolean isSonDurak() {
        return sonDurak;
    }
    public void setSonDurak(boolean sonDurak) {
        this.sonDurak = sonDurak;
    }

    public List<NextStop> getNextStops() {
        return nextStops;
    }
    public void setNextStops(List<NextStop> nextStops) {
        this.nextStops = nextStops;
    }

}