package com.example.deajeonbusapp.ListviewAdapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.deajeonbusapp.ListviewAdapter.DTO.Businfo;
import com.example.deajeonbusapp.R;
import com.example.deajeonbusapp.create_database.Create_Table_ByRoute;

import java.util.List;


public class Businfo_Adapter extends BaseAdapter {
    private Context context;
    private List<Businfo> businfo;

    public Businfo_Adapter(Context context, List<Businfo> businfoList){
        this.context = context;
        this.businfo = businfoList;
    }

    @Override
    public int getCount() {
        return businfo.size();
    }

    //특정한 유저를 반환하는 메소드
    @Override
    public Object getItem(int i) {
        return businfo.get(i);
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
            itemLayout = View.inflate(context, R.layout.list_businfo, null);
        } else {
            itemLayout = convertView;
        }

        //급행 버스, 마을 버스, 첨단 버스, 외곽 버스 처리
        String ROUTE_NO = businfo.get(position).ROUTE_NO;
        if (businfo.get(position).ROUTE_TP.equals("1")) {
            ROUTE_NO = "급행" + ROUTE_NO;
        } else if (businfo.get(position).ROUTE_TP.equals("5")) {
            ROUTE_NO = "마을" + ROUTE_NO;
        } else if (businfo.get(position).ROUTE_TP.equals("6")) {
            ROUTE_NO = "첨단" + ROUTE_NO;
        }

        TextView textView = (TextView) itemLayout.findViewById(R.id.businfo_ROUTE_NO);
        textView.setText(ROUTE_NO);

        TextView textView1 = (TextView) itemLayout.findViewById(R.id.businfo_route_string);
        Create_Table_ByRoute byRoute = new Create_Table_ByRoute(context);
        Cursor first = byRoute.getSearch(businfo.get(position).START_NODE_ID);
        Cursor last = byRoute.getSearch(businfo.get(position).END_NODE_ID);

        if (first.getCount() == 0 || last.getCount() == 0){
            textView1.setText(businfo.get(position).START_NODE_ID + "↔" + businfo.get(position).END_NODE_ID);
        } else {
            first.moveToNext();
            last.moveToNext();
            textView1.setText(first.getString(1) + "↔" + last.getString(1));
        }
        return itemLayout;
    }
}