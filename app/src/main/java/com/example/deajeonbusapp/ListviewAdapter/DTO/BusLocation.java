package com.example.deajeonbusapp.ListviewAdapter.DTO;

public class BusLocation {
    public String ROUTE_CD;         //버스 아이디
    public String BUS_NODE_ID;      //정류장 아이디
    public String PLATE_NO;         //버스 번호
    public String STRE_DT;          //버스가 출발한 시간
    public String ud_type;          //버스 상행 하행 정보
    public String TOTAL_DIST;       //운행 시간
    public String GPS_LATI;         //위도
    public String GPS_LONG;         //경도

    public BusLocation(String a1, String a2, String a3, String a4, String a5, String a6, String a7, String a8) {
        this.ROUTE_CD = a1;
        this.BUS_NODE_ID = a2;
        this.PLATE_NO = a3;
        this.STRE_DT = a4;
        this.ud_type = a5;
        this.TOTAL_DIST = a6;
        this.GPS_LATI = a7;
        this.GPS_LONG = a8;
    }
}
