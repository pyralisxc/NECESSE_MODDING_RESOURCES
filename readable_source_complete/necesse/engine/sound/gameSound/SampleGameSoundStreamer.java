/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.sound.gameSound;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.gameSound.GameSound;
import necesse.engine.sound.gameSound.GameSoundStreamer;

public abstract class SampleGameSoundStreamer
extends GameSoundStreamer {
    public final GameSound.VorbisSamples data;
    private final AtomicBoolean isWorking;
    private final LinkedList<Integer> buffers = new LinkedList();

    public SampleGameSoundStreamer(GameSound.VorbisSamples data) {
        this.data = data;
        this.isWorking = new AtomicBoolean(false);
    }

    @Override
    public int getLengthInSamples() {
        return this.data.info.totalSamples;
    }

    @Override
    public int getSampleRate() {
        return this.data.info.sampleRate;
    }

    @Override
    public float getLengthInSeconds() {
        return this.data.info.lengthSeconds;
    }

    @Override
    public boolean isDone(int loadedSamples) {
        return loadedSamples >= this.data.info.totalSamples * this.data.info.channels;
    }

    @Override
    public boolean isWorking() {
        return this.isWorking.get();
    }

    @Override
    public void getNextBuffers(int sampleOffset, int minSamples, GameSoundStreamer.BufferHandler handler) {
        if (this.isWorking()) {
            return;
        }
        this.isWorking.set(true);
        SoundManager.musicStreamer.submit(() -> {
            try {
                GameSound.VorbisSamples vorbisSamples = this.data;
                synchronized (vorbisSamples) {
                    int samplesToLoad = Math.min(this.data.info.totalSamples - sampleOffset, minSamples);
                    int lengthToLoad = samplesToLoad * this.data.info.channels;
                    int maxBufferSize = 0x100000;
                    int loadedSamples = sampleOffset;
                    int buffersLength = lengthToLoad / maxBufferSize + 1;
                    int[] buffers = new int[buffersLength];
                    this.generateBuffers(buffers);
                    int pos = 0;
                    for (int buffer : buffers) {
                        int size = Math.min(maxBufferSize, lengthToLoad - pos);
                        short[] partBuffer = new short[size];
                        int offset = loadedSamples;
                        this.data.samples.position(offset * this.data.info.channels);
                        this.data.samples.get(partBuffer);
                        pos += size;
                        this.bufferData(buffer, this.data.info.channels > 1, partBuffer, this.data.info.sampleRate);
                        this.buffers.add(buffer);
                        handler.handle(buffer, offset, loadedSamples += size / this.data.info.channels);
                    }
                }
            }
            finally {
                this.isWorking.set(false);
            }
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void disposeBuffer(int buffer) {
        GameSound.VorbisSamples vorbisSamples = this.data;
        synchronized (vorbisSamples) {
            if (this.buffers.contains(buffer)) {
                this.deleteBuffers(buffer);
                this.buffers.remove((Object)buffer);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void dispose() {
        GameSound.VorbisSamples vorbisSamples = this.data;
        synchronized (vorbisSamples) {
            this.buffers.forEach(this::deleteBuffers);
        }
    }

    protected abstract void deleteBuffers(int var1);

    protected abstract void generateBuffers(int[] var1);

    protected abstract void bufferData(int var1, boolean var2, short[] var3, int var4);
}

