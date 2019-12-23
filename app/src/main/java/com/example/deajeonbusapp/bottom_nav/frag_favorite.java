package com.example.deajeonbusapp.bottom_nav;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.deajeonbusapp.ListviewAdapter.DTO.Businfo;
import com.example.deajeonbusapp.ListviewAdapter.DTO.ByRoute;
import com.example.deajeonbusapp.ListviewAdapter.Favorite_bus_Adapter;
import com.example.deajeonbusapp.ListviewAdapter.Favorite_station_Adapter;
import com.example.deajeonbusapp.R;
import com.example.deajeonbusapp.SecondActivity;
import com.example.deajeonbusapp.ThirdActivity;
import com.example.deajeonbusapp.create_database.Create_Table_BUSINFO;
import com.example.deajeonbusapp.create_database.Create_Table_ByRoute;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class frag_favorite extends Fragment {
    ListView listView;
    ListView listView2;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View vh =  inflater.inflate(R.layout.fragment_favorite, container, false);
        listView = (ListView) vh.findViewById(R.id.favorite_bus_list);
        listView2 = (ListView) vh.findViewById(R.id.favorite_station_list);

        TabHost tabHost1 = (TabHost) vh.findViewById(R.id.tabHost1) ;
        tabHost1.setup() ;

        // 첫 번째 Tab. (탭 표시 텍스트:"TAB 1"), (페이지 뷰:"content1")
        TabHost.TabSpec ts1 = tabHost1.newTabSpec("Tab Spec 1") ;
        ts1.setContent(R.id.content1) ;
        ts1.setIndicator("Bus");
        tabHost1.addTab(ts1);

        // 두 번째 Tab. (탭 표시 텍스트:"TAB 2"), (페이지 뷰:"content2")
        TabHost.TabSpec ts2 = tabHost1.newTabSpec("Tab Spec 2") ;
        ts2.setContent(R.id.content2) ;
        ts2.setIndicator("Station") ;
        tabHost1.addTab(ts2) ;
        return vh;
    }

    @Override
    public void onResume() {
        c_bus_favorte();
        c_station_favorte();
        super.onResume();
    }

    public void c_station_favorte() {
        SharedPreferences settings = getActivity().getSharedPreferences("Favorite_Station", Context.MODE_PRIVATE);
        Collection<?> col =  settings.getAll().keySet();
        Iterator<?> it = col.iterator();
        final ArrayList<ByRoute> list = new ArrayList<>();
        Create_Table_ByRoute db = new Create_Table_ByRoute(getContext());

        //BUS_NODE_ID : BUSSTOP_NM
        while(it.hasNext()) {
            String BUS_NODE_ID = (String)it.next();
            Cursor res = db.getSearch(BUS_NODE_ID);
            if (res.getCount() == 0) {
                Toast.makeText(getContext(), "데이터베이스의 오류상으로 로딩할 수 없습니다.", Toast.LENGTH_SHORT).show();
                return;
            }
            res.moveToNext();
            list.add(new ByRoute(res.getString(0), res.getString(1), res.getString(2), res.getString(3),
                    res.getString(4), res.getString(5), res.getString(6), res.getString(7)));
        }
        Favorite_station_Adapter adapter = new Favorite_station_Adapter(getContext(), list);
        listView2.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), ThirdActivity.class);
                intent.putExtra("BUS_NODE_ID", list.get(position).BUS_NODE_ID);
                intent.putExtra("BUSSTOP_NM", list.get(position).BUSSTOP_NM);
                startActivity(intent);
            }
        });
    }

    public void c_bus_favorte() {
        SharedPreferences settings = getActivity().getSharedPreferences("Favorite_Bus", Context.MODE_PRIVATE);
        Collection<?> col =  settings.getAll().keySet();
        Iterator<?> it = col.iterator();
        Create_Table_BUSINFO db = new Create_Table_BUSINFO(getContext());
        final ArrayList<Businfo> list = new ArrayList<>();
        // ROUTE_CD : ROUTE_NO
        while(it.hasNext()) {
            String ROUTE_CD = (String)it.next();
            Cursor res = db.getSearchData(ROUTE_CD);
            if (res.getCount() == 0) {
                Toast.makeText(getContext(), "데이터베이스의 오류상으로 로딩할 수 없습니다.", Toast.LENGTH_SHORT).show();
                return;
            }
            res.moveToNext();
            list.add(new Businfo(res.getString(0), res.getString(1), res.getString(2), res.getString(3),
                    res.getString(4), res.getString(5), res.getString(6), res.getString(7),
                    res.getString(8), res.getString(9), res.getString(10), res.getString(11), res.getString(12),
                    res.getString(13)));
        }
        Favorite_bus_Adapter adapter = new Favorite_bus_Adapter(getContext(), list);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), SecondActivity.class);
                intent.putExtra("ROUTE_CD", list.get(position).ROUTE_CD);
                intent.putExtra("ROUTE_NO", list.get(position).ROUTE_NO);
                startActivity(intent);
            }
        });
    }
}
