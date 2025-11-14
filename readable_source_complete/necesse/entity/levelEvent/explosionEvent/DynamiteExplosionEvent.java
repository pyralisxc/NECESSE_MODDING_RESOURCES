/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.explosionEvent;

import necesse.engine.sound.SoundSettings;
import necesse.entity.levelEvent.explosionEvent.BombExplosionEvent;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.gfx.GameResources;

public class DynamiteExplosionEvent
extends BombExplosionEvent {
    public DynamiteExplosionEvent() {
        this(0.0f, 0.0f, 40, new GameDamage(80.0f), true, false, 0.0f, null);
    }

    public DynamiteExplosionEvent(float x, float y, int range, GameDamage damage, boolean destroysObjects, boolean destroyTiles, float toolTier, Mob owner) {
        super(x, y, range, damage, destroysObjects, destroyTiles, toolTier, owner);
    }

    @Override
    protected SoundSettings getExplosionSound() {
        return new SoundSettings(GameResources.explosionHeavy).volume(2.5f).fallOffDistance(1800);
    }
}

