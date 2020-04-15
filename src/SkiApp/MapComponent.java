package SkiApp;

import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;


class MapComponent extends StackPane {

    private final WebView webView = new WebView();
    private final WebEngine webEngine = webView.getEngine();


    MapComponent() {
        webEngine.load("file:///E:/Java_projects/SkiingData/resources/leafletmap/map.html");
        webEngine.setJavaScriptEnabled(true);
        getChildren().add(webView);
        webEngine.getLoadWorker().stateProperty().addListener((observableValue, state, t1) -> {
            if(state == Worker.State.RUNNING) {
                webEngine.executeScript("var myMap = L.map('map').setView([50.194579, 20.547605], 9);");
                webEngine.executeScript("L.tileLayer('http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {attribution: 'SkiApp by Rafał Wrześniak',}).addTo(myMap)");
            }
        });
    }

    void addMarker(TrackPoint trackPoint, String name, String name2, String name3) {
        webEngine.executeScript("var "+ name2.replace(' ', '_') + " = L.marker([" +  trackPoint.getLat() + ", " +  trackPoint.getLon() + "]).addTo(myMap);");
        if(name2.contains("Max altitude")) name = "" + name + " [m]";
        webEngine.executeScript(name2.replace(' ', '_') + ".bindPopup('<b><center>" + name + "</center></b>" + name3 + "<br>" + name2 + "')");
    }

    void addCircle(TrackPoint trackPoint, String color) {
        webEngine.executeScript("var circle = L.circle([" + trackPoint.getLat() + ", " +  trackPoint.getLon() + "], " +
                "{color: '" + color + "', fillColor: 'white', fillOpacity: 0.8, radius: 120}).addTo(myMap);");
    }

    void moveCircle(TrackPoint trackPoint) {
        webEngine.executeScript("circle.setLatLng([" + trackPoint.getLat() + ", " + trackPoint.getLon() + "])");
    }


    void removeObject(String objectName) {
        webEngine.executeScript("myMap.removeLayer(" + objectName + ");");
    }

    void setView(double lat, double lon, int zoomLevel) throws IllegalArgumentException {
        if(zoomLevel < -1 || zoomLevel > 19) throw new IllegalArgumentException("Zoom level should be in range 0-19. Use -1 to use default");
        if(zoomLevel == -1) zoomLevel = 9;
        webEngine.executeScript("myMap.setView([" + lat + ", " + lon + "], " + zoomLevel + ");");
    }

    void createTrack(ObservableList<TrackPoint> trackPoints, String color) {
        String polylineList = "[";
        for(TrackPoint trackPoint : trackPoints) {
            polylineList = polylineList.concat("[" + trackPoint.getLat() + ", " + trackPoint.getLon() + "]");
            if(trackPoints.indexOf(trackPoint) != trackPoints.size()-1) {
                polylineList = polylineList.concat(", ");
            } else {
                polylineList = polylineList.concat("]");
            }
        }
        String name = "track_" + trackPoints.get(0).getDate().toString();
        name = name.replace('-', '_');

        webEngine.executeScript("var " + name + " = L.polyline([" + polylineList + "], " +
                "{color: '" + color + "', weight: 3}).addTo(myMap); ");
        webEngine.executeScript("myMap.fitBounds(" + name + ".getBounds());");

    }

    void clearAll() {
        webEngine.executeScript(
                "for (i in myMap._layers) {" +
                   "   if (myMap._layers[i] instanceof L.Marker || myMap._layers[i] instanceof L.Path) {" +
                   "       myMap.removeLayer(myMap._layers[i]);" +
                   "   }" +
                   "}");
    }

}
