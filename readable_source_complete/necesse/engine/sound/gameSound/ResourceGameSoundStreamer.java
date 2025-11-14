/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.system.MemoryUtil
 */
package necesse.engine.sound.gameSound;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.gameSound.GameSound;
import necesse.engine.sound.gameSound.GameSoundStreamer;
import org.lwjgl.system.MemoryUtil;

public abstract class ResourceGameSoundStreamer
extends GameSoundStreamer {
    protected final AtomicBoolean isWorking;
    protected final AtomicBoolean isDisposed;
    protected final GameSound sound;
    protected final ByteBuffer inputBytes;
    protected final LinkedList<ShortBuffer> sampleBuffers = new LinkedList();
    protected final LinkedList<Integer> buffers = new LinkedList();
    protected long decoder = 0L;
    protected IntBuffer errorBuffer = null;
    protected int channels;
    protected int sampleRate;
    protected int sampleLength;
    protected float lengthSeconds;
    private boolean initialized;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ResourceGameSoundStreamer(GameSound sound, ByteBuffer inputBytes) {
        this.sound = sound;
        this.inputBytes = inputBytes;
        this.isWorking = new AtomicBoolean(false);
        this.isDisposed = new AtomicBoolean(false);
        ByteBuffer byteBuffer = this.inputBytes;
        synchronized (byteBuffer) {
            this.init();
        }
    }

    private void init() {
        this.initialized = true;
        this.loadVorbisData();
    }

    protected abstract void loadVorbisData();

    @Override
    public int getLengthInSamples() {
        return this.sampleLength;
    }

    @Override
    public int getSampleRate() {
        return this.sampleRate;
    }

    @Override
    public float getLengthInSeconds() {
        return this.lengthSeconds;
    }

    @Override
    public boolean isDone(int loadedSamples) {
        return this.initialized && this.sampleLength <= loadedSamples;
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
            ShortBuffer audioBuffer = null;
            try {
                ByteBuffer byteBuffer = this.inputBytes;
                synchronized (byteBuffer) {
                    if (!this.initialized) {
                        this.init();
                    }
                    audioBuffer = MemoryUtil.memAllocShort((int)(minSamples * this.channels));
                    this.sampleBuffers.add(audioBuffer);
                    if (this.decoder != 0L) {
                        this.decodeInput(sampleOffset, audioBuffer, handler);
                    }
                }
            }
            finally {
                this.isWorking.set(false);
            }
        });
    }

    protected abstract void decodeInput(int var1, ShortBuffer var2, GameSoundStreamer.BufferHandler var3);

    @Override
    public void disposeBuffer(int buffer) {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void dispose() {
        ByteBuffer byteBuffer = this.inputBytes;
        synchronized (byteBuffer) {
            this.isDisposed.set(true);
            if (this.errorBuffer != null) {
                MemoryUtil.memFree((Buffer)this.errorBuffer);
                this.errorBuffer = null;
            }
            for (ShortBuffer audioBuffer : this.sampleBuffers) {
                MemoryUtil.memFree((Buffer)audioBuffer);
            }
            this.sampleBuffers.clear();
            this.disposeVorbisData();
        }
    }

    protected abstract void disposeVorbisData();
}

