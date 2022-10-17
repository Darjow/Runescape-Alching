package logic.nodes;

import logic.GrandExchangeHandler;
import logic.GrandExchangePrices;
import main.Main;
import org.powbot.api.Condition;
import org.powbot.api.Input;
import org.powbot.api.Notifications;
import org.powbot.api.Random;
import org.powbot.api.rt4.*;

import static main.Main.itemIDs;

public class BuyAlchables extends Node {
    public BuyAlchables(Main script) {
        super(script);
    }

    @Override
    public boolean validate() {

        return !Inventory.stream().id(itemIDs).first().valid() &&
                !GrandExchangeHandler.checkCollect() &&
                GrandExchangePrices.succesfullyInitialized;

    }

    @Override
    public void execute() {
        if (Magic.magicspell().casting()) {
            script.getLog().severe("We were in need of alchables but were still casting");
            Input.pressKey(0);
        }
        if(Npcs.stream().name("Grand Exchange Clerk").first().tile().distanceTo(Players.local().tile()) > 5){
            System.out.println("Walking back to ge");
            Movement.builder(Npcs.stream().name("Grand Exchange Clerk").first().tile()).move();
        }
        script.getGeHandler().purchaseAlchItems();
        Condition.sleep(Random.nextInt(400, 1000));

    }
}
