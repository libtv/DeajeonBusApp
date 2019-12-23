package com.example.deajeonbusapp.ListviewAdapter.DTO;

public class ALARM_Bus {
    public String BUS_NODE_ID;                              //정류소 아이디                0
    public String BUSSTOP_NM;                               //정류소 명                    1

    public String ROUTE_CD;                                 //버스 아이디                  2
    public String ROUTE_NO;                                 //버스 이름                    3

    public String ALLO_INTERVAL;
    public String ALARM_HOUR;                               //알람 시간                    5

    public String ALARM_MIN;                                // 알람 분                     6

    public ALARM_Bus(String a1, String a2, String a3, String a4, String a5, String a6, String a7) {
        this.BUS_NODE_ID = a1;
        this.BUSSTOP_NM = a2;
        this.ROUTE_CD = a3;
        this.ROUTE_NO = a4;
        this.ALLO_INTERVAL = a5;
        this.ALARM_HOUR = a6;
        this.ALARM_MIN = a7;
    }
}
