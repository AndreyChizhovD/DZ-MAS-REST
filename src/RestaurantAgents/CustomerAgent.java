package RestaurantAgents;

import Templates.AgentType;
import Templates.MessageType;

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

public class CustomerAgent extends Agent {

    private Logger log;
    protected AID aid;
    private MessageTemplate messageTemplate;

    @Override
    protected void setup() {
        aid = getAID();
        log = Logger.getLogger(this.getClass().getName());
        log.info("New visitor came in : " + this.getAID().getLocalName() + "!");
        try {
            DFAgentDescription agentDescription = new DFAgentDescription();
            agentDescription.setName(aid);
            ServiceDescription serviceDescription = new ServiceDescription();
            serviceDescription.setType(AgentType.CUSTOMER);
            serviceDescription.setName(this.getName());
            agentDescription.addServices(serviceDescription);
            DFService.register(this, agentDescription);
        } catch (FIPAException ex) {
            ex.printStackTrace();
        }
        var args = getArguments();

        addBehaviour(new Behaviour() {
            private int step = 0;

            @Override
            public void action() {
                switch (step) {
                    case 0:
                        var msg = new ACLMessage(ACLMessage.CFP);
                        msg.addReceiver(GeneralAgent.aid);
                        msg.setConversationId(MessageType.ORDER_ID);
                        msg.setReplyWith(System.currentTimeMillis() + "");
                        myAgent.send(msg);
                        log.info("Visitor asked for menu ");
                        step++;
                        break;
                    case 1:
                        var reply = myAgent.receive(messageTemplate);
                        if (reply != null) {
                            String order_request = args[0].toString();
                            var order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                            order.setConversationId(MessageType.ORDER_ID);
                            order.addReceiver(GeneralAgent.aid);
                            order.setContent(order_request);
                            order.setReplyWith(System.currentTimeMillis() + MessageType.ORDER_ID);
                            log.info(order_request);
                            myAgent.send(order);
                            step++;
                        } else {
                            block();
                        }
                        break;
                    case 2:
                        messageTemplate = MessageTemplate.MatchPerformative(ACLMessage.CFP);
                        var message = myAgent.receive(messageTemplate);
                        if (message != null) {
                            log.info(message.getContent() + " is estimated time for "
                                + message.getSender()
                                .getName());
                            step++;
                        } else {
                            block();
                        }
                        break;
                }
            }

            @Override
            public boolean done() {
                return step > 2;
            }
        });
    }

    @Override
    protected void takeDown() {
        log.info("Customer agent " + getAID().getLocalName() + " succesfully terminated");
    }
}
