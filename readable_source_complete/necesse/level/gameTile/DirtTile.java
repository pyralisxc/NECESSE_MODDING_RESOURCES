/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameTile;

import java.awt.Color;
import java.awt.Point;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.level.gameTile.GameTile;
import necesse.level.gameTile.TerrainSplatterTile;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.snow.SnowBiome;
import necesse.level.maps.regionSystem.SimulatePriorityList;

public class DirtTile
extends TerrainSplatterTile {
    public static double snowChance = GameMath.getAverageSuccessRuns(1400.0);
    private final GameRandom drawRandom;
    public static Point[] spreadTiles = new Point[]{new Point(-1, 0), new Point(0, -1), new Point(1, 0), new Point(0, 1)};

    public DirtTile() {
        super(false, "dirt");
        this.mapColor = new Color(114, 90, 81);
        this.canBeMined = false;
        this.drawRandom = new GameRandom();
        this.isOrganic = true;
    }

    @Override
    public void addSimulateLogic(Level level, int x, int y, long ticks, SimulatePriorityList list, boolean sendChanges) {
        for (Point offset : spreadTiles) {
            double usedTicks;
            long remainingTicks;
            GameTile tile = level.getTile(x + offset.x, y + offset.y);
            double spreadChance = tile.spreadToDirtChance();
            if (!(spreadChance > 0.0) || tile.canPlace(level, x, y, false) != null || (remainingTicks = (long)((double)ticks - (usedTicks = Math.max(1.0, GameMath.getRunsForSuccess(spreadChance, GameRandom.globalRandom.nextDouble()))))) <= 0L) continue;
            list.add(x, y, remainingTicks, () -> {
                tile.placeTile(level, x, y, false);
                if (sendChanges) {
                    level.sendTileUpdatePacket(x, y);
                }
                tile.addSimulateLogic(level, x, y, remainingTicks, list, sendChanges);
                for (Point nextOffset : spreadTiles) {
                    Point nextTile = new Point(x + nextOffset.x, y + nextOffset.y);
                    int nextTileID = level.getTileID(nextTile.x, nextTile.y);
                    if (nextTileID != this.getID()) continue;
                    this.addSimulateLogic(level, nextTile.x, nextTile.y, remainingTicks, list, sendChanges);
                }
            });
        }
    }

    @Override
    public void tick(Level level, int x, int y) {
        if (!level.isServer()) {
            return;
        }
        if (level.getBiome(x, y) instanceof SnowBiome && level.weatherLayer.isRaining() && GameRandom.globalRandom.getChance(snowChance)) {
            level.setTile(x, y, TileRegistry.snowID);
            level.sendTileUpdatePacket(x, y);
        }
        Performance.record((PerformanceTimerManager)level.tickManager(), "grassTick", () -> {
            for (Point offset : spreadTiles) {
                GameTile tile = level.getTile(x + offset.x, y + offset.y);
                double spreadChance = tile.spreadToDirtChance();
                if (spreadChance == 0.0 || !GameRandom.globalRandom.getChance(spreadChance) || tile.canPlace(level, x, y, false) != null) continue;
                tile.placeTile(level, x, y, false);
                level.sendTileUpdatePacket(x, y);
                break;
            }
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Point getTerrainSprite(GameTextureSection terrainTexture, Level level, int tileX, int tileY) {
        int tile;
        GameRandom gameRandom = this.drawRandom;
        synchronized (gameRandom) {
            tile = this.drawRandom.seeded(DirtTile.getTileSeed(tileX, tileY)).nextInt(terrainTexture.getHeight() / 32);
        }
        return new Point(0, tile);
    }

    @Override
    public int getTerrainPriority() {
        return 0;
    }
}

