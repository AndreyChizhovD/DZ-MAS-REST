package RestaurantAgents;

import Templates.AgentType;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.logging.Logger;

import setup.Parser;
import setup.RestaurantLauncher;

public class GeneralAgent extends Agent {

    private Logger logger;
    public static AID aid;

    @Override
    protected void setup() {
        logger = Logger.getLogger(this.getClass().getName());
        logger.info("GENERAL MANAGER AGENT CREATED");
        aid = getAID();
        try {
            DFAgentDescription agentDescription = new DFAgentDescription();
            agentDescription.setName(aid);
            ServiceDescription serviceDescription = new ServiceDescription();
            serviceDescription.setType(AgentType.RUNNER);
            serviceDescription.setName(this.getName());
            agentDescription.addServices(serviceDescription);
            DFService.register(this, agentDescription);
        } catch (FIPAException ex) {
            ex.printStackTrace();
        }

        createRestaurantAgents();

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                var messageTemplate = MessageTemplate.MatchPerformative(ACLMessage.CFP);
                var msg = myAgent.receive(messageTemplate);
                if (msg != null) {
                    ACLMessage reply = msg.createReply();
                    reply.setPerformative(ACLMessage.PROPOSE);
                    reply.setContent(Parser.read("menu_dishes.json"));
                    myAgent.send(reply);
                } else {
                    block();
                }
            }
        });
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                var messageTemplate = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
                var msg = myAgent.receive(messageTemplate);
                if (msg != null) {
                    String order = msg.getContent();
                    ACLMessage reply = msg.createReply();
                    reply.setPerformative(ACLMessage.INFORM);
                    myAgent.send(reply);
                    String id = Parser.parse(order).get("vis_name").getAsString();
                    RestaurantLauncher.addAgent(OrderAgent.class, id,
                        new Object[]{order, msg.getSender().getName()});
                    logger.info("Received order : " + order);
                } else {
                    block();
                }
            }
        });
    }

    private void createRestaurantAgents() {
        RestaurantLauncher.createCustomers(logger);
        RestaurantLauncher.createWarehouse(logger);
        RestaurantLauncher.createEquipment(logger);
        RestaurantLauncher.createCooks(logger);
        RestaurantLauncher.createDishes(logger);
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
            logger.info("RUNNER TERMINATED");
        } catch (FIPAException exc) {
            exc.printStackTrace();
        }
    }
}
