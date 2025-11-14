/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.worldPresets;

import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.worldPresets.AbandonedCampGenerationPreset;
import necesse.engine.world.worldPresets.AbandonedFountainGenerationPreset;
import necesse.engine.world.worldPresets.AbandonedMineshaftGenerationPreset;
import necesse.engine.world.worldPresets.BigCemeteryGenerationPreset;
import necesse.engine.world.worldPresets.BigPirateShipGenerationPreset;
import necesse.engine.world.worldPresets.BrokenHusbandryFenceGenerationPreset;
import necesse.engine.world.worldPresets.CaveHoboHomeGenerationPreset;
import necesse.engine.world.worldPresets.ChapelGenerationPreset;
import necesse.engine.world.worldPresets.ChristmasHouseGenerationPreset;
import necesse.engine.world.worldPresets.CrashedMeteorGenerationPreset;
import necesse.engine.world.worldPresets.CrazedBlacksmithHideoutGenerationPreset;
import necesse.engine.world.worldPresets.DavesPlantHouseGenerationPreset;
import necesse.engine.world.worldPresets.DesertTavernGenerationPreset;
import necesse.engine.world.worldPresets.DuelingGroundsGenerationPreset;
import necesse.engine.world.worldPresets.FairyTreeCircleGenerationPreset;
import necesse.engine.world.worldPresets.FarmersRefugeGenerationPreset;
import necesse.engine.world.worldPresets.FishingHutGenerationPreset;
import necesse.engine.world.worldPresets.ForgottenShrineGenerationPreset;
import necesse.engine.world.worldPresets.GenerationPresetsWorldPreset;
import necesse.engine.world.worldPresets.GoblinVillageGenerationPreset;
import necesse.engine.world.worldPresets.GuardTowerGenerationPreset;
import necesse.engine.world.worldPresets.HunterCabinGenerationPreset;
import necesse.engine.world.worldPresets.LargeTempleRuinsGenerationPreset;
import necesse.engine.world.worldPresets.LevelPresetsRegion;
import necesse.engine.world.worldPresets.MageShopGenerationPreset;
import necesse.engine.world.worldPresets.MerchantsAmbushGenerationPreset;
import necesse.engine.world.worldPresets.MystrilHomesteadGenerationPreset;
import necesse.engine.world.worldPresets.RetirementHomeGenerationPreset;
import necesse.engine.world.worldPresets.SlimeRanchGenerationPreset;
import necesse.engine.world.worldPresets.SmallForgottenShrineGenerationPreset;
import necesse.engine.world.worldPresets.SmallMineEntranceGenerationPreset;
import necesse.engine.world.worldPresets.SmallOvergrownShipwreckGenerationPreset;
import necesse.engine.world.worldPresets.SunkenHomeGenerationPreset;
import necesse.engine.world.worldPresets.SwampSettlerRuinsGenerationPreset;
import necesse.engine.world.worldPresets.SwampyCaveLakeGenerationPreset;
import necesse.engine.world.worldPresets.TrainingDummyPicnicGenerationPreset;
import necesse.engine.world.worldPresets.TravellersCampsiteGenerationPreset;
import necesse.engine.world.worldPresets.VampireChurchGenerationPreset;
import necesse.engine.world.worldPresets.VillagerCampGenerationPreset;
import necesse.engine.world.worldPresets.WizardGardenHomeGenerationPreset;
import necesse.level.maps.presets.set.BushSet;
import necesse.level.maps.presets.set.CrystalSet;
import necesse.level.maps.presets.set.FenceSet;
import necesse.level.maps.presets.set.FloorSet;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.HedgeSet;
import necesse.level.maps.presets.set.RockAndOreSet;
import necesse.level.maps.presets.set.TreeSet;
import necesse.level.maps.presets.set.WallSet;

public class SurfacePresetsWorldPreset
extends GenerationPresetsWorldPreset {
    public SurfacePresetsWorldPreset() {
        super(0.05f);
    }

    @Override
    public void addCorePresets() {
        this.addForestPresets();
        this.addSnowPresets();
        this.addPlainsPresets();
        this.addSwampPresets();
        this.addDesertPresets();
        this.addPreset(60, new MerchantsAmbushGenerationPreset(BiomeRegistry.FOREST, BiomeRegistry.SNOW, BiomeRegistry.PLAINS, BiomeRegistry.SWAMP, BiomeRegistry.DESERT));
    }

    @Override
    public boolean shouldAddToRegion(LevelPresetsRegion presetsRegion) {
        return presetsRegion.identifier.equals(LevelIdentifier.SURFACE_IDENTIFIER);
    }

    protected void addForestPresets() {
        this.addPreset(100, new SmallMineEntranceGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.FOREST));
        this.addPreset(85, new BigCemeteryGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.FOREST));
        this.addPreset(70, new DavesPlantHouseGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.FOREST));
        this.addPreset(85, new SwampyCaveLakeGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.FOREST));
        this.addPreset(70, new TrainingDummyPicnicGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.FOREST));
        this.addPreset(100, new SmallForgottenShrineGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.FOREST));
        this.addPreset(100, new SmallOvergrownShipwreckGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.FOREST));
        this.addPreset(85, new VampireChurchGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.FOREST));
        this.addPreset(85, new LargeTempleRuinsGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.FOREST));
        this.addPreset(70, new VillagerCampGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.FOREST));
        this.addPreset(100, new FairyTreeCircleGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.FOREST));
        this.addPreset(85, new WizardGardenHomeGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.FOREST));
        this.addPreset(70, new ChapelGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.FOREST));
        this.addPreset(85, new RetirementHomeGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.FOREST));
        this.addPreset(100, new FishingHutGenerationPreset(new WallSet[]{WallSet.wood, WallSet.pine, WallSet.brick}, new int[]{TileRegistry.woodPathID, TileRegistry.stonePathID}, BiomeRegistry.FOREST));
        this.addPreset(100, new HunterCabinGenerationPreset(new WallSet[]{WallSet.wood, WallSet.pine, WallSet.brick}, new FurnitureSet[]{FurnitureSet.spruce, FurnitureSet.pine, FurnitureSet.oak}, BiomeRegistry.FOREST));
        this.addPreset(100, new BrokenHusbandryFenceGenerationPreset(new FenceSet[]{FenceSet.wood, FenceSet.stone, FenceSet.iron}, BiomeRegistry.FOREST));
        this.addPreset(85, new FarmersRefugeGenerationPreset(new WallSet[]{WallSet.wood, WallSet.pine, WallSet.brick}, new FurnitureSet[]{FurnitureSet.spruce, FurnitureSet.pine, FurnitureSet.oak}, new TreeSet[]{TreeSet.oak, TreeSet.spruce}, new BushSet[]{BushSet.blueberry}, BiomeRegistry.FOREST));
        this.addPreset(100, new AbandonedCampGenerationPreset(new WallSet[]{WallSet.wood, WallSet.pine}, new FurnitureSet[]{FurnitureSet.oak, FurnitureSet.pine, FurnitureSet.spruce}, new TreeSet[]{TreeSet.oak, TreeSet.spruce}, BiomeRegistry.FOREST));
        this.addPreset(100, new ForgottenShrineGenerationPreset(new WallSet[]{WallSet.wood, WallSet.pine}, new FurnitureSet[]{FurnitureSet.oak, FurnitureSet.pine, FurnitureSet.spruce}, new TreeSet[]{TreeSet.oak, TreeSet.spruce}, new HedgeSet[]{HedgeSet.forest}, BiomeRegistry.FOREST));
        this.addPreset(70, new CrashedMeteorGenerationPreset(new CrystalSet[]{CrystalSet.sapphire}, BiomeRegistry.FOREST));
        this.addPreset(100, new TravellersCampsiteGenerationPreset(new FenceSet[]{FenceSet.wood}, new TreeSet[]{TreeSet.oak, TreeSet.spruce}, new BushSet[]{BushSet.blueberry}, BiomeRegistry.FOREST));
        this.addPreset(85, new AbandonedFountainGenerationPreset(new TreeSet[]{TreeSet.oak, TreeSet.spruce}, new HedgeSet[]{HedgeSet.forest}, BiomeRegistry.FOREST));
        this.addPreset(85, new AbandonedMineshaftGenerationPreset(BiomeRegistry.FOREST, RockAndOreSet.forest, new WallSet[]{WallSet.wood, WallSet.pine}, new FurnitureSet[]{FurnitureSet.oak, FurnitureSet.pine, FurnitureSet.spruce}, new TreeSet[]{TreeSet.oak, TreeSet.spruce}, new String[]{"zombie", "zombiearcher"}, BiomeRegistry.FOREST));
        this.addPreset(100, new BigPirateShipGenerationPreset(BiomeRegistry.FOREST));
        this.addPreset(85, new SunkenHomeGenerationPreset(new WallSet[]{WallSet.willow, WallSet.swampStone, WallSet.wood}, new FurnitureSet[]{FurnitureSet.oak, FurnitureSet.pine, FurnitureSet.spruce}, new TreeSet[]{TreeSet.oak, TreeSet.spruce}, new BushSet[]{BushSet.raspberry, BushSet.blueberry, BushSet.blackberry}, new BushSet[]{BushSet.raspberry, BushSet.blueberry, BushSet.blackberry}, new FenceSet[]{FenceSet.iron, FenceSet.wood, FenceSet.stone}, new FloorSet[]{FloorSet.stoneTiled, FloorSet.stoneBrick, FloorSet.swampStoneBrick}, new FloorSet[]{FloorSet.stoneTiled, FloorSet.stoneBrick, FloorSet.swampStoneBrick}, BiomeRegistry.FOREST));
        this.addPreset(85, new MystrilHomesteadGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.FOREST));
    }

    protected void addSnowPresets() {
        this.addPreset(100, new SmallMineEntranceGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.SNOW));
        this.addPreset(85, new BigCemeteryGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.SNOW));
        this.addPreset(85, new GuardTowerGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.SNOW));
        this.addPreset(70, new TrainingDummyPicnicGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.SNOW));
        this.addPreset(100, new SmallForgottenShrineGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.SNOW));
        this.addPreset(85, new LargeTempleRuinsGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.SNOW));
        this.addPreset(70, new VillagerCampGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.SNOW));
        this.addPreset(100, new FairyTreeCircleGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.SNOW));
        this.addPreset(85, new CaveHoboHomeGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.SNOW));
        this.addPreset(85, new WizardGardenHomeGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.SNOW));
        this.addPreset(70, new ChapelGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.SNOW));
        this.addPreset(85, new RetirementHomeGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.SNOW));
        this.addPreset(100, new HunterCabinGenerationPreset(new WallSet[]{WallSet.wood, WallSet.pine, WallSet.brick, WallSet.snowStone}, new FurnitureSet[]{FurnitureSet.maple, FurnitureSet.pine, FurnitureSet.birch}, BiomeRegistry.SNOW));
        this.addPreset(100, new FishingHutGenerationPreset(new WallSet[]{WallSet.wood, WallSet.pine, WallSet.brick, WallSet.snowStone}, new int[]{TileRegistry.snowStonePathID, TileRegistry.woodPathID, TileRegistry.stonePathID}, BiomeRegistry.SNOW));
        this.addPreset(85, new FarmersRefugeGenerationPreset(new WallSet[]{WallSet.wood, WallSet.pine, WallSet.brick, WallSet.snowStone}, new FurnitureSet[]{FurnitureSet.dungeon, FurnitureSet.pine, FurnitureSet.oak, FurnitureSet.maple}, new TreeSet[]{TreeSet.pine}, new BushSet[]{BushSet.blackberry}, BiomeRegistry.SNOW));
        this.addPreset(100, new AbandonedCampGenerationPreset(new WallSet[]{WallSet.wood, WallSet.pine, WallSet.brick, WallSet.snowStone}, new FurnitureSet[]{FurnitureSet.dungeon, FurnitureSet.pine, FurnitureSet.oak, FurnitureSet.maple}, new TreeSet[]{TreeSet.pine}, BiomeRegistry.SNOW));
        this.addPreset(70, new CrashedMeteorGenerationPreset(new CrystalSet[]{CrystalSet.sapphire}, BiomeRegistry.SNOW));
        this.addPreset(70, new ChristmasHouseGenerationPreset(BiomeRegistry.SNOW));
        this.addPreset(85, new AbandonedMineshaftGenerationPreset(BiomeRegistry.SNOW, RockAndOreSet.snow, new WallSet[]{WallSet.wood, WallSet.pine, WallSet.brick, WallSet.snowStone}, new FurnitureSet[]{FurnitureSet.dungeon, FurnitureSet.pine, FurnitureSet.oak, FurnitureSet.maple}, new TreeSet[]{TreeSet.pine}, new String[]{"zombie", "zombiearcher", "trapperzombie"}, BiomeRegistry.SNOW));
        this.addPreset(85, new MystrilHomesteadGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.SNOW));
    }

    protected void addPlainsPresets() {
        this.addPreset(100, new SmallMineEntranceGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.PLAINS));
        this.addPreset(85, new BigCemeteryGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.PLAINS));
        this.addPreset(70, new DavesPlantHouseGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.PLAINS));
        this.addPreset(70, new TrainingDummyPicnicGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.PLAINS));
        this.addPreset(100, new SmallForgottenShrineGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.PLAINS));
        this.addPreset(100, new SmallOvergrownShipwreckGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.PLAINS));
        this.addPreset(70, new DuelingGroundsGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.PLAINS));
        this.addPreset(85, new LargeTempleRuinsGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.PLAINS));
        this.addPreset(70, new VillagerCampGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.PLAINS));
        this.addPreset(100, new FairyTreeCircleGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.PLAINS));
        this.addPreset(85, new WizardGardenHomeGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.PLAINS));
        this.addPreset(85, new GoblinVillageGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.PLAINS));
        this.addPreset(70, new ChapelGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.PLAINS));
        this.addPreset(85, new RetirementHomeGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.PLAINS));
        this.addPreset(100, new HunterCabinGenerationPreset(new WallSet[]{WallSet.wood, WallSet.pine, WallSet.brick}, new FurnitureSet[]{FurnitureSet.maple, FurnitureSet.birch, FurnitureSet.oak}, BiomeRegistry.PLAINS));
        this.addPreset(100, new FishingHutGenerationPreset(new WallSet[]{WallSet.wood, WallSet.pine, WallSet.brick}, new int[]{TileRegistry.woodPathID, TileRegistry.stonePathID}, BiomeRegistry.PLAINS));
        this.addPreset(85, new FarmersRefugeGenerationPreset(new WallSet[]{WallSet.wood, WallSet.pine, WallSet.brick}, new FurnitureSet[]{FurnitureSet.maple, FurnitureSet.birch, FurnitureSet.oak}, new TreeSet[]{TreeSet.birch, TreeSet.maple}, new BushSet[]{BushSet.raspberry}, BiomeRegistry.PLAINS));
        this.addPreset(100, new BrokenHusbandryFenceGenerationPreset(new FenceSet[]{FenceSet.wood, FenceSet.stone, FenceSet.iron}, BiomeRegistry.PLAINS));
        this.addPreset(100, new AbandonedCampGenerationPreset(new WallSet[]{WallSet.wood, WallSet.pine, WallSet.brick}, new FurnitureSet[]{FurnitureSet.maple, FurnitureSet.birch, FurnitureSet.oak}, new TreeSet[]{TreeSet.birch, TreeSet.maple}, BiomeRegistry.PLAINS));
        this.addPreset(100, new ForgottenShrineGenerationPreset(new WallSet[]{WallSet.wood, WallSet.pine, WallSet.brick}, new FurnitureSet[]{FurnitureSet.maple, FurnitureSet.birch, FurnitureSet.oak}, new TreeSet[]{TreeSet.birch, TreeSet.maple}, new HedgeSet[]{HedgeSet.plains}, BiomeRegistry.PLAINS));
        this.addPreset(70, new CrashedMeteorGenerationPreset(new CrystalSet[]{CrystalSet.sapphire}, BiomeRegistry.PLAINS));
        this.addPreset(100, new TravellersCampsiteGenerationPreset(new FenceSet[]{FenceSet.wood, FenceSet.stone}, new TreeSet[]{TreeSet.maple, TreeSet.birch}, new BushSet[]{BushSet.raspberry}, BiomeRegistry.PLAINS));
        this.addPreset(85, new AbandonedFountainGenerationPreset(new TreeSet[]{TreeSet.birch, TreeSet.maple}, new HedgeSet[]{HedgeSet.plains}, BiomeRegistry.PLAINS));
        this.addPreset(85, new AbandonedMineshaftGenerationPreset(BiomeRegistry.PLAINS, RockAndOreSet.plains, new WallSet[]{WallSet.wood, WallSet.stone, WallSet.brick}, new FurnitureSet[]{FurnitureSet.maple, FurnitureSet.birch, FurnitureSet.oak}, new TreeSet[]{TreeSet.birch, TreeSet.maple}, new String[]{"zombie", "zombiearcher"}, BiomeRegistry.PLAINS));
        this.addPreset(85, new BigPirateShipGenerationPreset(BiomeRegistry.PLAINS));
        this.addPreset(85, new SunkenHomeGenerationPreset(new WallSet[]{WallSet.willow, WallSet.swampStone, WallSet.wood}, new FurnitureSet[]{FurnitureSet.maple, FurnitureSet.oak}, new TreeSet[]{TreeSet.birch, TreeSet.maple}, new BushSet[]{BushSet.raspberry, BushSet.blueberry, BushSet.blackberry}, new BushSet[]{BushSet.raspberry, BushSet.blueberry, BushSet.blackberry}, new FenceSet[]{FenceSet.iron, FenceSet.wood, FenceSet.stone}, new FloorSet[]{FloorSet.graniteBrick, FloorSet.stoneTiled, FloorSet.stoneBrick, FloorSet.swampStoneBrick}, new FloorSet[]{FloorSet.graniteBrick, FloorSet.stoneTiled, FloorSet.stoneBrick, FloorSet.swampStoneBrick}, BiomeRegistry.PLAINS));
        this.addPreset(85, new MystrilHomesteadGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.PLAINS));
    }

    protected void addSwampPresets() {
        this.addPreset(100, new SmallMineEntranceGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.SWAMP));
        this.addPreset(85, new BigCemeteryGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.SWAMP));
        this.addPreset(70, new SlimeRanchGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.SWAMP));
        this.addPreset(70, new DavesPlantHouseGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.SWAMP));
        this.addPreset(85, new SwampyCaveLakeGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.SWAMP));
        this.addPreset(70, new TrainingDummyPicnicGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.SWAMP));
        this.addPreset(100, new SmallForgottenShrineGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.SWAMP));
        this.addPreset(85, new VampireChurchGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.SWAMP));
        this.addPreset(85, new LargeTempleRuinsGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.SWAMP));
        this.addPreset(70, new VillagerCampGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.SWAMP));
        this.addPreset(100, new SwampSettlerRuinsGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.SWAMP));
        this.addPreset(100, new FairyTreeCircleGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.SWAMP));
        this.addPreset(85, new WizardGardenHomeGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.SWAMP));
        this.addPreset(70, new ChapelGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.SWAMP));
        this.addPreset(85, new RetirementHomeGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.SWAMP));
        this.addPreset(100, new HunterCabinGenerationPreset(new WallSet[]{WallSet.wood, WallSet.swampStone}, new FurnitureSet[]{FurnitureSet.spruce, FurnitureSet.dungeon, FurnitureSet.oak}, BiomeRegistry.SWAMP));
        this.addPreset(100, new FishingHutGenerationPreset(new WallSet[]{WallSet.wood, WallSet.swampStone}, new int[]{TileRegistry.woodPathID, TileRegistry.stonePathID}, BiomeRegistry.SWAMP));
        this.addPreset(100, new BrokenHusbandryFenceGenerationPreset(new FenceSet[]{FenceSet.wood, FenceSet.stone, FenceSet.iron}, BiomeRegistry.SWAMP));
        this.addPreset(85, new FarmersRefugeGenerationPreset(new WallSet[]{WallSet.wood, WallSet.swampStone}, new FurnitureSet[]{FurnitureSet.spruce, FurnitureSet.dungeon, FurnitureSet.oak}, new TreeSet[]{TreeSet.willow, TreeSet.spruce}, new BushSet[]{BushSet.blackberry}, BiomeRegistry.SWAMP));
        this.addPreset(100, new AbandonedCampGenerationPreset(new WallSet[]{WallSet.wood, WallSet.swampStone}, new FurnitureSet[]{FurnitureSet.spruce, FurnitureSet.dungeon, FurnitureSet.oak}, new TreeSet[]{TreeSet.willow, TreeSet.spruce}, BiomeRegistry.SWAMP));
        this.addPreset(100, new ForgottenShrineGenerationPreset(new WallSet[]{WallSet.wood, WallSet.swampStone}, new FurnitureSet[]{FurnitureSet.spruce, FurnitureSet.dungeon, FurnitureSet.oak}, new TreeSet[]{TreeSet.willow, TreeSet.spruce}, new HedgeSet[]{HedgeSet.swamp}, BiomeRegistry.SWAMP));
        this.addPreset(70, new CrashedMeteorGenerationPreset(new CrystalSet[]{CrystalSet.amethyst}, BiomeRegistry.SWAMP));
        this.addPreset(100, new TravellersCampsiteGenerationPreset(new FenceSet[]{FenceSet.wood, FenceSet.iron, FenceSet.stone}, new TreeSet[]{TreeSet.willow}, new BushSet[]{BushSet.blackberry}, BiomeRegistry.SWAMP));
        this.addPreset(100, new AbandonedFountainGenerationPreset(new TreeSet[]{TreeSet.willow, TreeSet.spruce}, new HedgeSet[]{HedgeSet.swamp}, BiomeRegistry.SWAMP));
        this.addPreset(85, new AbandonedMineshaftGenerationPreset(BiomeRegistry.SWAMP, RockAndOreSet.swamp, new WallSet[]{WallSet.wood, WallSet.swampStone}, new FurnitureSet[]{FurnitureSet.spruce, FurnitureSet.dungeon, FurnitureSet.oak}, new TreeSet[]{TreeSet.willow, TreeSet.spruce}, new String[]{"zombie", "zombiearcher", "swampzombie", "swampshooter"}, BiomeRegistry.SWAMP));
        this.addPreset(100, new MageShopGenerationPreset(new FurnitureSet[]{FurnitureSet.spruce, FurnitureSet.dungeon, FurnitureSet.oak}, BiomeRegistry.SWAMP));
        this.addPreset(85, new SunkenHomeGenerationPreset(new WallSet[]{WallSet.willow, WallSet.swampStone, WallSet.wood}, new FurnitureSet[]{FurnitureSet.spruce, FurnitureSet.dungeon, FurnitureSet.oak, FurnitureSet.willow, FurnitureSet.deadwood}, new TreeSet[]{TreeSet.willow, TreeSet.spruce}, new BushSet[]{BushSet.raspberry, BushSet.blueberry, BushSet.blackberry}, new BushSet[]{BushSet.raspberry, BushSet.blueberry, BushSet.blackberry}, new FenceSet[]{FenceSet.iron, FenceSet.wood, FenceSet.stone}, new FloorSet[]{FloorSet.stoneTiled, FloorSet.stoneBrick, FloorSet.swampStoneBrick}, new FloorSet[]{FloorSet.stoneTiled, FloorSet.stoneBrick, FloorSet.swampStoneBrick}, BiomeRegistry.SWAMP));
        this.addPreset(85, new MystrilHomesteadGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.SWAMP));
    }

    protected void addDesertPresets() {
        this.addPreset(100, new SmallMineEntranceGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.DESERT));
        this.addPreset(85, new GuardTowerGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.DESERT));
        this.addPreset(100, new SmallOvergrownShipwreckGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.DESERT));
        this.addPreset(85, new DuelingGroundsGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.DESERT));
        this.addPreset(85, new LargeTempleRuinsGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.DESERT));
        this.addPreset(70, new VillagerCampGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.DESERT));
        this.addPreset(100, new CrazedBlacksmithHideoutGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.DESERT));
        this.addPreset(85, new RetirementHomeGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.DESERT));
        this.addPreset(100, new HunterCabinGenerationPreset(new WallSet[]{WallSet.wood, WallSet.palm, WallSet.sandstone}, new FurnitureSet[]{FurnitureSet.palm, FurnitureSet.oak}, BiomeRegistry.DESERT));
        this.addPreset(100, new FishingHutGenerationPreset(new WallSet[]{WallSet.wood, WallSet.palm, WallSet.sandstone}, new int[]{TileRegistry.woodPathID, TileRegistry.stonePathID, TileRegistry.sandstonePathID}, BiomeRegistry.DESERT));
        this.addPreset(85, new FarmersRefugeGenerationPreset(new WallSet[]{WallSet.wood, WallSet.palm, WallSet.sandstone}, new FurnitureSet[]{FurnitureSet.palm, FurnitureSet.oak}, new TreeSet[]{TreeSet.palm}, new BushSet[]{BushSet.raspberry}, BiomeRegistry.DESERT));
        this.addPreset(70, new CrashedMeteorGenerationPreset(new CrystalSet[]{CrystalSet.amethyst}, BiomeRegistry.DESERT));
        this.addPreset(85, new DesertTavernGenerationPreset(new FurnitureSet[]{FurnitureSet.palm, FurnitureSet.maple, FurnitureSet.birch}, new WallSet[]{WallSet.palm, WallSet.sandstone}, BiomeRegistry.DESERT));
        this.addPreset(85, new AbandonedMineshaftGenerationPreset(BiomeRegistry.DESERT, RockAndOreSet.desert, new WallSet[]{WallSet.wood, WallSet.palm, WallSet.sandstone}, new FurnitureSet[]{FurnitureSet.palm, FurnitureSet.oak}, new TreeSet[]{TreeSet.palm}, new String[]{"zombie", "zombiearcher", "mummy"}, BiomeRegistry.DESERT));
        this.addPreset(70, new BigPirateShipGenerationPreset(BiomeRegistry.DESERT));
        this.addPreset(85, new MystrilHomesteadGenerationPreset(LevelIdentifier.SURFACE_IDENTIFIER, BiomeRegistry.DESERT));
    }
}

