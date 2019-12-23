package com.example.deajeonbusapp.ListviewAdapter.DTO;

public class Businfo {
    public String ROUTE_CD; //버스 아이디
    public String ROUTE_NO; //버스 이름
    public String ROUTE_TP; //노선 유형
    public String BUSSTOP_CNT; //버스 정류장 수
    public String RUN_DIST_HALF;  //반환 거리
    public String RUN_TM;         //평균 운행시간
    public String START_NODE_ID;  //기점 정류소 아이디
    public String END_NODE_ID;    //종점 정류소 아이디
    public String ORIGIN_START;   //첫차 시간
    public String ORIGIN_START_SAT;//주말 첫차 시간
    public String ORIGIN_START_SUN;//일요일 첫차 시간
    public String ALLO_INTERVAL;  //평일 배차 시간
    public String ALLO_INTERVAL_SAT;//주말 배차 시간
    public String ALLO_INTERVAL_SUN;//일요일 배차 시간

    public Businfo(String a1, String a2, String a3, String a4, String a5, String a6, String a7, String a8, String a9, String a10, String a11, String a12, String a13, String a14) {
        ROUTE_CD = a1;
        ROUTE_NO = a2;
        ROUTE_TP = a3;
        BUSSTOP_CNT = a4;
        RUN_DIST_HALF = a5;
        RUN_TM = a6;
        START_NODE_ID = a7;
        END_NODE_ID = a8;
        ORIGIN_START = a9;
        ORIGIN_START_SAT = a10;
        ORIGIN_START_SUN = a11;
        ALLO_INTERVAL = a12;
        ALLO_INTERVAL_SAT = a13;
        ALLO_INTERVAL_SUN = a14;
    }

    public String getROUTE_NO() {
        return ROUTE_NO;
    }
    public String getROUTE_CD() {
        return ROUTE_CD;
    }
}