/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.explosionEvent;

import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.entity.levelEvent.explosionEvent.ExplosionEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.gfx.GameResources;

public class CannonBallExplosionEvent
extends ExplosionEvent
implements Attacker {
    public CannonBallExplosionEvent() {
        this(0.0f, 0.0f, new GameDamage(100.0f), null);
    }

    public CannonBallExplosionEvent(float x, float y, GameDamage damage, Mob owner) {
        super(x, y, 80, damage, false, 0.0f, owner);
        this.sendCustomData = false;
        this.sendOwnerData = true;
    }

    @Override
    protected void playExplosionEffects() {
        SoundManager.playSound(GameResources.explosionHeavy, (SoundEffect)SoundEffect.effect(this.x, this.y).volume(2.0f).pitch(1.3f));
        this.level.getClient().startCameraShake(this.x, this.y, 400, 50, 3.0f, 3.0f, true);
    }
}

