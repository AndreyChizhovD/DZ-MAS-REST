package setup;

import RestaurantAgents.CookAgent;
import RestaurantAgents.CustomerAgent;
import RestaurantAgents.EquipmentAgent;
import RestaurantAgents.GeneralAgent;
import RestaurantAgents.WarehouseAgent;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import java.text.MessageFormat;
import java.util.UUID;
import java.util.logging.Logger;

public class RestaurantLauncher {

    protected static ContainerController containerController;

    public RestaurantLauncher(ContainerController currentContainer) {
        containerController = currentContainer;
    }

    public static String addAgent(Class<?> agentClass, String nickname, Object[] args) {
        try {
            AgentController new_agent = containerController.createNewAgent(
                MessageFormat.format("{0}-{1}-{2}", agentClass.getSimpleName(), nickname,
                    UUID.randomUUID()), agentClass.getName(), args);
            new_agent.start();
            return new_agent.getName();
        } catch (StaleProxyException ex) {
            ex.printStackTrace();
        }
        return "{}-{}-{}";
    }

    public void start() {
        addAgent(GeneralAgent.class, "", new Object[]{});
    }

    public static void createCustomers(Logger logger) {
        JsonArray orders = Parser.readAsJson("visitors_orders.json")
            .getAsJsonArray("visitors_orders");
        for (var order : orders) {
            var name = ((JsonObject) order).get("vis_name").getAsString();
            logger.info("Customer with name" + name);
            Restaurant.customers.put(name,
                RestaurantLauncher.addAgent(CustomerAgent.class, name, new Object[]{order}));
        }
    }

    public static void createWarehouse(Logger logger) {
        RestaurantLauncher.addAgent(WarehouseAgent.class, "",
            new Object[]{Parser.read("product_types.json"), Parser.read("products.json"),});
    }

    public static void createEquipment(Logger logger) {
        var equipments = Parser.readAsJson("equipment_type.json").getAsJsonArray("equipment_type");
        for (JsonElement equipment : equipments) {
            String name = ((JsonObject) equipment).get("equip_type_name").getAsString();
            logger.info("Added equipment with name " + name);
            Restaurant.equipments.put(name,
                RestaurantLauncher.addAgent(EquipmentAgent.class, name, new Object[]{equipment}));
        }
    }

    public static void createCooks(Logger log) {
        var cooks = Parser.readAsJson("cookers.json").getAsJsonArray("cookers");
        for (var cook : cooks) {
            String cookerName = ((JsonObject) cook).get("cook_name").getAsString();
            log.info(String.format("Added cook whose name is ", cookerName));
            Restaurant.cookers.put(cookerName,
                RestaurantLauncher.addAgent(CookAgent.class, cookerName, new Object[]{cook}));
        }
    }

    public static void createDishes(Logger logger) {
        JsonArray dishes = Parser.readAsJson("dish_cards.json").getAsJsonArray("dish_cards");
        for (var dish : dishes) {
            var dishName = ((JsonObject) dish).get("dish_name").getAsString();
            Restaurant.dishes.put(dishName, dish.getAsJsonObject());
        }
    }
}
