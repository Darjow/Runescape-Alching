package main;

import com.github.tnakamot.json.parser.JSONParserException;
import logic.Alchable;
import logic.AlchableManager;
import logic.GrandExchangeHandler;
import logic.GrandExchangePrices;
import logic.nodes.*;

import org.powbot.api.Condition;
import org.powbot.api.rt4.*;
import org.powbot.api.rt4.walking.model.Skill;
import org.powbot.api.script.AbstractScript;
import org.powbot.api.script.ScriptCategory;
import org.powbot.api.script.ScriptManifest;
import org.powbot.api.script.ScriptState;
import org.powbot.api.script.paint.Paint;
import org.powbot.api.script.paint.PaintBuilder;
import org.powbot.mobile.script.ScriptManager;
import org.powbot.mobile.service.ScriptUploader;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.regex.Pattern;


@ScriptManifest(name = "Alcher", description = "Start at Grand exchange", version = "1.0.0", category = ScriptCategory.MoneyMaking, author = "Shock")
public class Main extends AbstractScript {

    @SuppressWarnings("resource")
    public static void main(String[] args) {
        new ScriptUploader().uploadAndStart("Alcher", "X", "localhost:56901", true, true);
    }

    public static int[] itemIDs;


    private GrandExchangeHandler geHandler;
    public AlchableManager alchManager;
    private final List<Node> nodes = new ArrayList<>();
    private String branch = "Initializing";
    long startingInventoryPrice = -1;
    long currentInventoryPrice = -1;

    public long profit = 0;

    long timeStarted = System.currentTimeMillis();


    public double getProfitPerhour() {
        return profit == 0 ? 0 : round(profit / ((System.currentTimeMillis() - timeStarted) / 3600000.0D), 2);
    }

    public long getInventoryValue() {
        int coins = Inventory.stream().id(995).first().stackSize();
        int items = 0;

        for (int id : itemIDs) {
            if (Inventory.stream().id(id).first().valid()) {
                GrandExchangeItem item = GrandExchangeItem.Companion.fromName(AlchableManager.getItem(id).getName());
                if (item.getGuidePrice() > 1) {
                    items += item.getGuidePrice() * Inventory.stream().id(id).first().stackSize();
                }
            } else if (Inventory.stream().id(id).first().valid())
                return -1;
        }

        return coins + items;
    }

    private void buildPaint() {
        Paint paint = PaintBuilder.newBuilder()
                .x(50)
                .y(50)
                .trackSkill(Skill.Magic)
                .addString("Profit:", () -> String.valueOf(profit))
                .addString("Profit per hour: ", () -> String.valueOf(getProfitPerhour()))
                .addString("Branch: ", () -> String.valueOf(branch))
                .addString("Collect is visible: ", () -> String.valueOf(GrandExchangeHandler.checkCollect()))
                .withoutDiscordWebhook()
                .build();
        addPaint(paint);
    }


    @Override
    public void onStart() {
        setNodes();
        buildPaint();
        failsafes();
        Game.setSingleTapToggle(false);

        start();
        super.onStart();
    }

    private void failsafes() {
        geHandler = new GrandExchangeHandler(this);
        timeStarted = System.currentTimeMillis();

        int i = 0;
        itemIDs = new int[AlchableManager.getitems().size() * 2];
        for (Alchable obj : AlchableManager.getitems().values()) {
            itemIDs[i++] = obj.getId();
            itemIDs[i++] = obj.getId() - 1;
        }
        if (startingInventoryPrice == -1)
            startingInventoryPrice = getInventoryValue();
        currentInventoryPrice = getInventoryValue();
    }

    private void setNodes() {
        BuyNatures t1 = new BuyNatures(this);
        BuyAlchables t2 = new BuyAlchables(this);
        CollectOffers t3 = new CollectOffers(this);
        CastAlchemy t4 = new CastAlchemy(this);
        AlchItem t5 = new AlchItem(this);

        nodes.addAll(Arrays.asList(
                t1,
                t2,
                t3,
                t4,
                t5
        ));


    }

    private void start() {
        Thread thread = new Thread("itemPrices") {
            @Override
            public void run() {
                while (ScriptManager.INSTANCE.state() == ScriptState.Running) {
                    GrandExchangePrices.setItemPrices();
                }
            }
        };
        thread.start();
    }

    ;


    public static double round(double value, int places) {
        if (places < 0)
            throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value).setScale(places, RoundingMode.HALF_UP).stripTrailingZeros();
        return bd.doubleValue();
    }

    @Override
    public void poll() {

        for (Node n : nodes) {
            if (n.validate()) {
                branch = n.status();
                n.execute();
            }
        }
    }
    public GrandExchangeHandler getGeHandler() {
        return geHandler;
    }
}

