package com.example.deajeonbusapp.Alarm_pack;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

public class RestartService extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        /* 받는거 처리하기 위해서 일단 여기서부터 시작 */
        String createORdelete = intent.getExtras().getString("STATE");
        final String BUS_NODE_ID = intent.getExtras().getString("BUS_NODE_ID");
        final String ROUTE_CD = intent.getExtras().getString("ROUTE_CD");
        final String BUSSTOP_NM = intent.getExtras().getString("BUSSTOP_NM");

        final int ALLO_INTERVAL = intent.getExtras().getInt("ALLO_INTERVAL");
        final int ALARM_HOUR = intent.getExtras().getInt("ALARM_HOUR");
        final int ALARM_MIN = intent.getExtras().getInt("ALARM_MIN");

        if (createORdelete.equals("CREATE")) {
            //생성하는거면
            Intent service_intent = new Intent(context, MyService.class);
            service_intent.putExtra("BUS_NODE_ID", BUS_NODE_ID);
            service_intent.putExtra("ROUTE_CD", ROUTE_CD);
            service_intent.putExtra("BUSSTOP_NM", BUSSTOP_NM);

            //인텐트 보내기
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(service_intent);
            } else {
                context.startService(service_intent);
            }

            new Handler().postDelayed(new Runnable() {
                @Override public void run() { // 실행할 동작 코딩
                    New_Alarm alarm = new New_Alarm(context);
                    alarm.alarm_sender("CREATE", BUS_NODE_ID, ROUTE_CD, BUSSTOP_NM, ALLO_INTERVAL, ALARM_HOUR, ALARM_MIN, 1);
                }}, 100000); // 이거좀 높이고
        }
    }
}