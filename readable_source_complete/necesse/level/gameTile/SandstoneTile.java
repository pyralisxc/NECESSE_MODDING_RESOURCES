/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameTile;

import java.awt.Color;
import java.awt.Point;
import necesse.engine.util.GameRandom;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.level.gameTile.TerrainSplatterTile;
import necesse.level.maps.Level;

public class SandstoneTile
extends TerrainSplatterTile {
    private final GameRandom drawRandom;

    public SandstoneTile() {
        super(false, "sandstonetile");
        this.mapColor = new Color(138, 107, 64);
        this.canBeMined = true;
        this.drawRandom = new GameRandom();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Point getTerrainSprite(GameTextureSection terrainTexture, Level level, int tileX, int tileY) {
        int tile;
        GameRandom gameRandom = this.drawRandom;
        synchronized (gameRandom) {
            tile = this.drawRandom.seeded(SandstoneTile.getTileSeed(tileX, tileY)).nextInt(terrainTexture.getHeight() / 32);
        }
        return new Point(0, tile);
    }

    @Override
    public int getTerrainPriority() {
        return 0;
    }
}

