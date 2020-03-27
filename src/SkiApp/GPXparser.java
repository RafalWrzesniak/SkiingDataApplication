package SkiApp;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class GPXparser {

    private File inputFile;
    private Document doc;

    GPXparser(File filePath) {
        this.inputFile = filePath;
        this.doc = createDocUsingDOMparser();
    }


    private Document createDocUsingDOMparser() {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(inputFile);
            doc.getDocumentElement().normalize();
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private TrackPoint createTrackPoint(int idXML) {
        String lat = getAllTrkpt().item(idXML).getAttributes().getNamedItem("lat").getTextContent();
        String lon = getAllTrkpt().item(idXML).getAttributes().getNamedItem("lon").getTextContent();
        String alt = getAllEle().item(idXML).getTextContent();
        String time = getAllTime().item(idXML).getTextContent();

        return new TrackPoint(lat, lon, alt, time);
    }

    public TrackPoint[] parseXMLtoTrackPointList() throws Exception {

        if(getAllTrkpt().getLength() == getAllEle().getLength() && getAllTrkpt().getLength() == getAllTime().getLength()) {
            TrackPoint[] trackPointList = new TrackPoint[getAllTrkpt().getLength()];
            for (int idXML = 0; idXML < getAllTrkpt().getLength(); idXML++) {
                trackPointList[idXML] = createTrackPoint(idXML);
            }
            return trackPointList;
        } else {
            throw new Exception("GPX file is incorrect");
        }
    }



    private NodeList getAllTrkpt() {
        return doc.getElementsByTagName("trkpt");
    }

    private NodeList getAllEle() {
        return doc.getElementsByTagName("ele");
    }

    private NodeList getAllTime() {
        return doc.getElementsByTagName("time");
    }

}
