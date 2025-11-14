/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import necesse.engine.util.GameRandom;
import necesse.entity.particle.AscendedSparkleParticle;
import necesse.entity.particle.Particle;
import necesse.level.gameObject.RockObject;
import necesse.level.maps.Level;

public class AscendedWallObject
extends RockObject {
    public AscendedWallObject(String ... category) {
        super("ascendedwall", new Color(255, 0, 231), null, 0, 0, 1, category);
        this.objectHealth = 500;
        this.lightLevel = 150;
        this.lightHue = 0.85f;
    }

    @Override
    public void tickEffect(Level level, int layerID, int tileX, int tileY) {
        super.tickEffect(level, layerID, tileX, tileY);
        GameRandom random = GameRandom.globalRandom;
        if (random.getChance(0.02f)) {
            level.entityManager.addParticle(new AscendedSparkleParticle(level, tileX * 32 + random.nextInt(32), tileY * 32 + random.nextInt(32), 500L), Particle.GType.IMPORTANT_COSMETIC);
        }
    }
}

