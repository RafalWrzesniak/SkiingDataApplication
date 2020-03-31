package SkiApp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

public class SingleDayStats {

    private ObservableList<TrackPoint> allTrackedPoints;
    private ObservableList<Double> altArray = FXCollections.observableArrayList();
    private ObservableList<Double> distanceArray = FXCollections.observableArrayList();
    private ObservableList<Double> timeArray = FXCollections.observableArrayList();
    private ObservableList<Double> speedArray = FXCollections.observableArrayList();

    private LocalTime totalTime;
    private double totalDistance;
    private double distDown;
    private double distUp;
    private double maxSpeed;
    private double maxAlt;
    private LocalDate date;


    SingleDayStats(ObservableList<TrackPoint> allTrackedPoints, LocalDate date) {
        setAllTrackedPoints(allTrackedPoints);
        this.date = date;
        createArrays();
        calcTotalTime();
    }

    void printSingleDayStats() {
        System.out.println("Total time: " + getTotalTime());
        System.out.printf("Total distance: %.2f km %n", getTotalDistance());
        System.out.printf("Distance down: %.2f km %n", distDown/1000);
        System.out.printf("Distance up: %.2f km %n", distUp/1000);
        System.out.printf("Max speed: %.2f km/h %n", maxSpeed);
        System.out.println();
    }

    private void createArrays() {
        double alt, dist, time, absTime, speed;
        double absDist = 0, distDown = 0, distUp = 0, maxSpeed = 0, maxAlt = 0;
        for (int i = 0; i < allTrackedPoints.size()-1; i++) {
            dist = distanceBetweenPoints(allTrackedPoints.get(i), allTrackedPoints.get(i+1));
            time = timeBetweenPoints(allTrackedPoints.get(i), allTrackedPoints.get(i+1));
            speed = (dist/time)*3.6;
            alt = allTrackedPoints.get(i).getAlt();
            absTime = timeBetweenPoints(allTrackedPoints.get(0), allTrackedPoints.get(i));
            if(dist < 1000) {
                if(alt > allTrackedPoints.get(i+1).getAlt()) {
                    distDown += dist;
                } else {
                    distUp += dist;
                }
                if(speed > maxSpeed && speed < 100) {
                    maxSpeed = speed;
                }
                if(alt > maxAlt) {
                    maxAlt = alt;
                }
                absDist += distanceBetweenPoints(allTrackedPoints.get(i), allTrackedPoints.get(i+1));
                distanceArray.add(absDist / 1000);
                altArray.add(alt);
                timeArray.add(absTime);
                speedArray.add(speed);
            } else {
//                allTrackedPoints.get(i).printTrackPoint();
//                allTrackedPoints.get(i+1).printTrackPoint();
//                System.out.println();
            }
        }
        this.maxAlt = maxAlt;
        this.maxSpeed = maxSpeed;
        this.distDown = distDown;
        this.distUp = distUp;
        this.totalDistance = distanceArray.get(distanceArray.size()-1);
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
//        return dist;
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

    public double getTotalDistance() {
        return totalDistance;
    }

    public LocalTime getTotalTime() {
        return totalTime;
    }

    public ObservableList<Double> getAltArray() {
        return altArray;
    }

    public ObservableList<Double> getDistanceArray() {
        return distanceArray;
    }

    public ObservableList<Double> getTimeArray() {
        return timeArray;
    }

    public ObservableList<Double> getSpeedArray() {
        return speedArray;
    }

    public double getDistDown() {
        return distDown;
    }

    public double getDistUp() {
        return distUp;
    }

    public LocalDate getDate() {
        return date;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public double getMaxAlt() {
        return maxAlt;
    }
}