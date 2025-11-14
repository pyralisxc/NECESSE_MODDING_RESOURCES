/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameRandom;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.gfx.GameResources;
import necesse.level.maps.Level;

public class FountainObjectEntity
extends ObjectEntity {
    protected float waterSoundPitch = 1.0f;

    public FountainObjectEntity(Level level, int x, int y) {
        this(level, "fountain", x, y);
    }

    public FountainObjectEntity(Level level, String type, int x, int y) {
        super(level, type, x, y);
    }

    @Override
    public void init() {
        super.init();
        if (this.isClient()) {
            this.waterSoundPitch = GameRandom.globalRandom.getFloatBetween(0.9f, 1.04f);
        }
    }

    @Override
    public boolean shouldPlayAmbientSound() {
        return true;
    }

    @Override
    protected SoundSettings getAmbientSound() {
        return new SoundSettings(GameResources.runningWater).volume(0.15f).basePitch(this.waterSoundPitch).pitchVariance(0.0f);
    }
}

