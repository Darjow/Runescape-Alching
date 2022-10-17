package logic;

import main.Main;
import org.powbot.api.Condition;
import org.powbot.api.Random;
import org.powbot.api.rt4.*;

import java.util.List;
import java.util.stream.Collectors;

public class GrandExchangeHandler
{

    Main mainScript;

    public GrandExchangeHandler(Main _script)
    {
        mainScript = _script;
    }

    public void purchaseAlchItems()
    {
        while(!GrandExchange.opened()){
            GrandExchange.open();
            Condition.wait(() -> GrandExchange.opened(),300,10);
        }
        boolean toReturn = cancelOffers();
        if(toReturn)
            return;


        Alchable toPurchase = getNextMostProfitable();
        int numCoins = Inventory.stream().id(995).first().stackSize() - 130000;
        mainScript.getLog().info("Purchasing " + toPurchase.getName() + " potential profit = "
                + (toPurchase.getPotentialProfit() * toPurchase.getBuyLimit()) + " Already bought: "
                + toPurchase.getNumberBought() + "/" + toPurchase.getRealLimit() + " remaining  "
                + toPurchase.getRealLimit() + " current gold in inventory " + numCoins);

        int needToBuy = toPurchase.getBuyLimit();
        if (mainScript.alchManager.AIR_BATTLESTAFF.getId() != toPurchase.getId()){
            AlchableManager.getitems().remove(toPurchase.getId());
            toPurchase.setLastBought(System.currentTimeMillis());
            toPurchase.setNumberBought(toPurchase.getNumberBought() + needToBuy);
            mainScript.getLog().info("Set number of bought for item " + toPurchase.getName() + " to " + toPurchase.getNumberBought()
                    + " buy limit remaining " + toPurchase.getBuyLimit());
            AlchableManager.getitems().put(toPurchase.getId(),toPurchase);
        }

        GrandExchangeItem offer = GrandExchangeItem.Companion.fromName(toPurchase.getName());
        GrandExchange.createOffer(offer, needToBuy,GrandExchangePrices.getPrice(toPurchase.getId()), true);

        Condition.wait(() -> orderProgressed(toPurchase), 1000, 30);
        if(orderProgressed(toPurchase)) {
            mainScript.getLog().info("Order made progress, will wait another minute");
            Condition.wait(() -> orderSuccess(toPurchase), 1000, 60);
        }
        if (!orderSuccess(toPurchase))
        {
            mainScript.getLog().info("Buy failed for: " + toPurchase.getName() + ", at price: " + toPurchase.getTradedPrice()
                    + "gp, aborting all offers");
            cancelOffers();
            AlchableManager.setTimestamp(toPurchase.getId(), System.currentTimeMillis());
        }

    }

    private boolean cancelOffers()
    {
        List<GeSlot> offers = GrandExchange.allSlots();

        boolean toReturn = false;

        for (GeSlot o : offers)
        {
            if (o.itemName().length() > 1) {
                mainScript.getLog().info("Attempting to cancel offer: " + o.itemName());
                if (GrandExchange.abortOffer(o)) {
                    Condition.wait(() -> o.isAborted(), 500, 5);
                    toReturn = true;
                }else{
                    System.out.println("Failed to cancel offer");
                }
            }
            Condition.sleep(Random.nextInt(20,30));
        }

        return toReturn;
    }

    public static boolean checkCollect()
    {
        Component x = Components.stream().action("Collect to inventory").findFirst().orElse(null);
        return x != null && x.visible();
    }
    private boolean orderProgressed(Alchable i){
        List<GeSlot> offers = GrandExchange.allSlots().stream().filter(e -> !e.isAvailable()).collect(Collectors.toList());

        for (GeSlot o : offers)
        {
            if (o.itemId() == i.getId() - 1 && (o.isFinished() || o.collectionSlot().progress() > 0)) {
                System.out.println("Order is finished or made progress");
                return true;
            }
            Condition.sleep(Random.nextInt(10,50));
        }
        return false;
    }



    private boolean orderSuccess(Alchable i)
    {
        List<GeSlot> offers = GrandExchange.allSlots().stream().filter(e -> !e.isAvailable()).collect(Collectors.toList());

        for (GeSlot o : offers)
        {
            if (o.itemId() == i.getId() - 1 && o.isFinished()) {
                System.out.println("Order is finished, we are collecting it");
                return true;
            }
            Condition.sleep(Random.nextInt(10,50));
        }

        return false;
    }

    private Alchable getNextMostProfitable()
    {

        Alchable currentMostProfit = mainScript.alchManager.AIR_BATTLESTAFF;
        long currentProfit = currentMostProfit.getPotentialProfit();
        for (Alchable obj : AlchableManager.getitems().values())
        {
            if (obj.getPotentialProfit() >= currentProfit && canBuy(obj))
            {
                currentProfit = obj.getPotentialProfit();
                currentMostProfit = obj;
            }

        }

        if ((currentProfit * currentMostProfit.getBuyLimit()) <= 100)
            return mainScript.alchManager.AIR_BATTLESTAFF;


        return currentMostProfit;
    }

    private boolean canBuy(Alchable x) {
        return AlchableManager.isValid(x);
    }

    public void purchase(int i) {
        if (!GrandExchange.opened()) {
            System.out.println("Opening the GE");
            GrandExchange.open();
            Condition.wait(() -> GrandExchange.opened(), 300, 15);
        }
        if (GrandExchange.opened()) {
            GrandExchangeItem nature = GrandExchangeItem.Companion.fromName("Nature rune");
            nature.setId(561);
            System.out.println("Making offer for item: " + nature.getName());
            GrandExchange.createOffer(nature, 500, 250, true);
            Condition.wait(() -> checkCollect(), 500, 1000);
        }
    }
}

