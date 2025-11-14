/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameTile;

import java.awt.Color;
import java.awt.Point;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.level.gameObject.GameObject;
import necesse.level.gameTile.GrassTile;
import necesse.level.gameTile.TerrainSplatterTile;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.Level;
import necesse.level.maps.TilePosition;
import necesse.level.maps.biomes.MobSpawnTable;
import necesse.level.maps.regionSystem.SimulatePriorityList;

public class PuddleCobbleTile
extends TerrainSplatterTile {
    public static MobSpawnTable fishianSpawnTable = new MobSpawnTable().add(100, "fishianhookwarrior").add(75, "fishianhealer").add(100, "staticjellyfish");
    public static double growChance = GameMath.getAverageSuccessRuns(15000.0);
    private final GameRandom drawRandom;

    public PuddleCobbleTile() {
        super(false, "puddlecobble");
        this.mapColor = new Color(0, 81, 98);
        this.canBeMined = true;
        this.drawRandom = new GameRandom();
        this.isOrganic = true;
    }

    @Override
    public void addSimulateLogic(Level level, int x, int y, long ticks, SimulatePriorityList list, boolean sendChanges) {
        PuddleCobbleTile.addSimulateGrow(level, x, y, growChance, ticks, "glowcoral", list, sendChanges);
    }

    public static void addSimulateGrow(Level level, int tileX, int tileY, double growChance, long ticks, String growObjectID, SimulatePriorityList list, boolean sendChanges) {
        PuddleCobbleTile.addSimulateGrow(level, tileX, tileY, growChance, ticks, growObjectID, (object, l, x, y, r) -> object.canPlace(l, x, y, r, false) == null, list, sendChanges);
    }

    public static void addSimulateGrow(Level level, int tileX, int tileY, double growChance, long ticks, String growObjectID, GrassTile.CanPlacePredicate canPlace, SimulatePriorityList list, boolean sendChanges) {
        GameObject obj;
        double runs;
        long remainingTicks;
        if (level.getObjectID(tileX, tileY) == 0 && (remainingTicks = (long)((double)ticks - (runs = Math.max(1.0, GameMath.getRunsForSuccess(growChance, GameRandom.globalRandom.nextDouble()))))) > 0L && canPlace.check(obj = ObjectRegistry.getObject(ObjectRegistry.getObjectID(growObjectID)), level, tileX, tileY, 0)) {
            list.add(tileX, tileY, remainingTicks, () -> {
                if (canPlace.check(obj, level, tileX, tileY, 0)) {
                    obj.placeObject(level, tileX, tileY, 0, false);
                    level.objectLayer.setIsPlayerPlaced(tileX, tileY, false);
                    if (sendChanges) {
                        level.sendObjectUpdatePacket(tileX, tileY);
                    }
                }
            });
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Point getTerrainSprite(GameTextureSection terrainTexture, Level level, int tileX, int tileY) {
        int tile;
        GameRandom gameRandom = this.drawRandom;
        synchronized (gameRandom) {
            tile = this.drawRandom.seeded(PuddleCobbleTile.getTileSeed(tileX, tileY)).nextInt(terrainTexture.getHeight() / 32);
        }
        return new Point(0, tile);
    }

    @Override
    public int getTerrainPriority() {
        return 0;
    }

    @Override
    public MobSpawnTable getMobSpawnTable(TilePosition pos, MobSpawnTable defaultTable) {
        if (pos.objectID() == 0 && !(pos.level instanceof IncursionLevel)) {
            return fishianSpawnTable;
        }
        return super.getMobSpawnTable(pos, defaultTable);
    }
}

