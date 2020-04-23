package SkiAppTest;

import SkiApp.GPXparser;
import SkiApp.SingleDayStats;
import SkiApp.TrackPoint;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class SingleDayStatsTest {

    SingleDayStats singleDayStats;


    @BeforeEach
    void setUp() throws Exception {
        GPXparser gpXparser = new GPXparser(new File("E:\\xInne\\SkiTracker-export.gpx"));
        ObservableList<ObservableList<TrackPoint>> dayList = SingleDayStats.divideAllPointsToDays(gpXparser.parseXMLtoTrackPointList());
        this.singleDayStats = new SingleDayStats(dayList.get(0));
    }


    @Test
    void testObjectCreation() throws Exception {
        GPXparser gpXparser = new GPXparser(new File("E:\\xInne\\SkiTracker-export.gpx"));
        ObservableList<ObservableList<TrackPoint>> dayList = SingleDayStats.divideAllPointsToDays(gpXparser.parseXMLtoTrackPointList());
        SingleDayStats singleDayStats = new SingleDayStats(dayList.get(0));
    }

    @Test
    void nullAsInputFotObject() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new SingleDayStats(null));
        assertEquals("Input argument cannot be null!", exception.getMessage());
    }

    @Test
    void nullAsInputForDivideAllPoints() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> SingleDayStats.divideAllPointsToDays(null));
        assertEquals("Input list cannot be null!", exception.getMessage());
    }

    @Test
    void divideAllPointsToDays() throws Exception {
        GPXparser gpXparser = new GPXparser(new File("E:\\xInne\\SkiTracker-export.gpx"));
        ObservableList<ObservableList<TrackPoint>> dayList = SingleDayStats.divideAllPointsToDays(gpXparser.parseXMLtoTrackPointList());
        assertEquals(6, dayList.size());
    }

    @Test
    void getTotalDistance() {
        assertEquals(66.86246712538284, singleDayStats.getTotalDistance(), 0.01);
    }

    @Test
    void getTotalTime() {
        assertEquals(LocalTime.of(5, 56, 29), singleDayStats.getTotalTime());
    }

    @Test
    void getDistDown() {
        assertEquals(42.38546946825812, singleDayStats.getDistDown(), 0.01);
    }

    @Test
    void getDistUp() {
        assertEquals(24.49617835421746, singleDayStats.getDistUp(), 0.01);
    }

    @Test
    void getDate() {
        assertEquals(LocalDate.of(2019, 3, 10), singleDayStats.getDate());
    }

    @Test
    void getMaxSpeed() {
        assertEquals(66.7609674021541, singleDayStats.getMaxSpeed(), 0.01);
    }

    @Test
    void getMaxAlt() {
        assertEquals(2734.076904296875, singleDayStats.getMaxAlt(), 0.01);
    }

    @Test
    void getMinAlt() {
        assertEquals(1478.083984375, singleDayStats.getMinAlt(), 0.01);
    }

    @Test
    void getAvgSpeed() {
        assertEquals(20.366643224378652, singleDayStats.getAvgSpeed(), 0.01);
    }

    @Test
    void getTimeDown() {
        assertEquals("2:4", singleDayStats.getTimeDown());
    }

    @Test
    void getTimeUp() {
        assertEquals("1:32", singleDayStats.getTimeUp());
    }

    @Test
    void getTimeRest() {
        assertEquals("2:21", singleDayStats.getTimeRest());
    }

    @Test
    void getDownhill() {
        assertEquals(15.0, singleDayStats.getDownhill(), 0.01);
    }

    @Test
    void getUphill() {
        assertEquals(14.0, singleDayStats.getUphill(), 0.01);
    }

    @Test
    void getAltDown() {
        assertEquals(6.539501220703125, singleDayStats.getAltDown(), 0.01);
    }

    @Test
    void getAltUp() {
        assertEquals(6.502605224609375, singleDayStats.getAltUp(), 0.01);
    }

}