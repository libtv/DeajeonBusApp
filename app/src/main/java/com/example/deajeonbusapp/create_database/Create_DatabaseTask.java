package com.example.deajeonbusapp.create_database;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;

import com.example.deajeonbusapp.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class Create_DatabaseTask extends AsyncTask<Integer , Integer , Integer> {
    /*로딩중 표시 */
    Context context;
    ProgressDialog progressDialog = null;
    SharedPreferences create_database;
    int value;
    /* 데이터 베이스 */
    Create_Table_BUSINFO busrouteinfo;
    Create_Table_ByRoute byRoute;

    /* 메소드 */
    public Create_DatabaseTask(Context context, SharedPreferences create_database) {
        this.context = context;
        this.create_database = create_database;
        this.progressDialog = new ProgressDialog(context);
        busrouteinfo =  new Create_Table_BUSINFO(context);
        byRoute = new Create_Table_ByRoute(context);
    }

    @Override
    protected void onPreExecute () {
        value = 0;
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("\t데이터베이스 로딩중...");
        //show dialog
        progressDialog.show();
    }

    protected Integer doInBackground (Integer ...values) {
        /* 버스 인포 */
        /* 시작 */
        Thread Thread_businfo = new INSERT_BUSINFO(busrouteinfo);
        Thread_businfo.start();
        try {
            Thread_businfo.join();
        }catch (Exception e) {
            e.printStackTrace();
        }
        /* 종료 */

        /* 버스 순서도 */
        ExecutorService executor = Executors.newFixedThreadPool(1);
        Future<Cursor> futureFb = executor.submit(new CallbleSearch(busrouteinfo));
        Cursor res;
        try {
            res = futureFb.get();
            if (res.getCount() == 0) {
            } else {
                while(res.moveToNext()) {
                    /* 시작 */
                    Thread Thread_bybusstopid = new INSERT_ByBusStopID(byRoute, res.getString(0));
                    Thread_bybusstopid.start();
                    try {
                        Thread_bybusstopid.join();
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                    /* 종료 */
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return value;
    }

    protected void onPostExecute (Integer result) {
        progressDialog.dismiss();
        Cursor res = busrouteinfo.getAllData();
        if (res.getCount() == 0) {
            // show message
            showMessage("Error", "Nothing found");
            return;
        }
        StringBuffer buffer = new StringBuffer();
        while (res.moveToNext()) {
            buffer.append("Id :" + res.getString(0) + "\n");
            buffer.append("Name :" + res.getString(1) + "\n");
            buffer.append("Surname :" + res.getString(2) + "\n");
        }

        showMessage("Data", buffer.toString());
        create_database.edit().putBoolean("isFirstRun", false).commit();
    }

    public void showMessage(String title, String Message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);  // 메세지만 보여주기
        builder.show();
    }
}

class INSERT_BUSINFO extends Thread {
    Create_Table_BUSINFO busrouteinfo;

    INSERT_BUSINFO(Create_Table_BUSINFO busrouteinfo) {
        this.busrouteinfo = busrouteinfo;
    }

    /* XML 파싱 기본 */
    private static String getTagValue(String tag, Element eElement) {
        NodeList nlList = eElement.getElementsByTagName(tag).item(0).getChildNodes();
        Node nValue = (Node) nlList.item(0);
        if (nValue == null)
            return null;
        return nValue.getNodeValue();
    }

    public synchronized void run() {
        try {
            for (int i = 1; i <= 3; i++) { // 전체 노선 기본정보 조회
                URL url;
                String urls = "http://openapitraffic.daejeon.go.kr/api/rest/busRouteInfo/getRouteInfoAll"
                        + "?ServiceKey=" +
                        "FuvfyWt%2FsqEsDtUX3%2F9hlW2s3ba9UxCaCc54zNAX9yAUlZNYW9YMcyZBwuNIbDoxX08mFgcNorzeY4vbs4rNDQ%3D%3D"
                        + "&reqPage=" + i; // url 설정
                url = new URL(urls);
                DocumentBuilderFactory dbFactoty = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactoty.newDocumentBuilder();
                Document doc = dBuilder.parse(new InputSource(url.openStream()));
                doc.getDocumentElement().normalize();
                NodeList nList = doc.getElementsByTagName("itemList"); // <item> </item> 을 잘라서 nList에 넣는다.

                for (int temp = 0; temp < nList.getLength(); temp++) {
                    Node nNode = nList.item(temp); //
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;

                        busrouteinfo.insertData(getTagValue("ROUTE_CD", eElement), getTagValue("ROUTE_NO", eElement), getTagValue("ROUTE_TP", eElement),
                                getTagValue("BUSSTOP_CNT", eElement),  getTagValue("RUN_DIST_HALF", eElement),getTagValue("RUN_TM", eElement),
                                getTagValue("START_NODE_ID", eElement), getTagValue("END_NODE_ID", eElement),
                                getTagValue("ORIGIN_START", eElement), getTagValue("ORIGIN_START_SAT", eElement), getTagValue("ORIGIN_START_SUN", eElement),
                                getTagValue("ALLO_INTERVAL", eElement), getTagValue("ALLO_INTERVAL_SAT", eElement), getTagValue("ALLO_INTERVAL_SUN", eElement));
                    }    // for end
                }    // if end
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class CallbleSearch implements Callable<Cursor> {
    Create_Table_BUSINFO busrouteinfo;

    CallbleSearch(Create_Table_BUSINFO busrouteinfo) {
        this.busrouteinfo = busrouteinfo;
    }
    @Override
    public Cursor call() throws Exception {
        return busrouteinfo.getAllData();
    }
}

class INSERT_ByBusStopID extends Thread {
    Create_Table_ByRoute ByRoute;
    String ROUTE_CD;

    INSERT_ByBusStopID(Create_Table_ByRoute byRoute, String ROUTE_CD) {
        this.ROUTE_CD = ROUTE_CD;
        this.ByRoute = byRoute;
    }

    /* XML 파싱 기본 */
    private static String getTagValue(String tag, Element eElement) {
        NodeList nlList = eElement.getElementsByTagName(tag).item(0).getChildNodes();
        Node nValue = (Node) nlList.item(0);
        if (nValue == null)
            return null;
        return nValue.getNodeValue();
    }

    public synchronized void run() {
        try {
            URL url;
                String urls = "http://openapitraffic.daejeon.go.kr/api/rest/busRouteInfo/getStaionByRoute"
                    + "?ServiceKey=" +
                    "2Y%2BA%2F6%2Bjpk6HtHAfNN0%2B6Ti8cFO1anGUcA%2F2SSyz6egY7OS852qBQG3xgIsyH8xp5zLKkGkWzdbbsFPxnMsJLw%3D%3D"
                    + "&busRouteId=" + ROUTE_CD; // url 설정
            url = new URL(urls);
            DocumentBuilderFactory dbFactoty = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactoty.newDocumentBuilder();
            Document doc = dBuilder.parse(new InputSource(url.openStream()));
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("itemList"); // <item> </item> 을 잘라서 nList에 넣는다.

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp); //
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    ByRoute.insertData(getTagValue("BUS_NODE_ID", eElement), getTagValue("BUSSTOP_NM", eElement), getTagValue("BUSSTOP_ENG_NM", eElement),
                            getTagValue("ROUTE_CD", eElement), getTagValue("BUSSTOP_TP", eElement), getTagValue("BUSSTOP_SEQ", eElement),
                            getTagValue("GPS_LATI", eElement), getTagValue("GPS_LONG", eElement));
                }    // for end
            }    // if end
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}