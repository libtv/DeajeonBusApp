package com.example.deajeonbusapp.Alarm_pack;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
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

public class Alarm_AnsyncTask extends AsyncTask<Integer , Integer , Integer> {
    String BUS_NODE_ID;
    String ROUTE_CD;
    String BUSSTOP_NM;
    Context context;
    boolean isCancel = false;

    public Alarm_AnsyncTask(String BUS_NODE_ID, String ROUTE_CD, String BUSSTOP_NM, Context context) {
        this.BUS_NODE_ID = BUS_NODE_ID;
        this.ROUTE_CD = ROUTE_CD;
        this. BUSSTOP_NM = BUSSTOP_NM;
        this.context = context;
    }

    protected void onPreExecute() {
    }

    public void NotificationSomethings(String ROUTE_NO, String STATUS_POS, String CAR_REG_NO, String EXTIME_MIN, String EXTIME_SEC) {
        final String NOTIFICATION_CHANNEL_ID = "10001";
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent,  PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.bus_icon)) //BitMap 이미지 요구
                .setContentTitle("대전 버스 어플리케이션")
                .setContentText(ROUTE_NO + " 버스가 " + EXTIME_MIN  + "분 뒤 "
                        + BUSSTOP_NM + " 정류장에 입장합니다.")
                // 더 많은 내용이라서 일부만 보여줘야 하는 경우 아래 주석을 제거하면 setContentText에 있는 문자열 대신 아래 문자열을 보여줌
                .setStyle(new NotificationCompat.BigTextStyle().bigText(ROUTE_NO + " 버스가 " + EXTIME_MIN  + "분 뒤 " + BUSSTOP_NM + " 정류장에 입장합니다. \n\n총 잔여 정류장 수 : "
                        +  STATUS_POS + " \n차량 번호 : " + CAR_REG_NO + "  \n예상 도착 시간 : " + new SimpleDateFormat( " HH:mm:ss").format(System.currentTimeMillis() + Integer.parseInt(EXTIME_SEC)*1000)
                        + " "))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent) // 사용자가 노티피케이션을 탭시 ResultActivity로 이동하도록 설정
                .setAutoCancel(true);

        //OREO API 26 이상에서는 채널 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            builder.setSmallIcon(R.drawable.bus_icon); //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남
            CharSequence channelName  = "노티페케이션 채널";
            String description = "오레오 이상을 위한 것임";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName , importance);
            channel.setDescription(description);

            // 노티피케이션 채널을 시스템에 등록
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);

        } else builder.setSmallIcon(R.drawable.bus_icon); // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남

        assert notificationManager != null;
        notificationManager.notify(1234, builder.build()); // 고유숫자로 노티피케이션 동작시킴
    }

    protected Integer doInBackground(Integer... values) {
        while (isCancel == false) {
            ExecutorService executor = Executors.newFixedThreadPool(1);
            Future<ArrayList> futureFb = executor.submit(new Search_Station_Location(BUS_NODE_ID));
            ArrayList<StationLocation> list;
            Log.e("kimjunho", "okay1");
            try {
                list = futureFb.get();
                for(StationLocation a : list) {
                    if (a.ROUTE_CD.equals(ROUTE_CD) && Integer.parseInt(a.EXTIME_MIN) <= 5 && a.MSG_TP.equals("03")) {
                        NotificationSomethings(a.ROUTE_NO, a.STATUS_POS, a.CAR_REG_NO, a.EXTIME_MIN, a.EXTIME_SEC);
                        isCancel = true;
                        break;
                    }
                }
            } catch (Exception e) {
                Log.e("error:: ", e.getMessage());
            }
            Log.e("kimjunho", "okay2");
            if (isCancel == true) {
                break;
            }
            /* 1분씩 쉬는 중  데이터가 없어요.. */
            try {
                Thread.sleep(60000);
            } catch (InterruptedException ex) {

            }
        }
        return 0;
    }

    protected void onProgressUpdate(Integer... values) {
    }
    protected void onPostExecute(Integer result) {
        SharedPreferences settings = context.getSharedPreferences("ALARM_SONG", Context.MODE_PRIVATE);
        boolean alarm_Song = settings.getBoolean("ALARM_SONG_ON", false);

        if (alarm_Song) {
            MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.bts_fire);
            mediaPlayer.start();
        }
    }

    //Task가 취소되었을때 호출
    protected void onCancelled() {
    }
}