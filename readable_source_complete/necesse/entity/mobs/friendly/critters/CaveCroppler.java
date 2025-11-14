/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.friendly.critters;

import java.awt.Rectangle;
import necesse.engine.registries.MobRegistry;
import necesse.engine.sound.SoundSettings;
import necesse.entity.mobs.friendly.critters.CritterMob;
import necesse.entity.particle.FleshParticle;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.inventory.lootTable.LootTable;

public class CaveCroppler
extends CritterMob {
    protected static LootTable lootTable = new LootTable();

    public CaveCroppler() {
        this.setSpeed(30.0f);
        this.setFriction(3.0f);
        this.collision = new Rectangle(-10, -7, 20, 14);
        this.hitBox = new Rectangle(-12, -14, 24, 24);
        this.selectBox = new Rectangle(-16, -28, 32, 34);
        this.swimMaskMove = 8;
        this.swimMaskOffset = 8;
        this.swimSinkOffset = 0;
    }

    @Override
    public LootTable getLootTable() {
        return lootTable;
    }

    @Override
    public void spawnDeathParticles(float knockbackX, float knockbackY) {
        for (int i = 0; i < 4; ++i) {
            this.getLevel().entityManager.addParticle(new FleshParticle(this.getLevel(), MobRegistry.Textures.beetCaveCroppler.body, 12, i, 32, this.x, this.y, 20.0f, knockbackX, knockbackY), Particle.GType.IMPORTANT_COSMETIC);
        }
    }

    @Override
    public int getRockSpeed() {
        return 10;
    }

    @Override
    protected SoundSettings getAmbientSound() {
        return new SoundSettings(GameResources.caveCropplerAmbient);
    }

    @Override
    protected SoundSettings getDeathSound() {
        return new SoundSettings(GameResources.caveCropplerDeath).volume(0.4f);
    }
}

