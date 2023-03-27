package RestaurantObjects;

import java.io.Serializable;
import java.util.ArrayList;

public class Operation implements Serializable {

    private long oper_type;
    private long equip_type;
    private double oper_time;
    private long oper_async_point;
    private ArrayList<OperProduct> oper_products;

    public long type() {
        return oper_type;
    }

    public double time() {
        return oper_time;
    }

    public long asyncPoint() {
        return oper_async_point;
    }

    public ArrayList<OperProduct> products() {
        return oper_products;
    }
}
