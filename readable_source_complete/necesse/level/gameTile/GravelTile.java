/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameTile;

import java.awt.Color;
import java.awt.Point;
import necesse.engine.util.GameRandom;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.level.gameTile.GameTile;
import necesse.level.gameTile.TerrainSplatterTile;
import necesse.level.maps.Level;

public class GravelTile
extends TerrainSplatterTile {
    private final GameRandom drawRandom;

    public GravelTile() {
        super(false, "gravel");
        this.mapColor = new Color(157, 100, 83);
        this.canBeMined = true;
        this.drawRandom = new GameRandom();
        this.isOrganic = true;
    }

    @Override
    public LootTable getLootTable(Level level, int tileX, int tileY) {
        return new LootTable(new LootItem("stone").preventLootMultiplier());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Point getTerrainSprite(GameTextureSection terrainTexture, Level level, int tileX, int tileY) {
        int tile;
        GameRandom gameRandom = this.drawRandom;
        synchronized (gameRandom) {
            tile = this.drawRandom.seeded(GravelTile.getTileSeed(tileX, tileY)).nextInt(terrainTexture.getHeight() / 32);
        }
        return new Point(0, tile);
    }

    @Override
    public boolean canBePlacedOn(Level level, int tileX, int tileY, GameTile placing) {
        return false;
    }

    @Override
    public int getTerrainPriority() {
        return 0;
    }
}

