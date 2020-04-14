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
//                addMarker(new TrackPoint("50.299849", "21.343366", "2000", "2019-03-10T09:30:44Z"), "Moja ukochana");
//                addMarker(new TrackPoint("50.089306", "19.751844", "2000", "2019-03-10T09:30:44Z"), "Zwierzątko");
//                addCircle(new TrackPoint("50.299849", "21.343366", "2000", "2019-03-10T09:30:44Z"));
            }
        });
    }

    void addMarker(TrackPoint trackPoint, String name) {
        webEngine.executeScript("var "+ name.replace(' ', '_') + " = L.marker([" +  trackPoint.getLat() + ", " +  trackPoint.getLon() + "]).addTo(myMap);");
        webEngine.executeScript(name.replace(' ', '_') + ".bindPopup('<b>" + name + "</b>').openPopup()");
    }

    void addCircle(TrackPoint trackPoint) {
        webEngine.executeScript("var circle = L.circle([" + trackPoint.getLat() + ", " +  trackPoint.getLon() + "], {color: 'royalblue'," +
                " fillColor: 'royalblue', fillOpacity: 0.35, radius: 500}).addTo(myMap);");
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

    void createTrack(ObservableList<TrackPoint> trackPoints) {
        String polylineList = "[";
        for(TrackPoint trackPoint : trackPoints) {
            polylineList = polylineList.concat("[" + trackPoint.getLat() + ", " + trackPoint.getLon() + "]");
            if(trackPoints.indexOf(trackPoint) != trackPoints.size()-1) {
                polylineList = polylineList.concat(", ");
            } else {
                polylineList = polylineList.concat("]");
            }
        }

        webEngine.executeScript("var track = L.polyline([" + polylineList + "], " +
                "{color: 'royalblue', weight: 3}).addTo(myMap); ");
        webEngine.executeScript("myMap.fitBounds(track.getBounds());");

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
