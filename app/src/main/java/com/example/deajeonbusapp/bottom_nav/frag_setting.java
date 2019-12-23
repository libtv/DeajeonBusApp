package com.example.deajeonbusapp.bottom_nav;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.deajeonbusapp.Alarm_pack.New_Alarm;
import com.example.deajeonbusapp.ListviewAdapter.Alarm_Setting_Adapter;
import com.example.deajeonbusapp.ListviewAdapter.DTO.ALARM_Bus;
import com.example.deajeonbusapp.R;
import com.example.deajeonbusapp.SecondActivity;
import com.example.deajeonbusapp.create_database.Create_Table_Alarm;

import java.util.ArrayList;

public class frag_setting extends Fragment {
    ListView listView;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View vh =  inflater.inflate(R.layout.fragment_setting, container, false);
        listView = vh.findViewById(R.id.setting_alarm_list);
        TabHost tabHost1 = (TabHost) vh.findViewById(R.id.tabHost1) ;
        tabHost1.setup() ;

        // 첫 번째 Tab. (탭 표시 텍스트:"TAB 1"), (페이지 뷰:"content1")
        TabHost.TabSpec ts1 = tabHost1.newTabSpec("Tab Spec 1") ;
        ts1.setContent(R.id.content1) ;
        ts1.setIndicator("SETTING");
        tabHost1.addTab(ts1);

        // 두 번째 Tab. (탭 표시 텍스트:"TAB 2"), (페이지 뷰:"content2")
        TabHost.TabSpec ts2 = tabHost1.newTabSpec("Tab Spec 2") ;
        ts2.setContent(R.id.content2) ;
        ts2.setIndicator("ALARM") ;
        tabHost1.addTab(ts2);

        alarm_song_check_event(vh);
        alarm_notice(vh);
        return vh;
    }
    public void alarm_notice(View v) {
        TextView alarm = (TextView) v.findViewById(R.id.alarm_notice);
        alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(getContext());
                dlg.setTitle("공지 사항");

                dlg.setPositiveButton("OK", null);
                dlg.setMessage("알람은 정류장 별로 각 한개씩 생성 할 수 있습니다." +
                        "\n\nAlarm Song ON/OFF 를 끈 경우 \n알람송은 울리지 않지만 알림은 가게 됩니다." +
                        "\n\nGPS는 현재 위치를 빨간색 마커로 설정하고 \n기본값으로는 10개의 가까운 정류장 표시합니다.");
                dlg.show();
            }
        });
    }

    public void alarm_song_check_event(View v) {
        CheckBox alarm = (CheckBox) v.findViewById(R.id.alarm_song_on_off);
        SharedPreferences settings = getActivity().getSharedPreferences("ALARM_SONG", Context.MODE_PRIVATE);
        boolean alarm_Song = settings.getBoolean("ALARM_SONG_ON", false);
        alarm.setChecked(alarm_Song);
        alarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences settings = getActivity().getSharedPreferences("ALARM_SONG", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("ALARM_SONG_ON", isChecked);
                editor.commit();
            }
        });
    }

    @Override
    public void onResume() {
        c_alarm_list();
        super.onResume();
    }

    public void c_alarm_list() {
        final ArrayList<ALARM_Bus> list = new ArrayList<>();
        final Create_Table_Alarm db = new Create_Table_Alarm(getContext());
        Cursor res = db.getAllData();

        if (res.getCount() == 0) {
            return;
        }

        while(res.moveToNext()) {
            list.add(new ALARM_Bus(res.getString(0), res.getString(1),
                    res.getString(2), res.getString(3),
                    res.getString(4), res.getString(5), res.getString(6)));
        }
        Alarm_Setting_Adapter adapter = new Alarm_Setting_Adapter(getContext(), list);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                New_Alarm alarm = new New_Alarm(getContext());
                alarm.cancel_Alarm(list.get(position).BUS_NODE_ID);
                Toast.makeText(getContext(), "알람이 취소되었습니다.", Toast.LENGTH_SHORT).show();
                db.delete(list.get(position).BUS_NODE_ID, list.get(position).ROUTE_CD);
                onResume();
            }
        });
    }

}
