package logic.nodes;

import logic.Alchable;
import logic.AlchableManager;
import logic.GrandExchangePrices;
import main.Main;
import org.powbot.api.Condition;
import org.powbot.api.Input;
import org.powbot.api.Notifications;
import org.powbot.api.Random;
import org.powbot.api.rt4.Game;
import org.powbot.api.rt4.Inventory;
import org.powbot.api.rt4.Item;
import org.powbot.api.rt4.Magic;

import java.util.Optional;

import static main.Main.itemIDs;

public class AlchItem extends Node {

    public AlchItem(Main script) {
        super(script);
    }

    @Override
    public boolean validate() {
        return Magic.Spell.HIGH_ALCHEMY.casting();

    }

    @Override
    public void execute() {
        int previousItemCount = Inventory.stream().id(itemIDs).first().stackSize();
        Item first = Inventory.stream().id(itemIDs).findFirst().orElse(null);
        if (first == null) {
            System.out.println("We have no items in inventory to alch ?");
            Condition.sleep(Random.nextInt(100, 200));
        } else {
            if (first.click()) {
                Condition.wait(() -> Inventory.stream().id(itemIDs).count() < previousItemCount, 300, 2);
                if(Inventory.stream().id(itemIDs).count() < previousItemCount) {
                    Alchable x = AlchableManager.getItem(first.id());
                    if(x == null){
                        System.out.println("Here is where previous bug happened");
                    }else {
                        script.profit += AlchableManager.getItem(first.id()).getProfit();
                    }
                }
                Condition.wait(() -> Game.tab() == Game.Tab.MAGIC, 200,4);
            }
        }
    }
}


