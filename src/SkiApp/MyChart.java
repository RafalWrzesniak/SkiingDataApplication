package SkiApp;

import javafx.collections.ObservableList;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

class MyChart extends AreaChart{

    private NumberAxis xAxis;
    private NumberAxis yAxis;
    private boolean isDistType;
    AreaChart<Number, Number> chart;

    MyChart(NumberAxis xAxis, NumberAxis yAxis, boolean isDistType) {
        super(xAxis, yAxis);
        this.xAxis = xAxis;
        this.yAxis = yAxis;
        this.isDistType = isDistType;
        if(isDistType) {
            distChart();
        } else {
            timeChart();
        }
        commonSetups();
    }

    private void commonSetups() {

        xAxis.setAutoRanging(true);
        xAxis.setMinorTickVisible(false);

        yAxis.setMinorTickVisible(false);
        yAxis.setAutoRanging(false);

        chart = new AreaChart<>(xAxis, yAxis);
        chart.legendVisibleProperty().setValue(false);
        chart.createSymbolsProperty().setValue(false);
    }

    private void distChart() {
        xAxis.setLabel("Distance [km]");
        xAxis.setTickUnit(10);

        yAxis.setLabel("Altitude [m]");
    }

    private void timeChart() {
        xAxis.setLabel("Time [h]");
        xAxis.setTickUnit(2);
        xAxis.setUpperBound(10);

        yAxis.setLabel("Altitude [m]");

    }

    void loadData(ObservableList<Double> xData, ObservableList<Double> yData) {

        if(chart.getData().size() > 0) {
            for(int i = 0; i < chart.getData().size(); i++) {
                chart.getData().remove(i);
            }
        }

        AreaChart.Series<Number, Number> axisData = new AreaChart.Series<>();
        for(int i = 0; i < xData.size(); i++) {
            axisData.getData().add(new XYChart.Data<>(isDistType ? xData.get(i) : xData.get(i)/3600, yData.get(i)));
        }
        if(isDistType){
//            xAxis.setUpperBound(xData.get(xData.size()-1));
        } else {
//            xAxis.setUpperBound(Math.round(xData.get(xData.size()-1)/3600));
        }


        double yMax = 0, yMin = 10000;
        for (Double yDatum : yData) {
            if (yDatum > yMax) yMax = yDatum;
            if (yDatum < yMin) yMin = yDatum;
        }
        int scale = 500;
        if(yMax<200) {
            scale = 5;
        }
        for (int i = 0, j = scale*20; i < scale*20 && j > 0; i+=scale, j-= scale) {
            if(yMin > i) yAxis.setLowerBound(i);
            if(yMax < j) yAxis.setUpperBound(j);
        }

        yAxis.setTickUnit(500);
        chart.getData().add(axisData);
    }

    void setYaxisLabel(String name) {
        String yUnit = name.equals("Altitude") ? "[m]" : "[km/h]";
        this.yAxis.setLabel(String.join(" ", name, yUnit));
    }
}
