package SkiApp;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.awt.event.MouseEvent;

class MyChart extends AreaChart{

    private NumberAxis xAxis;
    private NumberAxis yAxis;
    private static double maxXValue = 0;
    private static double maxYValue = 0;
    AreaChart<Number, Number> chart;
    private int hovers;

    MyChart(NumberAxis xAxis, NumberAxis yAxis) {
        super(xAxis, yAxis);
        this.xAxis = xAxis;
        this.yAxis = yAxis;
        commonSetups();
    }

    private void commonSetups() {

        xAxis.setAutoRanging(false);
        xAxis.setMinorTickVisible(false);
        xAxis.setUpperBound(100);
        xAxis.setTickUnit(10);

        yAxis.setAutoRanging(false);
        yAxis.setMinorTickVisible(false);
        yAxis.setUpperBound(3000);
        yAxis.setTickUnit(500);

        chart = new AreaChart<>(xAxis, yAxis);
        chart.legendVisibleProperty().setValue(true);
        chart.createSymbolsProperty().setValue(true);
        chart.setCreateSymbols(false);
        chart.setTitle("Chart of altitude versus distance");

        XYChart.Series<Number, Number> tempChartData = new XYChart.Series<>();
        tempChartData.getData().add(new XYChart.Data<>(0,0));
        tempChartData.setName("Chart legend");
        chart.getData().add(tempChartData);
        changeColorsOfChart(Color.ROYALBLUE);
        setYaxisLabel("Altitude");
        setXaxisLabel("Distance");

    }

    void loadData(ObservableList<Double> xData, ObservableList<Double> yData, Color color, boolean keepCharts) {

        // clear previous charts
        if(!keepCharts || chart.getData().get(0).getName().equals("Chart legend")) {
            chart.getData().clear();
            maxXValue = 0;
            maxYValue = 0;
        }

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
        for (int i = 0, j = scale*20; i < scale*20 && j > 0; i += scale, j -= scale) {
            if (yMin > i) yAxis.setLowerBound(i);
            if (yMax < j) yAxis.setUpperBound(j);
        }
        if(yAxis.getUpperBound() > maxYValue) maxYValue = yAxis.getUpperBound();
        if(keepCharts) yAxis.setUpperBound(maxYValue);

        // add to chart
        XYChart.Series<Number, Number> axisData = new XYChart.Series<>();
        for(int i = 0; i < xData.size(); i++) {
//        for(int i = 0; i < 500; i++) {
            axisData.getData().add(new XYChart.Data<>(xData.get(i), yData.get(i)));
        }

        // set x range
        xAxis.setAutoRanging(false);
        int lastX = axisData.getData().get(axisData.getData().size()-1).getXValue().intValue();
        if(lastX > 20) {
            xAxis.setTickUnit(5);
            lastX += 5;
            for (int i = 0; i < 5; i++) {
                if (lastX % 5 == 0) {
                    xAxis.setUpperBound(lastX);
                    break;
                } else {
                    lastX -= 1;
                }
            }
        } else {
            xAxis.setTickUnit(1);
            xAxis.setUpperBound(lastX+1);
        }
        if(xAxis.getUpperBound() > maxXValue) maxXValue = xAxis.getUpperBound();
        if(keepCharts) xAxis.setUpperBound(maxXValue);

        // animate data
        if(!keepCharts) {
            animateChart(axisData, yMin, color);
        } else {
            chart.getData().add(axisData);
        }

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
            Label tempLabel = (Label) chart.lookup("Label.chart-legend-item");
            tempLabel.getGraphic().setStyle("-fx-background-color: #" + color.toString().substring(2, 8) + "ff, white;");
        } catch (NullPointerException | IndexOutOfBoundsException ignored) {}
    }


    void setXaxisLabel(String name) {
        this.xAxis.setLabel(String.join(" ", name, name.equals("Distance") ? "[km]" : "[h]"));
    }

    void setYaxisLabel(String name) {
        this.yAxis.setLabel(String.join(" ", name, name.equals("Altitude") ? "[m]" : "[kh/h]"));
    }

    void animateChart(XYChart.Series<Number, Number> axisData, double yMin, Color color) {

        // prepare start data
        double size = 15;
        XYChart.Series<Number, Number> newAxisData = new XYChart.Series<>();
        for(int i = 0; i < axisData.getData().size(); i++) {
            newAxisData.getData().add(new XYChart.Data<>(axisData.getData().get(i).getXValue().doubleValue() / size, yMin + Math.random() * (yMin + 20) / 5));
        }
        chart.getData().add(newAxisData);
        changeColorsOfChart(color);
        XYChart.Series<Number, Number> newAxisDataCopy = new XYChart.Series<>();
        newAxisDataCopy.getData().addAll(newAxisData.getData());

        // move charts right
        Timeline moveXdataIntoChart = new Timeline();
        moveXdataIntoChart.getKeyFrames().add(new KeyFrame(Duration.millis(20), (ActionEvent actionEvent) -> {
            for(int i = 0; i < axisData.getData().size(); i++) {
                Number curx = newAxisData.getData().get(i).getXValue();
                if(curx.doubleValue() < axisData.getData().get(i).getXValue().doubleValue()) {
                    newAxisData.getData().get(i).setXValue(curx.doubleValue() + (axisData.getData().get(i).getXValue().doubleValue() / size));
                }
            }
            chart.getData().clear();
            chart.getData().add(newAxisData);
            changeColorsOfChart(color);
        }));

        // move charts up
        Timeline moveYdataIntoChart = new Timeline();
        moveYdataIntoChart.getKeyFrames().add(new KeyFrame(Duration.millis(30), (ActionEvent actionEvent) -> {
            for(int i = 0; i < axisData.getData().size(); i++) {
                Number cury = newAxisData.getData().get(i).getYValue();
                Number tary = axisData.getData().get(i).getYValue();
                if(cury.doubleValue() < tary.doubleValue()) {
                    newAxisData.getData().get(i).setYValue(cury.doubleValue() + (axisData.getData().get(i).getYValue().doubleValue() / (size)));
                } else if(cury.doubleValue() != axisData.getData().get(i).getYValue().doubleValue()){
                    newAxisData.getData().get(i).setYValue(axisData.getData().get(i).getYValue().doubleValue());

                }
            }
            chart.getData().clear();
            chart.getData().add(newAxisData);
            changeColorsOfChart(color);
        }));

        moveYdataIntoChart.setCycleCount((int) size);
        moveYdataIntoChart.setOnFinished(actionEvent -> {
            for(int i = 0; i < axisData.getData().size(); i++) {
                newAxisData.getData().get(i).setXValue(axisData.getData().get(i).getXValue());
                newAxisData.getData().get(i).setYValue(axisData.getData().get(i).getYValue());
            }
            chart.getData().clear();
            chart.getData().add(newAxisData);
            changeColorsOfChart(color);
        });

        moveXdataIntoChart.setCycleCount((int) size);
        moveXdataIntoChart.setOnFinished((actionEvent -> moveYdataIntoChart.play()));
        moveXdataIntoChart.play();
    }


    double getHoveredX(double mouseXPos) {
        double xAxisWidth = xAxis.getWidth();
        double xAxisTickUnit = xAxis.getTickUnit();
        double tickInPx = xAxisWidth/xAxisTickUnit;
        double numberOfTicks = xAxis.getUpperBound() / xAxisTickUnit;
        double currentCursorXPos;
        if (xAxis.getUpperBound() > 10) {
            currentCursorXPos = (int) (mouseXPos-72) / tickInPx*numberOfTicks;
        } else {
            currentCursorXPos = Layout.round((mouseXPos-72) / tickInPx*numberOfTicks);
        }
        ObservableList<Data<Number, Number>> chartData = chart.getData().get(0).getData();
        for(int i = 0; i < chartData.size(); i++) {
            if(Math.abs(chartData.get(i).getXValue().doubleValue() - currentCursorXPos) < 1) {
//                System.out.println(Layout.round(chartData.get(i).getXValue().doubleValue()) + ", " + Layout.round(chartData.get(i).getYValue().doubleValue()));
                return chartData.get(i).getXValue().doubleValue();
            }
        }
        return -1;
    }


    double getProperYFromMouse(double yAxisValue) {


        return 0;
    }

}
