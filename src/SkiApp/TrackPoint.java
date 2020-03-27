package SkiApp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;


public class TrackPoint {

   private double lat;
    private double lon;
    private int alt;
    private LocalDate date;
    private LocalTime time;

    public TrackPoint(String lat, String lon, String alt, String dateTime) {
        this.lat = Double.parseDouble(lat);
        this.lon = Double.parseDouble(lon);
        this.alt = (int) Math.round(Double.parseDouble(alt));
        setDate(trimInputZ(dateTime));
        setTime(trimInputZ(dateTime));
    }

    private String trimInputZ (String dateTime) {
        if(dateTime.endsWith("Z")){
            return dateTime.substring(0, dateTime.length()-1);
        } else {
            return dateTime;
        }
    }

    private void setDate(String dateTime) {
        this.date = LocalDateTime.parse(dateTime).toLocalDate();
    }

    private void setTime(String dateTime) {
        this.time = LocalDateTime.parse(dateTime).toLocalTime();
    }

    public void printTrackPoint() {
        System.out.println(String.join(": ", "DateTime", String.join(" ", getDate().toString(), getTime().toString())));
        System.out.println(String.join(": ", "Position", String.join(", ", String.valueOf(getLat()), String.valueOf(getLon()))));
        System.out.println(String.join(": ", "Altitude", String.valueOf(getAlt())));
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public int getAlt() {
        return alt;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
        return time;
    }
}


