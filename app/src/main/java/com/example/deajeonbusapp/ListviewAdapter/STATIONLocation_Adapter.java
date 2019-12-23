package com.example.deajeonbusapp.ListviewAdapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.deajeonbusapp.ListviewAdapter.DTO.ByRoute;
import com.example.deajeonbusapp.ListviewAdapter.DTO.StationLocation;
import com.example.deajeonbusapp.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class STATIONLocation_Adapter extends BaseAdapter {
    private Context context;
    private List<StationLocation> stationLocation;

    public STATIONLocation_Adapter(Context context, List<StationLocation> stationLocation){
        this.context = context;
        this.stationLocation = stationLocation;
    }

    @Override
    public int getCount() {
        return stationLocation.size();
    }

    //특정한 유저를 반환하는 메소드
    @Override
    public Object getItem(int i) {
        return stationLocation.get(i);
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
            itemLayout = View.inflate(context, R.layout.list_stationlocation, null);
        } else {
            itemLayout = convertView;
        }

        CircleImageView imageView = (CircleImageView) itemLayout.findViewById(R.id.ImageVIew);
        if (stationLocation.get(position).LAST_CAT.equals("1")) { //첫차 이미지 변경
            imageView.setImageResource(R.color.colorPrimary);
        } else if (stationLocation.get(position).LAST_CAT.equals("2")) { //막차 이미지 변경
            imageView.setImageResource(R.color.colorGray_2);
        } else {
            imageView.setImageResource(R.color.colorAccent);
        }

        TextView tx1 = itemLayout.findViewById(R.id.station_ROUTE_NO);
        tx1.setText(stationLocation.get(position).ROUTE_NO);

        TextView tx2 = itemLayout.findViewById(R.id.station_DESTINATION);
        tx2.setText(stationLocation.get(position).DESTINATION + " 방향");

        TextView tx3 = itemLayout.findViewById(R.id.station_logical);
        tx3.setTextColor(Color.parseColor("#000000"));
        TextView tx4 = itemLayout.findViewById(R.id.station_logical2);
        tx4.setTextColor(Color.parseColor("#000000"));

        tx3.setText("");
        tx4.setText("");

        // 로지컬하게 구분 (TP는 타입을 확인함 tx3)
        if (stationLocation.get(position).MSG_TP.equals("01")) { // TP는 타입을 확인함 (도착이니까 아무것도 표시 안함)
            tx4.setTextColor(Color.parseColor("#F87FAB"));
            tx3.setText(" ");
            tx4.setText("도착");
        } else if (stationLocation.get(position).MSG_TP.equals("02")) { // 출발
            tx3.setText(stationLocation.get(position).STATUS_POS +" 정류장 전");
            tx4.setText(stationLocation.get(position).EXTIME_MIN +" 분");
        } else if (stationLocation.get(position).MSG_TP.equals("03")) { // 몇분후 도착
            tx3.setText(stationLocation.get(position).STATUS_POS +" 정류장 전");
            tx4.setText(stationLocation.get(position).EXTIME_MIN +" 분");
        } else if (stationLocation.get(position).MSG_TP.equals("04")) { // 교차로 통과
            tx3.setText("이전 정류장을 출발");
            tx4.setText(stationLocation.get(position).EXTIME_MIN +" 분");
        } else if (stationLocation.get(position).MSG_TP.equals("06")) { // 진입중
            tx4.setTextColor(Color.parseColor("#F87FAB"));
            tx3.setTextColor(Color.parseColor("#F87FAB"));
            tx3.setText("진입중");
            tx4.setText("진입중");
        } else if (stationLocation.get(position).MSG_TP.equals("07")) { // 차고지 운행 대기중
            tx3.setTextColor(Color.parseColor("#F87FAB"));
            tx3.setText(stationLocation.get(position).EXTIME_SEC + " 출발 예정");
            tx4.setText(" ");
        }

        return itemLayout;
    }
}