package logic.nodes;

import main.Main;
import org.powbot.api.Condition;
import org.powbot.api.Input;
import org.powbot.api.Notifications;
import org.powbot.api.Random;
import org.powbot.api.rt4.*;

import java.util.Arrays;

import static main.Main.itemIDs;

public class CastAlchemy extends Node {


    public CastAlchemy(Main script) {
        super(script);
    }

    @Override
    public boolean validate() {
        return  !Magic.Spell.HIGH_ALCHEMY.casting() &&
                Inventory.stream().id(561).first().valid() &&
                Inventory.stream().id(itemIDs).first().valid();
    }

    @Override
    public void execute() {
        if (GrandExchange.opened()) {
            System.out.println("Closing ge");
            script.getLog().info("G.e is open when trying to alch, closing it");
            GrandExchange.close();
            Condition.wait(() -> !GrandExchange.opened(), 300, 5);
        } else {
            if(Magic.magicspell().casting() && !Magic.Spell.HIGH_ALCHEMY.casting()) {
                Input.tap(Widgets.widget(218).component(40).screenPoint());
                Condition.sleep(Random.nextInt(0,50));
            }
            if (!Magic.magicspell().casting()) {
                Condition.wait(() -> Game.tab() == Game.Tab.MAGIC, 200, 4);
                if (Game.tab() != Game.Tab.MAGIC) {
                    Game.tab(Game.Tab.MAGIC);
                }
                if (Game.tab() == Game.Tab.MAGIC) {
                    if (Widgets.widget(218).component(40).visible()) {
                        Condition.sleep(Random.nextInt(100,300));
                        Component x = Components.stream().filter(e -> e.name().contains("High Level Alchemy")).findFirst().orElse(null);
                        if(x != null){
                            x.click();
                        }
                        Condition.wait(() -> Game.tab() != Game.Tab.MAGIC, 100, 2);
                    }
                    Condition.sleep(Random.nextInt(100, 400));
                } else {
                    if (Game.tab() != Game.Tab.INVENTORY) {
                        System.out.println("Inventory didn't show up, failed to alch.");
                        Input.pressKey(0);
                    }
                }
            }
        }
    }
}
