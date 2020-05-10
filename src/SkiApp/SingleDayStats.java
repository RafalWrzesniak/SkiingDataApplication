package SkiApp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

public class SingleDayStats {

    private static final Logger logger = LoggerFactory.getLogger(SingleDayStats.class.getName());

    private ObservableList<Double> shortDistanceArray = FXCollections.observableArrayList();
    private ObservableList<Double> shortAltByDistArray = FXCollections.observableArrayList();
    private ObservableList<Double> shortTimeArray = FXCollections.observableArrayList();
    private ObservableList<Double> shortAltByTimeArray = FXCollections.observableArrayList();
    private ObservableList<TrackPoint> allUsedPoints = FXCollections.observableArrayList();


    private double totalDistance, distDown, distUp, maxSpeed, maxAlt, minAlt, avgSpeed, altDown, altUp;
    private String timeDown, timeUp, timeRest;
    private int downhill, uphill;
    private LocalTime totalTime;
    private final LocalDate date;

    public SingleDayStats(ObservableList<TrackPoint> allTrackedPoints) {
        if(allTrackedPoints == null) throw new IllegalArgumentException("Input argument cannot be null!");
        this.date = allTrackedPoints.get(0).getDate();
        createArrays(allTrackedPoints);
        calcTotalTime(allTrackedPoints);
    }

    private void createArrays(ObservableList<TrackPoint> allTrackedPoints) {
        boolean isGoingDown = false, shouldBeDown;
        double alt, dist, time, absTime, speed;
        double absDist = 0, distDown = 0, distUp = 0, maxSpeed = 0, sample = 0,
                sumSpeed = 0, timeDown = 0, timeUp = 0, timeRest = 0, speedToRest = 2, altDown = 0, altUp = 0;
        int counterDown = 0, counterUp = 0, downhill = 0, uphill = 0;
        shouldBeDown = allTrackedPoints.get(0).getAlt() > allTrackedPoints.get(1).getAlt();
        // initialize arrays
        ObservableList<Double> shortDistanceArray = FXCollections.observableArrayList();
        ObservableList<Double> shortAltByDistArray = FXCollections.observableArrayList();
        ObservableList<TrackPoint> shortTrackedPoints = FXCollections.observableArrayList();

        maxAlt = allTrackedPoints.get(0).getAlt();
        minAlt = allTrackedPoints.get(0).getAlt();
        allUsedPoints.add(allTrackedPoints.get(0));
        shortAltByTimeArray.add(allTrackedPoints.get(0).getAlt());
        shortTimeArray.add(0.0);
        this.shortDistanceArray.add(0.0);
        this.shortAltByDistArray.add(allTrackedPoints.get(0).getAlt());

        shortDistanceArray.add(0.0);
        shortAltByDistArray.add(allTrackedPoints.get(0).getAlt());
        shortTrackedPoints.add(allTrackedPoints.get(0));

        for (int i = 1; i < allTrackedPoints.size(); i++) {
            dist = distanceBetweenPoints(allTrackedPoints.get(i), allTrackedPoints.get(i-1));
            time = timeBetweenPoints(allTrackedPoints.get(i), allTrackedPoints.get(i-1));
            speed = (dist/time)*3.6;
            alt = allTrackedPoints.get(i).getAlt();
            absTime = timeBetweenPoints(allTrackedPoints.get(i), allTrackedPoints.get(0));
            absDist += distanceBetweenPoints(allTrackedPoints.get(i), allTrackedPoints.get(i-1));

            // get time, distance and alt arrays
            if(alt < allTrackedPoints.get(i-1).getAlt()) {
                distDown += dist;
                altDown += allTrackedPoints.get(i-1).getAlt() - alt;
                if(!isGoingDown && distanceBetweenPoints(allTrackedPoints.get(i), shortTrackedPoints.get(shortTrackedPoints.size()-1)) > 20) {
                    addPointToArrays(absDist, absTime, alt, allTrackedPoints.get(i));
                }
                isGoingDown = true;
                if(speed > speedToRest) {
                    timeDown += time;
                } else {
                    timeRest += time;
                }
                counterDown += 1;
                if(counterUp > 50 && shouldBeDown) {
                    counterUp = 0;
                    uphill += 1;
                    shouldBeDown = false;
                }

            } else {
                distUp += dist;
                altUp += alt - allTrackedPoints.get(i-1).getAlt();
                if(isGoingDown && distanceBetweenPoints(allTrackedPoints.get(i), shortTrackedPoints.get(shortTrackedPoints.size()-1)) > 20) {
                    addPointToArrays(absDist, absTime, alt, allTrackedPoints.get(i));
                }

                isGoingDown = false;
                if(speed > speedToRest) {
                    timeUp += time;
                } else {
                    timeRest += time;
                }
                counterUp += 1;
                if(counterDown > 50 && !shouldBeDown) {
                    downhill += 1;
                    counterDown = 0;
                    shouldBeDown = true;
                }

            }

            // add points to smooth time chart
            if(distanceBetweenPoints(allTrackedPoints.get(i), shortTrackedPoints.get(shortTrackedPoints.size()-1)) > 200) {
                addPointToArrays(absDist, absTime, alt, allTrackedPoints.get(i));
            }

            if(allTrackedPoints.size() < 100) {
                addPointToArrays(absDist, absTime, alt, allTrackedPoints.get(i));
            }

            // calc max and avg speed
            if(speed > maxSpeed && speed < 80) maxSpeed = speed;
            if(speed > 2 && speed < 120) {
                sumSpeed += speed;
                sample += 1;
            }

        }

        this.altDown = altDown / 1000;
        this.altUp = altUp / 1000;
        this.downhill = downhill;
        this.uphill = uphill;

        this.timeDown = changeSumTimeToString(timeDown);
        this.timeUp = changeSumTimeToString(timeUp);
        this.timeRest = changeSumTimeToString(timeRest);
        this.avgSpeed = sumSpeed/sample;
        this.maxSpeed = maxSpeed;
        this.distDown = distDown/1000;
        this.distUp = distUp/1000;
        this.totalDistance = shortDistanceArray.get(shortDistanceArray.size()-1);
        logger.info("All data gathered for day {} with {} points", getDate(), allUsedPoints.size());
//        System.out.println(maxAlt);
//        System.out.println(this.shortDistanceArray.size());
//        System.out.println(Arrays.toString(this.shortDistanceArray.toArray()));
//        System.out.println(this.shortAltByDistArray.size());
//        System.out.println(this.shortTimeArray.size());
//        System.out.println(this.shortAltByTimeArray.size());
    }

    private void addPointToArrays(double absDist, double absTime, double alt, TrackPoint currentPoint) {
        if(allUsedPoints.get(allUsedPoints.size()-1) == currentPoint) return;
        shortDistanceArray.add(absDist / 1000);
        shortTimeArray.add(absTime / 3600);
        shortAltByDistArray.add(alt);
        shortAltByTimeArray.add(alt);
        allUsedPoints.add(currentPoint);
        if(alt > maxAlt) {
            maxAlt = alt;
        } else if(alt < minAlt){
            minAlt = alt;
        }
    }

    private String changeSumTimeToString(Double value) {
        value = value / 3600;
        return value.intValue() + ":" + Math.round(Layout.round((value-value.intValue())*60));
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


    public static ObservableList<ObservableList<TrackPoint>> divideAllPointsToDays(ObservableList<TrackPoint> allPoints) {
        if(allPoints == null) throw new IllegalArgumentException("Input list cannot be null!");
        ObservableList<ObservableList<TrackPoint>> singleDayPointsList = FXCollections.observableArrayList();
        ObservableList<TrackPoint> singleDayPoints = FXCollections.observableArrayList();
        for (TrackPoint point: allPoints) {
            if(allPoints.indexOf(point) != 0) {
                if(point.getDate().compareTo(allPoints.get(allPoints.indexOf(point)-1).getDate()) != 0) {
                    ObservableList<TrackPoint> dayCopy = FXCollections.observableArrayList();
                    dayCopy.addAll(singleDayPoints);
                    if(dayCopy.size() > 1) singleDayPointsList.add(dayCopy);
                    singleDayPoints.removeAll(dayCopy);
                }
            }
            singleDayPoints.add(point);
            if (allPoints.indexOf(point) == allPoints.size()-1 && singleDayPoints.size() > 1) {
                singleDayPointsList.add(singleDayPoints);
            }
        }
        logger.info("All parsed points successfully divided into {} days", singleDayPointsList.size());
        return singleDayPointsList;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public LocalTime getTotalTime() {
        return totalTime;
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

    public double getMinAlt() {
        return minAlt;
    }

    public double getAvgSpeed() {
        return avgSpeed;
    }

    public String getTimeDown() {
        return timeDown;
    }

    public String getTimeUp() {
        return timeUp;
    }

    public String getTimeRest() {
        return timeRest;
    }

    public int getDownhill() {
        return downhill;
    }

    public int getUphill() {
        return uphill;
    }

    public double getAltDown() {
        return altDown;
    }

    public double getAltUp() {
        return altUp;
    }

    public ObservableList<Double> getShortAltByTimeArray() {
        return shortAltByTimeArray;
    }

    ObservableList<Double> getTimeArray() {
        return shortTimeArray;
    }


    public ObservableList<Double> getAltArray() {
        return shortAltByDistArray;
    }

    public ObservableList<Double> getDistanceArray() {
        return shortDistanceArray;
    }

    ObservableList<TrackPoint> getAllUsedPoints() {
        return allUsedPoints;
    }
}