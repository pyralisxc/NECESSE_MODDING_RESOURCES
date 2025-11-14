/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.worldPresets;

import java.awt.Point;
import java.util.LinkedList;
import java.util.function.BiPredicate;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.PointHashSet;
import necesse.engine.world.biomeGenerator.BiomeGeneratorStack;
import necesse.engine.world.worldPresets.LevelPresetsRegion;
import necesse.engine.world.worldPresets.WorldPreset;
import necesse.level.maps.biomes.Biome;

public abstract class BiomeCenterWorldPreset
extends WorldPreset {
    protected Biome biome;
    protected float pointsPerRegion = 0.004f;
    protected int randomAttempts = 50;
    protected int sectionMaxRegionCount = 400;
    protected int sectionMinRegionCount = 49;
    private static final Point[] NEIGHBOR_COORDINATES = new Point[]{new Point(0, -1), new Point(1, 0), new Point(0, 1), new Point(-1, 0)};
    private static final int HALF_REGION_SIZE = 8;

    public BiomeCenterWorldPreset(Biome biome) {
        this.biome = biome;
    }

    public boolean isLevelRegionWithinPresetRegion(int regionX, int regionY, LevelPresetsRegion presetsRegion, int padding) {
        return regionX >= presetsRegion.worldRegion.startLevelRegionX + padding && regionX < presetsRegion.worldRegion.startLevelRegionX + 64 - padding && regionY >= presetsRegion.worldRegion.startLevelRegionY + padding && regionY < presetsRegion.worldRegion.startLevelRegionY + 64 - padding;
    }

    public boolean runRegionCornerCheck(int regionX, int regionY, WorldPreset.ValidTilePredicate isValidTile) {
        int regionStartTileY;
        int regionStartTileX = GameMath.getTileCoordByRegion(regionX);
        return isValidTile.isValidPosition(regionStartTileX, regionStartTileY = GameMath.getTileCoordByRegion(regionY)) && isValidTile.isValidPosition(regionStartTileX + 16 - 1, regionStartTileY) && isValidTile.isValidPosition(regionStartTileX, regionStartTileY + 16 - 1) && isValidTile.isValidPosition(regionStartTileX + 16 - 1, regionStartTileY + 16 - 1);
    }

    public boolean isRegionWater(BiomeGeneratorStack generatorStack, int regionX, int regionY) {
        return this.runRegionCornerCheck(regionX, regionY, generatorStack::isSurfaceOcean);
    }

    public boolean isRegionDeepCaveLava(BiomeGeneratorStack generatorStack, int regionX, int regionY) {
        return this.runRegionCornerCheck(regionX, regionY, generatorStack::isDeepCaveLava);
    }

    public boolean isRegionBiomeAndValid(LevelPresetsRegion presetsRegion, BiomeGeneratorStack generatorStack, int regionX, int regionY, int biomeID) {
        int centerTileY;
        if (this.isLevelRegionWithinPresetRegion(regionX, regionY, presetsRegion, 0)) {
            return presetsRegion.isRegionBiome(regionX, regionY, biomeID) && this.isValidSectionRegion(regionX, regionY, presetsRegion, generatorStack);
        }
        int centerTileX = GameMath.getTileCoordByRegion(regionX) + 8;
        return generatorStack.getLazyBiomeID(centerTileX, centerTileY = GameMath.getTileCoordByRegion(regionY) + 8) == biomeID && this.isValidSectionRegion(regionX, regionY, presetsRegion, generatorStack);
    }

    public Point runBreathFirst(int startRegionX, int startRegionY, BiPredicate<Point, PointHashSet> onFind) {
        PointHashSet closedRegions = new PointHashSet();
        LinkedList<Point> openRegions = new LinkedList<Point>();
        openRegions.addLast(new Point(startRegionX, startRegionY));
        closedRegions.add(startRegionX, startRegionY);
        while (!openRegions.isEmpty()) {
            Point currentRegion = (Point)openRegions.removeFirst();
            if (onFind.test(currentRegion, closedRegions)) {
                return currentRegion;
            }
            for (Point offset : NEIGHBOR_COORDINATES) {
                int nextRegionX = currentRegion.x + offset.x;
                int nextRegionY = currentRegion.y + offset.y;
                if (closedRegions.contains(nextRegionX, nextRegionY)) continue;
                closedRegions.add(nextRegionX, nextRegionY);
                openRegions.addLast(new Point(nextRegionX, nextRegionY));
            }
        }
        return null;
    }

    @Override
    public void addToRegion(GameRandom random, LevelPresetsRegion presetsRegion, BiomeGeneratorStack generatorStack, PerformanceTimerManager performanceTimer) {
        PointHashSet closedRegions = new PointHashSet();
        int total = BiomeCenterWorldPreset.getTotalBiomePoints(random, presetsRegion, this.biome, this.pointsPerRegion);
        block0: for (int i = 0; i < total; ++i) {
            for (int j = 0; j < this.randomAttempts; ++j) {
                Point foundRegion;
                int averageRegionY;
                int averageRegionX;
                Point regionPosition = presetsRegion.getRandomBiomeRegion(random, this.biome.getID());
                if (closedRegions.contains(regionPosition.x, regionPosition.y)) continue;
                if (!this.isValidSectionRegion(regionPosition.x, regionPosition.y, presetsRegion, generatorStack)) {
                    closedRegions.add(regionPosition.x, regionPosition.y);
                    continue;
                }
                PointHashSet connectedRegions = new PointHashSet();
                long totalRegionX = regionPosition.x;
                long totalRegionY = regionPosition.y;
                LinkedList<Point> openRegions = new LinkedList<Point>();
                openRegions.addLast(regionPosition);
                connectedRegions.add(regionPosition.x, regionPosition.y);
                closedRegions.add(regionPosition.x, regionPosition.y);
                while (!openRegions.isEmpty()) {
                    Point currentRegion = (Point)openRegions.removeFirst();
                    for (Point offset : NEIGHBOR_COORDINATES) {
                        if (connectedRegions.size() >= this.sectionMaxRegionCount) break;
                        int nextRegionX = currentRegion.x + offset.x;
                        int nextRegionY = currentRegion.y + offset.y;
                        if (connectedRegions.contains(nextRegionX, nextRegionY) || closedRegions.contains(nextRegionX, nextRegionY) || !this.isRegionBiomeAndValid(presetsRegion, generatorStack, nextRegionX, nextRegionY, this.biome.getID())) continue;
                        connectedRegions.add(nextRegionX, nextRegionY);
                        closedRegions.add(nextRegionX, nextRegionY);
                        totalRegionX += (long)nextRegionX;
                        totalRegionY += (long)nextRegionY;
                        openRegions.addLast(new Point(nextRegionX, nextRegionY));
                    }
                    if (connectedRegions.size() < this.sectionMaxRegionCount) continue;
                    break;
                }
                if (!this.isLevelRegionWithinPresetRegion(averageRegionX = (int)(totalRegionX / (long)connectedRegions.size()), averageRegionY = (int)(totalRegionY / (long)connectedRegions.size()), presetsRegion, 0) || connectedRegions.size() <= this.sectionMinRegionCount || (foundRegion = this.runBreathFirst(averageRegionX, averageRegionY, (regionPos, closed) -> {
                    if (closed.size() >= connectedRegions.size()) {
                        return true;
                    }
                    return connectedRegions.contains(regionPos.x, regionPos.y) && this.isLevelRegionWithinPresetRegion(regionPos.x, regionPos.y, presetsRegion, 0) && this.isValidFinalRegion(regionPos.x, regionPos.y, presetsRegion, generatorStack);
                })) == null || !connectedRegions.contains(foundRegion.x, foundRegion.y) || !this.isLevelRegionWithinPresetRegion(foundRegion.x, foundRegion.y, presetsRegion, 0) || !this.isValidFinalRegion(foundRegion.x, foundRegion.y, presetsRegion, generatorStack)) continue block0;
                this.onFoundRegion(foundRegion.x, foundRegion.y, random, presetsRegion, generatorStack, performanceTimer);
                continue block0;
            }
        }
    }

    public abstract boolean isValidSectionRegion(int var1, int var2, LevelPresetsRegion var3, BiomeGeneratorStack var4);

    public abstract boolean isValidFinalRegion(int var1, int var2, LevelPresetsRegion var3, BiomeGeneratorStack var4);

    public abstract void onFoundRegion(int var1, int var2, GameRandom var3, LevelPresetsRegion var4, BiomeGeneratorStack var5, PerformanceTimerManager var6);
}

