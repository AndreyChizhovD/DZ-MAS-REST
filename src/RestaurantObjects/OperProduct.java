package RestaurantObjects;

import java.io.Serializable;

public class OperProduct implements Serializable {

    private long prod_type;
    private double prod_quantity;

    public long type() {
        return prod_type;
    }

    public double quantity() {
        return prod_quantity;
    }
}
