package com.example.deajeonbusapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.deajeonbusapp.ListviewAdapter.DTO.BusLocation;
import com.example.deajeonbusapp.ListviewAdapter.DTO.ByRoute;
import com.example.deajeonbusapp.ListviewAdapter.SEQRoute_Adapter;
import com.example.deajeonbusapp.create_database.Create_Table_BUSINFO;
import com.example.deajeonbusapp.create_database.Create_Table_ByRoute;
import com.example.deajeonbusapp.create_database.Search_BUS_Location;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SecondActivity extends AppCompatActivity {
    String ROUTE_CD, ROUTE_NO, First_Tab, Last_Tab; //
    MenuItem mAddAllCheckMenu; //
    int Thread_counts = 15;
    boolean Thread_boolean = false;
    /* 즐겨찾기 */
    SharedPreferences settings;
    boolean shared_Flag = false;
    private MenuItem menuToggleAppService;
    @Override
    protected void onResume() {
        setTitle(ROUTE_NO + " Bus");
        Thread_boolean = true;
        /* SharedPreferences 설정 */
        settings = getSharedPreferences("Favorite_Bus", MODE_PRIVATE);
        if (settings.getString(ROUTE_CD, "").equals(ROUTE_NO)) {
            shared_Flag = true;
        } else {
            shared_Flag = false;
        }
        super.onResume();
    }


    public ArrayList<BusLocation> where_bus(String ROUTE_CD) {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        Future<ArrayList> futureFb = executor.submit(new Search_BUS_Location(ROUTE_CD));
        try {
            return futureFb.get();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "인터엣에 오류가 발생하였습니다", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    public void c_list(String ROUTE_CD, ArrayList<BusLocation> Where_busList) {
        final ArrayList<ByRoute> list_first = new ArrayList<>();
        final ArrayList<ByRoute> list_last = new ArrayList<>();
        boolean Last_Destnation = false;

        Create_Table_ByRoute db = new Create_Table_ByRoute(this);
        Cursor res = db.SEQ_route_Search(ROUTE_CD);

        if (res.getCount() == 0) {
            Toast.makeText(getApplicationContext(), "데이터베이스가 정상적이지 않습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        while(res.moveToNext()) {
            ByRoute b = new ByRoute(res.getString(0), res.getString(1), res.getString(2), res.getString(3),
                    res.getString(4), res.getString(5), res.getString(6), res.getString(7));
            if (res.getString(4).equals("2")){
                Last_Destnation = true;
                list_first.add(b);
            }
            if (Last_Destnation == true) {
                list_last.add(b);
            } else {
                list_first.add(b);
            }

            for(int i=0; i<Where_busList.size(); i++) {
                if (b.BUS_NODE_ID.equals(Where_busList.get(i).BUS_NODE_ID)) {
                    b.BUS_Location = "yes";
                    b.BUS_Location_Car_name = Where_busList.get(i).PLATE_NO;
                    break;
                }
            }
        }

        First_Tab = list_last.get(0).BUSSTOP_NM;
        Last_Tab = list_first.get(0).BUSSTOP_NM;

        final SEQRoute_Adapter adapter_first = new SEQRoute_Adapter(getApplicationContext(), list_first);
        final SEQRoute_Adapter adapter_last = new SEQRoute_Adapter(getApplicationContext(), list_last);
        ListView listView_first = findViewById(R.id.bus_list_first);
        ListView listView_last = findViewById(R.id.bus_list_last);
        listView_first.setAdapter(adapter_first);
        listView_last.setAdapter(adapter_last);
        /* 리스트뷰 클릭 리스너 */
        listView_first.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ThirdActivity.class);
                intent.putExtra("BUS_NODE_ID", list_first.get(position).BUS_NODE_ID);
                intent.putExtra("BUSSTOP_NM", list_first.get(position).BUSSTOP_NM);
                startActivity(intent);
            }
        });
        listView_last.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ThirdActivity.class);
                intent.putExtra("BUS_NODE_ID", list_last.get(position).BUS_NODE_ID);
                intent.putExtra("BUSSTOP_NM", list_last.get(position).BUSSTOP_NM);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Intent intent = getIntent();
        ROUTE_CD= intent.getExtras().getString("ROUTE_CD");
        ROUTE_NO = intent.getExtras().getString("ROUTE_NO");
        c_list(ROUTE_CD, where_bus(ROUTE_CD));

        TabHost tabHost1 = (TabHost) findViewById(R.id.tabHost1) ;
        tabHost1.setup() ;
        // 첫 번째 Tab. (탭 표시 텍스트:"TAB 1"), (페이지 뷰:"content1")
        TabHost.TabSpec ts1 = tabHost1.newTabSpec("Tab Spec 1");
        ts1.setContent(R.id.content1) ;
        ts1.setIndicator( First_Tab + " 방향") ;
        tabHost1.addTab(ts1)  ;

        // 두 번째 Tab. (탭 표시 텍스트:"TAB 2"), (페이지 뷰:"content2")
        TabHost.TabSpec ts2 = tabHost1.newTabSpec("Tab Spec 2");
        ts2.setContent(R.id.content2) ;
        ts2.setIndicator( Last_Tab + " 방향") ;
        tabHost1.addTab(ts2) ;

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
        final SwipeRefreshLayout mSwipeRefreshLayout2;
        mSwipeRefreshLayout2 = (SwipeRefreshLayout) findViewById(R.id.swipe_layout2);
        mSwipeRefreshLayout2.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                onResume();
                mSwipeRefreshLayout2.setRefreshing(false);
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_info:
                /* 정보 받기 */
                Create_Table_BUSINFO db = new Create_Table_BUSINFO(getApplicationContext());
                Cursor res = db.getSearchData(ROUTE_CD);
                if (res.getCount() == 0) {
                    Toast.makeText(getApplicationContext(), "데이터베이스에 오류가 발생하였습니다", Toast.LENGTH_SHORT).show();
                    return false;
                }
                /* 정보 받기 */
                AlertDialog.Builder dlg = new AlertDialog.Builder(this);
                View dialogView = (View) View.inflate(this, R.layout.dialog_businfo, null); //DialogView
                dlg.setTitle("노선정보");
                /* 버스 정보 받아와서 출력하기 ..*/
                TextView allo_interval = (TextView) dialogView.findViewById(R.id.textView7);
                TextView allo_interval_sat = (TextView) dialogView.findViewById(R.id.textView8);
                TextView allo_interval_sun = (TextView) dialogView.findViewById(R.id.textView9);
                TextView ORIGIN_START = (TextView) dialogView.findViewById(R.id.textView18);
                TextView ORIGIN_START_SAT = (TextView) dialogView.findViewById(R.id.textView19);
                TextView ORIGIN_START_SUN = (TextView) dialogView.findViewById(R.id.textView20);
                TextView ORIGIN_END = (TextView) dialogView.findViewById(R.id.textView30);
                TextView ORIGIN_END_SAT = (TextView) dialogView.findViewById(R.id.textView31);
                TextView ORIGIN_END_SUN = (TextView) dialogView.findViewById(R.id.textView32);
                TextView BUSSTOP_CNT = (TextView) dialogView.findViewById(R.id.textView42);
                TextView RUN_TM = (TextView) dialogView.findViewById(R.id.textView43);

                res.moveToNext();
                allo_interval.setText(res.getString(11));
                allo_interval_sat.setText(res.getString(12));
                allo_interval_sun.setText(res.getString(13));
                ORIGIN_START.setText(res.getString(8));
                ORIGIN_START_SAT.setText(res.getString(9));
                ORIGIN_START_SUN.setText(res.getString(10));
                ORIGIN_END.setText("22:30");
                ORIGIN_END_SAT.setText("22:30");
                ORIGIN_END_SUN.setText("22:30");
                BUSSTOP_CNT.setText(res.getString(3) + " 개");
                RUN_TM.setText(res.getString(5) + " 분");
                /* !-- 버스 정보 받아와서 출력하기 ..-- !*/
                dlg.setView(dialogView);
                dlg.show();
                break;
            case R.id.action_favorite:
                SharedPreferences.Editor edit = settings.edit();
                if (settings.getString(ROUTE_CD, "").equals(ROUTE_NO)) {
                    shared_Flag = false;
                    menuToggleAppService.setIcon(R.drawable.checkout_favorite);
                    edit.remove(ROUTE_CD);
                    edit.commit();
                } else {
                    shared_Flag = true;
                    menuToggleAppService.setIcon(R.drawable.checked_favorite);
                    edit.putString(ROUTE_CD, ROUTE_NO);
                    edit.commit();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_second, menu);
        mAddAllCheckMenu = menu.findItem(R.id.reset_list);
        makeThread();

        /* Sharedfreperences 즐겨찾기 */
        menuToggleAppService = menu.findItem(R.id.action_favorite);
        if (shared_Flag == true) menuToggleAppService.setIcon(R.drawable.checked_favorite);
        return super.onCreateOptionsMenu(menu);
    }
}