package RestaurantObjects;

import java.io.Serializable;
import java.util.Date;

public class Product implements Serializable {

    public long prod_item_id;
    public long prod_item_type;
    public String prod_item_name;
    public String prod_item_company;
    public String prod_item_unit;
    public double prod_item_quantity;
    public double prod_item_cost;
    public Date prod_item_delivered;
    public Date prod_item_valid_until;
}