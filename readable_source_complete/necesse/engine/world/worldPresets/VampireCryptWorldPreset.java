/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.worldPresets;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.biomeGenerator.BiomeGeneratorStack;
import necesse.engine.world.worldPresets.LevelPresetsRegion;
import necesse.engine.world.worldPresets.LinesGenerationWorldPreset;
import necesse.engine.world.worldPresets.WorldPreset;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.generationModules.CellAutomaton;
import necesse.level.maps.levelBuffManager.LevelModifiers;
import necesse.level.maps.presets.PresetUtils;
import necesse.level.maps.presets.set.WallSet;

public class VampireCryptWorldPreset
extends WorldPreset {
    public Biome biome;
    public LevelIdentifier levelIdentifier;
    public float presetsPerRegion;

    public VampireCryptWorldPreset(Biome biome, LevelIdentifier levelIdentifier, float presetsPerRegion) {
        this.biome = biome;
        this.levelIdentifier = levelIdentifier;
        this.presetsPerRegion = presetsPerRegion;
    }

    @Override
    public boolean shouldAddToRegion(LevelPresetsRegion presetsRegion) {
        return presetsRegion.identifier.equals(this.levelIdentifier) && presetsRegion.hasAnyOfBiome(this.biome.getID());
    }

    @Override
    public void addToRegion(GameRandom random, LevelPresetsRegion presetsRegion, final BiomeGeneratorStack generatorStack, PerformanceTimerManager performanceTimer) {
        int total = VampireCryptWorldPreset.getTotalBiomePoints(random, presetsRegion, this.biome, this.presetsPerRegion);
        for (int i = 0; i < total; ++i) {
            final Dimension size = new Dimension(12, 12);
            Point tile = VampireCryptWorldPreset.findRandomBiomePresetTile(random, presetsRegion, generatorStack, this.biome, 20, size, "minibiomes", new WorldPreset.ValidTilePredicate(){

                @Override
                public boolean isValidPosition(int tileX, int tileY) {
                    return !generatorStack.isCaveRiverOrLava(tileX + size.width / 2, tileY + size.height / 2);
                }
            });
            if (tile == null) continue;
            final LinesGenerationWorldPreset lg = new LinesGenerationWorldPreset(tile.x + size.width / 2, tile.y + size.height / 2).addRandomArms(random, 2, 3.0f, 5.0f, 8.0f, 10.0f);
            final AtomicInteger cryptRotation = new AtomicInteger(i * 3);
            if (!lg.isWithinPresetRegionBounds(presetsRegion)) continue;
            presetsRegion.addPreset((WorldPreset)this, lg.getOccupiedTileRectangle(), "minibiomes", new LevelPresetsRegion.WorldPresetPlaceFunction(){

                @Override
                public void place(GameRandom random, Level level, PerformanceTimerManager timer) {
                    GameObject cryptGrass = ObjectRegistry.getObject("cryptgrass");
                    CellAutomaton ca = lg.doCellularAutomaton(random);
                    ca.streamAliveOrdered().forEachOrdered(tile -> {
                        level.setTile(tile.x, tile.y, TileRegistry.cryptAshID);
                        level.setObject(tile.x, tile.y, 0);
                    });
                    ca.placeEdgeWalls(level, WallSet.stone.wall, true);
                    ArrayList coffinTiles = new ArrayList();
                    ca.streamAliveOrdered().forEachOrdered(tile -> {
                        if (random.getChance(0.2f) && cryptGrass.canPlace(level, tile.x, tile.y, 0, false) == null) {
                            cryptGrass.placeObject(level, tile.x, tile.y, 0, false);
                        }
                        if (level.getObjectID(tile.x, tile.y) == 0 && level.getObjectID(tile.x - 1, tile.y) == 0 && level.getObjectID(tile.x + 1, tile.y) == 0 && level.getObjectID(tile.x, tile.y - 1) == 0 && level.getObjectID(tile.x, tile.y + 1) == 0) {
                            if (random.getChance(0.2f)) {
                                Point[] clearPoints;
                                int rotation = random.nextInt(4);
                                if (level.getRelativeAnd(tile.x, tile.y, PresetUtils.getRotatedPoints(0, 0, rotation, clearPoints = new Point[]{new Point(-1, -1), new Point(1, -1), new Point(0, -2)}), (tileX, tileY) -> ca.isAlive((int)tileX, (int)tileY) && level.getObjectID((int)tileX, (int)tileY) == 0)) {
                                    ObjectRegistry.getObject(ObjectRegistry.getObjectID("stonecoffin")).placeObject(level, tile.x, tile.y, rotation, false);
                                    coffinTiles.add(tile);
                                }
                            } else if (random.getChance(0.06f)) {
                                level.setObject(tile.x, tile.y, ObjectRegistry.getObjectID("stonecolumn"));
                            } else if (random.getChance(0.3f)) {
                                random.runOneOf(() -> level.setObject(tile.x, tile.y, ObjectRegistry.getObjectID("gravestone1"), random.nextInt(4)), () -> level.setObject(tile.x, tile.y, ObjectRegistry.getObjectID("gravestone2"), random.nextInt(4)));
                            }
                        }
                    });
                    if (!coffinTiles.isEmpty()) {
                        Point tile2 = (Point)random.getOneOf(coffinTiles);
                        LootTablePresets.caveCryptUniqueItems.applyToLevel(random, level.buffManager.getModifier(LevelModifiers.LOOT).floatValue(), level, tile2.x, tile2.y, level, cryptRotation);
                    }
                    for (Point tile3 : coffinTiles) {
                        LootTablePresets.caveCryptCoffin.applyToLevel(random, level.buffManager.getModifier(LevelModifiers.LOOT).floatValue(), level, tile3.x, tile3.y, level, cryptRotation);
                    }
                    ca.spawnMobs(level, random, "vampire", 25, 45, 1, 4);
                }
            }).setRemoveIfWithinSpawnRegionRange(2);
        }
    }
}

