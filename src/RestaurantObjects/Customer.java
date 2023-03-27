package RestaurantObjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Customer implements Serializable {

    private String vis_name;
    private Date vis_ord_started;
    private Date vis_ord_ended;
    private long vis_ord_total;
    private ArrayList<Order> vis_ord_dishes;

    public String name() {
        return vis_name;
    }

    public Date startTime() {
        return vis_ord_started;
    }

    public Date endTime() {
        return vis_ord_ended;
    }

    public long total() {
        return vis_ord_total;
    }

    public ArrayList<Order> dishes() {
        return vis_ord_dishes;
    }
}
