package RestaurantObjects;

import java.io.Serializable;

public class Order implements Serializable {

    private long ord_dish_id;
    private long menu_dish;

    public long id() {
        return ord_dish_id;
    }

    public long dishId() {
        return menu_dish;
    }
}
