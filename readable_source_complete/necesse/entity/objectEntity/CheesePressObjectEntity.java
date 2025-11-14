/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import java.awt.Color;
import necesse.engine.registries.RecipeTechRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.objectEntity.ProcessingTechInventoryObjectEntity;
import necesse.entity.particle.Particle;
import necesse.level.maps.Level;

public class CheesePressObjectEntity
extends ProcessingTechInventoryObjectEntity {
    public CheesePressObjectEntity(Level level, int x, int y) {
        super(level, "cheesepress", x, y, 2, 2, RecipeTechRegistry.CHEESE_PRESS);
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (this.isProcessing() && GameRandom.globalRandom.nextInt(10) == 0) {
            int startHeight = 24 + GameRandom.globalRandom.nextInt(16);
            this.getLevel().entityManager.addParticle(this.tileX * 32 + GameRandom.globalRandom.nextInt(32), this.tileY * 32 + 32, Particle.GType.COSMETIC).color(new Color(150, 150, 150)).heightMoves(startHeight, startHeight + 20).lifeTime(1000);
        }
    }

    @Override
    public int getProcessTime() {
        return 60000;
    }
}

