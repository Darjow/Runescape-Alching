package logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.github.tnakamot.json.JSONText;
import com.github.tnakamot.json.parser.JSONParserException;
import com.github.tnakamot.json.value.JSONValue;
import com.github.tnakamot.json.value.JSONValueObjectImmutable;
import com.github.tnakamot.json.value.JSONValueType;
import org.powbot.api.rt4.GrandExchange;
import org.powbot.api.rt4.GrandExchangeItem;

import static main.Main.itemIDs;

public class GrandExchangePrices {


    public static boolean succesfullyInitialized = false;


        public static void setItemPrices(){
            int i = 0;
            for(int id: itemIDs){
                if(i % 2 == 0) {
                    Alchable a = AlchableManager.getItem(id);
                    GrandExchangeItem item = GrandExchangeItem.Companion.fromName(a.getName());
                    if (item.initLivePrices()) {
                        int price = item.getHigh() + (int)(item.getHigh() * 0.006) + 1;
                        int previousPrice = getPrice(a.getId());
                        if (price != previousPrice) {
                            AlchableManager.setAlchablePrice(a.getId(), price);
                            System.out.println("Updated traded price of " + a.getName() + " from: " + previousPrice +" to " + price);
                        }
                    } else {
                        System.out.println("Failed to find price of item: " + AlchableManager.getItem(id).getName());
                        AlchableManager.setAlchablePrice(a.getId(), -1);
                    }
                }
                i++;
            }
            succesfullyInitialized = true;
        }

    public static Integer getPrice(int id) {
        return AlchableManager.getItem(id) == null? -1 : AlchableManager.getItem(id).getTradedPrice();
    }

    public static void setItemPrices(int[] itemIDs) throws IOException, JSONParserException {
        AlchableManager.clear();
        String priceString = getPriceData();
        JSONText priceText = JSONText.fromString(priceString);
        JSONValueObjectImmutable priceRoot = (JSONValueObjectImmutable) priceText.parse().root();
        JSONValueObjectImmutable priceData = (JSONValueObjectImmutable) priceRoot.get("data");

        for (int id : itemIDs) {
            JSONValue priceValue = priceData.get("" + (id - 1));
            if (priceValue == null) {
                continue;
            }

            JSONValueObjectImmutable priceItemRoot = (JSONValueObjectImmutable) JSONText
                    .fromString(priceValue.toTokenString()).parse().root();

            if (priceItemRoot == null) {
                continue;
            }
            int priceHigh = -1;
            int priceLow = -1;

            if (!priceItemRoot.get("high").type().equals(JSONValueType.NULL))
                priceHigh = (int) priceItemRoot.getLong("high");

            if (!priceItemRoot.get("low").type().equals(JSONValueType.NULL))
                priceLow = (int) priceItemRoot.getLong("low");

            AlchableManager.setAlchablePrice(id, Math.max(priceHigh, priceLow));
        }
    }

/*
        public int getPrice(int id)
        {
            if (temPrices.containsKey((short) id))
                return itemPrices.get((short) id);

            return -1;//no price
        }
*/
    private static String getPriceData() throws IOException {
        System.setProperty("http.agent", "Item price tracker for item flips1");
        URL url = new URL("https://prices.runescape.wiki/api/v1/osrs/latest");
        URLConnection con = url.openConnection();
        InputStream is = con.getInputStream();

        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        String line = null;

        String parsedData = "";
        while ((line = br.readLine()) != null) {

            parsedData += line;

        }
        return parsedData;
    }

}

