package logic.nodes;

import logic.GrandExchangeHandler;
import main.Main;
import org.powbot.api.Condition;
import org.powbot.api.Input;
import org.powbot.api.Notifications;
import org.powbot.api.Random;
import org.powbot.api.rt4.*;

public class BuyNatures extends Node {

    public BuyNatures(Main script) {
        super(script);
    }
    @Override
    public boolean validate() {

        return !Inventory.stream().id(561).first().valid() &&
                !GrandExchangeHandler.checkCollect();
    }

    @Override
    public void execute() {
        System.out.println("We need to buy nature runes");

        if (Magic.magicspell().casting()) {
            script.getLog().severe("We were in need of nature runes but were still casting");
            Input.pressKey(0);
        }

        if(Npcs.stream().name("Grand Exchange Clerk").first().tile().distanceTo(Players.local().tile()) > 5){
            System.out.println("Walking back to ge");
            Movement.builder(Npcs.stream().name("Grand Exchange Clerk").first().tile()).move();
        }else {
            script.getGeHandler().purchase(561);
            Condition.sleep(Random.nextInt(400, 1000));
        }
    }
}
