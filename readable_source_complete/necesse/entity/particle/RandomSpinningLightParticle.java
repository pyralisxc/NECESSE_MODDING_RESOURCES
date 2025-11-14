/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.particle;

import java.awt.Color;
import necesse.entity.particle.RandomSpinningParticle;
import necesse.level.maps.Level;

public class RandomSpinningLightParticle
extends RandomSpinningParticle {
    public RandomSpinningLightParticle(Level level, int spriteX, int spriteY, float x, float y, float dx, float dy, int startHeight, int lifeTime) {
        super(level, spriteX, spriteY, x, y, dx, dy, startHeight, lifeTime);
    }

    public RandomSpinningLightParticle(Level level, int spriteX, int spriteY, float x, float y, float dx, float dy, int startHeight) {
        super(level, spriteX, spriteY, x, y, dx, dy, startHeight);
    }

    public RandomSpinningLightParticle(Level level, int spriteX, int spriteY, float x, float y, int startHeight) {
        super(level, spriteX, spriteY, x, y, startHeight);
    }

    public RandomSpinningLightParticle(Level level, Color color, float x, float y, float dx, float dy, int startHeight, int lifeTime) {
        super(level, color, x, y, dx, dy, startHeight, lifeTime);
    }

    public RandomSpinningLightParticle(Level level, Color color, float x, float y, float dx, float dy, int startHeight) {
        super(level, color, x, y, dx, dy, startHeight);
    }

    @Override
    public void clientTick() {
        super.clientTick();
        this.getLevel().lightManager.refreshParticleLightFloat(this.x, this.y);
    }
}

