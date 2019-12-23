package com.example.deajeonbusapp.Alarm_pack;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.deajeonbusapp.ListviewAdapter.DTO.StationLocation;
import com.example.deajeonbusapp.MainActivity;
import com.example.deajeonbusapp.R;
import com.example.deajeonbusapp.create_database.Search_Station_Location;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class MyService extends Service {
    MediaPlayer mediaPlayer;
    String BUS_NODE_ID, ROUTE_CD, BUSSTOP_NM;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    void init() {
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "BusApplication";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "BusApplication",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("").build();
            startForeground(Integer.parseInt(BUS_NODE_ID), notification);
        } else  {
            startForeground(Integer.parseInt(BUS_NODE_ID), new Notification()); // 추가 (9.0에서는 변경해야 함)
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        BUS_NODE_ID = intent.getExtras().getString("BUS_NODE_ID");
        ROUTE_CD = intent.getExtras().getString("ROUTE_CD");
        BUSSTOP_NM = intent.getExtras().getString("BUSSTOP_NM");
        init();
        ExecutorService threadPool = Executors.newFixedThreadPool(10);
        Alarm_AnsyncTask alarm_Task = new Alarm_AnsyncTask(BUS_NODE_ID, ROUTE_CD, BUSSTOP_NM, getApplicationContext());
        alarm_Task.executeOnExecutor(threadPool, 10);
        return START_STICKY;
    }

    //서비스가 종료될 때 할 작업
    public void onDestroy() {
        stopForeground(true);
        super.onDestroy();
    }
}