/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.openal.AL10
 *  org.lwjgl.stb.STBVorbis
 *  org.lwjgl.stb.STBVorbisInfo
 *  org.lwjgl.system.MemoryUtil
 */
package necesse.engine.platforms.sharedOnPC.sound;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import necesse.engine.sound.gameSound.GameSound;
import necesse.engine.sound.gameSound.GameSoundStreamer;
import necesse.engine.sound.gameSound.ResourceGameSoundStreamer;
import org.lwjgl.openal.AL10;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryUtil;

public class LWJGLResourceGameSoundStreamer
extends ResourceGameSoundStreamer {
    public LWJGLResourceGameSoundStreamer(GameSound sound, ByteBuffer inputBytes) {
        super(sound, inputBytes);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void loadVorbisData() {
        if (this.errorBuffer != null) {
            MemoryUtil.memFree((Buffer)this.errorBuffer);
        }
        this.errorBuffer = MemoryUtil.memAllocInt((int)1);
        try (STBVorbisInfo info = STBVorbisInfo.malloc();){
            ByteBuffer byteBuffer = this.inputBytes;
            synchronized (byteBuffer) {
                block12: {
                    this.decoder = STBVorbis.stb_vorbis_open_memory((ByteBuffer)this.inputBytes, (IntBuffer)this.errorBuffer, null);
                    if (this.decoder != 0L) break block12;
                    System.err.println("Error creating decoder for " + this.sound.path + ": " + this.errorBuffer.get(0));
                    return;
                }
                STBVorbis.stb_vorbis_get_info((long)this.decoder, (STBVorbisInfo)info);
                this.channels = info.channels();
                this.sampleRate = info.sample_rate();
                this.sampleLength = STBVorbis.stb_vorbis_stream_length_in_samples((long)this.decoder);
                this.lengthSeconds = (float)this.sampleLength / (float)this.sampleRate;
            }
        }
    }

    @Override
    protected void decodeInput(int sampleOffset, ShortBuffer audioBuffer, GameSoundStreamer.BufferHandler handler) {
        if (STBVorbis.stb_vorbis_seek((long)this.decoder, (int)sampleOffset)) {
            int samplesLoaded = STBVorbis.stb_vorbis_get_samples_short_interleaved((long)this.decoder, (int)this.channels, (ShortBuffer)audioBuffer);
            if ((long)this.errorBuffer.get(0) != 0L) {
                System.err.println("Error getting audio samples for " + this.sound.path + " - " + this.errorBuffer.get(0));
                return;
            }
            audioBuffer.position(0);
            if (this.isDisposed.get()) {
                return;
            }
            int buffer = AL10.alGenBuffers();
            AL10.alBufferData((int)buffer, (int)(this.channels > 1 ? 4355 : 4353), (ShortBuffer)audioBuffer, (int)this.sampleRate);
            this.buffers.add(buffer);
            handler.handle(buffer, sampleOffset, samplesLoaded);
        }
    }

    @Override
    protected void disposeVorbisData() {
        this.buffers.forEach(AL10::alDeleteBuffers);
        if (this.decoder != 0L) {
            STBVorbis.stb_vorbis_close((long)this.decoder);
            this.decoder = 0L;
        }
    }
}

