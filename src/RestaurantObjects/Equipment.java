package RestaurantObjects;

import java.io.Serializable;

public class Equipment implements Serializable {

    private long equip_id;
    private long equip_type;
    private String equip_name;
    private boolean equip_active;

    public boolean isActive() {
        return equip_active;
    }

    public long type() {
        return equip_type;
    }

    public long id() {
        return equip_id;
    }

    public void setActive(boolean val) {
        equip_active = val;
    }

    public String name() {
        return equip_name;
    }
}
