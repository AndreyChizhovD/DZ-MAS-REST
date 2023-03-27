package RestaurantObjects;

import java.io.Serializable;

public class ProductType implements Serializable {

    private long prod_type_id;
    private String prod_type_name;
    private boolean prod_is_food;

    public long id() {
        return prod_type_id;
    }

    public String name() {
        return prod_type_name;
    }

    public boolean isFood() {
        return prod_is_food;
    }
}
