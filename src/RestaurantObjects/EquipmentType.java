package RestaurantObjects;

import java.io.Serializable;

public class EquipmentType implements Serializable {

    private long equip_type_id;
    private String equip_type_name;

    public long id() {
        return equip_type_id;
    }

    public String name() {
        return equip_type_name;
    }
}
