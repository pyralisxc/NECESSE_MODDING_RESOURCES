/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.gameLoop.tickManager.Performance
 *  necesse.engine.gameLoop.tickManager.PerformanceTimerManager
 *  necesse.engine.registries.ObjectRegistry
 *  necesse.engine.util.GameMath
 *  necesse.engine.util.GameRandom
 *  necesse.engine.util.LevelIdentifier
 *  necesse.engine.world.biomeGenerator.BiomeGeneratorStack
 *  necesse.engine.world.worldPresets.BiomeCenterWorldPreset
 *  necesse.engine.world.worldPresets.LevelPresetsRegion
 *  necesse.engine.world.worldPresets.RegionTileWorldPresetGenerator
 *  necesse.engine.world.worldPresets.WorldPreset
 *  necesse.level.gameObject.GameObject
 *  necesse.level.maps.generationModules.CellAutomaton
 *  necesse.level.maps.generationModules.LinesGeneration
 *  necesse.level.maps.presets.PresetUtils
 */
package aphorea.presets.worldpresets;

import aphorea.registry.AphBiomes;
import aphorea.registry.AphTiles;
import java.awt.Dimension;
import java.awt.Point;
import java.util.Objects;
import java.util.function.BiPredicate;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.biomeGenerator.BiomeGeneratorStack;
import necesse.engine.world.worldPresets.BiomeCenterWorldPreset;
import necesse.engine.world.worldPresets.LevelPresetsRegion;
import necesse.engine.world.worldPresets.RegionTileWorldPresetGenerator;
import necesse.engine.world.worldPresets.WorldPreset;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.generationModules.CellAutomaton;
import necesse.level.maps.generationModules.LinesGeneration;
import necesse.level.maps.presets.PresetUtils;

public class SpinelCavesWorldPreset
extends BiomeCenterWorldPreset {
    public Dimension size = new Dimension(40, 40);

    public SpinelCavesWorldPreset() {
        super(AphBiomes.INFECTED_FIELDS);
        this.randomAttempts = 50;
        this.sectionMaxRegionCount = 625;
        this.sectionMinRegionCount = 36;
    }

    public boolean shouldAddToRegion(LevelPresetsRegion presetsRegion) {
        return presetsRegion.identifier.equals(LevelIdentifier.CAVE_IDENTIFIER);
    }

    public boolean isValidSectionRegion(int regionX, int regionY, LevelPresetsRegion presetsRegion, BiomeGeneratorStack generatorStack) {
        return true;
    }

    public boolean isValidFinalRegion(int regionX, int regionY, LevelPresetsRegion presetsRegion, BiomeGeneratorStack generatorStack) {
        int centerTileX = GameMath.getTileCoordByRegion((int)regionX) + 8;
        int centerTileY = GameMath.getTileCoordByRegion((int)regionY) + 8;
        int startTileX = centerTileX - this.size.width / 2;
        int startTileY = centerTileY - this.size.height / 2;
        return this.runCornerCheck(startTileX, startTileY, this.size.width, this.size.height, (tileX, tileY) -> generatorStack.getLazyBiomeID(tileX, tileY) == this.biome.getID());
    }

    public void onFoundRegion(int regionX, int regionY, GameRandom random, LevelPresetsRegion presetsRegion, BiomeGeneratorStack generatorStack, PerformanceTimerManager performanceTimer) {
        float maxWidth;
        float minWidth;
        float maxRange;
        float minRange;
        int centerTileX = GameMath.getTileCoordByRegion((int)regionX) + 8;
        int centerTileY = GameMath.getTileCoordByRegion((int)regionY) + 8;
        float centerRange = 15.5f;
        int veinType = random.getIntBetween(0, 2);
        switch (veinType) {
            case 0: {
                minRange = 20.0f;
                maxRange = 40.0f;
                minWidth = 20.0f;
                maxWidth = 40.0f;
                break;
            }
            case 1: {
                minRange = 40.0f;
                maxRange = 80.0f;
                minWidth = 10.0f;
                maxWidth = 20.0f;
                break;
            }
            case 2: {
                minRange = 80.0f;
                maxRange = 120.0f;
                minWidth = 5.0f;
                maxWidth = 10.0f;
                break;
            }
            default: {
                minRange = 0.0f;
                maxRange = 0.0f;
                minWidth = 0.0f;
                maxWidth = 0.0f;
            }
        }
        LinesGeneration lg = new LinesGeneration(centerTileX, centerTileY, 15.5f);
        BiPredicate<LinesGeneration, Integer> isValidArm = (arm, padding) -> this.isTileWithinBounds(arm.x2, arm.y2, presetsRegion, (int)padding) && generatorStack.getLazyBiomeID(arm.x2, arm.y2) == this.biome.getID();
        int armAngle = random.nextInt(360);
        int arms = random.getIntBetween(3, 6);
        int anglePerArm = 360 / arms;
        for (int startTileX = 0; startTileX < arms; ++startTileX) {
            LinesGeneration lastArm = lg.addMultiArm(random, armAngle += anglePerArm, 15, random.getIntBetween(150, 200), minRange, maxRange, minWidth, maxWidth, armLG -> !isValidArm.test((LinesGeneration)armLG, 15));
            if (isValidArm.test(lastArm, 10)) continue;
            lg.removeLastLine();
        }
        if (!lg.getRoot().isEmpty()) {
            CellAutomaton ca = (CellAutomaton)Performance.record((PerformanceTimerManager)performanceTimer, (String)"doCellularAutomaton", () -> lg.doCellularAutomaton(random));
            Objects.requireNonNull(ca);
            Performance.record((PerformanceTimerManager)performanceTimer, (String)"cleanHardEdges", () -> ((CellAutomaton)ca).cleanHardEdges());
            int x = centerTileX - (int)Math.floor(15.5);
            while ((double)x <= (double)centerTileX + Math.ceil(15.5)) {
                int y = centerTileY - (int)Math.floor(15.5);
                while ((double)y <= (double)centerTileY + Math.ceil(15.5)) {
                    if (GameMath.getExactDistance((float)centerTileX, (float)centerTileY, (float)x, (float)y) <= 15.5f) {
                        ca.setAlive(x, y);
                    }
                    ++y;
                }
                ++x;
            }
            RegionTileWorldPresetGenerator tileGenerator = new RegionTileWorldPresetGenerator();
            Performance.record((PerformanceTimerManager)performanceTimer, (String)"addTileGenerator", () -> ca.streamAliveOrdered().forEach(tile -> tileGenerator.addTile(tile.x, tile.y, (random1, level, tileX, tileY, timer) -> Performance.record((PerformanceTimerManager)timer, (String)"setTile", () -> {
                level.setTile(tileX, tileY, AphTiles.SPINEL_GRAVEL);
                if (tileX == centerTileX && tileY == centerTileY) {
                    ObjectRegistry.getObject((String)"babylontower").placeObject(level, tileX - 1, tileY - 1, 2, false);
                    level.setObject(tileX, tileY, ObjectRegistry.getObjectID((String)"barrel"));
                }
                if (!level.getObject(tileX, tileY).getStringID().startsWith("babylontower")) {
                    level.setObject(tileX, tileY, 0);
                }
            }))));
            GameObject crystalClusterSmall = ObjectRegistry.getObject((String)"spinelclustersmall");
            GameObject crystalClusterBig = ObjectRegistry.getObject((String)"spinelcluster");
            GameObject fakeChest = ObjectRegistry.getObject((String)"fakespinelchest");
            ca.streamAliveOrdered().forEachOrdered(tile -> tileGenerator.addTile(tile.x, tile.y, (random1, level, tileX, tileY, timer) -> {
                if (level.getObjectID(tile.x, tile.y) == 0 && level.getObjectID(tile.x - 1, tile.y) == 0 && level.getObjectID(tile.x + 1, tile.y) == 0 && level.getObjectID(tile.x, tile.y - 1) == 0 && level.getObjectID(tile.x, tile.y + 1) == 0 && random.getChance(0.06f)) {
                    if (random.getChance(0.0167f)) {
                        fakeChest.placeObject(level, tile.x, tile.y, 2, false);
                    } else {
                        Point[] clearPoints;
                        int rotation = random.nextInt(4);
                        if (level.getRelativeAnd(tile.x, tile.y, PresetUtils.getRotatedPoints((int)0, (int)0, (int)rotation, (Point[])(clearPoints = new Point[]{new Point(-1, -1), new Point(1, -1)})), (tileX1, tileY1) -> ca.isAlive(tileX1.intValue(), tileY1.intValue()) && level.getObjectID(tileX1.intValue(), tileY1.intValue()) == 0)) {
                            crystalClusterBig.placeObject(level, tile.x, tile.y, rotation, false);
                        }
                    }
                }
                if (random.getChance(0.2f) && crystalClusterSmall.canPlace(level, tile.x, tile.y, 0, false) == null) {
                    crystalClusterSmall.placeObject(level, tile.x, tile.y, 0, false);
                }
            }));
            tileGenerator.forEachRegion((regionX1, regionY1, placeFunction) -> {
                int tileX = GameMath.getTileCoordByRegion((int)regionX1);
                int tileY = GameMath.getTileCoordByRegion((int)regionY1);
                presetsRegion.addPreset((WorldPreset)this, tileX, tileY, new Dimension(16, 16), new String[]{"minibiomes", "loot"}, placeFunction);
            });
        }
    }
}

