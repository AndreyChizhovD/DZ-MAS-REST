package RestaurantAgents;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.logging.Logger;
import Templates.AgentType;
import Templates.MessageType;
import setup.Restaurant;


public class WarehouseAgent extends Agent {

    private Restaurant restaurant;
    private Logger logger;
    protected static AID aid;

    @Override
    protected void setup() {
        restaurant = new Restaurant();
        aid = getAID();
        logger = Logger.getLogger(aid.getLocalName());
        logger.info("Warehouse is ready");
        DFAgentDescription agentDescription = new DFAgentDescription();
        agentDescription.setName(aid);
        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType(AgentType.WAREHOUSE);
        serviceDescription.setName(this.getName());
        agentDescription.addServices(serviceDescription);
        try {
            DFService.register(this, agentDescription);
        } catch (FIPAException ex) {
            ex.printStackTrace();
        }
        var args = getArguments();

        var types = ((JsonObject) new JsonParser().parse(args[0].toString())).getAsJsonArray(
            "product_types");
        for (var type : types) {
            String prod_type_id = type.getAsJsonObject().get("prod_type_id").getAsString();
            restaurant.productTypes.put(prod_type_id, type.getAsJsonObject());
        }
        logger.info(types.size() + " product types are in warehouse now");

        var prods = ((JsonObject) new JsonParser().parse(args[1].toString())).getAsJsonArray(
            "products");
        for (var prod : prods) {
            String productId = prod.getAsJsonObject().get("prod_item_type").getAsString();
            restaurant.products.put(productId, prod.getAsJsonObject());
        }
        logger.info(prods.size() + " are in warehouse now");

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                Restaurant.receipts = new HashMap<>();
                var messageTemplate = MessageTemplate.MatchPerformative(ACLMessage.CFP);
                var msg = myAgent.receive(messageTemplate);

                // filling receipts
                var menu = parse(read("menu_dishes.json")).get("menu_dishes").getAsJsonArray();
                var cards = parse(read("dish_cards.json")).get("dish_cards").getAsJsonArray();
                for (var dish : menu) {
                    for (var card : cards) {
                        var dish_card = dish.getAsJsonObject().get("menu_dish_card").getAsLong();
                        var id = card.getAsJsonObject().get("card_id").getAsLong();
                        if (dish_card == id) {
                            Restaurant.receipts.put(
                                dish.getAsJsonObject().get("menu_dish_id").getAsString(),
                                card.getAsJsonObject().get("operations").getAsJsonArray());
                            break;
                        }
                    }
                }
                if (msg != null) {
                    var content = msg.getContent();
                    JsonArray dishes = ((JsonObject) new JsonParser().parse(
                        content)).getAsJsonArray("vis_ord_dishes");
                    var reply = msg.createReply();
                    reply.setPerformative(ACLMessage.PROPOSE);
                    var reply_content =
                        isDishInMenu(dishes) ? MessageType.AVAILABLE : MessageType.NOT_AVAILABLE;
                    if (isDishInMenu(dishes)) {
                        removeDish(dishes);
                    }
                    reply.setContent(reply_content);
                    myAgent.send(reply);
                } else {
                    block();
                }
            }
        });
    }


    private boolean isDishInMenu(JsonArray menu) {
        for (var dish : menu) {
            var operations = Restaurant.receipts.get(
                String.valueOf(dish.getAsJsonObject().get("menu_dish").getAsLong()));
            for (var operation : operations) {
                var products = operation.getAsJsonObject().get("oper_products").getAsJsonArray();
                for (var product : products) {
                    double current = product.getAsJsonObject().get("prod_quantity").getAsDouble();
                    double available = Restaurant.products.get(
                            product.getAsJsonObject().get("prod_type").getAsString())
                        .get("prod_item_quantity").getAsDouble();
                    if (current > available) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void removeDish(JsonArray menu) {
        for (var dish : menu) {
            var operations = Restaurant.receipts.get(
                String.valueOf(dish.getAsJsonObject().get("menu_dish").getAsLong()));
            for (var operation : operations) {
                var products = operation.getAsJsonObject().get("oper_products").getAsJsonArray();
                for (var product : products) {
                    var current = Restaurant.products.get(
                        product.getAsJsonObject().get("prod_type").getAsString());
                    double quantity = current.get("prod_item_quantity").getAsDouble();
                    quantity -= product.getAsJsonObject().get("prod_quantity").getAsDouble();
                    current.addProperty("prod_item_quantity", String.valueOf(quantity));
                    logger.info("Amount of " + current.get("prod_item_name") + " is now " +
                        current.get("prod_item_quantity"));
                }
            }
        }
    }

    private static String read(String name) {
        try {
            return Files.readString(Path.of("src", "InputFiles").resolve(name));
        } catch (IOException e) {
            System.out.println("Cant read from json");
        }
        return "";
    }

    private static JsonObject parse(String content) {
        return (JsonObject) new JsonParser().parse(content);
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
            logger.info("Warehouse terminated");
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }
}
