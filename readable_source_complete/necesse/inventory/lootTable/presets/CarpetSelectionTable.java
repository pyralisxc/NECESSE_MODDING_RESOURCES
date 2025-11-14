/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import java.util.HashMap;
import java.util.Map;
import necesse.engine.util.GameRandom;
import necesse.engine.util.TicketSystemList;

public class CarpetSelectionTable {
    public static final HashMap<String, Integer> carpets = new HashMap();

    public static String getRandomCarpetID(GameRandom random) {
        if (!carpets.isEmpty()) {
            TicketSystemList ticketList = new TicketSystemList();
            for (Map.Entry<String, Integer> entry : carpets.entrySet()) {
                ticketList.addObject((int)entry.getValue(), entry.getKey());
            }
            return (String)ticketList.getRandomObject(random);
        }
        return "woolcarpet";
    }

    public static String getRandomCarpetIDFromSpecificHashmap(GameRandom random, HashMap<String, Integer> hashMap) {
        if (!hashMap.isEmpty()) {
            TicketSystemList ticketList = new TicketSystemList();
            for (Map.Entry<String, Integer> entry : hashMap.entrySet()) {
                ticketList.addObject((int)entry.getValue(), entry.getKey());
            }
            return (String)ticketList.getRandomObject(random);
        }
        return "velourcarpet";
    }

    static {
        carpets.put("woolcarpet", 100);
        carpets.put("leathercarpet", 100);
        carpets.put("brownbearcarpet", 100);
        carpets.put("bluecarpet", 100);
        carpets.put("goldgridcarpet", 100);
        carpets.put("greencarpet", 100);
        carpets.put("steelgreycarpet", 100);
        carpets.put("purplecarpet", 80);
        carpets.put("velourcarpet", 80);
        carpets.put("heartcarpet", 80);
        carpets.put("redyarncarpet", 60);
    }
}

