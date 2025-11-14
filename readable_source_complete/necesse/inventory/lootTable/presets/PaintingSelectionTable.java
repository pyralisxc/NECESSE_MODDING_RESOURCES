/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable.presets;

import java.util.HashMap;
import java.util.Map;
import necesse.engine.util.GameRandom;
import necesse.engine.util.TicketSystemList;

public class PaintingSelectionTable {
    public static final HashMap<String, Integer> epicPaintings = new HashMap();
    public static final HashMap<String, Integer> rarePaintings = new HashMap();
    public static final HashMap<String, Integer> largeRarePaintings = new HashMap();
    public static final HashMap<String, Integer> uncommonPaintings = new HashMap();
    public static final HashMap<String, Integer> commonPaintings = new HashMap();
    public static final HashMap<String, Integer> abandonedMinePaintings = new HashMap();

    public static String getRandomEpicPaintingIDBasedOnWeight(GameRandom random) {
        return PaintingSelectionTable.getRandomPaintingIDFromHashMapBasedOnWeight(epicPaintings, random);
    }

    public static String getRandomLargeRarePaintingIDBasedOnWeight(GameRandom random) {
        return PaintingSelectionTable.getRandomPaintingIDFromHashMapBasedOnWeight(largeRarePaintings, random);
    }

    public static String getRandomRarePaintingIDBasedOnWeight(GameRandom random) {
        return PaintingSelectionTable.getRandomPaintingIDFromHashMapBasedOnWeight(rarePaintings, random);
    }

    public static String getRandomUncommonPaintingIDBasedOnWeight(GameRandom random) {
        return PaintingSelectionTable.getRandomPaintingIDFromHashMapBasedOnWeight(uncommonPaintings, random);
    }

    public static String getRandomCommonPaintingIDBasedOnWeight(GameRandom random) {
        return PaintingSelectionTable.getRandomPaintingIDFromHashMapBasedOnWeight(commonPaintings, random);
    }

    public static String getRandomPaintingIDFromHashMapBasedOnWeight(HashMap<String, Integer> paintings, GameRandom random) {
        if (!paintings.isEmpty()) {
            TicketSystemList ticketList = new TicketSystemList();
            for (Map.Entry<String, Integer> entry : paintings.entrySet()) {
                ticketList.addObject((int)entry.getValue(), entry.getKey());
            }
            return (String)ticketList.getRandomObject(random);
        }
        return "paintingapple";
    }

    static {
        epicPaintings.put("paintingcooljonas", 100);
        epicPaintings.put("paintingelder", 100);
        largeRarePaintings.put("paintinglargeworldmap", 100);
        largeRarePaintings.put("paintinglargeship", 100);
        largeRarePaintings.put("paintinglargecastle", 100);
        largeRarePaintings.put("paintinglargeabstract", 100);
        rarePaintings.put("paintingstonecaveling", 100);
        rarePaintings.put("paintingsnowcaveling", 100);
        rarePaintings.put("paintingswampcaveling", 100);
        rarePaintings.put("paintingsandstonecaveling", 100);
        uncommonPaintings.put("paintingheart", 100);
        uncommonPaintings.put("paintingdagger", 100);
        uncommonPaintings.put("paintingeye", 100);
        uncommonPaintings.put("paintingmouse", 100);
        uncommonPaintings.put("paintingparrot", 100);
        uncommonPaintings.put("paintingduck", 100);
        uncommonPaintings.put("paintingcastle", 100);
        commonPaintings.put("paintingapple", 100);
        commonPaintings.put("paintingavocado", 100);
        commonPaintings.put("paintingbanana", 100);
        commonPaintings.put("paintingabstract", 100);
        commonPaintings.put("paintingrainsun", 100);
        abandonedMinePaintings.put("paintingcastle", 20);
        abandonedMinePaintings.put("paintingparrot", 20);
        abandonedMinePaintings.put("paintingeye", 20);
        abandonedMinePaintings.put("paintingdagger", 20);
        abandonedMinePaintings.put("paintingstonecaveling", 20);
        abandonedMinePaintings.put("paintingbroken", 250);
    }
}

