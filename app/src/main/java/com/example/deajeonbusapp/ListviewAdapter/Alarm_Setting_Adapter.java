package com.example.deajeonbusapp.ListviewAdapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.deajeonbusapp.ListviewAdapter.DTO.ALARM_Bus;
import com.example.deajeonbusapp.ListviewAdapter.DTO.Businfo;
import com.example.deajeonbusapp.R;
import com.example.deajeonbusapp.create_database.Create_Table_ByRoute;

import java.util.List;


public class Alarm_Setting_Adapter extends BaseAdapter {
    private Context context;
    private List<ALARM_Bus> alarm_list;

    public Alarm_Setting_Adapter(Context context, List<ALARM_Bus> alarm_list){
        this.context = context;
        this.alarm_list = alarm_list;
    }

    @Override
    public int getCount() {
        return alarm_list.size();
    }

    //특정한 유저를 반환하는 메소드
    @Override
    public Object getItem(int i) {
        return alarm_list.get(i);
    }

    //아이템별 아이디를 반환하는 메소드
    @Override
    public long getItemId(int i) {
        return i;
    }

    //가장 중요한 부분
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemLayout;
        if (convertView == null) {
            itemLayout = View.inflate(context, R.layout.list_alarmsetting, null);
        } else {
            itemLayout = convertView;
        }
        TextView tx1 = itemLayout.findViewById(R.id.alarm_list_ROUTE_NO);
        tx1.setText(alarm_list.get(position).ROUTE_NO + "번");

        TextView tx2 = itemLayout.findViewById(R.id.alarm_list_ALLO_INTERVAL);
        tx2.setText("배차시간 : " + alarm_list.get(position).ALLO_INTERVAL + "분");

        TextView tx3 = itemLayout.findViewById(R.id.alarm_list_BUSSTOP_NM);
        tx3.setText("Station : " + alarm_list.get(position).BUSSTOP_NM);

        TextView tx4 = itemLayout.findViewById(R.id.alarm_list_TIMPICKER);
        tx4.setText(alarm_list.get(position).ALARM_HOUR + ":" + alarm_list.get(position).ALARM_MIN);
        return itemLayout;
    }
}