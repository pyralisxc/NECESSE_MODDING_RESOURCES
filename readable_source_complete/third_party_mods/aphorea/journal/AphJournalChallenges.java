/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.journal.ItemObtainedJournalChallenge
 *  necesse.engine.journal.JournalChallenge
 *  necesse.engine.journal.MobsKilledJournalChallenge
 *  necesse.engine.journal.MultiJournalChallenge
 *  necesse.engine.journal.PickupItemsJournalChallenge
 *  necesse.engine.registries.JournalChallengeRegistry
 *  necesse.inventory.lootTable.LootItemInterface
 *  necesse.inventory.lootTable.LootTable
 *  necesse.inventory.lootTable.lootItem.LootItem
 *  necesse.inventory.lootTable.lootItem.LootItemList
 */
package aphorea.journal;

import aphorea.journal.ActivateBabylonTowerJournalChallenge;
import aphorea.journal.KillGelSlimesSurfaceForestJournalChallenge;
import aphorea.journal.KillUnstableGelSlimeJournalChallenge;
import necesse.engine.journal.ItemObtainedJournalChallenge;
import necesse.engine.journal.JournalChallenge;
import necesse.engine.journal.MobsKilledJournalChallenge;
import necesse.engine.journal.MultiJournalChallenge;
import necesse.engine.journal.PickupItemsJournalChallenge;
import necesse.engine.registries.JournalChallengeRegistry;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.LootItemList;

public class AphJournalChallenges {
    public static LootTable APH_FOREST_SURFACE_REWARD = new LootTable(new LootItemInterface[]{new LootItemList(new LootItemInterface[]{new LootItem("sapphirebackpack")}).setCustomListName("item", "sapphirebackpack")});
    public static int APH_FOREST_SURFACE_CHALLENGES_ID;
    public static int KILL_GEL_SLIMES_ID;
    public static int PICKUP_FLORAL_RING_ID;
    public static int KILL_UNSTABLE_GEL_SLIME_ID;
    public static LootTable APH_SWAMP_SURFACE_REWARD;
    public static int APH_SWAMP_SURFACE_CHALLENGES_ID;
    public static int KILL_PINK_WITCHES_ID;
    public static LootTable INFECTED_SURFACE_REWARD;
    public static int INFECTED_SURFACE_CHALLENGES_ID;
    public static int PICKUP_INFECTED_LOGS_ID;
    public static LootTable INFECTED_CAVE_REWARD;
    public static int INFECTED_CAVE_CHALLENGES_ID;
    public static int PICKUP_SPINEL_ID;
    public static int ACTIVATE_BABYLON_TOWER_ID;
    public static int FIND_THE_SPAMMER_ID;

    public static void registerCore() {
        KILL_GEL_SLIMES_ID = AphJournalChallenges.registerChallenge("killgelslimesforest", (JournalChallenge)new KillGelSlimesSurfaceForestJournalChallenge());
        PICKUP_FLORAL_RING_ID = AphJournalChallenges.registerChallenge("pickupfloralring", (JournalChallenge)new PickupItemsJournalChallenge(1, true, new String[]{"floralring"}));
        KILL_UNSTABLE_GEL_SLIME_ID = AphJournalChallenges.registerChallenge("killunstablegelslime", (JournalChallenge)new KillUnstableGelSlimeJournalChallenge());
        APH_FOREST_SURFACE_CHALLENGES_ID = AphJournalChallenges.registerChallenge("aphoreaforestsurface", (JournalChallenge)new MultiJournalChallenge(new Integer[]{KILL_GEL_SLIMES_ID, PICKUP_FLORAL_RING_ID, KILL_UNSTABLE_GEL_SLIME_ID}).setReward(APH_FOREST_SURFACE_REWARD));
        KILL_PINK_WITCHES_ID = AphJournalChallenges.registerChallenge("killpinkwitchesswamp", (JournalChallenge)new MobsKilledJournalChallenge(5, new String[]{"pinkwitch"}));
        APH_SWAMP_SURFACE_CHALLENGES_ID = AphJournalChallenges.registerChallenge("aphoreaswampsurface", (JournalChallenge)new MultiJournalChallenge(new Integer[]{KILL_PINK_WITCHES_ID}).setReward(APH_SWAMP_SURFACE_REWARD));
        PICKUP_INFECTED_LOGS_ID = AphJournalChallenges.registerChallenge("pickupinfectedlogs", (JournalChallenge)new PickupItemsJournalChallenge(100, true, new String[]{"infectedlog"}));
        INFECTED_SURFACE_CHALLENGES_ID = AphJournalChallenges.registerChallenge("infectedfieldssurface", (JournalChallenge)new MultiJournalChallenge(new Integer[]{PICKUP_INFECTED_LOGS_ID}).setReward(INFECTED_SURFACE_REWARD));
        PICKUP_SPINEL_ID = AphJournalChallenges.registerChallenge("pickupspinel", (JournalChallenge)new PickupItemsJournalChallenge(40, true, new String[]{"spinel"}));
        ACTIVATE_BABYLON_TOWER_ID = AphJournalChallenges.registerChallenge("activatebabylontower", (JournalChallenge)new ActivateBabylonTowerJournalChallenge());
        FIND_THE_SPAMMER_ID = AphJournalChallenges.registerChallenge("findthespammer", (JournalChallenge)new ItemObtainedJournalChallenge(new String[]{"thespammer"}));
        INFECTED_CAVE_CHALLENGES_ID = AphJournalChallenges.registerChallenge("infectedfieldscave", (JournalChallenge)new MultiJournalChallenge(new Integer[]{PICKUP_SPINEL_ID, ACTIVATE_BABYLON_TOWER_ID, FIND_THE_SPAMMER_ID}).setReward(INFECTED_CAVE_REWARD));
    }

    public static int registerChallenge(String stringID, JournalChallenge journalChallenge) {
        return JournalChallengeRegistry.registerChallenge((String)stringID, (JournalChallenge)journalChallenge);
    }

    static {
        APH_SWAMP_SURFACE_REWARD = new LootTable(new LootItemInterface[]{new LootItemList(new LootItemInterface[]{new LootItem("amethystbackpack")}).setCustomListName("item", "amethystbackpack")});
        INFECTED_SURFACE_REWARD = new LootTable(new LootItemInterface[]{new LootItemList(new LootItemInterface[]{new LootItem("rubybackpack")}).setCustomListName("item", "rubybackpack")});
        INFECTED_CAVE_REWARD = new LootTable(new LootItemInterface[]{new LootItemList(new LootItemInterface[]{new LootItem("diamondbackpack")}).setCustomListName("item", "diamondbackpack")});
    }
}

