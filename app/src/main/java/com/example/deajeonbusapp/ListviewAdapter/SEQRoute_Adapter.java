package com.example.deajeonbusapp.ListviewAdapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.deajeonbusapp.ListviewAdapter.DTO.ByRoute;
import com.example.deajeonbusapp.R;

import org.w3c.dom.Text;

import java.util.List;


public class SEQRoute_Adapter extends BaseAdapter {
    private Context context;
    private List<ByRoute> byroute;

    public SEQRoute_Adapter(Context context, List<ByRoute> byroute){
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
            itemLayout = View.inflate(context, R.layout.list_seqroute, null);
        } else {
            itemLayout = convertView;
        }
        ImageView imageView = (ImageView) itemLayout.findViewById(R.id.imageView);
        TextView textview2 = (TextView) itemLayout.findViewById(R.id.car_name);
        imageView.setImageResource(0);
        textview2.setText("");

        if (byroute.get(position).BUS_Location != null) {
            imageView.setImageResource(R.drawable.bus_where_icon);
        }

        if (byroute.get(position).BUS_Location_Car_name != null) {
            textview2.setText(byroute.get(position).BUS_Location_Car_name);
        }

        TextView textView = (TextView) itemLayout.findViewById(R.id.route_name);
        textView.setText(byroute.get(position).BUSSTOP_NM);
        return itemLayout;
    }
}