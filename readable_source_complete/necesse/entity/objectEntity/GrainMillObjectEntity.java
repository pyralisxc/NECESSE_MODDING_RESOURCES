/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import java.awt.Color;
import java.awt.Point;
import necesse.engine.registries.RecipeTechRegistry;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameRandom;
import necesse.entity.objectEntity.ProcessingTechInventoryObjectEntity;
import necesse.entity.particle.Particle;
import necesse.level.maps.Level;
import necesse.level.maps.multiTile.MultiTile;

public class GrainMillObjectEntity
extends ProcessingTechInventoryObjectEntity {
    public float bladeRotation;

    public GrainMillObjectEntity(Level level, int x, int y) {
        super(level, "grainmill", x, y, 2, 2, RecipeTechRegistry.GRAIN_MILL);
    }

    @Override
    public void frameTick(float delta) {
        super.frameTick(delta);
        if (this.isProcessing()) {
            this.bladeRotation = (this.bladeRotation + delta / 250.0f * 30.0f) % 360.0f;
        }
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (this.isProcessing() && GameRandom.globalRandom.nextInt(10) == 0) {
            MultiTile multiTile = this.getObject().getMultiTile(this.getLevel(), 0, this.tileX, this.tileY);
            Point offset = new Point(multiTile.getCenterXOffset() * 16, multiTile.getCenterYOffset() * 16);
            int startHeight = 24 + GameRandom.globalRandom.nextInt(16);
            this.getLevel().entityManager.addParticle(this.tileX * 32 + offset.x + GameRandom.globalRandom.nextInt(32), this.tileY * 32 + offset.y + 32, Particle.GType.COSMETIC).color(new Color(150, 150, 150)).heightMoves(startHeight, startHeight + 20).lifeTime(1000);
        }
    }

    @Override
    public int getProcessTime() {
        return 15000;
    }

    @Override
    protected SoundSettings getAmbientSound() {
        return null;
    }

    @Override
    public boolean shouldPlayAmbientSound() {
        return this.isProcessing();
    }
}

