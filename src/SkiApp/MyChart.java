package SkiApp;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.w3c.dom.ls.LSOutput;


class MyChart extends AreaChart{

    private NumberAxis xAxis;
    private NumberAxis yAxis;
    AreaChart<Number, Number> chart;

    MyChart(NumberAxis xAxis, NumberAxis yAxis) {
        super(xAxis, yAxis);
        this.xAxis = xAxis;
        this.yAxis = yAxis;
        commonSetups();
    }

    private void commonSetups() {

        xAxis.setAutoRanging(true);
        xAxis.setMinorTickVisible(false);

        yAxis.setAutoRanging(false);
        yAxis.setMinorTickVisible(false);
        yAxis.setUpperBound(100);
        yAxis.setTickUnit(10);

        chart = new AreaChart<>(xAxis, yAxis);
        chart.legendVisibleProperty().setValue(false);
        chart.createSymbolsProperty().setValue(false);
        chart.setCreateSymbols(false);



//        chart.getData().add(axisData);
//        axisData.getNode().lookup(".chart-series-area-fill").setStyle("-fx-fill: #0000FF25;");
//        axisData.getNode().lookup(".chart-series-area-line").setStyle("-fx-stroke: #0000FF; -fx-stroke-width: 2");


    }

    void loadData(ObservableList<Double> xData, ObservableList<Double> yData) {

        // clear previous charts
        chart.getData().clear();

        // calc max and min value on Y
        double yMax = calcMaxY(yData);
        double yMin = calcMinY(yData);

        int scale = 500;
        if(yMax<200) {
            scale = 10;
            yAxis.setTickUnit(10);
        } else {
            yAxis.setTickUnit(500);
        }
        //set bounds depending on max Y value
        for (int i = 0, j = scale*20; i < scale*20 && j > 0; i+=scale, j-= scale) {
            if(yMin > i) yAxis.setLowerBound(i);
            if(yMax < j) yAxis.setUpperBound(j);
        }

        // add to chart
        XYChart.Series<Number, Number> axisData = new XYChart.Series<>();
        for(int i = 0; i < xData.size(); i++) {
            XYChart.Data dataPoint = new XYChart.Data<>(xData.get(i), yData.get(i));
            axisData.getData().add(dataPoint);
        }
        animateChart(axisData, yMax, yMin);
//        chart.getData().add(axisData);

    }

    double calcMaxY(ObservableList<Double> yData) {
        double yMax = 0;
        for (Double yDatum : yData) {
            if (yDatum > yMax) yMax = yDatum;
        }
        return yMax;
    }

    double calcMinY(ObservableList<Double> yData) {
        double yMin = 10000;
        for (Double yDatum : yData) {
            if (yDatum < yMin) yMin = yDatum;
        }
        return yMin;
    }


    void changeColorsOfChart(Color color) {
        try {
            chart.getData().get(0).getNode().lookup(".chart-series-area-fill").setStyle("-fx-fill: #" + color.toString().substring(2, 8) + "35;");
            chart.getData().get(0).getNode().lookup(".chart-series-area-line").setStyle("-fx-stroke-width: 1.1; -fx-stroke: #" + color.toString().substring(2, 8) + "ff;");
        } catch (NullPointerException | IndexOutOfBoundsException ignored) {}
    }
    void setXaxisLabel(String name) {
        this.xAxis.setLabel(String.join(" ", name, name.equals("Distance") ? "[km]" : "[h]"));
    }

    void setYaxisLabel(String name) {
        this.yAxis.setLabel(String.join(" ", name, name.equals("Altitude") ? "[m]" : "[kh/h]"));
    }

    void animateChart(XYChart.Series<Number, Number> axisData, double yMax, double yMin) {
//        XYChart.Series<Number, Number> axisData = new XYChart.Series<>();
//        axisData.getData().add(new XYChart.Data<>(0, 65));
//        axisData.getData().add(new XYChart.Data<>(1, 80));
//        axisData.getData().add(new XYChart.Data<>(2, 70));
//        axisData.getData().add(new XYChart.Data<>(3, 76));
//        axisData.getData().add(new XYChart.Data<>(4, 55));
//        axisData.getData().add(new XYChart.Data<>(5, 91));
//        axisData.getData().add(new XYChart.Data<>(6, 80));
//        axisData.getData().add(new XYChart.Data<>(7, 66));
//        axisData.getData().add(new XYChart.Data<>(8, 74));
//        axisData.getData().add(new XYChart.Data<>(9, 69));
//        axisData.getData().add(new XYChart.Data<>(9.83, 94));

        xAxis.setAutoRanging(false);
        xAxis.setUpperBound(axisData.getData().get(axisData.getData().size()-1).getXValue().intValue()*1.1);

        // prepare start data
//        double size = axisData.getData().size();
        double size = 200;
        XYChart.Series<Number, Number> newAxisData = new XYChart.Series<>();
        for(int i = 0; i < axisData.getData().size(); i++) {
            newAxisData.getData().add(new XYChart.Data<>(axisData.getData().get(i).getXValue().doubleValue()/size,Math.random()*10));
        }
        chart.getData().add(newAxisData);

        // move charts right
        Timeline moveXdataIntoChart = new Timeline();
        moveXdataIntoChart.getKeyFrames().add(new KeyFrame(Duration.millis(5), (ActionEvent actionEvent) -> {
            for(int i = 0; i < axisData.getData().size(); i++) {
                Number curx = newAxisData.getData().get(i).getXValue();
                if(curx.doubleValue() < axisData.getData().get(i).getXValue().doubleValue()) {
                    newAxisData.getData().get(i).setXValue(curx.doubleValue() + (axisData.getData().get(i).getXValue().doubleValue() / size));
                }
            }
        }));

        // move charts up
        Timeline moveYdataIntoChart = new Timeline();
        moveYdataIntoChart.getKeyFrames().add(new KeyFrame(Duration.millis(5), (ActionEvent actionEvent) -> {
            for(int i = 0; i < axisData.getData().size(); i++) {
                Number cury = newAxisData.getData().get(i).getYValue();
                Number tary = axisData.getData().get(i).getYValue();
                System.out.println(cury.toString() + ", " + tary.toString());

                if(cury.doubleValue() < tary.doubleValue()) {
                    newAxisData.getData().get(i).setYValue(cury.doubleValue() + 10);
                } else if (i==axisData.getData().size()-1){
                    moveYdataIntoChart.stop();
                    break;
                }
            }
        }));

        moveYdataIntoChart.setCycleCount((int) yMax);
        moveXdataIntoChart.setOnFinished(actionEvent -> xAxis.setAutoRanging(true));

        moveXdataIntoChart.setCycleCount((int) size);
//        moveXdataIntoChart.setOnFinished((actionEvent -> moveYdataIntoChart.play()));
        moveXdataIntoChart.play();

    }


}
