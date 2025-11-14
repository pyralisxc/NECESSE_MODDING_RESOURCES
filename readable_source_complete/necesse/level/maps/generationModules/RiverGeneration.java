/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.generationModules;

import java.awt.Point;
import java.util.LinkedList;
import java.util.function.Function;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.PointHashMap;
import necesse.level.maps.Level;
import necesse.level.maps.generationModules.GenerationTools;
import necesse.level.maps.generationModules.LinesGeneration;

public class RiverGeneration {
    public static BridgeMaterials STONE_BRIDGE = new BridgeMaterials("stonepathtile", "stonefence");
    public static BridgeMaterials WOOD_BRIDGE = new BridgeMaterials("woodpathtile", "woodfence");
    public static BridgeMaterials SNOW_BRIDGE = new BridgeMaterials("snowstonepathtile", "woodfence");
    public static BridgeMaterials SWAMP_BRIDGE = new BridgeMaterials("swampstonepathtile", "woodfence");
    public static BridgeMaterials SAND_BRIDGE = new BridgeMaterials("sandstonepathtile", "woodfence");
    public static BridgeMaterials BASALT_BRIDGE = new BridgeMaterials("basaltpathtile", "stonefence");
    public static BridgeMaterials DRYAD_BRIDGE = new BridgeMaterials("dryadpath", "stonefence");

    public static void generateOneSmallRiver(GameRandom random, Level level, int waterTileID, String plantStringID, Function<GameRandom, BridgeMaterials> bridgeMaterials) {
        Point riverSpawnPoint = GenerationTools.getRandomMapPoint(level, random);
        RiverGeneration.generateOneRiver(riverSpawnPoint, random, level, random.getIntBetween(12, 18), 1, 2, 5, 10, 40, 0.4f, waterTileID, plantStringID, 4, 2, bridgeMaterials);
    }

    public static void generateOneSmallRiver(GameRandom random, Level level, String plantStringID, Function<GameRandom, BridgeMaterials> bridgeMaterials) {
        RiverGeneration.generateOneSmallRiver(random, level, TileRegistry.waterID, plantStringID, bridgeMaterials);
    }

    public static void generateOneGiantRiver(GameRandom random, Level level, int waterTileID, String plantStringID, Function<GameRandom, BridgeMaterials> bridgeMaterials) {
        Point riverSpawnPoint = GenerationTools.getRandomEdgePoint(level, random);
        RiverGeneration.generateOneRiver(riverSpawnPoint, random, level, 200, 3, 6, 6, 20, 25, 0.8f, waterTileID, plantStringID, 4, 3, bridgeMaterials);
    }

    public static void generateOneGiantRiver(GameRandom random, Level level, String plantStringID, Function<GameRandom, BridgeMaterials> bridgeMaterials) {
        RiverGeneration.generateOneGiantRiver(random, level, TileRegistry.waterID, plantStringID, bridgeMaterials);
    }

    public static void generateOneGiantWavyRiver(GameRandom random, Level level, int waterTileID, String plantStringID) {
        Point riverSpawnPoint = GenerationTools.getRandomEdgePoint(level, random);
        RiverGeneration.generateOneWavyRiver(riverSpawnPoint, random, level, 200, 3, 5, 6, 20, waterTileID, plantStringID);
    }

    public static void generateOneGiantWavyRiver(GameRandom random, Level level, String plantStringID) {
        RiverGeneration.generateOneGiantWavyRiver(random, level, TileRegistry.waterID, plantStringID);
    }

    public static void generateOneRiver(Point riverSpawnPoint, GameRandom random, Level level, int maxRiverArms, int minWidth, int maxWidth, int minRange, int maxRange, int maxTurnDegrees, float straightness, int waterTileID, String plantStringID, int bridgeCheckEveryXArm, int bridgeEveryXthChance, Function<GameRandom, BridgeMaterials> bridgeMaterials) {
        LinesGeneration riverLG = new LinesGeneration(riverSpawnPoint.x, riverSpawnPoint.y);
        class BridgePoint {
            public final int x;
            public final int y;
            public final float direction;

            public BridgePoint(int x, int y, float dir) {
                this.x = x;
                this.y = y;
                this.direction = dir;
            }
        }
        LinkedList<BridgePoint> bridgeSpawnPoints = new LinkedList<BridgePoint>();
        float lakeSpawnAngle = GameMath.getAngle(GameMath.normalize((float)riverSpawnPoint.x - (float)level.tileWidth / 2.0f, (float)riverSpawnPoint.y - (float)level.tileHeight / 2.0f));
        float currentAngle = GameMath.fixAngle(lakeSpawnAngle + 180.0f);
        for (int i = 0; i < maxRiverArms; ++i) {
            currentAngle = random.getFloatOffsetWithDist(currentAngle, maxTurnDegrees, (float)maxTurnDegrees * (1.0f - straightness));
            float width = random.getFloatBetween(minWidth, maxWidth);
            float range = random.getFloatBetween(minRange, maxRange);
            riverLG = riverLG.addArm(currentAngle, range, width);
            if (!level.isTileWithinBounds(riverLG.x2, riverLG.y2)) break;
            if (i % bridgeCheckEveryXArm != 0 || i == 0 || !random.getEveryXthChance(bridgeEveryXthChance)) continue;
            float percPos = random.getFloatBetween(0.02f, 0.98f);
            int bridgeX = GameMath.lerp(percPos, riverLG.x1, riverLG.x2);
            int bridgeY = GameMath.lerp(percPos, riverLG.y1, riverLG.y2);
            bridgeSpawnPoints.add(new BridgePoint(bridgeX, bridgeY, currentAngle));
        }
        RiverGeneration.setWaterTiles(riverLG, level, random, waterTileID, plantStringID);
        float bridgeWidth = Math.max((float)minWidth, 2.5f);
        for (BridgePoint bridgeSpawnPoint : bridgeSpawnPoints) {
            RiverGeneration.generateBridgeAcross(bridgeSpawnPoint.x, bridgeSpawnPoint.y, bridgeSpawnPoint.direction, bridgeWidth, maxWidth, level, bridgeMaterials == null ? null : bridgeMaterials.apply(random), random);
        }
    }

    private static void generateBridgeAcross(int x, int y, float riverAngle, float bridgeWidth, float riverWidth, Level map, BridgeMaterials bridgeMaterials, GameRandom random) {
        LinesGeneration bridgeLG = new LinesGeneration(x, y);
        float angle = riverAngle + 90.0f;
        bridgeLG = bridgeLG.addArm(angle, riverWidth + 4.0f, bridgeWidth);
        bridgeLG = bridgeLG.addArm(angle + 180.0f, riverWidth * 2.0f + 7.0f, bridgeWidth);
        RiverGeneration.setBridgeTiles(bridgeLG, map, bridgeMaterials, random);
    }

    public static void generateOneWavyRiver(Point riverSpawnPoint, GameRandom random, Level level, int maxRiverArms, int minWidth, int maxWidth, int minRange, int maxRange, int waterTileID, String plantStringID) {
        LinesGeneration riverLG;
        int maxTurnDegrees = 45;
        LinesGeneration current = riverLG = new LinesGeneration(riverSpawnPoint.x, riverSpawnPoint.y);
        float lakeSpawnAngle = GameMath.getAngle(GameMath.normalize((float)riverSpawnPoint.x - (float)level.tileWidth / 2.0f, (float)riverSpawnPoint.y - (float)level.tileHeight / 2.0f));
        lakeSpawnAngle = GameMath.fixAngle(lakeSpawnAngle + 180.0f);
        float sineFloat = 0.0f;
        for (int i = 0; i < maxRiverArms; ++i) {
            float sineMultiplier = GameMath.sin(sineFloat += 30.0f);
            float width = random.getFloatBetween(minWidth, maxWidth);
            float range = random.getFloatBetween(minRange, maxRange);
            float newAngle = GameMath.fixAngle(lakeSpawnAngle + (float)maxTurnDegrees * sineMultiplier);
            newAngle = random.getFloatOffsetWithDist(newAngle, 12.0f, 6.0f);
            current = current.addArm(newAngle, range, width);
            if (!level.isTileWithinBounds(current.x2, current.y2)) break;
        }
        RiverGeneration.setWaterTiles(riverLG, level, random, waterTileID, plantStringID);
    }

    private static void setBridgeTiles(LinesGeneration bridgeLG, Level level, BridgeMaterials bridgeMaterials, GameRandom random) {
        int fenceID;
        PointHashMap<LinesGeneration.PointDistance> bridgeTiles = bridgeLG.getSmoothPoints();
        int tileID = bridgeMaterials == null || bridgeMaterials.tileStringID == null ? -1 : TileRegistry.getTileID(bridgeMaterials.tileStringID);
        int n = fenceID = bridgeMaterials == null || bridgeMaterials.fenceStringID == null ? -1 : ObjectRegistry.getObjectID(bridgeMaterials.fenceStringID);
        if (tileID >= 0) {
            for (Point tile : bridgeTiles.getKeys()) {
                level.setObject(tile.x, tile.y, 0);
                level.setTile(tile.x, tile.y, tileID);
            }
        }
        if (fenceID >= 0) {
            for (Point tile : bridgeTiles.getKeys()) {
                if (!RiverGeneration.isTileNextToLiquid(level, tile.x, tile.y)) continue;
                level.setObject(tile.x, tile.y, fenceID);
            }
        }
    }

    public static boolean isTileNextToLiquid(Level level, int tileX, int tileY) {
        for (Point p : Level.adjacentGetters) {
            if (!level.isLiquidTile(tileX + p.x, tileY + p.y)) continue;
            return true;
        }
        return false;
    }

    private static void setWaterTiles(LinesGeneration riverLG, Level level, GameRandom random, int waterTileID, String plantStringID) {
        PointHashMap<LinesGeneration.PointDistance> lakeTiles = riverLG.getSmoothPoints();
        int plantID = plantStringID == null ? -1 : ObjectRegistry.getObjectID(plantStringID);
        for (Point tile : lakeTiles.getKeys()) {
            level.setTile(tile.x, tile.y, waterTileID);
            level.setObject(tile.x, tile.y, 0);
            if (plantID < 0 || !random.getEveryXthChance(9)) continue;
            level.setObject(tile.x, tile.y, plantID);
        }
    }

    public static class BridgeMaterials {
        public String tileStringID;
        public String fenceStringID;

        public BridgeMaterials(String tileStringID, String fenceStringID) {
            this.tileStringID = tileStringID;
            this.fenceStringID = fenceStringID;
        }
    }
}

