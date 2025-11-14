/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.worldPresets;

import java.awt.Dimension;
import java.awt.Point;
import java.util.Set;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.registries.IDData;
import necesse.engine.registries.IDDataContainer;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.world.biomeGenerator.BiomeGeneratorStack;
import necesse.engine.world.worldPresets.LevelPresetsRegion;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;

public abstract class WorldPreset
implements IDDataContainer {
    public final IDData idData = new IDData();
    private final int priority;
    public boolean shouldSaveGenerated = true;

    @Override
    public final String getStringID() {
        return this.idData.getStringID();
    }

    @Override
    public final int getID() {
        return this.idData.getID();
    }

    @Override
    public IDData getIDData() {
        return this.idData;
    }

    public WorldPreset(int priority) {
        this.priority = priority;
    }

    public WorldPreset() {
        this(0);
    }

    public int getPriority() {
        return this.priority;
    }

    public void onRegistryClosed() {
    }

    public abstract boolean shouldAddToRegion(LevelPresetsRegion var1);

    public abstract void addToRegion(GameRandom var1, LevelPresetsRegion var2, BiomeGeneratorStack var3, PerformanceTimerManager var4);

    public static void ensureRegionsAreGenerated(Level level, int tileX, int tileY, int width, int height) {
        int regionStartX = level.regionManager.getRegionXByTileLimited(tileX);
        int regionEndX = level.regionManager.getRegionXByTileLimited(tileX + width - 1);
        int regionStartY = level.regionManager.getRegionYByTileLimited(tileY);
        int regionEndY = level.regionManager.getRegionYByTileLimited(tileY + height - 1);
        for (int regionX = regionStartX; regionX <= regionEndX; ++regionX) {
            for (int regionY = regionStartY; regionY <= regionEndY; ++regionY) {
                level.regionManager.getRegion(regionX, regionY, true);
            }
        }
    }

    public static int getTotalPoints(GameRandom random, LevelPresetsRegion presetsRegion, float pointsPerRegion) {
        double totalRegions = (double)(presetsRegion.worldRegion.tileWidth * presetsRegion.worldRegion.tileHeight) / 256.0;
        double totalPoints = totalRegions * (double)pointsPerRegion;
        if (totalPoints != (double)((int)totalPoints) && random.getChance(totalPoints - (double)((int)totalPoints))) {
            totalPoints = (int)totalPoints + 1;
        }
        return (int)totalPoints;
    }

    public static Point findRandomPresetTile(GameRandom random, LevelPresetsRegion presetsRegion, int attempts, Dimension size, String[] checkOccupiedBoards, ValidTilePredicate isValidTile) {
        for (int i = 0; i < attempts; ++i) {
            Point tile = WorldPreset.findRandomPresetTile(random, presetsRegion, size);
            if (!(size == null ? isValidTile == null || isValidTile.isValidPosition(tile.x, tile.y) : !(checkOccupiedBoards != null && presetsRegion.isRectangleOccupied(checkOccupiedBoards, tile.x, tile.y, size.width, size.height) || isValidTile != null && !isValidTile.isValidPosition(tile.x, tile.y)))) continue;
            return tile;
        }
        return null;
    }

    public static Point findRandomPresetTile(GameRandom random, LevelPresetsRegion presetsRegion, int attempts, Dimension size, String checkOccupiedBoard, ValidTilePredicate isValidTile) {
        String[] stringArray;
        if (checkOccupiedBoard == null) {
            stringArray = null;
        } else {
            String[] stringArray2 = new String[1];
            stringArray = stringArray2;
            stringArray2[0] = checkOccupiedBoard;
        }
        return WorldPreset.findRandomPresetTile(random, presetsRegion, attempts, size, stringArray, isValidTile);
    }

    public static Point findRandomPresetTile(GameRandom random, LevelPresetsRegion presetsRegion, Dimension size) {
        if (size == null) {
            return new Point(presetsRegion.worldRegion.startTileX + random.nextInt(presetsRegion.worldRegion.tileWidth - 1), presetsRegion.worldRegion.startTileY + random.nextInt(presetsRegion.worldRegion.tileHeight - 1));
        }
        return new Point(presetsRegion.worldRegion.startTileX + random.nextInt(presetsRegion.worldRegion.tileWidth - size.width), presetsRegion.worldRegion.startTileY + random.nextInt(presetsRegion.worldRegion.tileHeight - size.height));
    }

    public static int getTotalBiomePoints(GameRandom random, LevelPresetsRegion presetsRegion, Biome biome, float pointsPerRegion) {
        double totalRegions = (double)(presetsRegion.worldRegion.tileWidth * presetsRegion.worldRegion.tileHeight) / 256.0;
        double totalPoints = totalRegions * (double)pointsPerRegion * (double)presetsRegion.getBiomeWeight(biome.getID());
        if (totalPoints != (double)((int)totalPoints) && random.getChance(totalPoints - (double)((int)totalPoints))) {
            totalPoints = (int)totalPoints + 1;
        }
        return (int)totalPoints;
    }

    public static Point findRandomBiomePresetTile(GameRandom random, LevelPresetsRegion presetsRegion, BiomeGeneratorStack stack, Biome biome, int attempts, Dimension size, String[] checkOccupiedBoards, ValidTilePredicate isValidTile) {
        for (int i = 0; i < attempts; ++i) {
            Point region = presetsRegion.getRandomBiomeRegion(random, biome.getID());
            if (region == null) {
                return null;
            }
            Point tile = WorldPreset.findRandomPresetTileFromRegion(random, presetsRegion, size, region);
            if (tile == null || (size != null ? stack.getLazyBiomeID(tile.x + size.width / 2, tile.y + size.height / 2) != biome.getID() || checkOccupiedBoards != null && presetsRegion.isRectangleOccupied(checkOccupiedBoards, tile.x, tile.y, size.width, size.height) : stack.getLazyBiomeID(tile.x, tile.y) != biome.getID())) continue;
            if (isValidTile != null && !isValidTile.isValidPosition(tile.x, tile.y)) continue;
            return tile;
        }
        return null;
    }

    public static Point findRandomBiomePresetTile(GameRandom random, LevelPresetsRegion presetsRegion, BiomeGeneratorStack stack, Biome biome, int attempts, Dimension size, String checkOccupiedBoard, ValidTilePredicate isValidTile) {
        String[] stringArray;
        if (checkOccupiedBoard == null) {
            stringArray = null;
        } else {
            String[] stringArray2 = new String[1];
            stringArray = stringArray2;
            stringArray2[0] = checkOccupiedBoard;
        }
        return WorldPreset.findRandomBiomePresetTile(random, presetsRegion, stack, biome, attempts, size, stringArray, isValidTile);
    }

    public static int getTotalBiomePoints(GameRandom random, LevelPresetsRegion presetsRegion, Set<Integer> biomeIDs, float pointsPerRegion) {
        double totalRegions = (double)(presetsRegion.worldRegion.tileWidth * presetsRegion.worldRegion.tileHeight) / 256.0;
        double totalBiomeWeight = biomeIDs.stream().mapToDouble(presetsRegion::getBiomeWeight).sum();
        double totalPoints = totalRegions * (double)pointsPerRegion * totalBiomeWeight;
        if (totalPoints != (double)((int)totalPoints) && random.getChance(totalPoints - (double)((int)totalPoints))) {
            totalPoints = (int)totalPoints + 1;
        }
        return (int)totalPoints;
    }

    public static Point findRandomBiomePresetTile(GameRandom random, LevelPresetsRegion presetsRegion, BiomeGeneratorStack stack, Set<Integer> biomeIDs, int attempts, Dimension size, String[] checkOccupiedBoards, ValidTilePredicate isValidTile) {
        for (int i = 0; i < attempts; ++i) {
            Point region = presetsRegion.getRandomBiomeRegion(random, biomeIDs);
            if (region == null) {
                return null;
            }
            Point tile = WorldPreset.findRandomPresetTileFromRegion(random, presetsRegion, size, region);
            if (tile == null || (size != null ? !biomeIDs.contains(stack.getLazyBiomeID(tile.x + size.width / 2, tile.y + size.height / 2)) || checkOccupiedBoards != null && presetsRegion.isRectangleOccupied(checkOccupiedBoards, tile.x, tile.y, size.width, size.height) : !biomeIDs.contains(stack.getLazyBiomeID(tile.x, tile.y)))) continue;
            if (isValidTile != null && !isValidTile.isValidPosition(tile.x, tile.y)) continue;
            return tile;
        }
        return null;
    }

    public static Point findRandomBiomePresetTile(GameRandom random, LevelPresetsRegion presetsRegion, BiomeGeneratorStack stack, Set<Integer> biomeIDs, int attempts, Dimension size, String checkOccupiedBoard, ValidTilePredicate isValidTile) {
        String[] stringArray;
        if (checkOccupiedBoard == null) {
            stringArray = null;
        } else {
            String[] stringArray2 = new String[1];
            stringArray = stringArray2;
            stringArray2[0] = checkOccupiedBoard;
        }
        return WorldPreset.findRandomBiomePresetTile(random, presetsRegion, stack, biomeIDs, attempts, size, stringArray, isValidTile);
    }

    public static Point findRandomPresetTileFromRegion(GameRandom random, LevelPresetsRegion presetsRegion, Dimension size, Point regionPos) {
        int startTileX = GameMath.getTileCoordByRegion(regionPos.x);
        int startTileY = GameMath.getTileCoordByRegion(regionPos.y);
        if (size == null) {
            return new Point(startTileX + random.nextInt(16), startTileY + random.nextInt(16));
        }
        if (startTileX + size.width >= presetsRegion.worldRegion.startTileX + presetsRegion.worldRegion.tileWidth) {
            return null;
        }
        if (startTileY + size.height >= presetsRegion.worldRegion.startTileY + presetsRegion.worldRegion.tileHeight) {
            return null;
        }
        int endTileX = Math.min(startTileX + 16 - 1, presetsRegion.worldRegion.startTileX + presetsRegion.worldRegion.tileWidth - size.width);
        int endTileY = Math.min(startTileY + 16 - 1, presetsRegion.worldRegion.startTileY + presetsRegion.worldRegion.tileHeight - size.height);
        int randomTileX = random.getIntBetween(startTileX, endTileX);
        int randomTileY = random.getIntBetween(startTileY, endTileY);
        return new Point(randomTileX, randomTileY);
    }

    public boolean isTileWithinBounds(int tileX, int tileY, LevelPresetsRegion presetsRegion) {
        return tileX >= presetsRegion.worldRegion.startTileX && tileX < presetsRegion.worldRegion.startTileX + presetsRegion.worldRegion.tileWidth && tileY >= presetsRegion.worldRegion.startTileY && tileY < presetsRegion.worldRegion.startTileY + presetsRegion.worldRegion.tileHeight;
    }

    public boolean isTileWithinBounds(int tileX, int tileY, LevelPresetsRegion presetsRegion, int padding) {
        return tileX >= presetsRegion.worldRegion.startTileX + padding && tileX < presetsRegion.worldRegion.startTileX + presetsRegion.worldRegion.tileWidth - padding && tileY >= presetsRegion.worldRegion.startTileY + padding && tileY < presetsRegion.worldRegion.startTileY + presetsRegion.worldRegion.tileHeight - padding;
    }

    public boolean runCornerCheck(int tileX, int tileY, int width, int height, ValidTilePredicate isValidTile) {
        if (!isValidTile.isValidPosition(tileX, tileY)) {
            return false;
        }
        if (!isValidTile.isValidPosition(tileX + width - 1, tileY)) {
            return false;
        }
        if (!isValidTile.isValidPosition(tileX, tileY + height - 1)) {
            return false;
        }
        return isValidTile.isValidPosition(tileX + width - 1, tileY + height - 1);
    }

    public boolean runGridCheck(int tileX, int tileY, int width, int height, int resolution, ValidTilePredicate isValidTile) {
        if (!this.runCornerCheck(tileX, tileY, width, height, isValidTile)) {
            return false;
        }
        int widthChecks = (width - resolution) / resolution;
        int heightChecks = (height - resolution) / resolution;
        int widthOffset = (width - resolution) % resolution / 2;
        int heightOffset = (height - resolution) % resolution / 2;
        for (int tileXOffset = 0; tileXOffset < widthChecks; ++tileXOffset) {
            int currentTileX = tileX + widthOffset + tileXOffset * resolution;
            for (int tileYOffset = 0; tileYOffset < heightChecks; ++tileYOffset) {
                int currentTileY = tileY + heightOffset + tileYOffset * resolution;
                if (isValidTile.isValidPosition(currentTileX, currentTileY)) continue;
                return false;
            }
        }
        return true;
    }

    public static interface ValidTilePredicate {
        public boolean isValidPosition(int var1, int var2);
    }

    public static interface ValidRegionPredicate {
        public boolean isValidRegion(int var1, int var2);
    }
}

