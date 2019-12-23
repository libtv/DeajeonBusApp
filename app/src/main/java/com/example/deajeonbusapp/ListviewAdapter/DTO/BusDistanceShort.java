package com.example.deajeonbusapp.ListviewAdapter.DTO;

public class BusDistanceShort {
    public String BUS_NODE_ID;
    public String BUSSTOP_NM;
    public String GPS_LATI;
    public String GPS_LONG;

    public BusDistanceShort(String a1, String a2, String a3, String a4) {
        this.BUS_NODE_ID = a1;
        this.BUSSTOP_NM = a2;
        this.GPS_LATI = a3;
        this.GPS_LONG = a4;
    }

}
