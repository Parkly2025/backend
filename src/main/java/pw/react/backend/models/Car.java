package pw.react.backend.models;

public class Car {
    public String id;
    public Model model;
    public Location location;
    public String imageUrl;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Model getModel() { return model; }
    public void setModel(Model model) { this.model = model; }
    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
