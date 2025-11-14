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
import necesse.level.gameTile.TerrainSplatterTile;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.snow.SnowBiome;

public class SnowTile
extends TerrainSplatterTile {
    public static double snowChance = GameMath.getAverageSuccessRuns(2800.0);
    private final GameRandom drawRandom;

    public SnowTile() {
        super(false, "snow");
        this.mapColor = new Color(223, 244, 255);
        this.canBeMined = true;
        this.drawRandom = new GameRandom();
    }

    @Override
    public void tick(Level level, int x, int y) {
        GameObject snow;
        if (!level.isServer()) {
            return;
        }
        if (level.getBiome(x, y) instanceof SnowBiome && level.weatherLayer.isRaining() && level.getObjectID(x, y) == 0 && GameRandom.globalRandom.getChance(snowChance) && (snow = ObjectRegistry.getObject(ObjectRegistry.getObjectID("snowpile0"))).canPlace(level, x, y, 0, false) == null) {
            snow.placeObject(level, x, y, 0, false);
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
            tile = this.drawRandom.seeded(SnowTile.getTileSeed(tileX, tileY)).nextInt(terrainTexture.getHeight() / 32);
        }
        return new Point(0, tile);
    }

    @Override
    public int getTerrainPriority() {
        return 0;
    }
}

