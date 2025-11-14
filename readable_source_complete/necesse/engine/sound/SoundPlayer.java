/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.sound;

import java.nio.FloatBuffer;
import necesse.engine.Settings;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundEmitter;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.gameSound.GameSound;
import necesse.engine.sound.gameSound.GameSoundStreamer;
import necesse.engine.window.WindowManager;

public abstract class SoundPlayer {
    public final GameSound gameSound;
    private final GameSoundStreamer streamer;
    public SoundEffect effect;
    protected int source;
    private int maxLoadedSamples;
    private int maxQueuedSamples;
    private int startSamples;
    private long startTime;
    private boolean playing;
    private boolean paused;
    private boolean stopped;
    private boolean disposed;
    private int pauseSamplesPosition;
    private float fadeInVolume;
    private float fadeInSeconds = -1.0f;
    private float fadeOutVolume;
    private float fadeOutSeconds = -1.0f;
    private float loopFadeVolume;
    private float loopFadeSeconds = -1.0f;
    private boolean wasStoppedLastFrame = true;

    public SoundPlayer(GameSound gameSound, SoundEffect effect) {
        this.gameSound = gameSound;
        this.streamer = gameSound.getStreamer();
        this.effect = effect;
    }

    public static SoundEmitter SimpleEmitter(float x, float y) {
        return new SimpleSoundEmitter(x, y);
    }

    public void playSound() {
        this.playSound(0.0f);
    }

    public void playSound(float seconds) {
        if (this.disposed) {
            return;
        }
        this.alStopAndDeleteSource();
        this.alSetupSource();
        this.maxLoadedSamples = this.startSamples = (int)(seconds * (float)this.streamer.getSampleRate());
        this.maxQueuedSamples = this.startSamples;
        this.startTime = System.currentTimeMillis();
        this.stopped = false;
        this.paused = false;
        this.playing = true;
        this.alPlaySource();
        this.update();
    }

    protected abstract void alStopAndDeleteSource();

    protected abstract void alSetupSource();

    public void update() {
        if (this.source == 0) {
            return;
        }
        this.updateStreamer();
        float volumeMod = 1.0f;
        if (Settings.muteOnFocusLoss && !WindowManager.getWindow().isFocused()) {
            volumeMod = 0.0f;
        } else {
            float volumeDecPerTick;
            if (this.fadeInSeconds > 0.0f) {
                float volumeIncPerTick = 1.0f / this.fadeInSeconds / 20.0f;
                volumeMod *= this.fadeInVolume;
                this.fadeInVolume = Math.min(this.fadeInVolume + volumeIncPerTick, 1.0f);
            }
            if (this.loopFadeSeconds > 0.0f) {
                volumeDecPerTick = 1.0f / this.loopFadeSeconds / 20.0f;
                volumeMod *= this.loopFadeVolume;
                this.loopFadeVolume = Math.max(0.0f, this.loopFadeVolume - volumeDecPerTick);
            }
            if (this.fadeOutSeconds > 0.0f) {
                volumeDecPerTick = 1.0f / this.fadeOutSeconds / 20.0f;
                volumeMod *= this.fadeOutVolume;
                this.fadeOutVolume = Math.max(0.0f, this.fadeOutVolume - volumeDecPerTick);
            }
        }
        this.effect.updateSound(this, this.source, volumeMod * this.gameSound.getVolumeModifier(), SoundManager.getALListener());
        boolean alIsSourceStopped = this.alIsSourceStopped();
        if (alIsSourceStopped && !this.wasStoppedLastFrame) {
            this.stop();
        }
        this.wasStoppedLastFrame = alIsSourceStopped;
    }

    protected abstract void alPlaySource();

    private void updateStreamer() {
        if (!this.streamer.isWorking() && !this.streamer.isDone(this.maxLoadedSamples)) {
            int samplesToLoad = this.streamer.getSampleRate() * 5;
            if (this.getPositionSamples() > this.maxQueuedSamples - samplesToLoad) {
                this.streamer.getNextBuffers(this.maxQueuedSamples, samplesToLoad, (buffer, samplesOffset, samplesLoaded) -> {
                    if (this.isDisposed()) {
                        return;
                    }
                    this.alQueueBuffers(buffer);
                    if (this.isPlaying() && this.alIsSourceStopped()) {
                        this.alPlaySource();
                    }
                    this.maxLoadedSamples = samplesOffset + samplesLoaded;
                });
                this.maxQueuedSamples += samplesToLoad;
            }
        }
    }

    public int getPositionSamples() {
        if (!this.isPlaying()) {
            return this.pauseSamplesPosition;
        }
        long timeSinceStart = System.currentTimeMillis() - this.startTime;
        return this.startSamples + (int)((float)timeSinceStart / 1000.0f * (float)this.streamer.getSampleRate());
    }

    public boolean isDisposed() {
        return this.disposed;
    }

    protected abstract void alQueueBuffers(int var1);

    public boolean isPlaying() {
        return this.playing;
    }

    protected abstract boolean alIsSourceStopped();

    public void stop() {
        if (this.stopped) {
            return;
        }
        this.stopped = true;
        this.paused = true;
        this.playing = false;
        if (this.source == 0) {
            return;
        }
        this.effect.updateSound(this, this.source, 0.0f, SoundManager.getALListener());
    }

    public void fadeOutAndStop(float seconds) {
        this.fadeOutSeconds = seconds;
        float volumeDecPerTick = 1.0f / seconds / 20.0f;
        this.fadeOutVolume = 1.0f + volumeDecPerTick;
    }

    public SoundPlayer refreshLooping() {
        return this.refreshLooping(0.5f);
    }

    public SoundPlayer refreshLooping(float fadeOutTimeSeconds) {
        if (fadeOutTimeSeconds <= 0.0f) {
            throw new IllegalArgumentException("fadeTimeSeconds must be above 0");
        }
        this.loopFadeSeconds = fadeOutTimeSeconds;
        float volumeDecPerTick = 1.0f / this.loopFadeSeconds / 20.0f;
        this.loopFadeVolume = 1.0f + volumeDecPerTick;
        this.alSetSourceLooping(true);
        return this;
    }

    protected abstract void alSetSourceLooping(boolean var1);

    public SoundPlayer fadeIn(float fadeInTimeSeconds) {
        if (fadeInTimeSeconds <= 0.0f) {
            throw new IllegalArgumentException("fadeInTimeSeconds must be above 0");
        }
        this.fadeInSeconds = fadeInTimeSeconds;
        this.fadeInVolume = 0.0f;
        return this;
    }

    public SoundPlayer copyFadeInProgress(SoundPlayer other) {
        this.fadeInSeconds = other.fadeInSeconds;
        this.fadeInVolume = other.fadeInVolume;
        return this;
    }

    public boolean isDone() {
        if (this.stopped) {
            return true;
        }
        if (this.paused) {
            return false;
        }
        if (!this.playing) {
            return false;
        }
        boolean stopped = false;
        if (this.fadeOutSeconds > 0.0f) {
            boolean bl = stopped = this.fadeOutVolume <= 0.0f || this.alIsSourceStopped();
        }
        if (!stopped) {
            stopped = this.loopFadeSeconds > 0.0f ? this.loopFadeVolume <= 0.0f : this.alIsSourceStopped();
        }
        return stopped && !this.streamer.isWorking() && this.streamer.isDone(this.maxLoadedSamples);
    }

    public void setPosition(float seconds) {
        if (this.disposed) {
            return;
        }
        this.playSound(seconds);
    }

    public float getSecondsLeft() {
        return this.getLengthInSeconds() - this.getPositionSeconds();
    }

    public float getLengthInSeconds() {
        return this.streamer.getLengthInSeconds();
    }

    public float getPositionSeconds() {
        return (float)this.getPositionSamples() / (float)this.streamer.getSampleRate();
    }

    public void startOver() {
        this.playSound(0.0f);
    }

    public void pause() {
        if (this.disposed || !this.playing) {
            return;
        }
        this.alPauseSource();
        this.pauseSamplesPosition = this.getPositionSamples();
        this.paused = true;
        this.playing = false;
    }

    public boolean isPaused() {
        return this.paused;
    }

    protected abstract void alPauseSource();

    public abstract void alSetPitch(float var1);

    public abstract void alSetGain(float var1);

    public abstract void alSetPosition(FloatBuffer var1);

    public void dispose() {
        if (this.disposed) {
            return;
        }
        this.disposed = true;
        this.alStopAndDeleteSource();
        this.streamer.dispose();
    }

    private static class SimpleSoundEmitter
    implements SoundEmitter {
        private final float x;
        private final float y;

        public SimpleSoundEmitter(float x, float y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public float getSoundPositionX() {
            return this.x;
        }

        @Override
        public float getSoundPositionY() {
            return this.y;
        }
    }
}

