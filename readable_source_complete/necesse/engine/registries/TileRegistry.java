/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.registries;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import necesse.engine.GameLoadingScreen;
import necesse.engine.localization.Localization;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.registries.GameRegistry;
import necesse.engine.registries.IDData;
import necesse.engine.registries.IDDataContainer;
import necesse.engine.registries.ItemRegistry;
import necesse.inventory.item.Item;
import necesse.inventory.item.placeableItem.tileItem.TileItem;
import necesse.level.gameTile.ArcanicFloorTile;
import necesse.level.gameTile.AscendedCorruptionTile;
import necesse.level.gameTile.AscendedGrowthTile;
import necesse.level.gameTile.AscendedVoidTile;
import necesse.level.gameTile.BasaltRockTile;
import necesse.level.gameTile.ChromaKeyTile;
import necesse.level.gameTile.CryptAshTile;
import necesse.level.gameTile.CrystalGravelTile;
import necesse.level.gameTile.CrystalTile;
import necesse.level.gameTile.DeepIceTile;
import necesse.level.gameTile.DeepRockTile;
import necesse.level.gameTile.DeepSandstoneTile;
import necesse.level.gameTile.DeepSnowRockTile;
import necesse.level.gameTile.DeepSwampRockTile;
import necesse.level.gameTile.DirtTile;
import necesse.level.gameTile.DungeonFloorTile;
import necesse.level.gameTile.EmptyTile;
import necesse.level.gameTile.FarmlandTile;
import necesse.level.gameTile.GameTile;
import necesse.level.gameTile.GraniteTile;
import necesse.level.gameTile.GrassTile;
import necesse.level.gameTile.GravelTile;
import necesse.level.gameTile.IceTile;
import necesse.level.gameTile.LavaPathTile;
import necesse.level.gameTile.LavaTile;
import necesse.level.gameTile.MoonPath;
import necesse.level.gameTile.MudTile;
import necesse.level.gameTile.OvergrownGrassTile;
import necesse.level.gameTile.OvergrownPlainsGrassTile;
import necesse.level.gameTile.OvergrownSwampGrassTile;
import necesse.level.gameTile.PathTiledTile;
import necesse.level.gameTile.PlainsGrassTile;
import necesse.level.gameTile.PuddleCobbleTile;
import necesse.level.gameTile.QuicksandTile;
import necesse.level.gameTile.RavenFloorTile;
import necesse.level.gameTile.RockTile;
import necesse.level.gameTile.SandBrickTile;
import necesse.level.gameTile.SandGravelTile;
import necesse.level.gameTile.SandTile;
import necesse.level.gameTile.SandstoneTile;
import necesse.level.gameTile.SimpleFloorTile;
import necesse.level.gameTile.SimpleTerrainTile;
import necesse.level.gameTile.SimpleTiledFloorTile;
import necesse.level.gameTile.SlimeLiquidTile;
import necesse.level.gameTile.SlimeRockTile;
import necesse.level.gameTile.SnowRockTile;
import necesse.level.gameTile.SnowTile;
import necesse.level.gameTile.SpiderNestTile;
import necesse.level.gameTile.SpiritWaterTile;
import necesse.level.gameTile.SwampGrassTile;
import necesse.level.gameTile.SwampRockTile;
import necesse.level.gameTile.WaterTile;

public class TileRegistry
extends GameRegistry<TileRegistryElement> {
    public static final TileRegistry instance = new TileRegistry();
    private static String[] stringIDs = null;
    public static int waterID;
    public static int emptyID;
    public static int dirtID;
    public static int sandID;
    public static int gravelID;
    public static int lavaID;
    public static int quicksandID;
    public static int snowID;
    public static int iceID;
    public static int deepIceID;
    public static int mudID;
    public static int spiderNestID;
    public static int cryptAshID;
    public static int puddleCobble;
    public static int spiritWaterID;
    public static int grassID;
    public static int overgrownGrassID;
    public static int plainsGrassID;
    public static int overgrownPlainsGrassID;
    public static int swampGrassID;
    public static int overgrownSwampGrassID;
    public static int woodFloorID;
    public static int pineFloorID;
    public static int palmFloorID;
    public static int willowFloorID;
    public static int dryadFloorID;
    public static int woodPathID;
    public static int stoneFloorID;
    public static int stoneBrickFloorID;
    public static int stoneTiledFloorID;
    public static int stonePathID;
    public static int rockID;
    public static int snowStoneFloorID;
    public static int snowStoneBrickFloorID;
    public static int snowStonePathID;
    public static int snowRockID;
    public static int swampStoneFloorID;
    public static int swampStoneBrickFloorID;
    public static int swampStonePathID;
    public static int swampRockID;
    public static int sandstoneFloorID;
    public static int sandBrickID;
    public static int sandstoneBrickFloorID;
    public static int sandstonePathID;
    public static int sandstoneID;
    public static int graniteFloorID;
    public static int graniteBrickFloorID;
    public static int granitePathID;
    public static int graniteRockID;
    public static int deepStoneFloorID;
    public static int deepStoneBrickFloorID;
    public static int deepStoneTiledFloorID;
    public static int deepRockID;
    public static int deepSnowStoneFloorID;
    public static int deepSnowStoneBrickFloorID;
    public static int deepSnowRockID;
    public static int deepSwampStoneFloorID;
    public static int deepSwampStoneBrickFloorID;
    public static int deepSwampRockID;
    public static int basaltFloorID;
    public static int basaltPathID;
    public static int basaltRockID;
    public static int dryadPathID;
    public static int deepSandstoneID;
    public static int dungeonFloorID;
    public static int bambooFloorID;
    public static int deadWoodFloorID;
    public static int ravenFloorID;
    public static int arcanicFloorID;
    public static int arcanicPathID;
    public static int ascendedCorruptionID;
    public static int ascendedGrowthID;
    public static int ascendedVoidID;

    private TileRegistry() {
        super("Tile", 32762);
    }

    @Override
    public void registerCore() {
        GameLoadingScreen.drawLoadingString(Localization.translate("loading", "tiles"));
        emptyID = TileRegistry.registerTile("emptytile", new EmptyTile(), 0.0f, false);
        dirtID = TileRegistry.registerTile("dirttile", (GameTile)new DirtTile(), 0.0f, false, false, true, new String[0]);
        waterID = TileRegistry.registerTile("watertile", new WaterTile(), 20.0f, true);
        grassID = TileRegistry.registerTile("grasstile", (GameTile)new GrassTile(), 0.0f, false, false, true, new String[0]);
        overgrownGrassID = TileRegistry.registerTile("overgrowngrasstile", (GameTile)new OvergrownGrassTile(), 0.0f, false, false, true, new String[0]);
        sandID = TileRegistry.registerTile("sandtile", new SandTile(), 0.5f, true);
        swampGrassID = TileRegistry.registerTile("swampgrasstile", (GameTile)new SwampGrassTile(), 0.0f, false, false, true, new String[0]);
        overgrownSwampGrassID = TileRegistry.registerTile("overgrownswampgrasstile", (GameTile)new OvergrownSwampGrassTile(), 0.0f, false, false, true, new String[0]);
        plainsGrassID = TileRegistry.registerTile("plainsgrasstile", (GameTile)new PlainsGrassTile(), 0.0f, false, false, true, new String[0]);
        overgrownPlainsGrassID = TileRegistry.registerTile("overgrownplainsgrasstile", (GameTile)new OvergrownPlainsGrassTile(), 0.0f, false, false, true, new String[0]);
        mudID = TileRegistry.registerTile("mudtile", new MudTile(), 5.0f, true);
        rockID = TileRegistry.registerTile("rocktile", new RockTile(), 5.0f, true);
        dungeonFloorID = TileRegistry.registerTile("dungeonfloor", new DungeonFloorTile(), 5.0f, true);
        TileRegistry.registerTile("farmland", new FarmlandTile(), 5.0f, true);
        woodFloorID = TileRegistry.registerTile("woodfloor", new SimpleFloorTile("woodfloor", new Color(118, 71, 20)), 2.0f, true);
        pineFloorID = TileRegistry.registerTile("pinefloor", new SimpleFloorTile("pinefloor", new Color(125, 83, 27)), 2.0f, true);
        palmFloorID = TileRegistry.registerTile("palmfloor", new SimpleTiledFloorTile("palmfloor", new Color(119, 83, 26)), 2.0f, true);
        willowFloorID = TileRegistry.registerTile("willowfloor", new SimpleTiledFloorTile("willowfloor", new Color(94, 55, 47)), 2.0f, true);
        dryadFloorID = TileRegistry.registerTile("dryadfloor", new SimpleTiledFloorTile("dryadfloor", new Color(70, 53, 43)), 2.0f, true);
        woodPathID = TileRegistry.registerTile("woodpathtile", new PathTiledTile("woodpath", new Color(103, 61, 18)), 5.0f, true);
        bambooFloorID = TileRegistry.registerTile("bamboofloor", new SimpleFloorTile("bamboofloor", new Color(116, 74, 71)), 2.0f, true);
        deadWoodFloorID = TileRegistry.registerTile("deadwoodfloor", new SimpleFloorTile("deadwoodfloor", new Color(59, 43, 40)), 2.0f, true);
        stoneFloorID = TileRegistry.registerTile("stonefloor", new SimpleFloorTile("stonefloor", new Color(125, 136, 146)), 2.0f, true);
        stoneBrickFloorID = TileRegistry.registerTile("stonebrickfloor", new SimpleFloorTile("stonebrickfloor", new Color(125, 136, 146)), 2.0f, true);
        stoneTiledFloorID = TileRegistry.registerTile("stonetiledfloor", new SimpleFloorTile("stonetiledfloor", new Color(125, 136, 146)), 2.0f, true);
        stonePathID = TileRegistry.registerTile("stonepathtile", new PathTiledTile("stonepath", new Color(110, 117, 127)), 5.0f, true);
        sandstoneID = TileRegistry.registerTile("sandstonetile", new SandstoneTile(), 5.0f, true);
        sandstoneFloorID = TileRegistry.registerTile("sandstonefloor", new SimpleFloorTile("sandstonefloor", new Color(160, 136, 94)), 2.0f, true);
        sandstoneBrickFloorID = TileRegistry.registerTile("sandstonebrickfloor", new SimpleFloorTile("sandstonebrickfloor", new Color(160, 136, 94)), 2.0f, true);
        sandstonePathID = TileRegistry.registerTile("sandstonepathtile", new PathTiledTile("sandstonepath", new Color(146, 121, 87)), 5.0f, true);
        swampRockID = TileRegistry.registerTile("swamprocktile", new SwampRockTile(), 5.0f, true);
        swampStoneFloorID = TileRegistry.registerTile("swampstonefloor", new SimpleFloorTile("swampstonefloor", new Color(63, 96, 80)), 2.0f, true);
        swampStoneBrickFloorID = TileRegistry.registerTile("swampstonebrickfloor", new SimpleFloorTile("swampstonebrickfloor", new Color(63, 96, 80)), 2.0f, true);
        swampStonePathID = TileRegistry.registerTile("swampstonepathtile", new PathTiledTile("swampstonepath", new Color(57, 77, 60)), 5.0f, true);
        snowRockID = TileRegistry.registerTile("snowrocktile", new SnowRockTile(), 5.0f, true);
        lavaID = TileRegistry.registerTile("lavatile", new LavaTile(), 20.0f, true);
        quicksandID = TileRegistry.registerTile("quicksandtile", new QuicksandTile(), 20.0f, true);
        snowID = TileRegistry.registerTile("snowtile", new SnowTile(), 5.0f, true);
        iceID = TileRegistry.registerTile("icetile", new IceTile(), 10.0f, true);
        snowStoneFloorID = TileRegistry.registerTile("snowstonefloor", new SimpleFloorTile("snowstonefloor", new Color(66, 102, 133)), 2.0f, true);
        snowStoneBrickFloorID = TileRegistry.registerTile("snowstonebrickfloor", new SimpleFloorTile("snowstonebrickfloor", new Color(74, 112, 156)), 2.0f, true);
        snowStonePathID = TileRegistry.registerTile("snowstonepathtile", new PathTiledTile("snowstonepath", new Color(150, 178, 217)), 5.0f, true);
        graniteRockID = TileRegistry.registerTile("graniterocktile", new GraniteTile(), 5.0f, true);
        graniteFloorID = TileRegistry.registerTile("granitefloor", new SimpleFloorTile("granitefloor", new Color(107, 53, 51)), 2.0f, true);
        graniteBrickFloorID = TileRegistry.registerTile("granitebrickfloor", new SimpleFloorTile("granitebrickfloor", new Color(107, 53, 51)), 2.0f, true);
        granitePathID = TileRegistry.registerTile("granitepathtile", new PathTiledTile("granitepath", new Color(135, 70, 67)), 5.0f, true);
        gravelID = TileRegistry.registerTile("graveltile", new GravelTile(), 2.0f, true);
        sandBrickID = TileRegistry.registerTile("sandbrick", new SandBrickTile(), 5.0f, true);
        deepRockID = TileRegistry.registerTile("deeprocktile", new DeepRockTile(), 5.0f, true);
        deepStoneFloorID = TileRegistry.registerTile("deepstonefloor", new SimpleFloorTile("deepstonefloor", new Color(52, 67, 81)), 2.0f, true);
        deepStoneBrickFloorID = TileRegistry.registerTile("deepstonebrickfloor", new SimpleFloorTile("deepstonebrickfloor", new Color(57, 59, 60)), 2.0f, true);
        deepStoneTiledFloorID = TileRegistry.registerTile("deepstonetiledfloor", new SimpleFloorTile("deepstonetiledfloor", new Color(66, 73, 75)), 2.0f, true);
        deepSnowRockID = TileRegistry.registerTile("deepsnowrocktile", new DeepSnowRockTile(), 5.0f, true);
        deepSnowStoneFloorID = TileRegistry.registerTile("deepsnowstonefloor", new SimpleFloorTile("deepsnowstonefloor", new Color(52, 72, 80)), 2.0f, true);
        deepSnowStoneBrickFloorID = TileRegistry.registerTile("deepsnowstonebrickfloor", new SimpleFloorTile("deepsnowstonebrickfloor", new Color(75, 78, 80)), 2.0f, true);
        deepSwampRockID = TileRegistry.registerTile("deepswamprocktile", new DeepSwampRockTile(), 5.0f, true);
        deepSwampStoneFloorID = TileRegistry.registerTile("deepswampstonefloor", new SimpleFloorTile("deepswampstonefloor", new Color(53, 80, 74)), 2.0f, true);
        deepSwampStoneBrickFloorID = TileRegistry.registerTile("deepswampstonebrickfloor", new SimpleFloorTile("deepswampstonebrickfloor", new Color(43, 63, 60)), 2.0f, true);
        basaltRockID = TileRegistry.registerTile("basaltrocktile", new BasaltRockTile(), 5.0f, true);
        basaltFloorID = TileRegistry.registerTile("basaltfloor", new SimpleFloorTile("basaltfloor", new Color(60, 49, 81)), 2.0f, true);
        basaltPathID = TileRegistry.registerTile("basaltpathtile", new PathTiledTile("basaltpath", new Color(52, 42, 62)), 5.0f, true);
        dryadPathID = TileRegistry.registerTile("dryadpath", new PathTiledTile("dryadpath", new Color(31, 35, 45)), 5.0f, true);
        deepIceID = TileRegistry.registerTile("deepicetile", new DeepIceTile(), 10.0f, true);
        TileRegistry.registerTile("sandgraveltile", new SandGravelTile(), 2.0f, true);
        spiderNestID = TileRegistry.registerTile("spidernesttile", (GameTile)new SpiderNestTile(), 0.0f, false, false, true, new String[0]);
        cryptAshID = TileRegistry.registerTile("cryptash", (GameTile)new CryptAshTile(), 0.0f, false, false, true, new String[0]);
        deepSandstoneID = TileRegistry.registerTile("deepsandstonetile", new DeepSandstoneTile(), 5.0f, true);
        ravenFloorID = TileRegistry.registerTile("ravenfloor", new RavenFloorTile(), 5.0f, true);
        arcanicFloorID = TileRegistry.registerTile("arcanicfloor", new ArcanicFloorTile(), 5.0f, true);
        arcanicPathID = TileRegistry.registerTile("arcanicpath", new PathTiledTile("arcanicpath", new Color(105, 138, 130)), 5.0f, true);
        TileRegistry.registerTile("strawtile", new SimpleTerrainTile("strawtile", new Color(255, 207, 5)), 2.0f, true);
        TileRegistry.registerTile("liquidslimetile", new SlimeLiquidTile(), 20.0f, true);
        TileRegistry.registerTile("slimerocktile", new SlimeRockTile(), 5.0f, true);
        TileRegistry.registerTile("cryptpath", new PathTiledTile("cryptpath", new Color(66, 66, 78)), 5.0f, true);
        TileRegistry.registerTile("spidercastlefloor", new SimpleFloorTile("spidercastlefloor", new Color(47, 44, 71)), 5.0f, true);
        TileRegistry.registerTile("spidercobbletile", new SimpleFloorTile("spidercobbletile", new Color(50, 90, 104)), 5.0f, true);
        TileRegistry.registerTile("spidercastlecarpet", new PathTiledTile("spidercastlecarpet", new Color(151, 84, 117)), 5.0f, true);
        TileRegistry.registerTile("dawnpath", new PathTiledTile("dawnpath", new Color(236, 197, 129)), 5.0f, true);
        TileRegistry.registerTile("lavapath", new LavaPathTile(), 5.0f, true);
        TileRegistry.registerTile("moonpath", new MoonPath(), 5.0f, true);
        TileRegistry.registerTile("darkmoonpath", new PathTiledTile("darkmoonpath", new Color(40, 103, 108)), 5.0f, true);
        TileRegistry.registerTile("darkfullmoonpath", new PathTiledTile("darkfullmoonpath", new Color(86, 80, 111)), 5.0f, true);
        TileRegistry.registerTile("crystaltile", new CrystalTile(), 5.0f, true);
        TileRegistry.registerTile("amethystgravel", new CrystalGravelTile("amethystgravel", new Color(88, 74, 127)), 10.0f, true);
        TileRegistry.registerTile("sapphiregravel", new CrystalGravelTile("sapphiregravel", new Color(11, 102, 127)), 10.0f, true);
        TileRegistry.registerTile("emeraldgravel", new CrystalGravelTile("emeraldgravel", new Color(0, 98, 81)), 10.0f, true);
        TileRegistry.registerTile("rubygravel", new CrystalGravelTile("rubygravel", new Color(113, 43, 59)), 10.0f, true);
        TileRegistry.registerTile("topazgravel", new CrystalGravelTile("topazgravel", new Color(113, 99, 43)), 10.0f, true);
        TileRegistry.registerTile("ancientruinfloor", new SimpleFloorTile("ancientruinfloor", new Color(114, 90, 81)), 5.0f, true);
        puddleCobble = TileRegistry.registerTile("puddlecobble", new PuddleCobbleTile(), 5.0f, true);
        spiritWaterID = TileRegistry.registerTile("spiritwatertile", new SpiritWaterTile(), 20.0f, true);
        ascendedCorruptionID = TileRegistry.registerTile("ascendedcorruption", (GameTile)new AscendedCorruptionTile(), 0.0f, false, false, true, new String[0]);
        ascendedGrowthID = TileRegistry.registerTile("ascendedgrowth", (GameTile)new AscendedGrowthTile(), 0.0f, false, false, true, new String[0]);
        ascendedVoidID = TileRegistry.registerTile("ascendedvoid", (GameTile)new AscendedVoidTile(), 0.0f, true, true, true, new String[0]);
        TileRegistry.registerTile("chromakeygreen", (GameTile)new ChromaKeyTile(new Color(0, 255, 0)), 0.0f, false, false, true, new String[0]);
        TileRegistry.registerTile("chromakeyblue", (GameTile)new ChromaKeyTile(new Color(0, 0, 255)), 0.0f, false, false, true, new String[0]);
        TileRegistry.registerTile("chromakeycyan", (GameTile)new ChromaKeyTile(new Color(0, 255, 255)), 0.0f, false, false, true, new String[0]);
        TileRegistry.registerTile("chromakeypink", (GameTile)new ChromaKeyTile(new Color(255, 0, 255)), 0.0f, false, false, true, new String[0]);
    }

    @Override
    protected void onRegister(TileRegistryElement object, int id, String stringID, boolean isReplace) {
        TileItem tItem = object.tile.generateNewTileItem();
        if (tItem != null) {
            ItemRegistry.replaceItem(object.tile.getStringID(), (Item)tItem, object.itemBrokerValue, object.itemObtainable, object.itemCountInStats, object.itemObtainableInCreative, object.isObtainedByOtherItemStringIDs);
        }
    }

    @Override
    protected void onRegistryClose() {
        this.streamElements().map(e -> e.tile).forEach(GameTile::updateLocalDisplayName);
        for (TileRegistryElement element : this.getElements()) {
            element.tile.onTileRegistryClosed();
        }
        stringIDs = (String[])instance.streamElements().map(e -> e.tile.getStringID()).toArray(String[]::new);
    }

    public static int registerTile(String stringID, GameTile tile, float itemBrokerValue, boolean itemObtainable) {
        return TileRegistry.registerTile(stringID, tile, itemBrokerValue, itemObtainable, itemObtainable, new String[0]);
    }

    public static int registerTile(String stringID, GameTile tile, float itemBrokerValue, boolean itemObtainable, boolean itemCountInStats, String ... isObtainedByOtherItemStringIDs) {
        return TileRegistry.registerTile(stringID, tile, itemBrokerValue, itemObtainable, itemCountInStats, itemObtainable, isObtainedByOtherItemStringIDs);
    }

    public static int registerTile(String stringID, GameTile tile, float itemBrokerValue, boolean itemObtainable, boolean itemCountInStats, boolean itemObtainableInCreative, String ... isObtainedByOtherItemStringIDs) {
        if (LoadedMod.isRunningModClientSide()) {
            throw new IllegalStateException("Client/server only mods cannot register tiles");
        }
        return instance.register(stringID, new TileRegistryElement(tile, itemBrokerValue, itemObtainable, itemObtainableInCreative, itemCountInStats, Arrays.asList(isObtainedByOtherItemStringIDs)));
    }

    public static int replaceTile(String stringID, GameTile tile, float itemBrokerValue, boolean itemObtainable) {
        return TileRegistry.replaceTile(stringID, tile, itemBrokerValue, itemObtainable, itemObtainable, new String[0]);
    }

    public static int replaceTile(String stringID, GameTile tile, float itemBrokerValue, boolean itemObtainable, boolean itemCountInStats, String ... isObtainedByOtherItemStringIDs) {
        return TileRegistry.replaceTile(stringID, tile, itemBrokerValue, itemObtainable, itemCountInStats, itemObtainable, isObtainedByOtherItemStringIDs);
    }

    public static int replaceTile(String stringID, GameTile tile, float itemBrokerValue, boolean itemObtainable, boolean itemCountInStats, boolean itemObtainableInCreative, String ... isObtainedByOtherItemStringIDs) {
        return instance.replace(stringID, new TileRegistryElement(tile, itemBrokerValue, itemObtainable, itemObtainableInCreative, itemCountInStats, Arrays.asList(isObtainedByOtherItemStringIDs)));
    }

    public static Stream<GameTile> streamTiles() {
        return instance.streamElements().map(e -> e.tile);
    }

    public static Iterable<GameTile> getTiles() {
        return TileRegistry.streamTiles().collect(Collectors.toList());
    }

    public static GameTile getTile(int id) {
        TileRegistryElement element;
        if (id >= instance.size()) {
            id = 0;
        }
        if ((element = (TileRegistryElement)instance.getElement(id)) == null) {
            throw new NullPointerException(TileRegistry.instance.objectCallName + " ID " + id + " seems to be missing or corrupted");
        }
        return element.tile;
    }

    public static int getTileID(String stringID) {
        return instance.getElementID(stringID);
    }

    public static GameTile getTile(String stringID) {
        TileRegistryElement element = (TileRegistryElement)instance.getElement(stringID);
        if (element == null) {
            throw new NullPointerException(TileRegistry.instance.objectCallName + " stringID " + stringID + " seems to be missing or corrupted");
        }
        return element.tile;
    }

    public static String getTileStringID(int id) {
        return instance.getElementStringID(id);
    }

    public static String[] getTileStringIDs() {
        if (stringIDs == null) {
            throw new IllegalStateException("TileRegistry not yet closed");
        }
        return stringIDs;
    }

    protected static class TileRegistryElement
    implements IDDataContainer {
        public final GameTile tile;
        public final float itemBrokerValue;
        public final boolean itemObtainable;
        public final boolean itemObtainableInCreative;
        public final boolean itemCountInStats;
        public final List<String> isObtainedByOtherItemStringIDs;

        public TileRegistryElement(GameTile tile, float itemBrokerValue, boolean itemObtainable, boolean itemObtainableInCreative, boolean itemCountInStats, List<String> isObtainedByOtherItemStringIDs) {
            this.tile = tile;
            this.itemBrokerValue = itemBrokerValue;
            this.itemObtainable = itemObtainable;
            this.itemObtainableInCreative = itemObtainableInCreative;
            this.itemCountInStats = itemCountInStats;
            this.isObtainedByOtherItemStringIDs = isObtainedByOtherItemStringIDs;
        }

        @Override
        public IDData getIDData() {
            return this.tile.idData;
        }
    }
}

