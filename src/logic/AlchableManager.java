package logic;

import java.util.HashMap;

public class AlchableManager {
    public static HashMap<Integer, Alchable> items = new HashMap<>();
    public static HashMap<Alchable, Long> timeFailed = new HashMap<>();

    static Alchable RUNE_PLATELEGS = new Alchable("Rune platelegs", 70, 38400, 1080,0);
    static Alchable DRAGON_LONGSWORD = new Alchable("Dragon longsword", 70, 60000, 1306,0);
    static Alchable RUNE_PLATEBODY = new Alchable("Rune platebody", 70, 39000, 1128,0);
    static Alchable DRAGON_DAGGER = new Alchable("Dragon dagger", 70, 18000, 1216,0);
    static Alchable ADAMANT_PLATELEGS = new Alchable("Adamant platelegs", 125, 3840, 1074,0);
    static Alchable RUNE_FULL_HELM = new Alchable("Rune full helm", 70, 21120, 1164,0);
    static Alchable RUNE_KITESHIELD = new Alchable("Rune kiteshield", 70, 32640, 1202,0);
    static Alchable BLUE_DHIDE_BODY = new Alchable("Blue d'hide body", 125, 5616, 2500,0);
    static Alchable RUNE_2H_SWORD = new Alchable("Rune 2h sword", 70, 38400, 1320,0);
    static Alchable RUNE_PLATESKIRT = new Alchable("Rune plateskirt", 70, 38400, 1094,0);
    static Alchable RUNE_MACE = new Alchable("Rune mace", 70, 8640, 1433,0);
    static Alchable RUNE_SCIMITAR = new Alchable("Rune scimitar", 70, 15360, 1334,0);
    static Alchable RUNE_CHAINBODY = new Alchable("Rune chainbody", 70, 30000, 1114,0);
    static Alchable BLACK_DHIDE_BODY = new Alchable("Black d'hide body", 70, 8088, 2504,0);
    static Alchable RUNE_AXE = new Alchable("Rune axe", 40, 7680, 1360,0);
    static Alchable RUNE_CROSSBOW = new Alchable("Rune crossbow", 70, 9720, 9186,0);
    static Alchable RED_DHIDE_VAMB = new Alchable("Red d'hide vambraces", 70, 2160, 2490,0);
    static Alchable RUNE_PICKAXE = new Alchable("Rune pickaxe", 40, 19200, 1276,0);
    static Alchable RUNE_DAGGER = new Alchable("Rune dagger", 70, 4800, 1214,0);
    static Alchable RED_DHIDE_CHAPS = new Alchable("Red d'hide chaps", 70, 3108, 2496,0);
    static Alchable BLACK_DHIDE_VAMB = new Alchable("Black d'hide vambraces", 70, 2592, 2492,0);
    static Alchable ADAMANT_PLATEBODY = new Alchable("Adamant platebody", 125, 9984, 1124,0);
    static Alchable REDWOOD_SHIELD = new Alchable("Redwood shield", 125, 768, 22267,0);
    static Alchable GREEN_DHIDE_BODY = new Alchable("Green d'hide body", 125, 4680, 1136,0);
    static Alchable RUNE_MED_HELM = new Alchable("Rune med helm", 70, 11520, 1148,0);
    static Alchable MITHRIL_PICKAXE = new Alchable("Mithril pickaxe", 40, 780, 1274,0);
    static Alchable AIR_BATTLESTAFF = new Alchable("Air battlestaff", 18000, 9300, 1398,0);
    static Alchable ONYX_BOLTS_E= new Alchable("Onyx bolts (e)", 11000, 9000, 9245,0);

    public static HashMap<Integer, Alchable> getitems() {
        if (items.isEmpty())
        {
            additems();
        }
        return items;
    }
    public static void setAlchablePrice(int id, int price){
        Alchable item = getItem(id);
        item.setTradedPrice(price);
        items.put(item.getId(),item);
    }

    public static boolean isValid(Alchable x) {
        if (items.isEmpty()){
            additems();
        }
        return System.currentTimeMillis() - getItem(x.getId()).getFailedTimeStamp() > 300000;

    }
    private static void additems(){
        items.put(1398, AIR_BATTLESTAFF);
        items.put(1148,RUNE_MED_HELM);
        items.put(1274,MITHRIL_PICKAXE);
        items.put(1080,RUNE_PLATELEGS);
        items.put(1306,DRAGON_LONGSWORD);
        items.put(1128,RUNE_PLATEBODY);
        items.put(1216,DRAGON_DAGGER);
        items.put(1074,ADAMANT_PLATELEGS);
        items.put(1164,RUNE_FULL_HELM);
        items.put(1202,RUNE_KITESHIELD);
        items.put(2500,BLUE_DHIDE_BODY);
        items.put(1320,RUNE_2H_SWORD);
        items.put(1094, RUNE_PLATESKIRT);
        items.put(1433,RUNE_MACE);
        items.put(1334,RUNE_SCIMITAR);
        items.put(1114,RUNE_CHAINBODY);
        items.put(2504,BLACK_DHIDE_BODY);
        items.put(1360,RUNE_AXE);
        items.put(9186,RUNE_CROSSBOW);
        items.put(2490,RED_DHIDE_VAMB);
        items.put(1276,RUNE_PICKAXE);
        items.put(1214,RUNE_DAGGER);
        items.put(2496,RED_DHIDE_CHAPS);
        items.put(2492,BLACK_DHIDE_VAMB);
        items.put(1124,ADAMANT_PLATEBODY);
        items.put(22267,REDWOOD_SHIELD);
        items.put(1136,GREEN_DHIDE_BODY);
        items.put(9245,ONYX_BOLTS_E);
    }

    public static void setTimestamp(int id, long currentTimeMillis) {
        getItem(id).setFailedTimeStamp(currentTimeMillis);
    }
    public static Alchable getItem(int id){
        Alchable x = null;
        if(getitems().containsKey(id)) {
            x = getitems().get(id);
        }else if(getitems().containsKey(id+1)){
            x = getitems().get(id + 1);
        }else if(getitems().containsKey(id-1)){
            x = getitems().get( id - 1);
        }else{
           System.out.println("FAILED to find item with id: " + id);
        }

        return x;
    }

    public static void clear() {
        getitems().entrySet().stream().forEach(e -> e.getValue().setTradedPrice(0));
    }
}
