package SkiApp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.File;

public class SingleDayStats {


    private ObservableList<TrackPoint> allTrackedPoints;

    public SingleDayStats(ObservableList<TrackPoint> allTrackedPoints) {
        setAllTrackedPoints(allTrackedPoints);
    }




    public static ObservableList<ObservableList<TrackPoint>> divideAllPointsToDays(ObservableList<TrackPoint> allPoints) {
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



    private void setAllTrackedPoints(ObservableList<TrackPoint> allTrackedPoints) {
        this.allTrackedPoints = allTrackedPoints;
    }
}
