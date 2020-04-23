package SkiAppTest;

import SkiApp.GPXparser;
import SkiApp.TrackPoint;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class GPXparserTest {

    @Test
    void testObjectCreation() throws ParserConfigurationException, SAXException, IOException {
        new GPXparser(new File("E:\\xInne\\SkiTracker-export.gpx"));
    }

    @Test
    void nullAsInput() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new GPXparser(null));
        assertEquals("Input file cannot be null!", exception.getMessage());
    }

    @Test
    void differentInputFileExtension() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new GPXparser(new File("E:\\xInne\\Internet.txt")));
        assertEquals("Wrong input file extension! Should be: .gpx, found: .txt", exception.getMessage());
    }


    @Test
    void notXmlLikeFileStructureInFile() {
        assertThrows(SAXParseException.class, () -> new GPXparser(new File("E:\\xInne\\Internet.gpx")));
    }


    @Test
    void parseXMLtoTrackPointList() throws Exception {
        ObservableList<TrackPoint> trackPointList = FXCollections.observableArrayList();
        TrackPoint trackPoint1 = new TrackPoint("45.3225", "6.5385","1897.373046875","2019-03-10T09:30:44Z");
        TrackPoint trackPoint2 = new TrackPoint("45.3228", "6.5384","1916.8690185546875","2019-03-10T09:30:49Z");
        TrackPoint trackPoint3 = new TrackPoint("45.3225", "6.5386","1898.676025390625","2019-03-10T09:31:20Z");
        TrackPoint trackPoint4 = new TrackPoint("45.3227", "6.5387","1878.43603515625","2019-03-12T09:31:42Z");
        TrackPoint trackPoint5 = new TrackPoint("45.3226", "6.5388","1867.345947265625","2019-03-12T09:32:17Z");
        trackPointList.addAll(trackPoint1, trackPoint2, trackPoint3, trackPoint4, trackPoint5);

        GPXparser gpXparser = new GPXparser(new File("E:\\xInne\\simpleTrk.gpx"));
        ObservableList<TrackPoint> createdTrackPointList = gpXparser.parseXMLtoTrackPointList();

        for (int i = 0; i < createdTrackPointList.size(); i++) {
            TrackPoint trackPoint = createdTrackPointList.get(i);
            TrackPoint trackPointCreated = createdTrackPointList.get(i);
            assertEquals(trackPoint.getLat(), trackPointCreated.getLat());
            assertEquals(trackPoint.getLon(), trackPointCreated.getLon());
            assertEquals(trackPoint.getAlt(), trackPointCreated.getAlt());
            assertEquals(trackPoint.getTime(), trackPointCreated.getTime());
            assertEquals(trackPoint.getDate(), trackPointCreated.getDate());
        }
    }



}