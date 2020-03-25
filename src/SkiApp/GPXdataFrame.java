package SkiApp;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


class GPXdataFrame {
    private GridPane singleDayStats = new GridPane();

    final private Label date = new Label("Date:");
    final private Label distance = new Label("Route:");
    final private Label time = new Label("Time:");
    final private Label speed = new Label("Speed:");
    final private Label altitude = new Label("Height:");

    private Label dateValue = new Label();
    private Label distanceValue = new Label();
    private Label timeValue = new Label();
    private Label speedValue = new Label();
    private Label altitudeValue = new Label();

    private Label[] labelDate = {date, dateValue};
//    private Label[] labels = {distance, time, speed, altitude};
    private Label[] labelValues = {distanceValue, timeValue, speedValue, altitudeValue};
    private Label[] labelsAll = {date, dateValue, distance, time, speed, altitude, distanceValue, timeValue, speedValue, altitudeValue};

    private boolean imClicked = false;
    private boolean normalColorStyle;
    private VBox wholeFrameStats;

    GPXdataFrame(String dateValue, String distanceValue, String timeValue, String speedValue, String altitudeValue, boolean normalColorStyle) {
        this.dateValue.setText(dateValue);
        this.distanceValue.setText(String.join(" ",distanceValue, "km"));
        this.timeValue.setText(timeValue);
        this.speedValue.setText(String.join(" ", speedValue, "km/h"));
        this.altitudeValue.setText(String.join(" ", altitudeValue, "m"));
        this.normalColorStyle = normalColorStyle;
        this.wholeFrameStats = createGPXdataFrame();
    }


    private VBox createGPXdataFrame() {

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
            if(!wholeFrameStats.getStyleClass().get(0).equals("frameClicked")) {
                if (normalColorStyle) {
                    wholeFrameStats.getStyleClass().set(0, "frameBlue");
                } else {
                    wholeFrameStats.getStyleClass().set(0, "frameYellow");
                }
            }
        });


        wholeFrameStats.setOnMouseEntered(mouseEvent -> {
            if(!wholeFrameStats.getStyleClass().get(0).equals("frameClicked")) {
                wholeFrameStats.getStyleClass().set(0, "frameEntered");
            }
        });

        wholeFrameStats.setOnMouseClicked(mouseEvent -> setImClicked(true));

        wholeFrameStats.setMaxWidth(206);
        return wholeFrameStats;
    }


    boolean getImClicked() {
        return imClicked;
    }

    void setImClicked(boolean imClicked) {
        this.imClicked = imClicked;
    }

    boolean getNormalColorStyle() {
        return normalColorStyle;
    }

    VBox getFrameStats() {
        return wholeFrameStats;
    }
}