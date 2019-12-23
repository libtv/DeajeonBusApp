package com.example.deajeonbusapp.create_database;

import com.example.deajeonbusapp.ListviewAdapter.DTO.StationLocation;

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

public class Search_Station_Location implements Callable<ArrayList> {
    String BUS_NODE_ID;
    ArrayList<StationLocation> list;

    public Search_Station_Location(String BUS_NODE_ID) {
        this.BUS_NODE_ID = BUS_NODE_ID;
        list = new ArrayList<StationLocation>();
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
    public ArrayList<StationLocation> call() throws Exception {
        try {
            URL url;
            String urls = "http://openapitraffic.daejeon.go.kr/api/rest/arrive/getArrInfoByStopID"
                    + "?ServiceKey=" +
                    "FuvfyWt%2FsqEsDtUX3%2F9hlW2s3ba9UxCaCc54zNAX9yAUlZNYW9YMcyZBwuNIbDoxX08mFgcNorzeY4vbs4rNDQ%3D%3D"
                    + "&BusStopID=" + BUS_NODE_ID; // url 설정
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
                    String CAR_REG_NO;
                    try {
                        CAR_REG_NO = getTagValue("CAR_REG_NO", eElement);
                    } catch (Exception e) {
                        CAR_REG_NO = "null";
                    }
                        list.add(new StationLocation(getTagValue("ROUTE_CD", eElement), getTagValue("ROUTE_NO", eElement), getTagValue("ROUTE_TP", eElement),
                                getTagValue("LAST_CAT", eElement), getTagValue("MSG_TP", eElement), getTagValue("STATUS_POS", eElement),
                                getTagValue("DESTINATION", eElement), CAR_REG_NO, getTagValue("EXTIME_MIN", eElement), getTagValue("EXTIME_SEC", eElement)));
                }    // for end
            }    // if end
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
