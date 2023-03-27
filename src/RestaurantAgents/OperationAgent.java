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
import java.util.Random;
import java.util.logging.Logger;

public class OperationAgent extends Agent {

    private Logger log;
    public static AID aid;

    @Override
    protected void setup() {
        aid = getAID();
        log = Logger.getLogger(this.getClass().getName());
        log.info("Operation agent created" + aid.getLocalName() + " started");
        try {
            DFAgentDescription agentDescription = new DFAgentDescription();
            agentDescription.setName(aid);
            ServiceDescription serviceDescription = new ServiceDescription();
            serviceDescription.setType(AgentType.OPERATION);
            serviceDescription.setName(this.getName());
            agentDescription.addServices(serviceDescription);
            DFService.register(this, agentDescription);
        } catch (FIPAException ex) {
            ex.printStackTrace();
        }
        var args = getArguments();
        addBehaviour(new OperationBehaviour());
    }

    public class OperationBehaviour extends CyclicBehaviour {

        @Override
        public void action() {
            MessageTemplate messageTemplate = MessageTemplate.MatchPerformative(ACLMessage.CFP);
            ACLMessage msg = myAgent.receive(messageTemplate);
            if (msg != null) {
                ACLMessage reply = msg.createReply();
                reply.setPerformative(ACLMessage.PROPOSE);
                reply.setContent(String.valueOf(new Random().nextInt(10)));
                myAgent.send(reply);
            } else {
                block();
            }
        }
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
            log.info("Operation terminated " + getAID().getName());
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }
}
