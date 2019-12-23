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


public class Favorite_station_Adapter extends BaseAdapter {
    private Context context;
    private List<ByRoute> favorite_list;

    public Favorite_station_Adapter(Context context, List<ByRoute> favorite_list){
        this.context = context;
        this.favorite_list = favorite_list;
    }

    @Override
    public int getCount() {
        return favorite_list.size();
    }

    //특정한 유저를 반환하는 메소드
    @Override
    public Object getItem(int i) {
        return favorite_list.get(i);
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
            itemLayout = View.inflate(context, R.layout.list_favorite_station, null);
        } else {
            itemLayout = convertView;
        }
        TextView tx1 = (TextView) itemLayout.findViewById(R.id.favorite_BUSSTOP_NM);
        tx1.setText(favorite_list.get(position).BUSSTOP_NM);
        TextView tx2 = (TextView) itemLayout.findViewById(R.id.favorite_BUSSTOP__NM);
        tx2.setText(favorite_list.get(position).BUSSTOP_NM);
        TextView tx3 = (TextView) itemLayout.findViewById(R.id.favorite_BUSSTOP_ENG_NM);
        tx3.setText(favorite_list.get(position).BUSSTOP_ENG_NM);

        return itemLayout;
    }
}