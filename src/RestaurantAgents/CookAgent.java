package RestaurantAgents;

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
import Templates.MessageType;

public class CookAgent extends Agent {

    protected AID aid;
    private Logger logger;
    private boolean isActive = false;


    @Override
    protected void setup() {
        aid = getAID();
        logger = Logger.getLogger(this.getClass().getName());
        logger.info(String.format("New Cook Agent: " + aid.getLocalName()));
        DFAgentDescription agentDescription = new DFAgentDescription();
        agentDescription.setName(aid);
        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType(AgentType.COOK);
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
                    if (content == MessageType.USED) {
                        if (isActive) {
                            logger.info(aid.getLocalName() + "is unavailable now");
                        } else {
                            isActive = true;
                        }
                    } else {
                        isActive = false;
                    }

                    var reply = msg.createReply();
                    reply.setPerformative(ACLMessage.INFORM);
                    // send(reply);
                }
            }
        });
    }

    @Override
    protected void takeDown() {
        logger.info("Cook : " + getAID().getName() + " terminated");
    }
}
