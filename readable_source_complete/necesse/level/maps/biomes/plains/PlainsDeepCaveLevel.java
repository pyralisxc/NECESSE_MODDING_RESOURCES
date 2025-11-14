/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.biomes.plains;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.concurrent.atomic.AtomicInteger;
import necesse.engine.GameEvents;
import necesse.engine.events.worldGeneration.GenerateCaveLayoutEvent;
import necesse.engine.events.worldGeneration.GenerateCaveMiniBiomesEvent;
import necesse.engine.events.worldGeneration.GenerateCaveOresEvent;
import necesse.engine.events.worldGeneration.GenerateCaveStructuresEvent;
import necesse.engine.events.worldGeneration.GeneratedCaveLayoutEvent;
import necesse.engine.events.worldGeneration.GeneratedCaveMiniBiomesEvent;
import necesse.engine.events.worldGeneration.GeneratedCaveOresEvent;
import necesse.engine.events.worldGeneration.GeneratedCaveStructuresEvent;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.WorldEntity;
import necesse.entity.levelEvent.SpiritCorruptedLevelEvent;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.biomes.plains.PlainsCaveLevel;
import necesse.level.maps.generationModules.CaveGeneration;
import necesse.level.maps.generationModules.CellAutomaton;
import necesse.level.maps.generationModules.GenerationTools;
import necesse.level.maps.generationModules.LinesGeneration;
import necesse.level.maps.generationModules.PresetGeneration;
import necesse.level.maps.levelBuffManager.LevelModifiers;
import necesse.level.maps.levelData.CursedCroneArenasLevelData;
import necesse.level.maps.presets.PresetUtils;
import necesse.level.maps.presets.RandomCaveChestRoom;
import necesse.level.maps.presets.TheCursedCroneArenaPreset;
import necesse.level.maps.presets.caveRooms.CaveRuins;
import necesse.level.maps.presets.modularPresets.runeboundTombPresets.RuneboundTombPreset;
import necesse.level.maps.presets.set.ChestRoomSet;
import necesse.level.maps.presets.set.FurnitureSet;
import necesse.level.maps.presets.set.WallSet;

public class PlainsDeepCaveLevel
extends PlainsCaveLevel {
    public long nextSpiritCorruptedTime;

    public PlainsDeepCaveLevel(LevelIdentifier identifier, int width, int height, WorldEntity worldEntity) {
        super(identifier, width, height, worldEntity);
    }

    public PlainsDeepCaveLevel(int islandX, int islandY, int dimension, WorldEntity worldEntity, Biome biome) {
        super(new LevelIdentifier(islandX, islandY, dimension), 300, 300, worldEntity);
        this.baseBiome = biome;
        this.isCave = true;
        this.generateLevel();
    }

    public static Point getArenaCenterTile(Level level) {
        GameRandom random = new GameRandom(level.getSeed()).nextSeeded(level.getIslandDimension()).nextSeeded(64);
        return new Point(random.getIntBetween(80, level.tileWidth - 80), random.getIntBetween(80, level.tileHeight - 80));
    }

    @Override
    public void generateLevel() {
        int deepRockTile = TileRegistry.getTileID("basaltrocktile");
        CaveGeneration cg = new CaveGeneration(this, "basaltrocktile", "basaltrock");
        GameEvents.triggerEvent(new GenerateCaveLayoutEvent(this, cg), e -> cg.generateLevel(0.39f, 4, 3, 6));
        GameEvents.triggerEvent(new GeneratedCaveLayoutEvent(this, cg));
        Point arenaCenterTile = PlainsDeepCaveLevel.getArenaCenterTile(this);
        GameEvents.triggerEvent(new GenerateCaveMiniBiomesEvent(this, cg), e -> {
            int[] riverAngles;
            GenerationTools.generateRandomObjectVeinsOnTile(this, cg.random, 0.2f, 4, 8, deepRockTile, ObjectRegistry.getObjectID("wildcaveglow"), 0.2f, false);
            GenerationTools.generateRandomSmoothTileVeins(this, cg.random, 0.07f, 2, 3.0f, 9.0f, 3.0f, 6.0f, TileRegistry.getTileID("lavatile"), 0.25f, true);
            LinesGeneration lg2 = new LinesGeneration(arenaCenterTile.x, arenaCenterTile.y, 15.0f);
            for (int angle : riverAngles = new int[]{45, cg.random.getChance(0.5f) ? -1 : 45, 135, cg.random.getChance(0.5f) ? -1 : 135, 225, cg.random.getChance(0.5f) ? -1 : 225, 270, cg.random.getChance(0.5f) ? -1 : 270, 315, cg.random.getChance(0.5f) ? -1 : 315}) {
                if (angle == -1) continue;
                lg2.addMultiArm(cg.random, angle, 20, Math.max(this.tileWidth, this.tileHeight), 5.0f, 10.0f, 4.0f, 6.0f, armLG -> armLG.x2 < 0 || armLG.x2 > this.tileWidth || armLG.y2 < 0 || armLG.y2 > this.tileHeight);
            }
            CellAutomaton ca = lg2.doCellularAutomaton(cg.random);
            int waterGrassID = ObjectRegistry.getObjectID("watergrass");
            int waterLanternID = ObjectRegistry.getObjectID("waterlantern");
            ca.forEachTile(this, (level, tileX, tileY) -> {
                level.setTile(tileX, tileY, TileRegistry.spiritWaterID);
                if (cg.random.getChance(0.1f)) {
                    level.setObject(tileX, tileY, waterGrassID);
                } else if (cg.random.getChance(0.02f)) {
                    level.setObject(tileX, tileY, waterLanternID);
                }
            });
            this.liquidManager.calculateShores();
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("basaltcaverock"), 0.005f);
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("basaltcaverocksmall"), 0.01f);
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("dryadtree"), 0.02f);
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("cavebirchtree"), 0.02f);
            cg.generateRandomSingleRocks(ObjectRegistry.getObjectID("cavemapletree"), 0.02f);
            System.out.println("hello");
            GameObject crystalClusterSmall = ObjectRegistry.getObject("topazclustersmall");
            GenerationTools.generateRandomSmoothVeinsL(this, cg.random, 0.005f, 4, 4.0f, 7.0f, 4.0f, 6.0f, lg -> {
                CellAutomaton ca = lg.doCellularAutomaton(cg.random);
                ca.streamAliveOrdered().forEachOrdered(tile -> {
                    cg.addIllegalCrateTile(tile.x, tile.y);
                    this.setTile(tile.x, tile.y, TileRegistry.getTileID("topazgravel"));
                    this.setObject(tile.x, tile.y, 0);
                });
                ca.streamAliveOrdered().forEachOrdered(tile -> {
                    Point[] clearPoints;
                    int rotation;
                    if (this.getObjectID(tile.x, tile.y) == 0 && this.getObjectID(tile.x - 1, tile.y) == 0 && this.getObjectID(tile.x + 1, tile.y) == 0 && this.getObjectID(tile.x, tile.y - 1) == 0 && this.getObjectID(tile.x, tile.y + 1) == 0 && cg.random.getChance(0.08f) && this.getRelativeAnd(tile.x, tile.y, PresetUtils.getRotatedPoints(0, 0, rotation = cg.random.nextInt(4), clearPoints = new Point[]{new Point(-1, -1), new Point(1, -1)}), (tileX, tileY) -> ca.isAlive((int)tileX, (int)tileY) && this.getObjectID((int)tileX, (int)tileY) == 0)) {
                        ObjectRegistry.getObject(ObjectRegistry.getObjectID("topazcluster")).placeObject(this, tile.x, tile.y, rotation, false);
                    }
                    if (cg.random.getChance(0.3f) && crystalClusterSmall.canPlace(this, tile.x, tile.y, 0, false) == null) {
                        crystalClusterSmall.placeObject(this, tile.x, tile.y, 0, false);
                    }
                });
            });
        });
        GameEvents.triggerEvent(new GeneratedCaveMiniBiomesEvent(this, cg));
        GameEvents.triggerEvent(new GenerateCaveOresEvent(this, cg), e -> {
            cg.generateOreVeins(0.05f, 3, 6, ObjectRegistry.getObjectID("copperorebasaltrock"));
            cg.generateOreVeins(0.25f, 3, 6, ObjectRegistry.getObjectID("ironorebasaltrock"));
            cg.generateOreVeins(0.15f, 3, 6, ObjectRegistry.getObjectID("goldorebasaltrock"));
            cg.generateOreVeins(0.2f, 3, 6, ObjectRegistry.getObjectID("tungstenorebasaltrock"));
            cg.generateOreVeins(0.05f, 3, 6, ObjectRegistry.getObjectID("lifequartzbasaltrock"));
            cg.generateOreVeins(0.15f, 3, 6, ObjectRegistry.getObjectID("amberbasaltrock"));
        });
        GameEvents.triggerEvent(new GeneratedCaveOresEvent(this, cg));
        PresetGeneration presets = new PresetGeneration(this);
        GameEvents.triggerEvent(new GenerateCaveStructuresEvent(this, cg, presets), e -> {
            presets.addOccupiedSpace(new Rectangle(arenaCenterTile.x - 15, arenaCenterTile.y - 15, 30, 30));
            AtomicInteger chestRoomRotation = new AtomicInteger();
            int chestRoomAmount = cg.random.getIntBetween(13, 18);
            for (int i = 0; i < chestRoomAmount; ++i) {
                RandomCaveChestRoom chestRoom = new RandomCaveChestRoom(cg.random, LootTablePresets.deepPlainsCaveChest, chestRoomRotation, ChestRoomSet.basalt);
                chestRoom.replaceTile(TileRegistry.basaltFloorID, cg.random.getOneOf(TileRegistry.basaltFloorID, TileRegistry.basaltPathID));
                presets.findRandomValidPositionAndApply(cg.random, 5, chestRoom, 10, true, true);
            }
            AtomicInteger caveRuinsRotation = new AtomicInteger();
            int caveRuinsCount = cg.random.getIntBetween(25, 35);
            for (int i = 0; i < caveRuinsCount; ++i) {
                WallSet wallSet = WallSet.basalt;
                FurnitureSet furnitureSet = cg.random.getOneOf(FurnitureSet.birch, FurnitureSet.maple);
                String floorStringID = cg.random.getOneOf("basaltfloor", "basaltpath");
                CaveRuins room = cg.random.getOneOf(CaveRuins.caveRuinGetters).get(cg.random, wallSet, furnitureSet, floorStringID, LootTablePresets.plainsDeepCaveRuinsChest, caveRuinsRotation);
                presets.findRandomValidPositionAndApply(cg.random, 5, room, 10, true, true);
            }
            cg.generateRandomCrates(0.03f, ObjectRegistry.getObjectID("crate"));
        });
        RuneboundTombPreset.generateRuneboundTombOnLevel(this, cg.random, presets);
        GameEvents.triggerEvent(new GeneratedCaveStructuresEvent(this, cg, presets));
        new TheCursedCroneArenaPreset().applyToLevelCentered(this, arenaCenterTile.x, arenaCenterTile.y);
        GenerationTools.checkValid(this);
    }

    @Override
    public LootTable getCrateLootTable() {
        return LootTablePresets.plainsDeepCrate;
    }

    @Override
    public GameMessage getLocationMessage(int tileX, int tileY) {
        return new LocalMessage("biome", "deepcave", "biome", this.getBiome(tileX, tileY).getLocalization());
    }

    @Override
    public void serverTick() {
        super.serverTick();
        if (this.nextSpiritCorruptedTime == 0L) {
            this.nextSpiritCorruptedTime = this.getTime() + (long)GameRandom.globalRandom.getIntBetween(25000, 40000);
        }
        if (this.nextSpiritCorruptedTime <= this.getTime()) {
            this.nextSpiritCorruptedTime = 0L;
            if (!this.buffManager.getModifier(LevelModifiers.SPIRIT_CORRUPTED).booleanValue()) {
                CursedCroneArenasLevelData arenas = CursedCroneArenasLevelData.getCursedCroneArenasData(this, false);
                if (arenas == null || !arenas.hasAnySpawnedCrone()) {
                    return;
                }
                this.entityManager.events.add(new SpiritCorruptedLevelEvent(this.getTime() + 25000L));
            }
        }
    }
}

