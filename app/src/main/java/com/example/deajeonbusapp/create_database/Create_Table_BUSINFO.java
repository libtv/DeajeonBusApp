package com.example.deajeonbusapp.create_database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Create_Table_BUSINFO extends SQLiteOpenHelper {

    // 데이터베이스
    private static final String DATABASE_NAME      = "BUS.db";
    private static final int DATABASE_VERSION      = 1;

    // 테이블
    public static final String TABLE_NAME       = "BUSROUTEINFO";

    public static final String ROUTE_CD        = "ROUTE_CD";       //버스 아이디                 0
    public static final String ROUTE_NO      = "ROUTE_NO";       //버스 네임                     1
    public static final String ROUTE_TP      = "ROUTE_TP";       //노선 유형                     2

    public static final String BUSSTOP_CNT     = "BUSSTOP_CNT";    //버스 정류장 수              3
    public static final String RUN_DIST_HALF      = "RUN_DIST_HALF";  //반환 거리                4
    public static final String RUN_TM      = "RUN_TM";         //평균 운행시간                   5

    public static final String START_NODE_ID      = "START_NODE_ID";  //기점 정류소 아이디       6
    public static final String END_NODE_ID      = "END_NODE_ID";    //종점 정류소 아이디         7

    public static final String ORIGIN_START      = "ORIGIN_START";   //첫차 시간                 8
    public static final String ORIGIN_START_SAT      = "ORIGIN_START_SAT";//주말 첫차 시간       9
    public static final String ORIGIN_START_SUN      = "ORIGIN_START_SUN";//일요일 첫차 시간     10

    public static final String ALLO_INTERVAL      = "ALLO_INTERVAL";  //평일 배차 시간           11
    public static final String ALLO_INTERVAL_SAT      = "ALLO_INTERVAL_SAT";//주말 배차 시간     12
    public static final String ALLO_INTERVAL_SUN      = "ALLO_INTERVAL_SUN";//일요일 배차 시간   13


    private static final String DATABASE_CREATE_TEAM = "create table "
            + TABLE_NAME + "(" + ROUTE_CD + " Integer, " //버스 아이디
            + ROUTE_NO + " Integer , "                                              //버스 네임
            + ROUTE_TP + " Integer, "                                              //노선 유형

            + BUSSTOP_CNT + " Integer, "                                           //버스 정류장 수
            + RUN_DIST_HALF + " text, "                                           //반환 거리
            + RUN_TM + " Integer, "                                           //평균 운행시간

            + START_NODE_ID + " Integer, "                                           //기점 정류소 아이디
            + END_NODE_ID + " Integer, "                                           //종점 정류소 아이디

            + ORIGIN_START + " text, "                                          //첫차 시간
            + ORIGIN_START_SAT + " text, "                                           //주말 첫차 시간
            + ORIGIN_START_SUN + " text, "                                           //일요일 첫차 시간

            + ALLO_INTERVAL + " text, "                                           //평일 배차 시간
            + ALLO_INTERVAL_SAT + " text, "                                       //주말 배차 시간
            + ALLO_INTERVAL_SUN + " text);";                                        //일요일 배차 시간

    public Create_Table_BUSINFO(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL(DATABASE_CREATE_TEAM);
    }

    public boolean insertData(String route_cd, String route_no, String route_tp
                                , String busstop_cnt, String run_dist_half, String run_tm, String start_node_id, String end_node_id
                                , String origin_start, String origin_start_sat, String origin_start_sun
                                , String allo_interval, String allo_interval_sat, String allo_interval_sun) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ROUTE_CD, route_cd);
        contentValues.put(ROUTE_NO, route_no);
        contentValues.put(ROUTE_TP, route_tp);

        contentValues.put(BUSSTOP_CNT, busstop_cnt);
        contentValues.put(RUN_DIST_HALF, run_dist_half);

        contentValues.put(RUN_TM, run_tm);
        contentValues.put(START_NODE_ID, start_node_id);
        contentValues.put(END_NODE_ID, end_node_id);

        contentValues.put(ORIGIN_START, origin_start);
        contentValues.put(ORIGIN_START_SAT, origin_start_sat);
        contentValues.put(ORIGIN_START_SUN, origin_start_sun);

        contentValues.put(ALLO_INTERVAL, allo_interval);
        contentValues.put(ALLO_INTERVAL_SAT, allo_interval_sat);
        contentValues.put(ALLO_INTERVAL_SUN, allo_interval_sun);

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

    public Cursor getSearchData(String ROUTE_CD) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "select * from " + TABLE_NAME + " where ROUTE_CD = " + ROUTE_CD;
        Cursor res = db.rawQuery(sql, null);
        return res;
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}