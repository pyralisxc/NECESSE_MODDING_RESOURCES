/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.projectile.boomerangProjectile;

import necesse.engine.sound.SoundSettings;
import necesse.entity.projectile.boomerangProjectile.SpinningProjectile;
import necesse.gfx.GameResources;

public abstract class BoomerangProjectile
extends SpinningProjectile {
    public BoomerangProjectile() {
        this.isBoomerang = true;
    }

    @Override
    public void init() {
        super.init();
        if (this.getOwner() == null) {
            this.remove();
        }
        this.trailOffset = 0.0f;
        this.returningToOwner = false;
    }

    @Override
    protected void spawnDeathParticles() {
    }

    @Override
    public float getAngle() {
        return this.getWorldEntity().getTime() - this.spawnTime;
    }

    @Override
    protected SoundSettings getMoveSound() {
        return new SoundSettings(GameResources.swing2).volume(0.4f).pitchVariance(0.02f);
    }

    @Override
    protected SoundSettings getSpawnSound() {
        return new SoundSettings(GameResources.regularBoomerang).volume(0.8f);
    }
}

