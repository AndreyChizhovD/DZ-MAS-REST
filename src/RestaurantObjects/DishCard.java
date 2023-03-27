package RestaurantObjects;

import java.io.Serializable;
import java.util.ArrayList;

public class DishCard implements Serializable {

    public long card_id;
    public String dish_name;
    public String card_descr;
    public double card_time;
    ArrayList<Operation> operations;
}
