package RestaurantObjects;

import java.io.Serializable;
import java.util.ArrayList;

public class Operation implements Serializable {

    public long oper_type;
    public long equip_type;
    public double oper_time;
    public long oper_async_point;
    public ArrayList<OperProduct> oper_products;
}
