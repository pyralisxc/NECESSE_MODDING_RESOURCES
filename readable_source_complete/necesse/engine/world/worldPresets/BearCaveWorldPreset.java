/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.worldPresets;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.registries.MobRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.biomeGenerator.BiomeGeneratorStack;
import necesse.engine.world.worldPresets.LevelPresetsRegion;
import necesse.engine.world.worldPresets.LinesGenerationWorldPreset;
import necesse.engine.world.worldPresets.WorldPreset;
import necesse.entity.mobs.Mob;
import necesse.entity.objectEntity.PortalObjectEntity;
import necesse.inventory.lootTable.LootTablePresets;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.generationModules.CellAutomaton;

public class BearCaveWorldPreset
extends WorldPreset {
    public Biome biome;
    public LevelIdentifier levelIdentifier;
    public float presetsPerRegion;

    public BearCaveWorldPreset(Biome biome, LevelIdentifier levelIdentifier, float presetsPerRegion) {
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
        int total = BearCaveWorldPreset.getTotalBiomePoints(random, presetsRegion, this.biome, this.presetsPerRegion);
        for (int i = 0; i < total; ++i) {
            LinesGenerationWorldPreset lg;
            final Dimension size = new Dimension(20, 20);
            Point tile = BearCaveWorldPreset.findRandomBiomePresetTile(random, presetsRegion, generatorStack, this.biome, 20, size, "minibiomes", new WorldPreset.ValidTilePredicate(){

                @Override
                public boolean isValidPosition(int tileX, int tileY) {
                    return !generatorStack.isCaveRiverOrLava(tileX + size.width / 2, tileY + size.height / 2);
                }
            });
            if (tile == null || !(lg = new LinesGenerationWorldPreset(tile.x + size.width / 2, tile.y + size.height / 2).addRandomArms(random, 4, 6.0f, 10.0f, 6.0f, 8.0f)).isWithinPresetRegionBounds(presetsRegion)) continue;
            presetsRegion.addPreset((WorldPreset)this, lg.getOccupiedTileRectangle(), "minibiomes", new LevelPresetsRegion.WorldPresetPlaceFunction(){

                @Override
                public void place(GameRandom random, Level level, PerformanceTimerManager timer) {
                    CellAutomaton ca = lg.doCellularAutomaton(random);
                    ArrayList validChestTiles = new ArrayList();
                    ca.streamAliveOrdered().forEachOrdered(tile -> {
                        level.setTile(tile.x, tile.y, TileRegistry.mudID);
                        level.setObject(tile.x, tile.y, 0);
                        validChestTiles.add(new Point((Point)tile));
                        GameTile gravelTile = TileRegistry.getTile(TileRegistry.gravelID);
                        if (random.getChance(0.3f) && gravelTile.canPlace(level, tile.x, tile.y, false) == null) {
                            gravelTile.placeTile(level, tile.x, tile.y, false);
                        }
                    });
                    Point chestTile = (Point)random.getOneOf(validChestTiles);
                    if (chestTile != null) {
                        level.setObject(chestTile.x, chestTile.y, ObjectRegistry.getObjectID("barrel"));
                        LootTablePresets.bearBarrel.applyToLevel(random, 1.0f, level, chestTile.x, chestTile.y, new Object[0]);
                        Mob grizzlyBear = MobRegistry.getMob("grizzlybear", level);
                        grizzlyBear.canDespawn = false;
                        Point spawnPos = PortalObjectEntity.getTeleportDestinationAroundObject(level, grizzlyBear, chestTile.x, chestTile.y, true);
                        if (spawnPos == null) {
                            spawnPos = new Point(chestTile.x * 32 + 16, chestTile.y * 32 + 16);
                        }
                        grizzlyBear.onSpawned(spawnPos.x, spawnPos.y);
                        level.entityManager.addMob(grizzlyBear, spawnPos.x, spawnPos.y);
                    }
                }
            });
        }
    }
}

