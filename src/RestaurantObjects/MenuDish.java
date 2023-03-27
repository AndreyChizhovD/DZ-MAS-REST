package RestaurantObjects;

import java.io.Serializable;

public class MenuDish implements Serializable {

    private long menu_dish_id;
    private long menu_dish_card;
    private long menu_dish_price;
    private boolean menu_dish_active;

    public long id() {
        return menu_dish_id;
    }

    public long cardId() {
        return menu_dish_card;
    }

    public long price() {
        return menu_dish_price;
    }

    public boolean isActive() {
        return menu_dish_active;
    }

    public void setInactive() {
        menu_dish_active = false;
    }
}

