/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.registries;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.stream.Stream;
import necesse.engine.expeditions.CaveExpedition;
import necesse.engine.expeditions.CommonFishingTripExpedition;
import necesse.engine.expeditions.DeepCaveExpedition;
import necesse.engine.expeditions.DeepDesertCaveExpedition;
import necesse.engine.expeditions.DeepPlainsCaveExpedition;
import necesse.engine.expeditions.DeepSnowCaveExpedition;
import necesse.engine.expeditions.DeepSwampCaveExpedition;
import necesse.engine.expeditions.DesertCaveExpedition;
import necesse.engine.expeditions.DungeonExpedition;
import necesse.engine.expeditions.FishingTripExpedition;
import necesse.engine.expeditions.MiningTripExpedition;
import necesse.engine.expeditions.PirateExpedition;
import necesse.engine.expeditions.PlainsCaveExpedition;
import necesse.engine.expeditions.SettlerExpedition;
import necesse.engine.expeditions.SnowCaveExpedition;
import necesse.engine.expeditions.SwampCaveExpedition;
import necesse.engine.expeditions.TypesFishingTripExpedition;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.GameRegistry;

public class ExpeditionMissionRegistry
extends GameRegistry<SettlerExpedition> {
    public static LinkedHashMap<String, GameMessage> categoryDisplayNames = new LinkedHashMap();
    public static final ExpeditionMissionRegistry instance = new ExpeditionMissionRegistry();
    public static Set<Integer> explorerExpeditionIDs = new HashSet<Integer>();
    public static Set<Integer> miningTripExpeditionIDs = new HashSet<Integer>();
    public static Set<Integer> fishingTripIDs = new HashSet<Integer>();

    private ExpeditionMissionRegistry() {
        super("Expedition", 32762);
    }

    @Override
    public void registerCore() {
        categoryDisplayNames.put("expedition", new LocalMessage("ui", "expeditionmission"));
        categoryDisplayNames.put("miningtrip", new LocalMessage("ui", "miningtripmission"));
        categoryDisplayNames.put("fishingtrip", new LocalMessage("ui", "fishingtripmission"));
        ExpeditionMissionRegistry.registerExplorerExpedition("cave", new CaveExpedition());
        ExpeditionMissionRegistry.registerExplorerExpedition("snowcave", new SnowCaveExpedition());
        ExpeditionMissionRegistry.registerExplorerExpedition("dungeon", new DungeonExpedition());
        ExpeditionMissionRegistry.registerExplorerExpedition("plainscave", new PlainsCaveExpedition());
        ExpeditionMissionRegistry.registerExplorerExpedition("swampcave", new SwampCaveExpedition());
        ExpeditionMissionRegistry.registerExplorerExpedition("desertcave", new DesertCaveExpedition());
        ExpeditionMissionRegistry.registerExplorerExpedition("pirate", new PirateExpedition());
        ExpeditionMissionRegistry.registerExplorerExpedition("deepcave", new DeepCaveExpedition());
        ExpeditionMissionRegistry.registerExplorerExpedition("deepsnowcave", new DeepSnowCaveExpedition());
        ExpeditionMissionRegistry.registerExplorerExpedition("deepplainscave", new DeepPlainsCaveExpedition());
        ExpeditionMissionRegistry.registerExplorerExpedition("deepswampcave", new DeepSwampCaveExpedition());
        ExpeditionMissionRegistry.registerExplorerExpedition("deepdesertcave", new DeepDesertCaveExpedition());
        ExpeditionMissionRegistry.registerMiningTrip("copperminingtrip", new MiningTripExpedition(null, 100, 75, 100, "stone", new MiningTripExpedition.OreConfig("copperore", 400), new MiningTripExpedition.OreConfig("sapphire", 0.3f, 200)));
        ExpeditionMissionRegistry.registerMiningTrip("ironminingtrip", new MiningTripExpedition(null, 150, 100, 150, "stone", new MiningTripExpedition.OreConfig("ironore", 400), new MiningTripExpedition.OreConfig("sapphire", 0.5f, 200)));
        ExpeditionMissionRegistry.registerMiningTrip("goldminingtrip", new MiningTripExpedition("evilsprotector", 200, 175, 250, "stone", new MiningTripExpedition.OreConfig("goldore", 400), new MiningTripExpedition.OreConfig("sapphire", 0.7f, 200)));
        ExpeditionMissionRegistry.registerMiningTrip("frostshardminingtrip", new MiningTripExpedition("queenspider", 250, 200, 300, "snowstone", "frostshard", new String[0]));
        ExpeditionMissionRegistry.registerMiningTrip("runestoneminingtrip", new MiningTripExpedition("chieftain", 300, 225, 325, "granite", "runestone", new String[0]));
        ExpeditionMissionRegistry.registerMiningTrip("ivyminingtrip", new MiningTripExpedition("swampguardian", 350, 250, 350, "swampstone", "ivyore", new String[0]));
        ExpeditionMissionRegistry.registerMiningTrip("quartzminingtrip", new MiningTripExpedition("ancientvulture", 450, 300, 450, "sandstone", new MiningTripExpedition.OreConfig("quartz", 400), new MiningTripExpedition.OreConfig("amethyst", 0.7f, 200)));
        ExpeditionMissionRegistry.registerMiningTrip("lifequartzminingtrip", new MiningTripExpedition("reaper", 600, 400, 600, "deepstone", "lifequartz", new String[0]));
        ExpeditionMissionRegistry.registerMiningTrip("tungstenminingtrip", new MiningTripExpedition("reaper", 600, 400, 600, "deepstone", new MiningTripExpedition.OreConfig("tungstenore", 400), new MiningTripExpedition.OreConfig("ruby", 0.7f, 200)));
        ExpeditionMissionRegistry.registerMiningTrip("glacialminingtrip", new MiningTripExpedition("cryoqueen", 600, 400, 600, "deepsnowstone", "glacialore", new String[0]));
        ExpeditionMissionRegistry.registerMiningTrip("amberminingtrip", new MiningTripExpedition("thecursedcrone", 600, 400, 600, "basalt", "amber", new String[0]));
        ExpeditionMissionRegistry.registerMiningTrip("myceliumminingtrip", new MiningTripExpedition("pestwarden", 600, 400, 600, "deepswampstone", "myceliumore", new String[0]));
        ExpeditionMissionRegistry.registerMiningTrip("ancientfossilminingtrip", new MiningTripExpedition("sageandgrit", 600, 400, 600, "deepsandstone", "ancientfossilore", new String[0]));
        ExpeditionMissionRegistry.registerFishingTrip("commonfishtrip", new CommonFishingTripExpedition());
        ExpeditionMissionRegistry.registerFishingTrip("gobfishtrip", new TypesFishingTripExpedition(null, 500, 400, 500, "gobfish", "halffish", "furfish"));
        ExpeditionMissionRegistry.registerFishingTrip("terrorfishtrip", new TypesFishingTripExpedition(null, 500, 400, 500, "terrorfish", "rockfish"));
        ExpeditionMissionRegistry.registerFishingTrip("halffishtrip", new TypesFishingTripExpedition(null, 500, 400, 500, "halffish", "gobfish", "furfish"));
        ExpeditionMissionRegistry.registerFishingTrip("rockfishtrip", new TypesFishingTripExpedition(null, 500, 400, 500, "rockfish", "terrorfish"));
        ExpeditionMissionRegistry.registerFishingTrip("furfishtrip", new TypesFishingTripExpedition(null, 500, 400, 500, "furfish", "gobfish", "halffish"));
        ExpeditionMissionRegistry.registerFishingTrip("icefishtrip", new TypesFishingTripExpedition(null, 500, 400, 500, "icefish", "gobfish", "halffish"));
        ExpeditionMissionRegistry.registerFishingTrip("swampfishtrip", new TypesFishingTripExpedition(null, 500, 400, 500, "swampfish", "gobfish", "halffish"));
    }

    @Override
    protected void onRegister(SettlerExpedition object, int id, String stringID, boolean isReplace) {
        object.initDisplayName();
    }

    @Override
    protected void onRegistryClose() {
        for (SettlerExpedition element : this.getElements()) {
            element.onExpeditionMissionRegistryClosed();
        }
        explorerExpeditionIDs = Collections.unmodifiableSet(explorerExpeditionIDs);
        miningTripExpeditionIDs = Collections.unmodifiableSet(miningTripExpeditionIDs);
        fishingTripIDs = Collections.unmodifiableSet(fishingTripIDs);
    }

    public static int registerExplorerExpedition(String stringID, SettlerExpedition expedition) {
        expedition.setCategory("expedition");
        int id = ExpeditionMissionRegistry.registerExpedition(stringID, expedition);
        explorerExpeditionIDs.add(id);
        return id;
    }

    @Deprecated
    public static int registerMinerExpedition(String stringID, SettlerExpedition expedition) {
        return ExpeditionMissionRegistry.registerMiningTrip(stringID, expedition);
    }

    public static int registerMiningTrip(String stringID, SettlerExpedition expedition) {
        expedition.setCategory("miningtrip");
        int id = ExpeditionMissionRegistry.registerExpedition(stringID, expedition);
        miningTripExpeditionIDs.add(id);
        return id;
    }

    public static int registerFishingTrip(String stringID, FishingTripExpedition expedition) {
        expedition.setCategory("fishingtrip");
        int id = ExpeditionMissionRegistry.registerExpedition(stringID, expedition);
        fishingTripIDs.add(id);
        return id;
    }

    public static int registerExpedition(String stringID, SettlerExpedition expedition) {
        return instance.register(stringID, expedition);
    }

    public static int replaceExpedition(String stringID, SettlerExpedition expedition) {
        return instance.replace(stringID, expedition);
    }

    public static Iterable<SettlerExpedition> getExpeditions() {
        return instance.getElements();
    }

    public static Stream<SettlerExpedition> streamExpeditions() {
        return instance.streamElements();
    }

    public static SettlerExpedition getExpedition(int id) {
        if (id >= instance.size()) {
            id = 0;
        }
        return (SettlerExpedition)instance.getElement(id);
    }

    public static int getExpeditionID(String stringID) {
        return instance.getElementID(stringID);
    }

    public static SettlerExpedition getExpedition(String stringID) {
        return (SettlerExpedition)instance.getElement(stringID);
    }
}

