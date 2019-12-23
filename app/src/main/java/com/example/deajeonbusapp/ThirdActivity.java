package com.example.deajeonbusapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.deajeonbusapp.Alarm_pack.New_Alarm;
import com.example.deajeonbusapp.ListviewAdapter.DTO.StationLocation;
import com.example.deajeonbusapp.ListviewAdapter.STATIONLocation_Adapter;
import com.example.deajeonbusapp.create_database.Create_Table_Alarm;
import com.example.deajeonbusapp.create_database.Create_Table_BUSINFO;
import com.example.deajeonbusapp.create_database.Search_Station_Location;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ThirdActivity extends AppCompatActivity {
    String BUS_NODE_ID, BUSSTOP_NM;
    MenuItem mAddAllCheckMenu;
    int Thread_counts = 15;
    boolean Thread_boolean = false;
    /* 즐겨찾기 */
    SharedPreferences settings;
    boolean shared_Flag = false;
    private MenuItem menuToggleAppService;
    @Override
    protected void onResume() {
        super.onResume();
        /* SharedPreferences 설정 */
        settings = getSharedPreferences("Favorite_Station", MODE_PRIVATE);
        if (settings.getString(BUS_NODE_ID, "").equals(BUSSTOP_NM)) {
            shared_Flag = true;
        } else {
            shared_Flag = false;
        }
        setTitle("Deajeon Bus");
        Thread_boolean = true;
        c_list(station_location(BUS_NODE_ID));
    }

    public ArrayList<StationLocation> station_location(String BUS_NODE_ID) {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        Future<ArrayList> futureFb = executor.submit(new Search_Station_Location(BUS_NODE_ID));
        try {
            return futureFb.get();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "인터엣에 오류가 발생하였습니다", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_favorite:
                SharedPreferences.Editor edit = settings.edit();
                if (settings.getString(BUS_NODE_ID, "").equals(BUSSTOP_NM)) {
                    shared_Flag = false;
                    menuToggleAppService.setIcon(R.drawable.checkout_favorite);
                    edit.remove(BUS_NODE_ID);
                    edit.commit();
                } else {
                    shared_Flag = true;
                    menuToggleAppService.setIcon(R.drawable.checked_favorite);
                    edit.putString(BUS_NODE_ID, BUSSTOP_NM);
                    edit.commit();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        TabHost tabHost1 = (TabHost) findViewById(R.id.tabHost1);
        tabHost1.setup();

        Intent intent = getIntent();
        BUS_NODE_ID = intent.getExtras().getString("BUS_NODE_ID");
        BUSSTOP_NM = intent.getExtras().getString("BUSSTOP_NM");

        // 첫 번째 Tab. (탭 표시 텍스트:"TAB 1"), (페이지 뷰:"content1")
        TabHost.TabSpec ts1 = tabHost1.newTabSpec("Tab Spec 1");
        ts1.setContent(R.id.content1);
        ts1.setIndicator(BUSSTOP_NM);
        tabHost1.addTab(ts1);

        /* 툴바 설치 */
        /* 액션 바 */
        ActionBar ab = getSupportActionBar();
        ab.setDisplayUseLogoEnabled(true);
        ab.setDisplayShowHomeEnabled(true);

        //리프레쉬
        final SwipeRefreshLayout mSwipeRefreshLayout;
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout1);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                onResume();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    protected void onDestroy() {
        Thread_boolean = false;
        super.onDestroy();
    }

    void makeThread() {
        new Thread() {
            @Override
            public void run() {
                while(Thread_boolean) { // 음악이 시작되어있으면 100 쉬고 업데이트하고 100ms 쉬고
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Thread_counts--;
                            mAddAllCheckMenu.setTitle("" +Thread_counts);
                            if (Thread_counts <= 0) {
                                onResume();
                                Thread_counts = 15;
                            }
                        }
                    });
                    SystemClock.sleep(1000);
                }
            }
        }.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_third, menu);
        mAddAllCheckMenu = menu.findItem(R.id.reset_list);
        makeThread();

        /* Sharedfreperences 즐겨찾기 */
        menuToggleAppService = menu.findItem(R.id.action_favorite);
        if (shared_Flag == true) menuToggleAppService.setIcon(R.drawable.checked_favorite);
        return super.onCreateOptionsMenu(menu);
    }

    /*
    ////////////////////////////////////////////////////
     알람 매니저로 생성하기
    ////////////////////////////////////////////////////
     */

    public void c_list(final ArrayList<StationLocation> Where_busList) {
        final STATIONLocation_Adapter adapter_first = new STATIONLocation_Adapter(getApplicationContext(), Where_busList);
        ListView listView = findViewById(R.id.list_destination_route);
        listView.setAdapter(adapter_first);


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                /* 변수 설정 */
                AlertDialog.Builder dlg  = new AlertDialog.Builder(ThirdActivity.this);
                View dialogView = (View) View.inflate(ThirdActivity.this, R.layout.dialog_settingalarm, null);
                EditText BUS_NODE_ID_E = (EditText) dialogView.findViewById(R.id.alarm_BUS_NODE_ID);
                final EditText ROUTE_CD = (EditText) dialogView.findViewById(R.id.alarm_ROUTE_CD);
                final EditText ROUTE_NO = (EditText) dialogView.findViewById(R.id.alarm_ROUTE_NO);
                final TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.timePicker);
                final EditText ALLO_INTERVAL = (EditText) dialogView.findViewById(R.id.alarm_bustime);

                /* 배차 시간 찾기 */
                Create_Table_BUSINFO db = new Create_Table_BUSINFO(ThirdActivity.this);
                Cursor res = db.getSearchData(Where_busList.get(position).ROUTE_CD);
                res.moveToNext();
                int ALLO_INTERVAL_ = Integer.parseInt(res.getString(11));

                /* 설정 */
                dlg.setTitle("알림 설정");
                BUS_NODE_ID_E.setText(BUS_NODE_ID);
                ROUTE_CD.setText(Where_busList.get(position).ROUTE_CD);
                ROUTE_NO.setText(Where_busList.get(position).ROUTE_NO);

                ALLO_INTERVAL.setText(String.valueOf(ALLO_INTERVAL_));
                dlg.setView(dialogView);
                dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Create_Table_Alarm db = new Create_Table_Alarm(getApplicationContext());
                        db.insertData(BUS_NODE_ID, BUSSTOP_NM, ROUTE_CD.getText().toString(), ROUTE_NO.getText().toString(), ALLO_INTERVAL.getText().toString(), timePicker.getCurrentHour().toString(), timePicker.getCurrentMinute().toString());
                        New_Alarm new_alarm = new New_Alarm(getApplicationContext());
                        new_alarm.alarm_sender("CREATE", BUS_NODE_ID, ROUTE_CD.getText().toString(), BUSSTOP_NM, Integer.parseInt(ALLO_INTERVAL.getText().toString()),
                                Integer.parseInt(timePicker.getCurrentHour().toString()), Integer.parseInt(timePicker.getCurrentMinute().toString()), 0);
                        Toast.makeText(getApplicationContext(), "설정이 완료되었습니다." , Toast.LENGTH_SHORT).show();
                    }
                });
                dlg.setNegativeButton("취소", null);
                dlg.show();

                return false;
            }
        });
    }
}