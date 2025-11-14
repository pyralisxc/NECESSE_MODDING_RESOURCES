/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.registries;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import necesse.engine.GameLoadingScreen;
import necesse.engine.localization.Localization;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.registries.GameRegistry;
import necesse.engine.registries.IDData;
import necesse.engine.registries.IDDataContainer;
import necesse.engine.util.GameRandom;
import necesse.level.maps.incursion.CrystalHollowIncursionBiome;
import necesse.level.maps.incursion.DesertDeepCaveIncursionBiome;
import necesse.level.maps.incursion.ForestDeepCaveIncursionBiome;
import necesse.level.maps.incursion.GraveyardIncursionBiome;
import necesse.level.maps.incursion.IncursionBiome;
import necesse.level.maps.incursion.MoonArenaIncursionBiome;
import necesse.level.maps.incursion.SettlementRuinsIncursionBiome;
import necesse.level.maps.incursion.SlimeCaveIncursionBiome;
import necesse.level.maps.incursion.SnowDeepCaveIncursionBiome;
import necesse.level.maps.incursion.SpiderCastleIncursionBiome;
import necesse.level.maps.incursion.SunArenaIncursionBiome;
import necesse.level.maps.incursion.SwampDeepCaveIncursionBiome;

public class IncursionBiomeRegistry
extends GameRegistry<IncursionBiomeRegistryElement<?>> {
    public static IncursionBiome FOREST_DEEP_CAVE_INCURSION;
    public static IncursionBiome SNOW_DEEP_CAVE_INCURSION;
    public static IncursionBiome SWAMP_DEEP_CAVE_INCURSION;
    public static IncursionBiome DESERT_DEEP_CAVE_INCURSION;
    public static IncursionBiome SLIME_CAVE;
    public static IncursionBiome GRAVEYARD;
    public static IncursionBiome SPIDER_CASTLE;
    public static IncursionBiome SUN_ARENA;
    public static IncursionBiome MOON_ARENA;
    public static IncursionBiome CRYSTAL_HOLLOW;
    public static IncursionBiome SETTLEMENT_RUINS;
    public static final IncursionBiomeRegistry instance;

    public IncursionBiomeRegistry() {
        super("IncursionBiome", 32762);
    }

    @Override
    public void registerCore() {
        GameLoadingScreen.drawLoadingString(Localization.translate("loading", "incursions"));
        FOREST_DEEP_CAVE_INCURSION = IncursionBiomeRegistry.registerBiome("forestcave", new ForestDeepCaveIncursionBiome(), 1);
        SNOW_DEEP_CAVE_INCURSION = IncursionBiomeRegistry.registerBiome("snowcave", new SnowDeepCaveIncursionBiome(), 1);
        SWAMP_DEEP_CAVE_INCURSION = IncursionBiomeRegistry.registerBiome("swampcave", new SwampDeepCaveIncursionBiome(), 1);
        DESERT_DEEP_CAVE_INCURSION = IncursionBiomeRegistry.registerBiome("desertcave", new DesertDeepCaveIncursionBiome(), 1);
        SLIME_CAVE = IncursionBiomeRegistry.registerBiome("slimecave", new SlimeCaveIncursionBiome(), 2);
        GRAVEYARD = IncursionBiomeRegistry.registerBiome("graveyard", new GraveyardIncursionBiome(), 3);
        SPIDER_CASTLE = IncursionBiomeRegistry.registerBiome("spidercastle", new SpiderCastleIncursionBiome(), 4);
        MOON_ARENA = IncursionBiomeRegistry.registerBiome("moonarena", new MoonArenaIncursionBiome(), 5);
        SUN_ARENA = IncursionBiomeRegistry.registerBiome("sunarena", new SunArenaIncursionBiome(), 6);
        CRYSTAL_HOLLOW = IncursionBiomeRegistry.registerBiome("crystalhollow", new CrystalHollowIncursionBiome(), 7);
        SETTLEMENT_RUINS = IncursionBiomeRegistry.registerBiome("settlementruins", new SettlementRuinsIncursionBiome(), 10000);
    }

    @Override
    protected void onRegister(IncursionBiomeRegistryElement<?> object, int id, String stringID, boolean isReplace) {
    }

    @Override
    protected void onRegistryClose() {
        instance.streamElements().map(e -> e.biome).forEach(IncursionBiome::updateLocalDisplayName);
        for (IncursionBiomeRegistryElement element : this.getElements()) {
            ((IncursionBiome)element.biome).onIncursionBiomeRegistryClosed();
        }
    }

    public static <T extends IncursionBiome> T registerBiome(String stringID, T biome, int tier) {
        if (LoadedMod.isRunningModClientSide()) {
            throw new IllegalStateException("Client/server only mods cannot register incursion biomes");
        }
        return IncursionBiomeRegistry.instance.registerObj((String)stringID, new IncursionBiomeRegistryElement<T>(biome, (int)tier)).biome;
    }

    public static List<IncursionBiome> getBiomes() {
        return instance.streamElements().map(e -> e.biome).collect(Collectors.toList());
    }

    public static int getTotalBiomes() {
        return instance.size();
    }

    public static IncursionBiome getBiome(String stringID) {
        return IncursionBiomeRegistry.getBiome(IncursionBiomeRegistry.getBiomeID(stringID));
    }

    public static IncursionBiome getBiome(int id) {
        if (id == -1) {
            return null;
        }
        return ((IncursionBiomeRegistryElement)IncursionBiomeRegistry.instance.getElement((int)id)).biome;
    }

    public static int getBiomeTier(int id) {
        return ((IncursionBiomeRegistryElement)IncursionBiomeRegistry.instance.getElement((int)id)).baseTier;
    }

    public static int getBiomeID(String stringID) {
        return instance.getElementID(stringID);
    }

    public static int getBiomeIDRaw(String stringID) throws NoSuchElementException {
        return instance.getElementIDRaw(stringID);
    }

    public static String getBiomeStringID(int id) {
        return instance.getElementStringID(id);
    }

    public static IncursionBiome getRandomBiome(long seed) {
        int id = new GameRandom(seed).nextInt(instance.size());
        return ((IncursionBiomeRegistryElement)IncursionBiomeRegistry.instance.getElement((int)id)).biome;
    }

    public static ArrayList<IncursionBiome> getIncursionsFromBaseTier(int tier) {
        List<IncursionBiome> biomes = IncursionBiomeRegistry.getBiomes();
        ArrayList<IncursionBiome> incursionsWithSimilarBaseTier = new ArrayList<IncursionBiome>();
        for (IncursionBiome biome : biomes) {
            if (IncursionBiomeRegistry.getBiomeTier(biome.getID()) != tier) continue;
            incursionsWithSimilarBaseTier.add(biome);
        }
        return incursionsWithSimilarBaseTier;
    }

    static {
        instance = new IncursionBiomeRegistry();
    }

    protected static class IncursionBiomeRegistryElement<T extends IncursionBiome>
    implements IDDataContainer {
        public final T biome;
        public final int baseTier;

        public IncursionBiomeRegistryElement(T biome, int baseTier) {
            this.biome = biome;
            this.baseTier = baseTier;
        }

        @Override
        public IDData getIDData() {
            return ((IncursionBiome)this.biome).idData;
        }
    }
}

