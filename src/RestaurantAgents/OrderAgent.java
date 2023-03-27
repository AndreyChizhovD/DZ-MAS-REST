package RestaurantAgents;

import com.google.gson.JsonObject;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.logging.Logger;

import setup.Parser;
import setup.Restaurant;
import setup.RestaurantLauncher;

import Templates.AgentType;
import Templates.MessageType;


public class OrderAgent extends Agent {

    private Logger logger;
    protected AID aid;
    private MessageTemplate messageTemplate;

    @Override
    protected void setup() {
        aid = getAID();
        logger = Logger.getLogger(this.getClass().getName());
        logger.info("New order was made " + aid.getLocalName());
        DFAgentDescription agentDescription = new DFAgentDescription();
        agentDescription.setName(aid);
        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType(AgentType.ORDER);
        serviceDescription.setName(this.getName());
        agentDescription.addServices(serviceDescription);
        try {
            DFService.register(this, agentDescription);
        } catch (FIPAException ex) {
            ex.printStackTrace();
        }
        var args = getArguments();

        var order = Parser.parse(args[0].toString()).getAsJsonArray("vis_ord_dishes");
        for (var dish : order) {
            var id = ((JsonObject) dish).get("ord_dish_id").getAsString();
            logger.info("Dish " + id + " added to order");
            AID new_aid = new AID(
                RestaurantLauncher.addAgent(OperationAgent.class, id, new Object[]{dish}));
            Restaurant.processes.put(id, new_aid);
        }

        addBehaviour(new Behaviour() {
            private int stage = 0;
            private double estimatedTime;
            ACLMessage cfpMessage;

            @Override
            public void action() {
                switch (stage) {
                    case 0:
                        cfpMessage = new ACLMessage(ACLMessage.CFP);
                        cfpMessage.addReceiver(WarehouseAgent.aid);
                        cfpMessage.setConversationId(MessageType.ORDER_TIME);
                        cfpMessage.setContent(args[0].toString());
                        cfpMessage.setReplyWith(System.currentTimeMillis() + "");
                        myAgent.send(cfpMessage);
                        messageTemplate = MessageTemplate.and(
                            MessageTemplate.MatchConversationId(MessageType.ORDER_TIME),
                            MessageTemplate.MatchInReplyTo(cfpMessage.getReplyWith()));

                        logger.info("Dish products");
                        ++stage;
                        break;
                    case 1:
                        ACLMessage reply = myAgent.receive(messageTemplate);
                        if (reply != null) {
                            if (reply.getContent().contentEquals(MessageType.AVAILABLE)) {
                                logger.info("There are enough products in the warehouse");
                                ++stage;
                            } else {
                                logger.info("There are NOT enough products in the warehouse");
                                stage += 3;
                            }
                        } else {
                            block();
                        }
                        break;
                    case 2:
                        cfpMessage = new ACLMessage(ACLMessage.CFP);
                        for (var process : Restaurant.processes.values()) {
                            cfpMessage.addReceiver(process);
                        }
                        cfpMessage.setConversationId(MessageType.ORDER_TIME);
                        cfpMessage.setReplyWith("" + System.currentTimeMillis());
                        myAgent.send(cfpMessage);
                        messageTemplate = MessageTemplate.and(
                            MessageTemplate.MatchConversationId(MessageType.ORDER_TIME),
                            MessageTemplate.MatchInReplyTo(cfpMessage.getReplyWith()));
                        logger.info("Estimated time");
                        stage++;
                        break;
                    case 3:
                        reply = myAgent.receive(messageTemplate);
                        if (reply != null) {
                            var time = reply.getContent();
                            logger.info("Process " + reply.getSender().getLocalName() +
                                " will take " + time + " minutes");
                            estimatedTime = Math.max(estimatedTime, Double.parseDouble(time));
                        } else {
                            block();
                        }
                        break;
                }
            }

            @Override
            public boolean done() {

                return stage > 3;
            }
        });
    }

    @Override
    protected void takeDown() {
            logger.info("Order agent " + aid.getName() + " was terminated");

    }
}
