/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.sound;

import necesse.engine.sound.SoundCooldown;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundPlayer;
import necesse.engine.sound.gameSound.GameSound;

public class PausableSound {
    protected GameSound gameSound;
    protected SoundEffect soundEffect;
    protected SoundCooldown cooldown;
    protected SoundPlayer soundPlayer;
    protected float lastProgressTime = 0.0f;

    public PausableSound(GameSound sound, SoundEffect soundEffect, SoundCooldown cooldown) {
        this.gameSound = sound;
        this.soundEffect = soundEffect;
        this.cooldown = cooldown;
        this.soundPlayer = this.startSound(0.0f);
    }

    public PausableSound(GameSound sound, SoundEffect soundEffect) {
        this(sound, soundEffect, null);
    }

    public PausableSound gameTick() {
        if (this.gameSound == null || this.soundPlayer == null) {
            return null;
        }
        if (!this.soundPlayer.isDone()) {
            float nextPosition = this.soundPlayer.getPositionSeconds();
            if (this.lastProgressTime < nextPosition - 0.1f) {
                this.soundPlayer.fadeOutAndStop(0.2f);
                this.soundPlayer = this.startSound(this.lastProgressTime);
                if (this.soundPlayer == null) {
                    return null;
                }
            } else {
                this.lastProgressTime = nextPosition;
            }
        } else if (this.lastProgressTime < this.gameSound.getLengthInSeconds() - 0.1f) {
            this.soundPlayer = this.startSound(this.lastProgressTime);
            if (this.soundPlayer == null) {
                return null;
            }
        } else {
            this.gameSound = null;
            return null;
        }
        this.soundPlayer.fadeOutAndStop(0.2f);
        return this;
    }

    protected SoundPlayer startSound(float seconds) {
        return SoundManager.playSoundAtPosition(this.gameSound, seconds, this.soundEffect, this.cooldown, sp -> sp.fadeIn(0.2f));
    }

    public boolean isOver() {
        return this.gameSound == null;
    }
}

