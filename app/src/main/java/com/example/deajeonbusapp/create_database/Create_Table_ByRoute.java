package com.example.deajeonbusapp.create_database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Create_Table_ByRoute extends SQLiteOpenHelper {

    // 데이터베이스
    private static final String DATABASE_NAME      = "BUS.ByRoute.db";
    private static final int DATABASE_VERSION      = 1;

    // 테이블
    public static final String TABLE_NAME       = "BYROUTE";
    public static final String BUS_NODE_ID        = "BUS_NODE_ID";       //정류소 아이디
    public static final String BUSSTOP_NM      = "BUSSTOP_NM";       //정류소 명
    public static final String BUSSTOP_ENG_NM      = "BUSSTOP_ENG_NM";       //정류소 영어명

    public static final String ROUTE_CD        = "ROUTE_CD";       //버스 아이디
    public static final String BUSSTOP_TP        = "BUSSTOP_TP";       //기점 정점 종류
    public static final String BUSSTOP_SEQ        = "BUSSTOP_SEQ";       // 순서

    public static final String GPS_LATI     = "GPS_LATI";    //정류소 위도 좌표
    public static final String GPS_LONG      = "GPS_LONG";  //정류소 경도 좌표


    private static final String DATABASE_CREATE_TEAM = "create table "
            + TABLE_NAME + "(" + BUS_NODE_ID + " Integer, " //정류소 아이디



            + BUSSTOP_NM + " text, "                                              //정류소 명
            + BUSSTOP_ENG_NM + " text, "                                              //정류소 영어명

            + ROUTE_CD + " integer, "                                              //버스 아이디
            + BUSSTOP_TP + " integer, "                                              //기점 정점 종류
            + BUSSTOP_SEQ + " integer, "                                              // 순서

            + GPS_LATI + " text, "                                           //정류소 위도 좌표
            + GPS_LONG + " text);";                                        //일요일 배차 시간

    public Create_Table_ByRoute(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL(DATABASE_CREATE_TEAM);
    }

    public boolean insertData(String bus_node_id, String busstop_nm, String busstop_eng_nm,
                              String route_cd, String busstop_tp, String busstop_seq,
                                String gps_lati, String gps_long) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(BUS_NODE_ID, bus_node_id);
        contentValues.put(BUSSTOP_NM, busstop_nm);
        contentValues.put(BUSSTOP_ENG_NM, busstop_eng_nm);

        contentValues.put(ROUTE_CD, route_cd);
        contentValues.put(BUSSTOP_TP, busstop_tp);
        contentValues.put(BUSSTOP_SEQ, busstop_seq);

        contentValues.put(GPS_LATI, gps_lati);
        contentValues.put(GPS_LONG, gps_long);

        long result = db.insert(TABLE_NAME, null, contentValues);
        if (result == -1)
            return false;
        else
            return true;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        return res;
    }

    public Cursor getLocationData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select DISTINCT " + BUS_NODE_ID + ", " + BUSSTOP_NM + ", " + GPS_LATI + ", " + GPS_LONG  + " from " + TABLE_NAME, null);
        return res;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public Cursor getSearch(String BUS_NODE_ID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select * from " + TABLE_NAME + " where BUS_NODE_ID = " + BUS_NODE_ID;
        Cursor res = db.rawQuery(sql, null);
        return res;
    }

    public Cursor getSearch2(String ROUTE_CD) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select * from " + TABLE_NAME + " where ROUTE_CD = " + ROUTE_CD;
        Cursor res = db.rawQuery(sql, null);
        return res;
    }

    public Cursor SEQ_route_Search(String ROUTE_CD) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "select * from " + TABLE_NAME + " where ROUTE_CD = " + ROUTE_CD + " order by " + BUSSTOP_SEQ;
        Cursor res = db.rawQuery(sql, null);
        return res;
    }
}