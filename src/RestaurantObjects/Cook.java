package RestaurantObjects;

import java.io.Serializable;

public class Cook implements Serializable {

    private long cook_id;
    private String cook_name;
    private boolean cook_active;

    public long id() {
        return cook_id;
    }

    public String name() {
        return cook_name;
    }

    public boolean isActive() {
        return cook_active;
    }

    public void setActive(boolean value) {
        cook_active = value;
    }
}
