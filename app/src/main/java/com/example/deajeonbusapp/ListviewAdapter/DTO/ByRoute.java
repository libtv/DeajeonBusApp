package com.example.deajeonbusapp.ListviewAdapter.DTO;

public class ByRoute {
    public String BUS_NODE_ID; //정류소 아이디          0
    public String BUSSTOP_NM; //정류소 명               1
    public String BUSSTOP_ENG_NM; //정류소 영어명       2

    public String ROUTE_CD;       //버스 이름           3
    public String BUSSTOP_TP;       //기점 정점 종류    4
    public String BUSSTOP_SEQ; // 순서                  5


    public String GPS_LATI; //위도                      6
    public String GPS_LONG;  //경도                     7

    /* 여기서부터는 다른xml파싱 */
    public String BUS_Location; // 버스 위치            8
    public String BUS_Location_Car_name; // 버스번호    9


    public ByRoute(String a1, String a2, String a3, String a4, String a5, String a6, String a7, String a8) {
        BUS_NODE_ID = a1;
        BUSSTOP_NM = a2;
        BUSSTOP_ENG_NM = a3;

        ROUTE_CD = a4;
        BUSSTOP_TP = a5;
        BUSSTOP_SEQ = a6;

        GPS_LATI = a7;
        GPS_LONG = a8;
    }

    public String getBUSSTOP_NM() {
        return BUSSTOP_NM;
    }
}