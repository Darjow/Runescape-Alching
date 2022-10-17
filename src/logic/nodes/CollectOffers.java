package logic.nodes;

import main.Main;
import org.powbot.api.Condition;
import org.powbot.api.Notifications;
import org.powbot.api.Random;
import org.powbot.api.rt4.Component;
import org.powbot.api.rt4.Components;
import org.powbot.api.rt4.GrandExchange;
import org.powbot.mobile.script.ScriptManager;

public class CollectOffers extends Node{

    public static int incorrect = 0;

    public CollectOffers(Main script) {
        super(script);
    }

    @Override
    public boolean validate() {
        if (GrandExchange.opened()) {
            Component collectWidget = Components.stream().action("Collect to inventory").first();
            if (collectWidget.valid() && collectWidget.visible()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void execute() {
        System.out.println("Collecting offers");

        if(Components.stream().action("Collect to inventory").first().click("Collect to inventory")) {
            script.getLog().info("Collected items from Grand Exchange");
            Condition.wait(() -> !Components.stream().action("Collect to inventory").first().visible(), 100,5);
            Condition.sleep(Random.nextInt(100,500));
            incorrect = 0;
        }else if(incorrect++ > 3) {
            script.getLog().info("ERROR: Something went wrong, please contact the sexy dev.");
            ScriptManager.INSTANCE.stop();
        }
        Condition.sleep(Random.nextInt(100,300));

    }
}
