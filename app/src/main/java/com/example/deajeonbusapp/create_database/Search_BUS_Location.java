package com.example.deajeonbusapp.create_database;

import com.example.deajeonbusapp.ListviewAdapter.DTO.BusLocation;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class Search_BUS_Location implements Callable<ArrayList> {
    String ROUTE_CD;
    ArrayList<BusLocation> list;
    public Search_BUS_Location(String ROUTE_CD) {
        this.ROUTE_CD = ROUTE_CD;
        list = new ArrayList<BusLocation>();
    }

    /* XML 파싱 기본 */
    private static String getTagValue(String tag, Element eElement) {
        NodeList nlList = eElement.getElementsByTagName(tag).item(0).getChildNodes();
        Node nValue = (Node) nlList.item(0);
        if (nValue == null)
            return null;
        return nValue.getNodeValue();
    }

    @Override
    public ArrayList<BusLocation> call() throws Exception {
        try {
            URL url;
            String urls = "http://openapitraffic.daejeon.go.kr/api/rest/busposinfo/getBusPosByRtid"
                    + "?ServiceKey=" +
                    "AdPGBAHwJiyLiNyZq9h004Q3lTqMzq2gnw9XgB0dFIJ%2Bs2ZlIdiPGu3cOWWBdtH%2FwpmOLTBUcsKTipV%2Bd7yVaQ%3D%3D"
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
                    list.add(new BusLocation(getTagValue("ROUTE_CD", eElement), getTagValue("BUS_NODE_ID", eElement), getTagValue("PLATE_NO", eElement),
                            getTagValue("STRE_DT", eElement), getTagValue("ud_type", eElement), getTagValue("TOTAL_DIST", eElement),
                            getTagValue("GPS_LATI", eElement), getTagValue("GPS_LONG", eElement)));
                }    // for end
            }    // if end
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
