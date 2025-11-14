/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.worldPresets;

import java.awt.Dimension;
import java.awt.Point;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.biomeGenerator.BiomeGeneratorStack;
import necesse.engine.world.worldPresets.LevelPresetsRegion;
import necesse.engine.world.worldPresets.LinesGenerationWorldPreset;
import necesse.engine.world.worldPresets.WorldPreset;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.generationModules.CellAutomaton;
import necesse.level.maps.presets.PresetUtils;

public class CustomCrystalsWorldPreset
extends WorldPreset {
    public Biome biome;
    public LevelIdentifier levelIdentifier;
    public float presetsPerRegion;
    public String gravelTileStringID;
    public String smallClusterObjectStringID;
    public String bigClusterObjectStringID;

    public CustomCrystalsWorldPreset(Biome biome, LevelIdentifier levelIdentifier, float presetsPerRegion, String gravelTileStringID, String smallClusterObjectStringID, String bigClusterObjectStringID) {
        this.biome = biome;
        this.levelIdentifier = levelIdentifier;
        this.presetsPerRegion = presetsPerRegion;
        this.gravelTileStringID = gravelTileStringID;
        this.smallClusterObjectStringID = smallClusterObjectStringID;
        this.bigClusterObjectStringID = bigClusterObjectStringID;
    }

    @Override
    public boolean shouldAddToRegion(LevelPresetsRegion presetsRegion) {
        return presetsRegion.identifier.equals(this.levelIdentifier) && presetsRegion.hasAnyOfBiome(this.biome.getID());
    }

    @Override
    public void addToRegion(GameRandom random, LevelPresetsRegion presetsRegion, final BiomeGeneratorStack generatorStack, PerformanceTimerManager performanceTimer) {
        int total = CustomCrystalsWorldPreset.getTotalBiomePoints(random, presetsRegion, this.biome, this.presetsPerRegion);
        for (int i = 0; i < total; ++i) {
            LinesGenerationWorldPreset lg;
            final Dimension size = new Dimension(14, 14);
            Point tile = CustomCrystalsWorldPreset.findRandomBiomePresetTile(random, presetsRegion, generatorStack, this.biome, 20, size, "minibiomes", new WorldPreset.ValidTilePredicate(){

                @Override
                public boolean isValidPosition(int tileX, int tileY) {
                    return !generatorStack.isCaveRiverOrLava(tileX + size.width / 2, tileY + size.height / 2);
                }
            });
            if (tile == null || !(lg = new LinesGenerationWorldPreset(tile.x + size.width / 2, tile.y + size.height / 2).addRandomArms(random, 4, 4.0f, 7.0f, 4.0f, 6.0f)).isWithinPresetRegionBounds(presetsRegion)) continue;
            presetsRegion.addPreset((WorldPreset)this, lg.getOccupiedTileRectangle(), "minibiomes", new LevelPresetsRegion.WorldPresetPlaceFunction(){

                @Override
                public void place(GameRandom random, Level level, PerformanceTimerManager timer) {
                    int gravelTileID = TileRegistry.getTileID(CustomCrystalsWorldPreset.this.gravelTileStringID);
                    GameObject crystalClusterSmall = ObjectRegistry.getObject(CustomCrystalsWorldPreset.this.smallClusterObjectStringID);
                    GameObject crystalClusterBig = ObjectRegistry.getObject(CustomCrystalsWorldPreset.this.bigClusterObjectStringID);
                    CellAutomaton ca = lg.doCellularAutomaton(random);
                    ca.streamAliveOrdered().forEachOrdered(tile -> {
                        level.setTile(tile.x, tile.y, gravelTileID);
                        level.setObject(tile.x, tile.y, 0);
                    });
                    ca.streamAliveOrdered().forEachOrdered(tile -> {
                        Point[] clearPoints;
                        int rotation;
                        if (level.getObjectID(tile.x, tile.y) == 0 && level.getObjectID(tile.x - 1, tile.y) == 0 && level.getObjectID(tile.x + 1, tile.y) == 0 && level.getObjectID(tile.x, tile.y - 1) == 0 && level.getObjectID(tile.x, tile.y + 1) == 0 && random.getChance(0.08f) && level.getRelativeAnd(tile.x, tile.y, PresetUtils.getRotatedPoints(0, 0, rotation = random.nextInt(4), clearPoints = new Point[]{new Point(-1, -1), new Point(1, -1)}), (tileX, tileY) -> ca.isAlive((int)tileX, (int)tileY) && level.getObjectID((int)tileX, (int)tileY) == 0)) {
                            crystalClusterBig.placeObject(level, tile.x, tile.y, rotation, false);
                        }
                        if (random.getChance(0.3f) && crystalClusterSmall.canPlace(level, tile.x, tile.y, 0, false) == null) {
                            crystalClusterSmall.placeObject(level, tile.x, tile.y, 0, false);
                        }
                    });
                }
            });
        }
    }
}

