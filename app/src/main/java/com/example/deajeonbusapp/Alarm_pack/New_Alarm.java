package com.example.deajeonbusapp.Alarm_pack;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.Calendar;

public class New_Alarm {
    Context context;
    public static AlarmManager mAlarmMgr = null;
    public static PendingIntent mAlarmIntent = null;

    public New_Alarm(Context context) {
        this.context = context;
    }

    public void alarm_sender(String STATE, String BUS_NODE_ID, String ROUTE_CD, String BUSSTOP_NM, int ALLO_INTERVAL, int ALARM_HOUR, int ALARM_MIN, int RESTART) {
        Intent intent;
        long minus = ALLO_INTERVAL * 60 * 1000;

        Calendar restart = Calendar.getInstance();

        restart.set(Calendar.HOUR_OF_DAY, ALARM_HOUR);
        restart.set(Calendar.MINUTE, ALARM_MIN);
        restart.set(Calendar.SECOND, 0);

        intent = new Intent(context, RestartService.class);
        intent.setAction(BUS_NODE_ID);

        if (RESTART == 1) {
            restart.add(Calendar.DATE, 1);

            //한번 더 확인해서...
            if (restart.before(Calendar.getInstance())) {
                restart.add(Calendar.DATE, 1);
            }
        }

        intent.putExtra("STATE", STATE);
        intent.putExtra("BUS_NODE_ID", BUS_NODE_ID);
        intent.putExtra("ROUTE_CD", ROUTE_CD);
        intent.putExtra("BUSSTOP_NM", BUSSTOP_NM);
        intent.putExtra("ALLO_INTERVAL", ALLO_INTERVAL);
        intent.putExtra("ALARM_HOUR", ALARM_HOUR);
        intent.putExtra("ALARM_MIN", ALARM_MIN);

        mAlarmIntent = PendingIntent.getBroadcast(context, Integer.parseInt(BUS_NODE_ID), intent, 0);
        mAlarmMgr = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);

        // 알람 매니저, 인텐트등 설정 후
        if (Build.VERSION.SDK_INT >= 23) {
            mAlarmMgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, restart.getTimeInMillis() - minus, mAlarmIntent);
        } else if (Build.VERSION.SDK_INT >= 19){
            mAlarmMgr.setExact(AlarmManager.RTC_WAKEUP, restart.getTimeInMillis() - minus, mAlarmIntent);
        } else {
            mAlarmMgr.set(AlarmManager.RTC_WAKEUP, restart.getTimeInMillis() - minus, mAlarmIntent);
        }
    }

    public void cancel_Alarm(String BUS_NODE_ID) {
        mAlarmMgr = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        Intent intent = new Intent(context, RestartService.class);
        intent.setAction(BUS_NODE_ID);
        mAlarmIntent = PendingIntent.getBroadcast(context, Integer.parseInt(BUS_NODE_ID), intent, 0);
        mAlarmMgr.cancel(mAlarmIntent);
        mAlarmIntent.cancel();
        mAlarmMgr = null;
        mAlarmIntent = null;
    }
}
