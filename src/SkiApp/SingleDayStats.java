package SkiApp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.File;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

public class SingleDayStats {

    private ObservableList<Double> distanceArray = FXCollections.observableArrayList();
    private ObservableList<Double> timeArray = FXCollections.observableArrayList();
    private ObservableList<Double> altArray = FXCollections.observableArrayList();
    private ObservableList<Double> speedArray = FXCollections.observableArrayList();

    private double totalDistance, distDown, distUp, maxSpeed, maxAlt, minAlt;
    private LocalTime totalTime;
    private LocalDate date;


    SingleDayStats(ObservableList<TrackPoint> allTrackedPoints) {
        createArrays(allTrackedPoints);
        calcTotalTime(allTrackedPoints);
        this.date = allTrackedPoints.get(0).getDate();
    }

    void printSingleDayStats() {
        System.out.println("Total time: " + getTotalTime());
        System.out.printf("Total distance: %.2f km %n", getTotalDistance());
        System.out.printf("Distance down: %.2f km %n", distDown/1000);
        System.out.printf("Distance up: %.2f km %n", distUp/1000);
        System.out.printf("Max speed: %.2f km/h %n", maxSpeed);
        System.out.println();
    }

    private void createArrays(ObservableList<TrackPoint> allTrackedPoints) {
        double alt, dist, time, absTime, speed;
        double absDist = 0, distDown = 0, distUp = 0, maxAlt = 0, minAlt = 10000;
        ObservableList<Double> distanceArray = FXCollections.observableArrayList();
        ObservableList<Double> timeArray = FXCollections.observableArrayList();
        ObservableList<Double> altArray = FXCollections.observableArrayList();
        ObservableList<Double> speedArray = FXCollections.observableArrayList();
        for (int i = 1; i < allTrackedPoints.size(); i++) {
            dist = distanceBetweenPoints(allTrackedPoints.get(i), allTrackedPoints.get(i-1));
            time = timeBetweenPoints(allTrackedPoints.get(i), allTrackedPoints.get(i-1));
            speed = (dist/time)*3.6;
            alt = allTrackedPoints.get(i).getAlt();
            absTime = timeBetweenPoints(allTrackedPoints.get(i), allTrackedPoints.get(0));

            if(dist < 1000) {
                if(alt < allTrackedPoints.get(i-1).getAlt()) {
                    distDown += dist;
                } else {
                    distUp += dist;
                }
                if(alt > maxAlt) {
                    maxAlt = alt;
                } else if(alt < minAlt){
                    minAlt = alt;
                }
                absDist += distanceBetweenPoints(allTrackedPoints.get(i), allTrackedPoints.get(i-1));
                distanceArray.add(absDist / 1000);
                altArray.add(alt);
                timeArray.add(absTime / 3600);
                if(speedArray.size() == 0 || speed < (speedArray.get(speedArray.size()-1)+1)*5) {
                    speedArray.add(speed);
                } else {
                    speedArray.add(speedArray.get(speedArray.size()-1));
                }

            } else if (i == 1){
                speedArray.add(speed);
            }
        }

        this.distanceArray = distanceArray;
        this.timeArray = timeArray;
        this.altArray = altArray;
        this.speedArray = removeTooBigValuesAndCalcMaxSpeed(speedArray);

        this.minAlt = minAlt;
        this.maxAlt = maxAlt;
        this.distDown = distDown;
        this.distUp = distUp;
        this.totalDistance = distanceArray.get(distanceArray.size()-1);
    }

    private ObservableList<Double> removeTooBigValuesAndCalcMaxSpeed(ObservableList<Double> speedArray) {
        double sumSpeed = 0, maxSpeed = 0;
        for (Double speed : speedArray) {
            sumSpeed += speed;
        }
        double mediumSpeed = sumSpeed/speedArray.size();
        for(int i = 1; i < speedArray.size(); i++) {
            if(speedArray.get(i) > mediumSpeed * 6) {
                speedArray.set(i, speedArray.get(i-1));
            }
            if(speedArray.get(i) > maxSpeed) maxSpeed = speedArray.get(i);
        }
        this.maxSpeed = maxSpeed;
        return speedArray;
    }


    private void calcTotalTime(ObservableList<TrackPoint> allTrackedPoints) {
        int hours = Duration.between(allTrackedPoints.get(0).getTime(), allTrackedPoints.get(allTrackedPoints.size()-1).getTime()).toHoursPart();
        int min = Duration.between(allTrackedPoints.get(0).getTime(), allTrackedPoints.get(allTrackedPoints.size()-1).getTime()).toMinutesPart();
        int sec = Duration.between(allTrackedPoints.get(0).getTime(), allTrackedPoints.get(allTrackedPoints.size()-1).getTime()).toSecondsPart();
        this.totalTime = LocalTime.of(hours, min, sec);
    }


    private long timeBetweenPoints(TrackPoint trackPoint1, TrackPoint trackPoint2) {
        return Duration.between(trackPoint2.getTime(), trackPoint1.getTime()).toSeconds();
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

    double getTotalDistance() {
        return totalDistance;
    }

    LocalTime getTotalTime() {
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

    LocalDate getDate() {
        return date;
    }

    double getMaxSpeed() {
        return maxSpeed;
    }

    double getMaxAlt() {
        return maxAlt;
    }
    public double getMinAlt() {
        return minAlt;
    }

}