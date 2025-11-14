/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiPredicate;
import necesse.engine.AreaFinder;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.PointHashSet;
import necesse.engine.world.WorldEntity;
import necesse.engine.world.biomeGenerator.BiomeGeneratorStack;
import necesse.engine.world.worldPresets.LevelPresetsRegion;
import necesse.engine.world.worldPresets.WorldPreset;
import necesse.level.maps.biomes.Biome;

public class SpawnTileFinder {
    public static int TOTAL_PRESET_RANGE = 3;
    public static int ATTEMPTS_PER_PRESET_REGION = 50;
    public static Biome SPAWN_BIOME = BiomeRegistry.FOREST;
    public static HashSet<Integer> REQUIRED_NEARBY_BIOMES = new HashSet();
    public static HashSet<RequiredPreset> REQUIRED_NEARBY_PRESETS;
    public static int MAX_REGIONS_PER_SECTION;
    public static int MIN_REGIONS_PER_SECTION;
    public static int MIN_DISTANCE_TO_OTHER_BIOMES;
    public static int CLEAR_SPAWN_REGION_RANGE;
    private static final Point[] NEIGHBOR_COORDINATES;
    private static final int HALF_REGION_SIZE = 8;

    public static Point findSpawnTile(final WorldEntity worldEntity) {
        final BiomeGeneratorStack generatorStack = worldEntity.getGeneratorStack();
        final GameRandom random = new GameRandom(worldEntity.getWorldSeed());
        final AtomicReference<Object> backupSpawnTile = new AtomicReference<Object>(null);
        final AtomicReference<Object> spawnTile = new AtomicReference<Object>(null);
        AreaFinder finder = new AreaFinder(0, 0, TOTAL_PRESET_RANGE){

            @Override
            public boolean checkPoint(int presetRegionX, int presetRegionY) {
                int startRegionX = presetRegionX * 64 + 32;
                int startRegionY = presetRegionY * 64 + 32;
                LevelPresetsRegion regions = worldEntity.getWorldPresets(startRegionX, startRegionY).getLevelRegions(worldEntity.spawnLevelIdentifier, 0);
                PointHashSet closedRegions = new PointHashSet();
                for (int i = 0; i < ATTEMPTS_PER_PRESET_REGION; ++i) {
                    Point foundRegion;
                    Point regionPosition = regions.getRandomBiomeRegion(random, SPAWN_BIOME.getID());
                    if (closedRegions.contains(regionPosition.x, regionPosition.y)) continue;
                    if (!SpawnTileFinder.isValidSectionRegion(regionPosition.x, regionPosition.y, regions, generatorStack)) {
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
                    HashSet<Integer> missingRequiredBiomes = new HashSet<Integer>(REQUIRED_NEARBY_BIOMES);
                    while (!openRegions.isEmpty()) {
                        Point currentRegion = (Point)openRegions.removeFirst();
                        for (Point offset : NEIGHBOR_COORDINATES) {
                            if (connectedRegions.size() >= MAX_REGIONS_PER_SECTION) break;
                            int nextRegionX = currentRegion.x + offset.x;
                            int nextRegionY = currentRegion.y + offset.y;
                            if (connectedRegions.contains(nextRegionX, nextRegionY) || closedRegions.contains(nextRegionX, nextRegionY)) continue;
                            if (SpawnTileFinder.isRegionBiomeAndValid(regions, generatorStack, nextRegionX, nextRegionY, SPAWN_BIOME.getID())) {
                                connectedRegions.add(nextRegionX, nextRegionY);
                                closedRegions.add(nextRegionX, nextRegionY);
                                totalRegionX += (long)nextRegionX;
                                totalRegionY += (long)nextRegionY;
                                openRegions.addLast(new Point(nextRegionX, nextRegionY));
                                continue;
                            }
                            missingRequiredBiomes.removeIf(biomeID -> SpawnTileFinder.isRegionBiome(regions, generatorStack, nextRegionX, nextRegionY, biomeID) && !SpawnTileFinder.isRegionOcean(generatorStack, nextRegionX, nextRegionY));
                        }
                        if (connectedRegions.size() < MAX_REGIONS_PER_SECTION) continue;
                        break;
                    }
                    int averageRegionX = (int)(totalRegionX / (long)connectedRegions.size());
                    int averageRegionY = (int)(totalRegionY / (long)connectedRegions.size());
                    if (!missingRequiredBiomes.isEmpty()) continue;
                    if (connectedRegions.size() <= MIN_REGIONS_PER_SECTION) break;
                    if (SpawnTileFinder.isValidSectionRegion(averageRegionX, averageRegionY, regions, generatorStack)) {
                        backupSpawnTile.set(new Point(GameMath.getTileCoordByRegion(averageRegionX) + 8, GameMath.getTileCoordByRegion(averageRegionY) + 8));
                    }
                    if ((foundRegion = SpawnTileFinder.runBreathFirst(averageRegionX, averageRegionY, (regionPos, closed) -> {
                        if (closed.size() >= connectedRegions.size()) {
                            return true;
                        }
                        return SpawnTileFinder.isValidSectionRegion(regionPos.x, regionPos.y, regions, generatorStack) && SpawnTileFinder.runRegionAngleAndCheck(regionPos.x, regionPos.y, 8, 2.0f, (checkRegionX, checkRegionY) -> !SpawnTileFinder.isRegionOcean(generatorStack, checkRegionX, checkRegionY)) && SpawnTileFinder.runRegionAngleAndCheck(regionPos.x, regionPos.y, 8, MIN_DISTANCE_TO_OTHER_BIOMES, (checkRegionX, checkRegionY) -> SpawnTileFinder.isRegionBiome(regions, generatorStack, checkRegionX, checkRegionY, SPAWN_BIOME.getID())) && SpawnTileFinder.checkNearbyPresets(regionPos.x, regionPos.y, worldEntity);
                    })) == null || !SpawnTileFinder.isValidSectionRegion(foundRegion.x, foundRegion.y, regions, generatorStack) || !SpawnTileFinder.runRegionAngleAndCheck(foundRegion.x, foundRegion.y, 8, 2.0f, (checkRegionX, checkRegionY) -> !SpawnTileFinder.isRegionOcean(generatorStack, checkRegionX, checkRegionY)) || !SpawnTileFinder.runRegionAngleAndCheck(foundRegion.x, foundRegion.y, 8, MIN_DISTANCE_TO_OTHER_BIOMES, (checkRegionX, checkRegionY) -> SpawnTileFinder.isRegionBiome(regions, generatorStack, checkRegionX, checkRegionY, SPAWN_BIOME.getID())) || !SpawnTileFinder.checkNearbyPresets(foundRegion.x, foundRegion.y, worldEntity)) break;
                    System.out.println("Found spawn at region: " + foundRegion.x + "x" + foundRegion.y);
                    spawnTile.set(new Point(GameMath.getTileCoordByRegion(foundRegion.x) + 8, GameMath.getTileCoordByRegion(foundRegion.y) + 8));
                    return true;
                }
                return false;
            }
        };
        finder.runFinder();
        if (spawnTile.get() != null) {
            return spawnTile.get();
        }
        if (backupSpawnTile.get() != null) {
            return backupSpawnTile.get();
        }
        int startRegionX = 32;
        int startRegionY = 32;
        return new Point(GameMath.getTileCoordByRegion(startRegionX) + 8, GameMath.getTileCoordByRegion(startRegionY) + 8);
    }

    public static Point runBreathFirst(int startRegionX, int startRegionY, BiPredicate<Point, PointHashSet> onFind) {
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

    public static boolean isLevelRegionWithinPresetRegion(int regionX, int regionY, LevelPresetsRegion presetsRegion, int padding) {
        return regionX >= presetsRegion.worldRegion.startLevelRegionX + padding && regionX < presetsRegion.worldRegion.startLevelRegionX + 64 - padding && regionY >= presetsRegion.worldRegion.startLevelRegionY + padding && regionY < presetsRegion.worldRegion.startLevelRegionY + 64 - padding;
    }

    public static boolean runRegionCornerAndCheck(int regionX, int regionY, WorldPreset.ValidTilePredicate isValidTile) {
        int regionStartTileY;
        int regionStartTileX = GameMath.getTileCoordByRegion(regionX);
        return isValidTile.isValidPosition(regionStartTileX, regionStartTileY = GameMath.getTileCoordByRegion(regionY)) && isValidTile.isValidPosition(regionStartTileX + 16 - 1, regionStartTileY) && isValidTile.isValidPosition(regionStartTileX, regionStartTileY + 16 - 1) && isValidTile.isValidPosition(regionStartTileX + 16 - 1, regionStartTileY + 16 - 1);
    }

    public static boolean runRegionCornerOrCheck(int regionX, int regionY, WorldPreset.ValidTilePredicate isValidTile) {
        int regionStartTileY;
        int regionStartTileX = GameMath.getTileCoordByRegion(regionX);
        return isValidTile.isValidPosition(regionStartTileX, regionStartTileY = GameMath.getTileCoordByRegion(regionY)) || isValidTile.isValidPosition(regionStartTileX + 16 - 1, regionStartTileY) || isValidTile.isValidPosition(regionStartTileX, regionStartTileY + 16 - 1) || isValidTile.isValidPosition(regionStartTileX + 16 - 1, regionStartTileY + 16 - 1);
    }

    public static boolean runRegionAngleAndCheck(int regionX, int regionY, int angleChecks, float range, WorldPreset.ValidRegionPredicate isValidRegion) {
        float anglePerCheck = 360.0f / (float)angleChecks;
        for (int i = 0; i < angleChecks; ++i) {
            int checkRegionY;
            float angle = anglePerCheck * (float)i;
            Point2D.Float dir = GameMath.getAngleDir(angle);
            int checkRegionX = Math.round((float)regionX + dir.x * range);
            if (isValidRegion.isValidRegion(checkRegionX, checkRegionY = Math.round((float)regionY + dir.y * range))) continue;
            return false;
        }
        return true;
    }

    public static boolean runRegionAngleOrCheck(int regionX, int regionY, int angleChecks, float range, WorldPreset.ValidRegionPredicate isValidRegion) {
        float anglePerCheck = 360.0f / (float)angleChecks;
        for (int i = 0; i < angleChecks; ++i) {
            int checkRegionY;
            float angle = anglePerCheck * (float)i;
            Point2D.Float dir = GameMath.getAngleDir(angle);
            int checkRegionX = Math.round((float)regionX + dir.x * range);
            if (!isValidRegion.isValidRegion(checkRegionX, checkRegionY = Math.round((float)regionY + dir.y * range))) continue;
            return true;
        }
        return false;
    }

    public static boolean checkNearbyPresets(int regionX, int regionY, WorldEntity worldEntity) {
        int centerTileX = GameMath.getTileCoordByRegion(regionX) + 8;
        int centerTileY = GameMath.getTileCoordByRegion(regionY) + 8;
        for (RequiredPreset preset : REQUIRED_NEARBY_PRESETS) {
            LevelPresetsRegion.FoundPresetData closest = worldEntity.findClosestWorldPreset(LevelIdentifier.SURFACE_IDENTIFIER, pp -> true, centerTileX, centerTileY, preset.maxTileDistance, p -> p.getPreset().getStringID().equals(preset.presetStringID));
            if (closest == null) {
                return false;
            }
            double distance = GameMath.getExactDistance(centerTileX, centerTileY, closest.getTileX(), closest.getTileY());
            if (!(distance < (double)preset.minTileDistance) && !(distance > (double)preset.maxTileDistance)) continue;
            return false;
        }
        return true;
    }

    public static boolean isRegionOcean(BiomeGeneratorStack generatorStack, int regionX, int regionY) {
        return SpawnTileFinder.runRegionCornerOrCheck(regionX, regionY, generatorStack::isSurfaceOcean);
    }

    public static boolean isValidSectionRegion(int regionX, int regionY, LevelPresetsRegion presetsRegion, BiomeGeneratorStack generatorStack) {
        return !SpawnTileFinder.isRegionOcean(generatorStack, regionX, regionY);
    }

    public static boolean isRegionBiome(LevelPresetsRegion presetsRegion, BiomeGeneratorStack generatorStack, int regionX, int regionY, int biomeID) {
        int centerTileY;
        if (SpawnTileFinder.isLevelRegionWithinPresetRegion(regionX, regionY, presetsRegion, 0)) {
            return presetsRegion.isRegionBiome(regionX, regionY, biomeID);
        }
        int centerTileX = GameMath.getTileCoordByRegion(regionX) + 8;
        return generatorStack.getLazyBiomeID(centerTileX, centerTileY = GameMath.getTileCoordByRegion(regionY) + 8) == biomeID;
    }

    public static boolean isRegionBiomeAndValid(LevelPresetsRegion presetsRegion, BiomeGeneratorStack generatorStack, int regionX, int regionY, int biomeID) {
        int centerTileY;
        if (SpawnTileFinder.isLevelRegionWithinPresetRegion(regionX, regionY, presetsRegion, 0)) {
            return presetsRegion.isRegionBiome(regionX, regionY, biomeID) && SpawnTileFinder.isValidSectionRegion(regionX, regionY, presetsRegion, generatorStack);
        }
        int centerTileX = GameMath.getTileCoordByRegion(regionX) + 8;
        return generatorStack.getLazyBiomeID(centerTileX, centerTileY = GameMath.getTileCoordByRegion(regionY) + 8) == biomeID && SpawnTileFinder.isValidSectionRegion(regionX, regionY, presetsRegion, generatorStack);
    }

    static {
        REQUIRED_NEARBY_BIOMES.add(BiomeRegistry.SNOW.getID());
        REQUIRED_NEARBY_BIOMES.add(BiomeRegistry.PLAINS.getID());
        REQUIRED_NEARBY_PRESETS = new HashSet();
        REQUIRED_NEARBY_PRESETS.add(new RequiredPreset("dungeonentrance", 250, 800));
        MAX_REGIONS_PER_SECTION = 400;
        MIN_REGIONS_PER_SECTION = 225;
        MIN_DISTANCE_TO_OTHER_BIOMES = 6;
        CLEAR_SPAWN_REGION_RANGE = 7;
        NEIGHBOR_COORDINATES = new Point[]{new Point(0, -1), new Point(1, 0), new Point(0, 1), new Point(-1, 0)};
    }

    public static class RequiredPreset {
        public String presetStringID;
        public int minTileDistance;
        public int maxTileDistance;

        public RequiredPreset(String presetStringID, int minTileDistance, int maxTileDistance) {
            this.presetStringID = presetStringID;
            this.minTileDistance = minTileDistance;
            this.maxTileDistance = maxTileDistance;
        }
    }
}

