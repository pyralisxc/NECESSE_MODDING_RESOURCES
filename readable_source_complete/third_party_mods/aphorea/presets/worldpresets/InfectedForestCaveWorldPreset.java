/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.gameLoop.tickManager.PerformanceTimerManager
 *  necesse.engine.registries.ObjectRegistry
 *  necesse.engine.util.GameRandom
 *  necesse.engine.util.LevelIdentifier
 *  necesse.engine.world.biomeGenerator.BiomeGeneratorStack
 *  necesse.engine.world.worldPresets.LevelPresetsRegion
 *  necesse.engine.world.worldPresets.LinesGenerationWorldPreset
 *  necesse.engine.world.worldPresets.WorldPreset
 *  necesse.engine.world.worldPresets.WorldPreset$ValidTilePredicate
 *  necesse.level.gameObject.GameObject
 *  necesse.level.maps.biomes.Biome
 *  necesse.level.maps.generationModules.CellAutomaton
 *  necesse.level.maps.levelBuffManager.LevelModifiers
 *  necesse.level.maps.presets.PresetUtils
 */
package aphorea.presets.worldpresets;

import aphorea.registry.AphLootTables;
import aphorea.registry.AphTiles;
import java.awt.Dimension;
import java.awt.Point;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.biomeGenerator.BiomeGeneratorStack;
import necesse.engine.world.worldPresets.LevelPresetsRegion;
import necesse.engine.world.worldPresets.LinesGenerationWorldPreset;
import necesse.engine.world.worldPresets.WorldPreset;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.generationModules.CellAutomaton;
import necesse.level.maps.levelBuffManager.LevelModifiers;
import necesse.level.maps.presets.PresetUtils;

public class InfectedForestCaveWorldPreset
extends WorldPreset {
    public Biome biome;
    public LevelIdentifier levelIdentifier;
    public float presetsPerRegion;

    public InfectedForestCaveWorldPreset(Biome biome, LevelIdentifier levelIdentifier, float presetsPerRegion) {
        this.biome = biome;
        this.levelIdentifier = levelIdentifier;
        this.presetsPerRegion = presetsPerRegion;
    }

    public boolean shouldAddToRegion(LevelPresetsRegion presetsRegion) {
        return presetsRegion.identifier.equals(this.levelIdentifier) && presetsRegion.hasAnyOfBiome(this.biome.getID());
    }

    public void addToRegion(GameRandom random, LevelPresetsRegion presetsRegion, final BiomeGeneratorStack generatorStack, PerformanceTimerManager performanceTimer) {
        int total = InfectedForestCaveWorldPreset.getTotalBiomePoints((GameRandom)random, (LevelPresetsRegion)presetsRegion, (Biome)this.biome, (float)this.presetsPerRegion);
        for (int i = 0; i < total; ++i) {
            LinesGenerationWorldPreset lg;
            final Dimension size = new Dimension(random.getIntBetween(14, 20), random.getIntBetween(14, 20));
            Point tile = InfectedForestCaveWorldPreset.findRandomBiomePresetTile((GameRandom)random, (LevelPresetsRegion)presetsRegion, (BiomeGeneratorStack)generatorStack, (Biome)this.biome, (int)20, (Dimension)size, (String)"minibiomes", (WorldPreset.ValidTilePredicate)new WorldPreset.ValidTilePredicate(){

                public boolean isValidPosition(int tileX, int tileY) {
                    return !generatorStack.isCaveRiverOrLava(tileX + size.width / 2, tileY + size.height / 2);
                }
            });
            if (tile == null || !(lg = new LinesGenerationWorldPreset(tile.x + size.width / 2, tile.y + size.height / 2).addRandomArms(random, 4, 4.0f, 7.0f, 4.0f, 6.0f)).isWithinPresetRegionBounds(presetsRegion)) continue;
            presetsRegion.addPreset((WorldPreset)this, lg.getOccupiedTileRectangle(), new String[]{"minibiomes", "loot"}, (random1, level, timer) -> {
                int tileID = AphTiles.INFECTED_GRASS;
                GameObject infectedTree = ObjectRegistry.getObject((String)"infectedtree");
                int centerX = tile.x + size.width / 2 + (size.width % 2 == 1 ? random1.getIntBetween(0, 1) : 0);
                int centerY = tile.y + size.height / 2 + (size.width % 2 == 1 ? random1.getIntBetween(0, 1) : 0);
                CellAutomaton ca = lg.doCellularAutomaton(random1);
                ca.streamAliveOrdered().forEachOrdered(tile1 -> {
                    level.setTile(tile1.x, tile1.y, tileID);
                    level.setObject(tile1.x, tile1.y, 0);
                    if (tile1.x == centerX && tile1.y == centerY) {
                        if (random1.getChance(0.8f)) {
                            level.setObject(centerX, centerY, ObjectRegistry.getObjectID((String)"barrel"));
                            AphLootTables.infectedLootLake.applyToLevel(random1, ((Float)level.buffManager.getModifier(LevelModifiers.LOOT)).floatValue(), level, tile1.x, tile1.y, new Object[]{level});
                        } else {
                            level.setObject(centerX, centerY, ObjectRegistry.getObjectID((String)"infectedtrialentrance"));
                        }
                    }
                });
                ca.streamAliveOrdered().forEachOrdered(tile1 -> {
                    Point[] clearPoints;
                    int rotation;
                    if (level.getObjectID(tile1.x, tile1.y) == 0 && level.getObjectID(tile1.x - 1, tile1.y) == 0 && level.getObjectID(tile1.x + 1, tile1.y) == 0 && level.getObjectID(tile1.x, tile1.y - 1) == 0 && level.getObjectID(tile1.x, tile1.y + 1) == 0 && random1.getChance(0.08f) && level.getRelativeAnd(tile1.x, tile1.y, PresetUtils.getRotatedPoints((int)0, (int)0, (int)(rotation = random1.nextInt(4)), (Point[])(clearPoints = new Point[]{new Point(-1, -1), new Point(1, -1)})), (tileX, tileY) -> ca.isAlive(tileX.intValue(), tileY.intValue()) && level.getObjectID(tileX.intValue(), tileY.intValue()) == 0)) {
                        infectedTree.placeObject(level, tile1.x, tile1.y, rotation, false);
                    }
                });
                ca.spawnMobs(level, random1, "infectedtreant", 3, 45, 2, 5);
            });
        }
    }
}

