/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameTile;

import java.awt.Color;
import java.awt.Point;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.level.gameTile.TerrainSplatterTile;
import necesse.level.maps.Level;

public class CrystalGravelTile
extends TerrainSplatterTile {
    public CrystalGravelTile(String textureName, Color mapColor) {
        super(false, textureName, "splattingmaskwide");
        this.mapColor = mapColor;
        this.canBeMined = true;
    }

    @Override
    public Point getTerrainSprite(GameTextureSection terrainTexture, Level level, int tileX, int tileY) {
        return new Point(Math.floorMod(tileX, 4), Math.floorMod(tileY, 4));
    }

    @Override
    public int getTerrainPriority() {
        return 100;
    }
}

