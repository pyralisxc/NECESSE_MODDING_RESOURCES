/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine;

import necesse.engine.sound.GameMusic;

public class MusicOptions {
    public final GameMusic music;
    protected int fadeInMillis;
    protected int fadeOutMillis;
    protected float volume = 1.0f;
    protected int previousFadeoutMaxMillis = -1;

    public MusicOptions(GameMusic music) {
        this.music = music;
        this.fadeInMillis = music.fadeInMillis;
        this.fadeOutMillis = music.fadeOutMillis;
    }

    public long getMusicListMilliseconds() {
        return this.music.sound.getLengthInMillis() - (long)this.fadeOutMillis;
    }

    public MusicOptions fadeInTime(int milliseconds) {
        this.fadeInMillis = milliseconds;
        return this;
    }

    public int getFadeInTime() {
        return this.fadeOutMillis;
    }

    public MusicOptions fadeOutTime(int milliseconds) {
        this.fadeOutMillis = milliseconds;
        return this;
    }

    public int getFadeOutTime() {
        return this.fadeOutMillis;
    }

    public MusicOptions forcePreviousMaxFadeout(int milliseconds) {
        this.previousFadeoutMaxMillis = milliseconds;
        return this;
    }

    public MusicOptions volume(float volume) {
        if (volume <= 0.0f) {
            throw new IllegalArgumentException("Volume cannot be below 0");
        }
        this.volume = volume;
        return this;
    }

    public float getVolume() {
        return this.volume;
    }
}

