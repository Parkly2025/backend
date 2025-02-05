package pw.react.backend.models;

public class Model {
    public String id;
    public String brandName;
    public String name;
    public int productionYear;
    public String fuelType;
    public long fuelCapacity;
    public long seatCount;
    public long doorCount;
    public double dailyRate;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public long getProductionYear() { return productionYear; }
    public void setProductionYear(int productionYear) { this.productionYear = productionYear; }
    public String getFuelType() { return fuelType; }
    public void setFuelType(String fuelType) { this.fuelType = fuelType; }
    public long getFuelCapacity() { return fuelCapacity; }
    public void setFuelCapacity(long fuelCapacity) { this.fuelCapacity = fuelCapacity; }
    public long getSeatCount() { return seatCount; }
    public void setSeatCount(long seatCount) { this.seatCount = seatCount; }
    public long getDoorCount() { return doorCount; }
    public void setDoorCount(long doorCount) { this.doorCount = doorCount; }
    public double getDailyRate() { return dailyRate; }
    public void setDailyRate(double dailyRate) { this.dailyRate = dailyRate; }
}
