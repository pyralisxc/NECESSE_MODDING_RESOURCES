/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameRandom;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.gfx.GameResources;
import necesse.level.maps.Level;

public class BannerObjectEntity
extends ObjectEntity {
    public BannerObjectEntity(Level level, String type, int x, int y) {
        super(level, type, x, y);
    }

    public BannerObjectEntity(Level level, int x, int y) {
        super(level, "banner", x, y);
    }

    @Override
    public boolean shouldPlayAmbientSound() {
        return GameRandom.globalRandom.getEveryXthChance(30);
    }

    @Override
    protected SoundSettings getAmbientSound() {
        return new SoundSettings(GameRandom.globalRandom.getOneOf(GameResources.flagNoises)).volume(0.2f).basePitch(1.6f).pitchVariance(0.2f);
    }

    @Override
    public boolean ambienceIsLooping() {
        return false;
    }
}

