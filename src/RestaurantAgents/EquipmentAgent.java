package RestaurantAgents;

import static java.lang.Thread.sleep;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

import java.util.logging.Logger;

import Templates.AgentType;

public class EquipmentAgent extends Agent {

    protected AID aid;
    private Logger logger;
    private boolean isActive = false;

    @Override
    protected void setup() {
        aid = getAID();
        logger = Logger.getLogger(this.getClass().getName());
        logger.info(String.format("Creating Cook Agent: " + aid.getLocalName()));
        DFAgentDescription agentDescription = new DFAgentDescription();
        agentDescription.setName(aid);
        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType(AgentType.EQUIPMENT);
        serviceDescription.setName(this.getName());
        agentDescription.addServices(serviceDescription);
        try {
            DFService.register(this, agentDescription);
        } catch (FIPAException ex) {
            ex.printStackTrace();
        }

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                var msg = myAgent.receive();
                if (msg != null) {
                    var content = msg.getContent();
                    try {
                        sleep((long) Double.parseDouble(content) * 100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    ACLMessage reply = msg.createReply();
                    reply.setPerformative(ACLMessage.INFORM);
                    // send(reply);
                }
            }
        });
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
            logger.info("Equipment " + getAID().getName() + " terminated");
        } catch (FIPAException e) {
            System.out.println(e.getMessage());
        }
    }
}
