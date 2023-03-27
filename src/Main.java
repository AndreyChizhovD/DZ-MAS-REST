import RestaurantAgents.GeneralAgent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.StaleProxyException;

import setup.RestaurantLauncher;

public class Main {

    public static void main(String[] args) {
        final Runtime runtime = Runtime.instance();
        final Profile profile = new ProfileImpl();

        profile.setParameter(Profile.MAIN_HOST, "localhost");
        profile.setParameter(Profile.MAIN_PORT, "8080");
        profile.setParameter(Profile.GUI, "true");
        var mainContainer = runtime.createMainContainer(profile);

        RestaurantLauncher restaurantLauncher = new RestaurantLauncher(mainContainer);
        restaurantLauncher.createNewAgent(GeneralAgent.class, "", new Object[]{});

        try {
            mainContainer.kill();
        } catch (StaleProxyException e) {
            throw new RuntimeException(e);
        }
    }
}
