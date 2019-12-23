package com.example.deajeonbusapp.ListviewAdapter.DTO;

public class StationLocation {
    public String ROUTE_CD;         //버스 아이디
    public String ROUTE_NO;         //버스 이름
    public String ROUTE_TP;         //버스 유형

    public String LAST_CAT;         //첫차 막차 일반 구분(1, 2, 3)
    public String MSG_TP;           //메시지유형 '01'도착, '02' 출발, '03' 몇분후 도착 '04' 교차로 통과 '06 진입중  '07 차고지 운행대기중

    public String STATUS_POS;       //잔여 정류장 수
    public String DESTINATION;        //도착지
    public String CAR_REG_NO;       //차량 번호

    public String EXTIME_MIN;       //도착 예정 분
    public String EXTIME_SEC;       //도착 예정 초

    public StationLocation(String a1, String a2, String a3, String a4, String a5, String a6, String a7, String a8, String a9, String a10) {
        this.ROUTE_CD = a1;
        this.ROUTE_NO = a2;
        this.ROUTE_TP = a3;
        this.LAST_CAT = a4;
        this.MSG_TP = a5;
        this.STATUS_POS = a6;
        this.DESTINATION = a7;
        this.CAR_REG_NO = a8;
        this.EXTIME_MIN = a9;
        this.EXTIME_SEC = a10;
    }
}
