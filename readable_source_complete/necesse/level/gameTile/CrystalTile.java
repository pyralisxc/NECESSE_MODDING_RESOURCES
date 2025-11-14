/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameTile;

import java.awt.Color;
import java.awt.Point;
import necesse.engine.util.GameRandom;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.level.gameTile.TerrainSplatterTile;
import necesse.level.maps.Level;

public class CrystalTile
extends TerrainSplatterTile {
    public CrystalTile() {
        super(false, "crystaltile", "splattingmaskwide");
        this.mapColor = new Color(16, 77, 78);
        this.canBeMined = true;
        this.lightLevel = 30;
    }

    @Override
    public Point getTerrainSprite(GameTextureSection terrainTexture, Level level, int tileX, int tileY) {
        return new Point(Math.floorMod(tileX, 3), Math.floorMod(tileY, 3));
    }

    @Override
    public int getTerrainPriority() {
        return 100;
    }

    @Override
    public void tickEffect(Level level, int x, int y) {
        super.tickEffect(level, x, y);
        GameRandom random = GameRandom.globalRandom;
        if (random.getChance(0.001f)) {
            int posX = x * 32 + random.nextInt(32);
            int posY = y * 32 + random.nextInt(32);
            level.entityManager.addParticle(posX, posY, Particle.GType.IMPORTANT_COSMETIC).sprite((options, lifeTime, timeAlive, lifePercent) -> options.add(GameResources.glintParticles.sprite((int)(4.0f * lifePercent), 0, 18, 30))).size((options, lifeTime, timeAlive, lifePercent) -> options.size(9, 15)).lifeTime(600).height(16.0f);
        }
    }
}

