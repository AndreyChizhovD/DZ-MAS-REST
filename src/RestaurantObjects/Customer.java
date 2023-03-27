package RestaurantObjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Customer implements Serializable {

    public String vis_name;
    public Date vis_ord_started;
    public Date vis_ord_ended;
    public long vis_ord_total;
    public ArrayList<Order> vis_ord_dishes;
}
