/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameTile;

import java.awt.Color;
import java.awt.Point;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.level.gameTile.TerrainSplatterTile;
import necesse.level.maps.Level;

public class AscendedGrowthTile
extends TerrainSplatterTile {
    public AscendedGrowthTile() {
        super(false, "ascendedgrowth", "splattingmaskwide");
        this.mapColor = new Color(104, 60, 157);
        this.canBeMined = false;
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

