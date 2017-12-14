package rnjt.com.myride.model;

/**
 * Created by admin on 14-12-2017.
 */
public class CustomArray {
    String price;
    String CarModel;
    long timestamp;

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCarModel() {
        return CarModel;
    }

    public void setCarModel(String carModel) {
        CarModel = carModel;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
