/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.seasons;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import necesse.engine.GameLaunch;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.seasons.SeasonCrate;
import necesse.engine.seasons.SeasonalHat;
import necesse.engine.seasons.SeasonalHatLight;
import necesse.engine.seasons.SeasonalMobLootTable;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.inventory.InventoryItem;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.ConditionLootItemList;

public class GameSeasons {
    private static int month;
    private static int day;
    private static boolean aprilFools;
    private static boolean halloween;
    private static boolean christmas;
    private static boolean newYear;
    private static ArrayList<SeasonalHat> hats;
    private static ArrayList<SeasonCrate> crates;
    private static ArrayList<SeasonalMobLootTable> mobLoot;
    public static ArrayList<SeasonCrate> activeCrates;

    public static void loadSeasons() {
        boolean ignore;
        boolean bl = ignore = GameLaunch.launchOptions != null && GameLaunch.launchOptions.containsKey("ignoreseasons");
        if (ignore) {
            System.out.println("Ignoring seasonal content with -ignoreseasons launch option");
            aprilFools = false;
            halloween = false;
            christmas = false;
            newYear = false;
        } else {
            Calendar instance = Calendar.getInstance(Locale.ENGLISH);
            month = instance.get(2) + 1;
            day = instance.get(5);
            aprilFools = GameSeasons.isBetween(1, 4, 2, 4);
            halloween = GameSeasons.isBetween(18, 10, 7, 11);
            christmas = GameSeasons.isBetween(1, 12, 28, 12);
            newYear = GameSeasons.isBetween(28, 12, 7, 1);
        }
        GameSeasons.refreshActive();
    }

    public static void loadResources() {
        for (SeasonalHat hat : hats) {
            hat.loadTextures();
        }
        for (SeasonCrate crate : crates) {
            crate.loadTextures();
        }
    }

    public static void writeSeasons(PacketWriter writer) {
        writer.putNextBoolean(aprilFools);
        writer.putNextBoolean(halloween);
        writer.putNextBoolean(christmas);
        writer.putNextBoolean(newYear);
    }

    public static void readSeasons(PacketReader reader) {
        aprilFools = reader.getNextBoolean();
        halloween = reader.getNextBoolean();
        christmas = reader.getNextBoolean();
        newYear = reader.getNextBoolean();
        GameSeasons.refreshActive();
    }

    private static void refreshActive() {
        activeCrates.clear();
        for (SeasonCrate crate : crates) {
            if (!crate.isActive.get().booleanValue()) continue;
            activeCrates.add(crate);
        }
    }

    public static boolean isBetween(int startDate, int startMonth, int endDate, int endMonth) {
        boolean reversed;
        boolean bl = startMonth == endMonth ? endDate < startDate : (reversed = endMonth < startMonth);
        if (reversed) {
            if (month == startMonth && month == endMonth) {
                return day >= startDate || day <= endDate;
            }
            if (month == startMonth) {
                return day >= startDate;
            }
            if (month == endMonth) {
                return day <= endDate;
            }
            return month > startMonth || month < endMonth;
        }
        if (month == startMonth && month == endMonth) {
            return day >= startDate && day <= endDate;
        }
        if (month == startMonth) {
            return day >= startDate;
        }
        if (month == endMonth) {
            return day <= endDate;
        }
        return month > startMonth && month < endMonth;
    }

    public static boolean isAprilFools() {
        return aprilFools;
    }

    public static boolean isHalloween() {
        return halloween;
    }

    public static boolean isChristmas() {
        return christmas;
    }

    public static boolean isNewYear() {
        return newYear;
    }

    public static SeasonalHat getHat(GameRandom random) {
        for (SeasonalHat hat : hats) {
            if (!hat.isActive.get().booleanValue() || !random.getChance(hat.mobWearChance)) continue;
            return hat;
        }
        return null;
    }

    public static SeasonCrate getCrate(GameRandom random) {
        if (activeCrates.isEmpty()) {
            return null;
        }
        for (SeasonCrate crate : activeCrates) {
            if (!random.getChance(crate.crateChance)) continue;
            return crate;
        }
        return null;
    }

    public static void addMobDrops(Mob mob, ArrayList<InventoryItem> drops, GameRandom random, float lootMultiplier) {
        for (SeasonalMobLootTable loot : mobLoot) {
            if (!loot.isActive.get().booleanValue()) continue;
            loot.addDrops(mob, drops, random, lootMultiplier);
        }
    }

    static {
        hats = new ArrayList();
        crates = new ArrayList();
        mobLoot = new ArrayList();
        activeCrates = new ArrayList();
        hats.add(new SeasonalHatLight(GameSeasons::isHalloween, 0.16666667f, "pumpkinmask", 0.1f, "pumpkinmask"));
        hats.add(new SeasonalHat(GameSeasons::isChristmas, 0.16666667f, "christmashat", 0.1f, "christmashat"));
        hats.add(new SeasonalHat(GameSeasons::isNewYear, 0.16666667f, "partyhat", 0.1f, "partyhat"));
        crates.add(new SeasonCrate(GameSeasons::isChristmas, 0.2f, "christmascrates"));
        mobLoot.add(new SeasonalMobLootTable(GameSeasons::isChristmas, new LootTable(new ConditionLootItemList((gameRandom, objects) -> {
            Mob mob = LootTable.expectExtra(Mob.class, objects, 0);
            return mob != null && mob.isHostile && !mob.isSummoned;
        }, new ChanceLootItem(0.04f, "christmaspresent")))));
    }
}

