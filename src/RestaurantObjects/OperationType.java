package RestaurantObjects;

import java.io.Serializable;

public class OperationType implements Serializable {

    private long oper_type_id;
    private String oper_type_name;

    public long type() {
        return oper_type_id;
    }

    public String name() {
        return oper_type_name;
    }
}
