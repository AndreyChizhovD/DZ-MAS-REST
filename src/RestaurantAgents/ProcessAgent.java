package RestaurantAgents;

import setup.Restaurant;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

import java.util.logging.Logger;

import setup.RestaurantLauncher;

import Templates.AgentType;

public class ProcessAgent extends Agent {

    private Logger logger;
    protected static AID aid;

    @Override
    protected void setup() {
        aid = getAID();
        logger = Logger.getLogger(this.getClass().getName());
        logger.info("Created process " + aid.getName());
        DFAgentDescription agentDescription = new DFAgentDescription();
        agentDescription.setName(aid);
        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType(AgentType.PROCESS);
        serviceDescription.setName(this.getName());
        agentDescription.addServices(serviceDescription);
        try {
            DFService.register(this, agentDescription);
        } catch (FIPAException ex) {
            ex.printStackTrace();
        }

        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                for (var key : Restaurant.dishes.keySet()) {
                    if (getAgent().getAID().getName().contains(key)) {
                        for (var operation : Restaurant.dishes.get(key).get("operations")
                            .getAsJsonArray()) {
                            RestaurantLauncher.createNewAgent(OperationAgent.class,
                                operation.getAsJsonObject().get("equip_type").toString(),
                                new Object[]{});
                        }
                        break;
                    }
                }
            }
        });
    }

    @Override
    protected void takeDown() {
            logger.info("Process " + getAID().getName() + " terminated");
    }
}

