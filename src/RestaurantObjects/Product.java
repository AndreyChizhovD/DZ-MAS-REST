package RestaurantObjects;

import java.io.Serializable;
import java.util.Date;

public class Product implements Serializable {

    private long prod_item_id;
    private long prod_item_type;
    private String prod_item_name;
    private String prod_item_company;
    private String prod_item_unit;
    private double prod_item_quantity;
    private double prod_item_cost;
    private Date prod_item_delivered;
    private Date prod_item_valid_until;

    public long id() {
        return prod_item_id;
    }

    public long type() {
        return prod_item_type;
    }

    public String name() {
        return prod_item_name;
    }

    public String company() {
        return prod_item_company;
    }

    public String unit() {
        return prod_item_unit;
    }

    public double quantity() {
        return prod_item_quantity;
    }

    public void reduce(double amount) {
        prod_item_quantity -= amount;
    }

    public double cost() {
        return prod_item_cost;
    }

    public Date delivered() {
        return prod_item_delivered;
    }

    public Date validUntil() {
        return prod_item_valid_until;
    }
}