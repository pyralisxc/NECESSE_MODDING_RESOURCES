/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.journal.JournalEntry
 *  necesse.engine.registries.JournalRegistry
 *  necesse.engine.util.LevelIdentifier
 *  necesse.inventory.lootTable.LootItemInterface
 *  necesse.inventory.lootTable.LootTable
 *  necesse.inventory.lootTable.lootItem.LootItem
 */
package aphorea.registry;

import aphorea.journal.AphJournalChallenges;
import aphorea.registry.AphBiomes;
import aphorea.registry.AphLootTables;
import java.util.Objects;
import necesse.engine.journal.JournalEntry;
import necesse.engine.registries.JournalRegistry;
import necesse.engine.util.LevelIdentifier;
import necesse.inventory.lootTable.LootItemInterface;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;

public class AphJournal {
    public static void registerCore() {
        AphJournal.vanillaSurface();
        AphJournal.vanillaCaves();
        AphJournal.vanillaOther();
        JournalRegistry.getJournalEntries().forEach(journalEntry -> {
            AphJournal.vanillaBulkAll(journalEntry);
            if (journalEntry.levelIdentifier == LevelIdentifier.SURFACE_IDENTIFIER) {
                AphJournal.vanillaBulkSurface(journalEntry);
            }
        });
        AphJournal.infectedFields();
    }

    public static void vanillaSurface() {
        JournalEntry forestSurfaceJournal = JournalRegistry.getJournalEntry((String)"forestsurface");
        forestSurfaceJournal.addEntryChallenges(new Integer[]{AphJournalChallenges.APH_FOREST_SURFACE_CHALLENGES_ID});
        forestSurfaceJournal.addMobEntries(new String[]{"unstablegelslime"});
        JournalEntry swampSurfaceJournal = JournalRegistry.getJournalEntry((String)"swampsurface");
        swampSurfaceJournal.addEntryChallenges(new Integer[]{AphJournalChallenges.APH_SWAMP_SURFACE_CHALLENGES_ID});
        swampSurfaceJournal.addMobEntries(new String[]{"pinkwitch"});
    }

    public static void vanillaCaves() {
        JournalEntry forestCaveJournal = JournalRegistry.getJournalEntry((String)"forestcave");
        forestCaveJournal.addMobEntries(new String[]{"rockygelslime"});
        forestCaveJournal.addTreasureEntry(new LootTable[]{new LootTable(new LootItemInterface[]{new LootItem("blowgun"), new LootItem("sling")})});
        JournalEntry snowCaveJournal = JournalRegistry.getJournalEntry((String)"snowcave");
        snowCaveJournal.addTreasureEntry(new LootTable[]{new LootTable(new LootItemInterface[]{new LootItem("frozenperiapt")})});
    }

    public static void vanillaOther() {
        JournalEntry dungeonJournal = JournalRegistry.getJournalEntry((String)"dungeon");
        dungeonJournal.addMobEntries(new String[]{"voidadept"});
        dungeonJournal.addTreasureEntry(new LootTable[]{new LootTable(new LootItemInterface[]{new LootItem("heartring")})});
    }

    public static void vanillaBulkAll(JournalEntry journalEntry) {
        if (journalEntry.mobsData.stream().anyMatch(m -> Objects.equals(m.mob.getStringID(), "goblin"))) {
            journalEntry.addMobEntries(new String[]{"copperdaggergoblin", "irondaggergoblin", "golddaggergoblin"});
        }
    }

    public static void vanillaBulkSurface(JournalEntry journalEntry) {
        journalEntry.addMobEntries(new String[]{"gelslime", "wildphosphorslime"});
        journalEntry.addTreasureEntry(new String[]{"blowgun", "sling"});
    }

    public static void infectedFields() {
        JournalEntry infectedFieldsSurface = new JournalEntry(AphBiomes.INFECTED_FIELDS, LevelIdentifier.SURFACE_IDENTIFIER);
        infectedFieldsSurface.addBiomeLootEntry(new String[]{"infectedlog", "fossilrapier", "rockfish"});
        infectedFieldsSurface.addMobEntries(new String[]{"rockygelslime", "infectedtreant"});
        infectedFieldsSurface.addEntryChallenges(new Integer[]{AphJournalChallenges.INFECTED_SURFACE_CHALLENGES_ID});
        JournalRegistry.registerJournalEntry((String)"infectedfieldssurface", (JournalEntry)infectedFieldsSurface);
        JournalEntry infectedFieldsCave = new JournalEntry(AphBiomes.INFECTED_FIELDS, LevelIdentifier.CAVE_IDENTIFIER);
        infectedFieldsCave.addBiomeLootEntry(new String[]{"tungstenore", "rockygel"});
        infectedFieldsCave.addMobEntries(new String[]{"rockygelslime", "infectedtreant", "spinelcaveling", "spinelgolem", "spinelmimic", "babylontower"});
        infectedFieldsCave.addTreasureEntry(new LootTable[]{AphLootTables.infectedCaveForest, AphLootTables.infectedCaveVariousTreasures, AphLootTables.infectedLootLake});
        infectedFieldsCave.addEntryChallenges(new Integer[]{AphJournalChallenges.INFECTED_CAVE_CHALLENGES_ID});
        JournalRegistry.registerJournalEntry((String)"infectedfieldscave", (JournalEntry)infectedFieldsCave);
    }
}

