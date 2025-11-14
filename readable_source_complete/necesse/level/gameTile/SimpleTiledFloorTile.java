/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameTile;

import java.awt.Color;
import java.awt.Point;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.level.gameTile.TerrainSplatterTile;
import necesse.level.maps.Level;

public class SimpleTiledFloorTile
extends TerrainSplatterTile {
    public SimpleTiledFloorTile(String textureName, Color mapColor) {
        super(true, textureName);
        this.mapColor = mapColor;
        this.canBeMined = true;
    }

    @Override
    public Point getTerrainSprite(GameTextureSection terrainTexture, Level level, int tileX, int tileY) {
        int spriteX = tileX % (terrainTexture.getWidth() / 32);
        int spriteY = tileY % (terrainTexture.getHeight() / 32);
        return new Point(spriteX, spriteY);
    }

    @Override
    public int getTerrainPriority() {
        return 400;
    }
}

