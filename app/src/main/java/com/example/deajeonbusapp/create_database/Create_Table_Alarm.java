package com.example.deajeonbusapp.create_database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Create_Table_Alarm extends SQLiteOpenHelper {

    // 데이터베이스
    private static final String DATABASE_NAME      = "BUS.ALARM.db";
    private static final int DATABASE_VERSION      = 1;

    // 테이블
    public static final String TABLE_NAME       = "ALARM";


    public static final String BUS_NODE_ID        = "BUS_NODE_ID";                      //정류소 아이디
    public static final String BUSSTOP_NM        = "BUSSTOP_NM";                      //정류소 이름

    public static final String ROUTE_CD      = "ROUTE_CD";                            //버스 아이디
    public static final String ROUTE_NO      = "ROUTE_NO";                              //버스 네임

    public static final String ALLO_INTERVAL        = "ALLO_INTERVAL";                  //배차시간

    public static final String ALARM_HOUR = "ALARM_HOUR";                               //시
    public static final String ALARM_MIN = "ALARM_MIN";                                 //분


    private static final String DATABASE_CREATE_TEAM = "create table "
            + TABLE_NAME + "(" + BUS_NODE_ID + " Integer, " //정류소 아이디
            + BUSSTOP_NM + " text, "

            + ROUTE_CD + " integer, "
            + ROUTE_NO + " integer, "

            + ALLO_INTERVAL + " integer, "

            + ALARM_HOUR + " integer, "
            + ALARM_MIN + " integer);";

    public Create_Table_Alarm(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL(DATABASE_CREATE_TEAM);
    }

    public void delete(String bus_node_id, String route_cd) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME +" WHERE BUS_NODE_ID like " + bus_node_id +" AND ROUTE_CD like " + route_cd);
    }

    public boolean insertData(String bus_node_id, String busstop_nm, String route_cd, String route_no, String allo_interval, String alarm_hour, String alarm_min) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(BUS_NODE_ID, bus_node_id);
        contentValues.put(BUSSTOP_NM, busstop_nm);

        contentValues.put(ROUTE_CD, route_cd);
        contentValues.put(ROUTE_NO, route_no);

        contentValues.put(ALLO_INTERVAL, allo_interval);

        contentValues.put(ALARM_HOUR, alarm_hour);
        contentValues.put(ALARM_MIN, alarm_min);

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

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}