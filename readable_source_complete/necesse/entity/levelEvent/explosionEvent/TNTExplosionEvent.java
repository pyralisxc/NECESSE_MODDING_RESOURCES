/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.explosionEvent;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.entity.levelEvent.explosionEvent.ExplosionEvent;
import necesse.entity.mobs.GameDamage;
import necesse.gfx.GameResources;

public class TNTExplosionEvent
extends ExplosionEvent {
    public TNTExplosionEvent() {
        this(0.0f, 0.0f);
    }

    public TNTExplosionEvent(float tileX, float tileY) {
        super(tileX * 32.0f + 16.0f, tileY * 32.0f + 16.0f, 300, new GameDamage(400.0f, 1000.0f), true, 10.0f);
        this.sendCustomData = false;
    }

    @Override
    public void serverTick() {
        this.level.sendObjectChangePacket(this.level.getServer(), this.tileX, this.tileY, 0);
        super.serverTick();
    }

    @Override
    public GameMessage getAttackerName() {
        return new LocalMessage("deaths", "tntname");
    }

    @Override
    protected void playExplosionEffects() {
        SoundManager.playSound(GameResources.explosionHeavy, (SoundEffect)SoundEffect.effect(this.x, this.y).volume(3.5f).falloffDistance(2000));
        this.level.getClient().startCameraShake(this.x, this.y, 500, 40, 5.0f, 5.0f, true);
    }
}

