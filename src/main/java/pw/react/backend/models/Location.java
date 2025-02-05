package pw.react.backend.models;

public class Location {
    public String id;
    public String fullAddress;
    public double latitude;
    public double longitude;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getFullAddress() { return fullAddress; }
    public void setFullAddress(String fullAddress) { this.fullAddress = fullAddress; }
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
}
