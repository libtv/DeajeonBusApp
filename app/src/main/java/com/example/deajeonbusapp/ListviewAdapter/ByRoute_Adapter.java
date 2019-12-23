package com.example.deajeonbusapp.ListviewAdapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.deajeonbusapp.ListviewAdapter.DTO.Businfo;
import com.example.deajeonbusapp.ListviewAdapter.DTO.ByRoute;
import com.example.deajeonbusapp.R;
import com.example.deajeonbusapp.create_database.Create_Table_ByRoute;

import java.util.List;


public class ByRoute_Adapter extends BaseAdapter {
    private Context context;
    private List<ByRoute> byroute;

    public ByRoute_Adapter(Context context, List<ByRoute> byroute){
        this.context = context;
        this.byroute = byroute;
    }

    @Override
    public int getCount() {
        return byroute.size();
    }

    //특정한 유저를 반환하는 메소드
    @Override
    public Object getItem(int i) {
        return byroute.get(i);
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
            itemLayout = View.inflate(context, R.layout.list_route, null);
        } else {
            itemLayout = convertView;
        }

        //급행 버스, 마을 버스, 첨단 버스, 외곽 버스 처리
        String BUSSTOP_NM = byroute.get(position).BUSSTOP_NM;

        TextView textView = (TextView) itemLayout.findViewById(R.id.route_busstop_nm);
        textView.setText(BUSSTOP_NM);

        TextView textView1 = (TextView) itemLayout.findViewById(R.id.route_route_no);
            textView1.setText(byroute.get(position).BUSSTOP_ENG_NM);
        return itemLayout;
    }
}