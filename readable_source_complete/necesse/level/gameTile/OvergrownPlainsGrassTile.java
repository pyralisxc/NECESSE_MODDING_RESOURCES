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
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.level.gameObject.GameObject;
import necesse.level.gameTile.GrassTile;
import necesse.level.gameTile.TerrainSplatterTile;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.SimulatePriorityList;

public class OvergrownPlainsGrassTile
extends TerrainSplatterTile {
    public static double growChance = GameMath.getAverageSuccessRuns(3500.0);
    public static double spreadChance = GameMath.getAverageSuccessRuns(550.0);
    private final GameRandom drawRandom;

    public OvergrownPlainsGrassTile() {
        super(false, "overgrownplainsgrass");
        this.mapColor = new Color(187, 122, 17);
        this.canBeMined = true;
        this.drawRandom = new GameRandom();
        this.isOrganic = true;
    }

    @Override
    public LootTable getLootTable(Level level, int tileX, int tileY) {
        return new LootTable(new ChanceLootItem(0.04f, "overgrownplainsgrassseed"));
    }

    @Override
    public void addSimulateLogic(Level level, int x, int y, long ticks, SimulatePriorityList list, boolean sendChanges) {
        GrassTile.addSimulateGrow(level, x, y, growChance, ticks, "plainsgrass", list, sendChanges);
    }

    @Override
    public double spreadToDirtChance() {
        return spreadChance;
    }

    @Override
    public void tick(Level level, int x, int y) {
        GameObject grass;
        if (!level.isServer()) {
            return;
        }
        if (level.getObjectID(x, y) == 0 && GameRandom.globalRandom.getChance(growChance) && (grass = ObjectRegistry.getObject("plainsgrass")).canPlace(level, x, y, 0, false) == null) {
            grass.placeObject(level, x, y, 0, false);
            level.objectLayer.setIsPlayerPlaced(x, y, false);
            level.sendObjectUpdatePacket(x, y);
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
            tile = this.drawRandom.seeded(OvergrownPlainsGrassTile.getTileSeed(tileX, tileY)).nextInt(terrainTexture.getHeight() / 32);
        }
        return new Point(0, tile);
    }

    @Override
    public int getTerrainPriority() {
        return 200;
    }
}

