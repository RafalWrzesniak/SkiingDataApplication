package SkiApp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.File;
import java.time.Duration;
import java.time.LocalTime;

public class SingleDayStats {

    public static void main(String[] args) {
        GPXparser gpXparser;
        try {
            gpXparser = new GPXparser(new File("E:\\xInne\\SkiTracker-export.gpx"));
//            gpXparser = new GPXparser(new File("E:\\Java_projects\\SkiingData\\resources\\simpleTrk.gpx"));
            ObservableList<TrackPoint> allTrackedPoints = gpXparser.parseXMLtoTrackPointList();
            ObservableList<ObservableList<TrackPoint>> dayList = divideAllPointsToDays(allTrackedPoints);
            for (ObservableList<TrackPoint> singleDayFromList: dayList) {
                SingleDayStats singleDay = new SingleDayStats(singleDayFromList);
            }
//            SingleDayStats singleDayStats = new SingleDayStats(dayList.get(0));

        } catch (Exception e) {
            System.out.println("dupa");
            e.printStackTrace();
        }

    }

    private ObservableList<TrackPoint> allTrackedPoints;
    private ObservableList<Double> altArray = FXCollections.observableArrayList();
    private ObservableList<Double> distanceArray = FXCollections.observableArrayList();
    private ObservableList<Double> timeArray = FXCollections.observableArrayList();
    private ObservableList<Double> speedArray = FXCollections.observableArrayList();


    private LocalTime totalTime;
    private double totalDistance;


    private SingleDayStats(ObservableList<TrackPoint> allTrackedPoints) {
        setAllTrackedPoints(allTrackedPoints);
        createArrays();
        calcTotalTime();
        calcTotalDist();
        System.out.printf("Total distance: %.2f km %n", getTotalDistance());
        System.out.println("Total time: " + getTotalTime());
    }

    private void createArrays() {
        double alt;
        double dist;
        double absDist = 0;
        double time;
        double absTime;
        double speed;
        for (int i = 0; i < this.allTrackedPoints.size()-1; i++) {
            dist = distanceBetweenPoints(this.allTrackedPoints.get(i), this.allTrackedPoints.get(i+1));
            time = timeBetweenPoints(this.allTrackedPoints.get(i), this.allTrackedPoints.get(i+1));
            speed = (dist/time)*3.6;
            alt = this.allTrackedPoints.get(i).getAlt();
            absTime = timeBetweenPoints(this.allTrackedPoints.get(0), this.allTrackedPoints.get(i));
            absDist += distanceBetweenPoints(this.allTrackedPoints.get(i), this.allTrackedPoints.get(i+1));
            if(time > 150) {
                System.out.println("no nie wiem");
            }
            distanceArray.add(absDist/1000);
            altArray.add(alt);
            timeArray.add(absTime);
            speedArray.add(speed);
        }
    }

    private void calcTotalDist() {
        this.totalDistance = distanceArray.get(distanceArray.size()-1);
    }
    public double getTotalDistance() {
        return totalDistance;
    }

    public LocalTime getTotalTime() {
        return totalTime;
    }
    private void calcTotalTime() {
        int hours = Duration.between(allTrackedPoints.get(0).getTime(), allTrackedPoints.get(allTrackedPoints.size()-1).getTime()).toHoursPart();
        int min = Duration.between(allTrackedPoints.get(0).getTime(), allTrackedPoints.get(allTrackedPoints.size()-1).getTime()).toMinutesPart();
        int sec = Duration.between(allTrackedPoints.get(0).getTime(), allTrackedPoints.get(allTrackedPoints.size()-1).getTime()).toSecondsPart();
        this.totalTime = LocalTime.of(hours, min, sec);
    }


    private long timeBetweenPoints(TrackPoint trackPoint1, TrackPoint trackPoint2) {
        return Duration.between(trackPoint1.getTime(), trackPoint2.getTime()).toSeconds();
    }


    private double distanceBetweenPoints(TrackPoint trackPoint1, TrackPoint trackPoint2) {
        double earthRadius = 6369169; // meters
        double dLat = Math.toRadians(trackPoint2.getLat() - trackPoint1.getLat());
        double dLng = Math.toRadians(trackPoint2.getLon() - trackPoint1.getLon());
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(trackPoint1.getLat())) * Math.cos(Math.toRadians(trackPoint2.getLat())) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = c * earthRadius; // Haversine formula
        double dAlt = trackPoint1.getAlt()-trackPoint2.getAlt();
//        System.out.println(trackPoint2.getAlt() + " - " + trackPoint1.getAlt());
//        System.out.println("Alt diff: " + Math.round(dAlt) + " m");
//        System.out.println("Dist w/o alt: " + dist);
        return Math.sqrt(Math.pow(dist, 2) + Math.pow(dAlt, 2));
    }

    private void setAllTrackedPoints(ObservableList<TrackPoint> allTrackedPoints) {
        this.allTrackedPoints = allTrackedPoints;
    }

    static ObservableList<ObservableList<TrackPoint>> divideAllPointsToDays(ObservableList<TrackPoint> allPoints) {
        ObservableList<ObservableList<TrackPoint>> singleDayPointsList = FXCollections.observableArrayList();
        ObservableList<TrackPoint> singleDayPoints = FXCollections.observableArrayList();
        for (TrackPoint point: allPoints) {
            if(allPoints.indexOf(point) != 0) {
                if(point.getDate().compareTo(allPoints.get(allPoints.indexOf(point)-1).getDate()) != 0) {
                    ObservableList<TrackPoint> dayCopy = FXCollections.observableArrayList();
                    dayCopy.addAll(singleDayPoints);
                    singleDayPointsList.add(dayCopy);
                    singleDayPoints.removeAll(dayCopy);
                }
            }
            singleDayPoints.add(point);
            if (allPoints.indexOf(point) == allPoints.size()-1) {
                singleDayPointsList.add(singleDayPoints);
            }
        }
        return singleDayPointsList;
    }


}