package SkiApp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class GPXparser {

    private File inputFile;
    private Document doc;
    private NodeList trkptList;
    private NodeList altList;
    private NodeList timeList;

    public GPXparser(File inputFile) throws IOException, SAXException, ParserConfigurationException {
        if(inputFile == null) {
            throw new IllegalArgumentException("Input file cannot be null!");
        } else if(!inputFile.toString().endsWith(".gpx")) {
            throw new IllegalArgumentException("Wrong input file extension! Should be: .gpx, found: " + inputFile.toString().substring(inputFile.toString().indexOf('.'))) {};
        } else {
            this.inputFile = inputFile;
        }
        this.doc = createDocUsingDOMparser();
    }


    private Document createDocUsingDOMparser() throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(inputFile);
        doc.getDocumentElement().normalize();
        return doc;

    }


    private TrackPoint createTrackPoint(int idXML) {
        String lat = this.trkptList.item(idXML).getAttributes().getNamedItem("lat").getTextContent();
        String lon = this.trkptList.item(idXML).getAttributes().getNamedItem("lon").getTextContent();
        String alt = this.altList.item(idXML).getTextContent();
        String time = this.timeList.item(idXML).getTextContent();

        return new TrackPoint(lat, lon, alt, time);
    }

    public ObservableList<TrackPoint> parseXMLtoTrackPointList() throws Exception {
        this.trkptList = doc.getElementsByTagName("trkpt");
        this.altList = doc.getElementsByTagName("ele");
        this.timeList = doc.getElementsByTagName("time");

        if(trkptList.getLength() == altList.getLength() && trkptList.getLength() == timeList.getLength()) {
            ObservableList<TrackPoint> trackPointList = FXCollections.observableArrayList();
            for (int idXML = 0; idXML < trkptList.getLength(); idXML++) {
                try {
                    trackPointList.add(createTrackPoint(idXML));
                } catch (IllegalArgumentException ignored) {}
            }
            return trackPointList;
        } else {
            throw new Exception("Mismatch in .input file - different amount of gps coordinates, altitudes and time");
        }
    }

}
