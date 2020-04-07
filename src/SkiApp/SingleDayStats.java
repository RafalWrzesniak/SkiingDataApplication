package SkiApp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

class SingleDayStats {

    private ObservableList<Double> shortDistanceArray = FXCollections.observableArrayList();
    private ObservableList<Double> shortAltArray = FXCollections.observableArrayList();
    private ObservableList<Double> shortTimeArray = FXCollections.observableArrayList();

    private ObservableList<Double> longAltArray = FXCollections.observableArrayList();
    private ObservableList<Double> longTimeArray = FXCollections.observableArrayList();


    private double totalDistance, distDown, distUp, maxSpeed, maxAlt, minAlt, avgSpeed;
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
        System.out.printf("Distance down: %.2f km %n", distDown);
        System.out.printf("Distance up: %.2f km %n", distUp);
        System.out.printf("Max speed: %.2f km/h %n", maxSpeed);
        System.out.println();
    }

    private void createArrays(ObservableList<TrackPoint> allTrackedPoints) {
        boolean isGoingDown = false;
        double alt, dist, time, absTime, speed;
        double absDist = 0, distDown = 0, distUp = 0, maxAlt = 0, minAlt = 10000, maxSpeed = 0, sample = 0, sumSpeed = 0;
        ObservableList<Double> shortDistanceArray = FXCollections.observableArrayList();
        ObservableList<Double> shortAltArray = FXCollections.observableArrayList();
        ObservableList<Double> shortTimeArray = FXCollections.observableArrayList();
        ObservableList<TrackPoint> shortTrackedPoints = FXCollections.observableArrayList();

        for (int i = 1; i < allTrackedPoints.size(); i++) {
            dist = distanceBetweenPoints(allTrackedPoints.get(i), allTrackedPoints.get(i-1));
            time = timeBetweenPoints(allTrackedPoints.get(i), allTrackedPoints.get(i-1));
            speed = (dist/time)*3.6;
            alt = allTrackedPoints.get(i).getAlt();
            absTime = timeBetweenPoints(allTrackedPoints.get(i), allTrackedPoints.get(0));
            absDist += distanceBetweenPoints(allTrackedPoints.get(i), allTrackedPoints.get(i-1));

            if (i == 1) {
                shortDistanceArray.add((double) 0);
                shortAltArray.add(allTrackedPoints.get(0).getAlt());
                shortTrackedPoints.add(allTrackedPoints.get(0));
            }
            longTimeArray.add(absTime / 3600);
            longAltArray.add(alt);

            if(alt < allTrackedPoints.get(i-1).getAlt()) {
                distDown += dist;
                if(!isGoingDown && distanceBetweenPoints(allTrackedPoints.get(i), shortTrackedPoints.get(shortTrackedPoints.size()-1)) > 80) {
                    shortTimeArray.add(absTime / 3600);
                    shortDistanceArray.add(absDist / 1000);
                    shortAltArray.add(alt);
                    shortTrackedPoints.add(allTrackedPoints.get(i));
                }
                isGoingDown = true;
            } else {
                distUp += dist;
                if(isGoingDown && distanceBetweenPoints(allTrackedPoints.get(i), shortTrackedPoints.get(shortTrackedPoints.size()-1)) > 80) {
                    shortTimeArray.add(absTime / 3600);
                    shortDistanceArray.add(absDist / 1000);
                    shortAltArray.add(alt);
                    shortTrackedPoints.add(allTrackedPoints.get(i));
                }
                isGoingDown = false;
            }
            if(alt > maxAlt) {
                maxAlt = alt;
            } else if(alt < minAlt){
                minAlt = alt;
            }

            if(speed > maxSpeed && speed < 80) maxSpeed = speed;
            if(speed > 2) {
                sumSpeed += speed;
                sample += 1;
            }

        }

        this.shortDistanceArray = shortDistanceArray;
        this.shortTimeArray = shortTimeArray;
        this.shortAltArray = shortAltArray;

        this.avgSpeed = sumSpeed/sample;
        this.maxSpeed = maxSpeed;
        this.minAlt = minAlt;
        this.maxAlt = maxAlt;
        this.distDown = distDown/1000;
        this.distUp = distUp/1000;
        this.totalDistance = shortDistanceArray.get(shortDistanceArray.size()-1);
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

    ObservableList<Double> getAltArray() {
        return shortAltArray;
    }

    ObservableList<Double> getDistanceArray() {
        return shortDistanceArray;
    }

    ObservableList<Double> getTimeArray() {
        return shortTimeArray;
    }

    double getDistDown() {
        return distDown;
    }

    double getDistUp() {
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

    double getMinAlt() {
        return minAlt;
    }

    double getAvgSpeed() {
        return avgSpeed;
    }

    public ObservableList<Double> getLongAltArray() {
        return longAltArray;
    }

    public ObservableList<Double> getLongTimeArray() {
        return longTimeArray;
    }
}