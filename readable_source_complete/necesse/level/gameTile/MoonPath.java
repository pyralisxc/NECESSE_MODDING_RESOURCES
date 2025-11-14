/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameTile;

import java.awt.Color;
import necesse.engine.util.GameRandom;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.level.gameTile.PathTiledTile;
import necesse.level.maps.Level;

public class MoonPath
extends PathTiledTile {
    public MoonPath() {
        super("moonpath", new Color(235, 240, 246));
        this.lightLevel = 100;
    }

    @Override
    public void tickEffect(Level level, int x, int y) {
        super.tickEffect(level, x, y);
        GameRandom random = GameRandom.globalRandom;
        if (random.getChance(0.002f)) {
            int posX = x * 32 + random.nextInt(32);
            int posY = y * 32 + random.nextInt(32);
            level.entityManager.addParticle(posX, posY, Particle.GType.IMPORTANT_COSMETIC).sprite(GameResources.magicSparkParticles.sprite(random.nextInt(4), 0, 22)).sizeFades(11, 22).minDrawLight(100).lifeTime(1500).height(16.0f);
        }
    }
}

