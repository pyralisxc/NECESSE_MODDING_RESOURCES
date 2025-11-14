/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.sound;

import necesse.engine.sound.PositionSoundEffect;
import necesse.engine.sound.PrimitiveSoundEmitter;
import necesse.engine.sound.SoundCooldown;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.gameSound.GameSound;
import necesse.engine.util.GameRandom;

public class SoundSettings {
    public static final int defaultFallOffDistance = 750;
    public static final float defaultVolume = 1.0f;
    public static final float defaultBasePitch = 1.0f;
    public static final float defaultPitchVariance = 0.05f;
    public final GameSound[] sounds;
    protected UnsetVar<Integer> fallOffDistance = new UnsetVar<Integer>(750);
    protected UnsetVar<Float> volume = new UnsetVar<Float>(Float.valueOf(1.0f));
    protected UnsetVar<Float> basePitch = new UnsetVar<Float>(Float.valueOf(1.0f));
    protected UnsetVar<Float> pitchVariance = new UnsetVar<Float>(Float.valueOf(0.05f));
    protected UnsetVar<SoundCooldown> cooldown = new UnsetVar<Object>(null);

    public SoundSettings(GameSound sound) {
        this.sounds = sound == null ? null : new GameSound[]{sound};
    }

    public SoundSettings(GameSound sound1, GameSound ... otherSounds) {
        if (sound1 == null) {
            this.sounds = null;
        } else {
            this.sounds = new GameSound[otherSounds.length + 1];
            this.sounds[0] = sound1;
            System.arraycopy(otherSounds, 0, this.sounds, 1, otherSounds.length);
        }
    }

    public SoundSettings volume(float volume) {
        this.volume.setValue(Float.valueOf(volume));
        return this;
    }

    public SoundSettings basePitch(float basePitch) {
        this.basePitch.setValue(Float.valueOf(basePitch));
        return this;
    }

    public SoundSettings pitchVariance(float pitchVariance) {
        this.pitchVariance.setValue(Float.valueOf(pitchVariance));
        return this;
    }

    public SoundSettings fallOffDistance(int fallOffDistance) {
        this.fallOffDistance.setValue(fallOffDistance);
        return this;
    }

    public SoundSettings cooldown(SoundCooldown cooldown) {
        this.cooldown.setValue(cooldown);
        return this;
    }

    public SoundSettings setVolumeIfNotSet(float volume) {
        if (!this.volume.hasBeenSet()) {
            this.volume.setValue(Float.valueOf(volume));
        }
        return this;
    }

    public SoundSettings setBasePitchIfNotSet(float basePitch) {
        if (!this.basePitch.hasBeenSet()) {
            this.basePitch.setValue(Float.valueOf(basePitch));
        }
        return this;
    }

    public SoundSettings setPitchVarianceIfNotSet(float pitchVariance) {
        if (!this.pitchVariance.hasBeenSet()) {
            this.pitchVariance.setValue(Float.valueOf(pitchVariance));
        }
        return this;
    }

    public SoundSettings setFallOffDistanceIfNotSet(int fallOffDistance) {
        if (!this.fallOffDistance.hasBeenSet()) {
            this.fallOffDistance.setValue(fallOffDistance);
        }
        return this;
    }

    public SoundSettings setCooldownIfNotSet(SoundCooldown cooldown) {
        if (!this.cooldown.hasBeenSet()) {
            this.cooldown.setValue(cooldown);
        }
        return this;
    }

    public GameSound getRandomSound() {
        if (this.sounds == null || this.sounds.length == 0) {
            return null;
        }
        return this.sounds[GameRandom.globalRandom.nextInt(this.sounds.length)];
    }

    public float getVolume() {
        return this.volume.getValue().floatValue();
    }

    public float getBasePitch() {
        return this.basePitch.getValue().floatValue();
    }

    public float getPitchVariance() {
        return this.pitchVariance.getValue().floatValue();
    }

    public float getFinalRandomPitch() {
        float variance = this.pitchVariance.getValue().floatValue();
        if (variance <= 0.0f) {
            return this.basePitch.getValue().floatValue();
        }
        return GameRandom.globalRandom.getFloatOffset(this.basePitch.getValue().floatValue(), variance);
    }

    public int getFallOffDistance() {
        return this.fallOffDistance.getValue();
    }

    public SoundCooldown getCooldown() {
        return this.cooldown.getValue();
    }

    public SoundEffect applySettings(PositionSoundEffect effect) {
        return effect.volume(this.getVolume()).pitch(this.getFinalRandomPitch()).falloffDistance(this.getFallOffDistance());
    }

    public void play(PositionSoundEffect soundEffect) {
        SoundManager.playSound(this, soundEffect);
    }

    public void play(PrimitiveSoundEmitter emitter) {
        SoundManager.playSound(this, emitter);
    }

    public void play(float x, float y) {
        SoundManager.playSound(this, x, y);
    }

    public static class UnsetVar<T> {
        private boolean hasBeenSet = false;
        protected T value;

        public UnsetVar(T defaultValue) {
            this.value = defaultValue;
        }

        public UnsetVar<T> setValue(T newValue) {
            this.value = newValue;
            this.hasBeenSet = true;
            return this;
        }

        public T getValue() {
            return this.value;
        }

        public boolean hasBeenSet() {
            return this.hasBeenSet;
        }
    }
}

