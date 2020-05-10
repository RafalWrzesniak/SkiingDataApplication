package SkiApp;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


class OneDayDataWithFrame extends SingleDayStats{

    private static final Logger logger = LoggerFactory.getLogger(OneDayDataWithFrame.class.getName());
    private boolean imClicked = false;
    private final boolean normalColorStyle;
    private final VBox wholeFrameStats;

    OneDayDataWithFrame(ObservableList<TrackPoint> allTrackedPoints, boolean normalColorStyle) {
        super(allTrackedPoints);
        this.normalColorStyle = normalColorStyle;
        this.wholeFrameStats = createGPXdataFrame();
    }


    private VBox createGPXdataFrame() {

        Label dateValue = new Label(super.getDate().toString());
        Label distanceValue = new Label(Math.round(super.getTotalDistance()) + " km");
        Label timeValue = new Label(super.getTotalTime().getHour() + "h " + super.getTotalTime().getMinute() + "m");
        Label altitudeValue = new Label(Math.round(super.getMaxAlt()) + " m");
        Label speedValue = new Label(Math.round(super.getMaxSpeed()) + " km/h");

        GridPane singleDayStats = new GridPane();
        final Label date = new Label("Date:");
        final Label distance = new Label("Route:");
        final Label time = new Label("Time:");
        final Label speed = new Label("Speed:");
        final Label altitude = new Label("Height:");

        Label[] labelDate = {date, dateValue};
//        Label[] labels = {distance, time, speed, altitude};
        Label[] labelValues = {distanceValue, timeValue, speedValue, altitudeValue};
        Label[] labelsAll = {date, dateValue, distance, time, speed, altitude, distanceValue, timeValue, speedValue, altitudeValue};

        for (Label label : labelsAll) {
            label.setPrefWidth(50);
            label.setAlignment(Pos.CENTER);
            label.setPadding(new Insets(3, 1, 3, 1));
        }
        for (Label label : labelDate) {
            label.getStyleClass().add("labelDate");
        }
        for (Label label : labelValues) {
            label.getStyleClass().add("labelValue");
            label.setPrefWidth(60);
        }

        singleDayStats.gridLinesVisibleProperty().setValue(true);
        singleDayStats.addRow(1, distance, distanceValue, altitude, altitudeValue);
        singleDayStats.addRow(2, time, timeValue, speed, speedValue);

        dateValue.setPrefWidth(120);
        HBox dateOnly = new HBox(date, dateValue);
        dateOnly.setPadding(new Insets(0, 0, 0, 10));

        VBox wholeFrameStats = new VBox(dateOnly, singleDayStats);
        if(normalColorStyle) {
            wholeFrameStats.getStyleClass().add("frameBlue");
        } else {
            wholeFrameStats.getStyleClass().add("frameYellow");
        }

        wholeFrameStats.setOnMouseExited(mouseEvent -> {
            if(!wholeFrameStats.getStyleClass().get(0).equals("frameClicked") && !wholeFrameStats.getStyleClass().get(0).equals("frameWasClicked")) {
                if (normalColorStyle) {
                    wholeFrameStats.getStyleClass().set(0, "frameBlue");
                } else {
                    wholeFrameStats.getStyleClass().set(0, "frameYellow");
                }
            }
        });


        wholeFrameStats.setOnMouseEntered(mouseEvent -> {
            if(!wholeFrameStats.getStyleClass().get(0).equals("frameClicked") && !wholeFrameStats.getStyleClass().get(0).equals("frameWasClicked")) {
                wholeFrameStats.getStyleClass().set(0, "frameEntered");
            }
        });

        wholeFrameStats.setOnMouseClicked(mouseEvent -> setImClicked(true));

        wholeFrameStats.setMaxWidth(206);
        logger.debug("WholeFrameDataBox created for {}", getDate());
        return wholeFrameStats;
    }


    boolean isImClicked() {
        return imClicked;
    }

    void setImClicked(boolean imClicked) {
        this.imClicked = imClicked;
        logger.info("Frame date {} isClicked is now: {}", getDate(), imClicked);
    }

    boolean isNormalColorStyle() {
        return normalColorStyle;
    }

    VBox getFrameStats() {
        return wholeFrameStats;
    }
}