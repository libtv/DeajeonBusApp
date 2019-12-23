package com.example.deajeonbusapp.bottom_nav;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.deajeonbusapp.ListviewAdapter.Businfo_Adapter;
import com.example.deajeonbusapp.ListviewAdapter.ByRoute_Adapter;
import com.example.deajeonbusapp.ListviewAdapter.DTO.BusDistanceShort;
import com.example.deajeonbusapp.ListviewAdapter.DTO.Businfo;
import com.example.deajeonbusapp.ListviewAdapter.DTO.ByRoute;
import com.example.deajeonbusapp.R;
import com.example.deajeonbusapp.SecondActivity;
import com.example.deajeonbusapp.ThirdActivity;
import com.example.deajeonbusapp.create_database.Create_Table_BUSINFO;
import com.example.deajeonbusapp.create_database.Create_Table_ByRoute;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class frag_home extends Fragment implements OnMapReadyCallback {
    private FragmentActivity mContext;
    private static final String TAG = "Hello";
    private GoogleMap mMap;
    private MapView mapView = null;
    private Marker currentMarker = null;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient; // Deprecated된 FusedLocationApi를 대체
    private LocationRequest locationRequest;
    private Location mCurrentLocatiion;

    private final LatLng mDefaultLocation = new LatLng(37.532600, 127.024612);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int UPDATE_INTERVAL_MS = 1000 * 60 * 1;  // 1분 단위 시간 갱신
    private static final int FASTEST_UPDATE_INTERVAL_MS = 1000 * 30 ; // 30초 단위로 화면 갱신

    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";


    /* 구글맵 추가 */
    @Override
    public void onAttach(Activity activity) { // Fragment 가 Activity에 attach 될 때 호출된다.
        mContext =(FragmentActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 초기화 해야 하는 리소스들을 여기서 초기화 해준다.
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View vh = inflater.inflate(R.layout.fragment_home, container, false);
        TabHost tabHost1 = (TabHost) vh.findViewById(R.id.tabHost1);
        tabHost1.setup();

        // 첫 번째 Tab. (탭 표시 텍스트:"TAB 1"), (페이지 뷰:"content1")
        TabHost.TabSpec ts1 = tabHost1.newTabSpec("Tab Spec 1");
        ts1.setContent(R.id.content1);
        ts1.setIndicator("Home");
        tabHost1.addTab(ts1);

        // 두 번째 Tab. (탭 표시 텍스트:"TAB 2"), (페이지 뷰:"content2")
        TabHost.TabSpec ts2 = tabHost1.newTabSpec("Tab Spec 2");
        ts2.setContent(R.id.content2);
        ts2.setIndicator("노선 정보");
        tabHost1.addTab(ts2);
        c_businfo(vh);

        // 세 번째 Tab. (탭 표시 텍스트:"TAB 3"), (페이지 뷰:"content3")
        TabHost.TabSpec ts3 = tabHost1.newTabSpec("Tab Spec 3");
        ts3.setContent(R.id.content3);
        ts3.setIndicator("정류장 정보");
        tabHost1.addTab(ts3);
        c_byroute(vh);

        // Layout 을 inflate 하는 곳이다.
        if (savedInstanceState != null) {
            mCurrentLocatiion = savedInstanceState.getParcelable(KEY_LOCATION);
            CameraPosition mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
        mapView = (MapView)vh.findViewById(R.id.map);
        if(mapView != null) {
            mapView.onCreate(savedInstanceState);
        }
        mapView.getMapAsync(this);
        return vh;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        // Fragement에서의 OnCreateView를 마치고, Activity에서 onCreate()가 호출되고 나서 호출되는 메소드이다.
        // Activity와 Fragment의 뷰가 모두 생성된 상태로, View를 변경하는 작업이 가능한 단계다.
        super.onActivityCreated(savedInstanceState);

        //액티비티가 처음 생성될 때 실행되는 함수
        MapsInitializer.initialize(mContext);

        locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY) // 정확도를 최우선적으로 고려
                .setInterval(UPDATE_INTERVAL_MS) // 위치가 Update 되는 주기
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS); // 위치 획득후 업데이트되는 주기

        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder();

        builder.addLocationRequest(locationRequest);

        // FusedLocationProviderClient 객체 생성
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        setDefaultLocation(); // GPS를 찾지 못하는 장소에 있을 경우 지도의 초기 위치가 필요함.
        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();
    }

    /* Map View */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mCurrentLocatiion = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void setDefaultLocation() {
        if (currentMarker != null) currentMarker.remove();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(mDefaultLocation);
        markerOptions.title("위치정보 가져올 수 없음");
        markerOptions.snippet("위치 퍼미션과 GPS 활성 여부 확인하세요");
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentMarker = mMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mDefaultLocation, 15);
        mMap.moveCamera(cameraUpdate);
    }

    private double pythaGoras(String a1, String a2) {
        LatLng lng = currentMarker.getPosition();

        double v1 = lng.latitude - Double.parseDouble(a1);
        double v2 = lng.longitude - Double.parseDouble(a2);

        return Math.sqrt(Math.pow(v1, 2.0)+ Math.pow(v2, 2.0));
    }

    private void setBusRouteLocation() {
        Create_Table_ByRoute db = new Create_Table_ByRoute(getContext());
        MarkerOptions markerOptions = new MarkerOptions();
        ArrayList<BusDistanceShort> list = new ArrayList<>();
        Cursor res = db.getLocationData();
        if (res.getCount() == 0) {
            return;
        }
        while (res.moveToNext()) {
            if (list.size() >= 10) {
                for(int i = 0; i < list.size(); i++) {
                    if ( pythaGoras(list.get(i).GPS_LATI, list.get(i).GPS_LONG) > pythaGoras(res.getString(2), res.getString(3)) ) {
                        list.remove(i);
                        list.add(new BusDistanceShort(res.getString(0), res.getString(1), res.getString(2), res.getString(3)));

                        for(int j = 0; j < list.size(); j++) {
                            for(int z=0; z < j; z++) {
                                if (pythaGoras(list.get(j).GPS_LATI, list.get(j).GPS_LONG) > pythaGoras(list.get(z).GPS_LATI, list.get(z).GPS_LONG)) {
                                    String T_BUS_NODE_ID = list.get(z).BUS_NODE_ID;
                                    String T_BUSSTOP_NM = list.get(z).BUSSTOP_NM;
                                    String T_GPS_LATI = list.get(z).GPS_LATI;
                                    String T_GPS_LONG = list.get(z).GPS_LONG;

                                    list.set(z, new BusDistanceShort(list.get(j).BUS_NODE_ID, list.get(j).BUSSTOP_NM, list.get(j).GPS_LATI, list.get(j).GPS_LONG));
                                    list.set(j, new BusDistanceShort(T_BUS_NODE_ID, T_BUSSTOP_NM, T_GPS_LATI, T_GPS_LONG));
                                }
                            }
                        }
                        break;
                    }
                }
            } else {
                list.add(new BusDistanceShort(res.getString(0), res.getString(1), res.getString(2), res.getString(3) ) );
            }
        }
        for(int i=0; i<list.size(); i++) {
            LatLng busLocation = new LatLng(Double.parseDouble(list.get(i).GPS_LATI), Double.parseDouble(list.get(i).GPS_LONG));
            markerOptions.position(busLocation);
            markerOptions.title(list.get(i).BUSSTOP_NM);
            markerOptions.snippet(list.get(i).GPS_LATI + ", " + list.get(i).GPS_LONG);
            markerOptions.draggable(true);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            mMap.addMarker(markerOptions);
        }
    }

    String getCurrentAddress(LatLng latlng) {
        // 위치 정보와 지역으로부터 주소 문자열을 구한다.
        List<Address> addressList = null ;
        Geocoder geocoder = new Geocoder( mContext, Locale.getDefault());

        // 지오코더를 이용하여 주소 리스트를 구한다.
        try {
            addressList = geocoder.getFromLocation(latlng.latitude,latlng.longitude,1);
        } catch (IOException e) {
            Toast. makeText( mContext, "위치로부터 주소를 인식할 수 없습니다. 네트워크가 연결되어 있는지 확인해 주세요.", Toast.LENGTH_SHORT ).show();
            e.printStackTrace();
            return "주소 인식 불가" ;
        }

        if (addressList.size() < 1) { // 주소 리스트가 비어있는지 비어 있으면
            return "해당 위치에 주소 없음" ;
        }

        // 주소를 담는 문자열을 생성하고 리턴
        Address address = addressList.get(0);
        StringBuilder addressStringBuilder = new StringBuilder();
        for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
            addressStringBuilder.append(address.getAddressLine(i));
            if (i < address.getMaxAddressLineIndex())
                addressStringBuilder.append("\n");
        }

        return addressStringBuilder.toString();
    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            List<Location> locationList = locationResult.getLocations();

            if (locationList.size() > 0) {
                Location location = locationList.get(locationList.size() - 1);

                LatLng currentPosition
                        = new LatLng(location.getLatitude(), location.getLongitude());

                String markerTitle = getCurrentAddress(currentPosition);
                String markerSnippet = "위도:" + String.valueOf(location.getLatitude())
                        + " 경도:" + String.valueOf(location.getLongitude());

                Log.d(TAG, "Time :" + CurrentTime() + " onLocationResult : " + markerSnippet);

                //현재 위치에 마커 생성하고 이동
                setCurrentLocation(location, markerTitle, markerSnippet);
                mCurrentLocatiion = location;
            }
        }

    };

    private String CurrentTime(){
        Date today = new Date();
        SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat time = new SimpleDateFormat("hh:mm:ss a");
        return time.format(today);
    }

    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {
        if (currentMarker != null) currentMarker.remove();

        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);

        currentMarker = mMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
        mMap.moveCamera(cameraUpdate);

        setBusRouteLocation(); //개수 정해서 몇개만 클릭할 예정

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String markerId = marker.getId();
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(marker.getTitle());
                View v = getLayoutInflater().inflate(R.layout.marker_layout, null);
                builder.setView(v);
                builder.show();
            }
        });

    }

    private void getDeviceLocation() {
        try {
            if (mLocationPermissionGranted) {
                mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(mContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(mContext,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }


    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onStart() { // 유저에게 Fragment가 보이도록 해준다.
        super.onStart();
        mapView.onStart();
        Log.d(TAG, "onStart ");
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
        if (mFusedLocationProviderClient != null) {
            Log.d(TAG, "onStop : removeLocationUpdates");
            mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
    }

    @Override
    public void onResume() { // 유저에게 Fragment가 보여지고, 유저와 상호작용이 가능하게 되는 부분
        super.onResume();
        mapView.onResume();
        if (mLocationPermissionGranted) {
            Log.d(TAG, "onResume : requestLocationUpdates");
            mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
            if (mMap!=null)
                mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroyView() { // 프래그먼트와 관련된 View 가 제거되는 단계
        super.onDestroyView();
        if (mFusedLocationProviderClient != null) {
            Log.d(TAG, "onDestroyView : removeLocationUpdates");
            mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
    }

    @Override
    public void onDestroy() {
        // Destroy 할 때는, 반대로 OnDestroyView에서 View를 제거하고, OnDestroy()를 호출한다.
        super.onDestroy();
        mapView.onDestroy();
    }

    /* 리스트뷰 추가 */
    public void c_businfo(View view) {
        final ArrayList<Businfo> list = new ArrayList<>();
        final List<Businfo> savedlist = new ArrayList<>();
        Create_Table_BUSINFO busrouteinfo = new Create_Table_BUSINFO(getContext());
        Cursor res = busrouteinfo.getAllData();
        if (res.getCount() == 0) {
            Toast.makeText(getContext(), "데이터베이스를 읽어오지 못하였습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        while (res.moveToNext()) {
            list.add(new Businfo(res.getString(0), res.getString(1), res.getString(2), res.getString(3),
                    res.getString(4), res.getString(5), res.getString(6), res.getString(7),
                    res.getString(8), res.getString(9), res.getString(10), res.getString(11), res.getString(12),
                    res.getString(13)));
        }
        final Businfo_Adapter adapter = new Businfo_Adapter(getActivity().getApplicationContext(), list);
        ListView listView = view.findViewById(R.id.businfo_list);
        listView.setAdapter(adapter);
        savedlist.addAll(list);

        final EditText editSearch = (EditText) view.findViewById(R.id.editSearch);
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                // input창에 문자를 입력할때마다 호출된다.
                // search 메소드를 호출한다.
                String text = editSearch.getText().toString();
                search(text, list, savedlist, adapter);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), SecondActivity.class);
                intent.putExtra("ROUTE_CD", list.get(position).getROUTE_CD().toString());
                intent.putExtra("ROUTE_NO", list.get(position).getROUTE_NO().toString());
                startActivity(intent);
            }
        });
    }

    public void search(String charText, ArrayList list, List<Businfo> savedList, Businfo_Adapter adapter) {
        list.clear();
        if (charText.length() == 0) {
            list.addAll(savedList);
        } else {
            for (int i = 0; i < savedList.size(); i++) {
                if (savedList.get(i).getROUTE_NO().contains(charText)) {
                    list.add(savedList.get(i));
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    public void search2(String charText, ArrayList list, List<ByRoute> savedList, ByRoute_Adapter adapter) {
        list.clear();
        if (charText.length() == 0) {
            list.addAll(savedList);
        } else {
            for (int i = 0; i < savedList.size(); i++) {
                if (savedList.get(i).getBUSSTOP_NM().contains(charText)) {
                    list.add(savedList.get(i));
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    public void c_byroute(View view) {
        final ArrayList<ByRoute> list = new ArrayList<>();
        final List<ByRoute> savedlist = new ArrayList<>();
        Create_Table_ByRoute byRoute = new Create_Table_ByRoute(getContext());
        Cursor res = byRoute.getAllData();
        if (res.getCount() == 0) {
            Toast.makeText(getContext(), "데이터베이스를 읽어오지 못하였습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        while (res.moveToNext()) {
            list.add(new ByRoute(res.getString(0), res.getString(1), res.getString(2), res.getString(3),
                    res.getString(4), res.getString(5), res.getString(6), res.getString(7)));
        }
        savedlist.addAll(list);
        final ByRoute_Adapter adapter = new ByRoute_Adapter(getActivity().getApplicationContext(), list);
        ListView listView = view.findViewById(R.id.route_list);
        listView.setAdapter(adapter);

        final EditText editSearch = (EditText) view.findViewById(R.id.editSearch2);
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = editSearch.getText().toString();
                search2(text, list, savedlist, adapter);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), ThirdActivity.class);
                intent.putExtra("BUS_NODE_ID", list.get(position).BUS_NODE_ID);
                intent.putExtra("BUSSTOP_NM", list.get(position).BUSSTOP_NM);
                startActivity(intent);
            }
        });
    }

    /*
    ////////////////////////////////////////////////////
     현재 위치에서 버스찾기 ( 내가 탄 버스를 찾는 것 )
    ////////////////////////////////////////////////////
    */


}