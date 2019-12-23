package com.example.deajeonbusapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.deajeonbusapp.First_settings.GPS_AnsyncTask;
import com.example.deajeonbusapp.bottom_nav.frag_favorite;
import com.example.deajeonbusapp.bottom_nav.frag_home;
import com.example.deajeonbusapp.bottom_nav.frag_setting;
import com.example.deajeonbusapp.create_database.Create_DatabaseTask;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    /* 바텀 네비게이션 */
    private FragmentManager fragmentManager = getSupportFragmentManager();
    BottomNavigationView bottomNavigationView;
    FragmentTransaction transaction;
    private frag_home frag_home = new frag_home();
    private frag_favorite frag_favorite = new frag_favorite();
    private frag_setting frag_setting = new frag_setting();
    /* 앱 처음 실행시 데이터베이스 생성 */
    private SharedPreferences create_database;
    /* 앱 처음 실행시 데이터베이스 생성 */
    private SharedPreferences create_gps;

    public void check_DATABASE(){
        boolean isFirstRun = create_database.getBoolean("isFirstRun",true);
        if(isFirstRun) {
            Create_DatabaseTask mAsyncTask = new Create_DatabaseTask(this, create_database);
            mAsyncTask.execute();
        }
        /*
        boolean isFirstRun2 = create_gps.getBoolean("isFirstRun", true);
        if(isFirstRun2) {
            GPS_AnsyncTask m2AsyncTask = new GPS_AnsyncTask(this);
            m2AsyncTask.execute();
        }
        */
    }


    @Override
    protected void onResume() {
        create_database = getSharedPreferences("CREATE_DATABASE", MODE_PRIVATE);
        create_gps = getSharedPreferences("CREATE_GPS_SETTING", MODE_PRIVATE);
        check_DATABASE();
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* 바텀 네비게이션 */
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout, frag_home).commitAllowingStateLoss();
        bottomNavigationView = findViewById(R.id.navigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new ItemSelectedListener());

        try {
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setCustomView(R.layout.actionbar_main);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /* 바텀 네비게이션 */
    class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener{
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            transaction = fragmentManager.beginTransaction();

            switch(menuItem.getItemId())
            {
                case R.id.nav_home:
                    transaction.replace(R.id.frameLayout, frag_home).commitAllowingStateLoss();
                    break;
                case R.id.nav_busnode:
                    transaction.replace(R.id.frameLayout, frag_favorite).commitAllowingStateLoss();
                    break;
                case R.id.nav_setting:
                    transaction.replace(R.id.frameLayout, frag_setting).commitAllowingStateLoss();
                    break;
            }
            return true;
        }
    }
}