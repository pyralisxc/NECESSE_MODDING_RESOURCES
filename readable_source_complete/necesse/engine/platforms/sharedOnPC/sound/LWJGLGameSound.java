/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.openal.AL10
 *  org.lwjgl.stb.STBVorbis
 *  org.lwjgl.stb.STBVorbisInfo
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.system.MemoryUtil
 */
package necesse.engine.platforms.sharedOnPC.sound;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import necesse.engine.GameLoadingScreen;
import necesse.engine.sound.SoundCooldown;
import necesse.engine.sound.gameSound.GameSound;
import org.lwjgl.openal.AL10;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public class LWJGLGameSound
extends GameSound {
    protected LWJGLGameSound(String path, SoundCooldown cooldown, boolean isMusic) throws IOException {
        super(path, cooldown, isMusic);
        GameLoadingScreen.drawLoadingSub("sound/" + path);
        byte[] data = this.loadBytes();
        if (isMusic) {
            this.inputBytes = MemoryUtil.memAlloc((int)data.length);
            this.inputBytes.put(data);
            this.inputBytes.position(0);
            try (MemoryStack stack = MemoryStack.stackPush();){
                IntBuffer errorBuffer = stack.mallocInt(1);
                STBVorbisInfo info = STBVorbisInfo.malloc((MemoryStack)stack);
                long decoder = STBVorbis.stb_vorbis_open_memory((ByteBuffer)this.inputBytes, (IntBuffer)errorBuffer, null);
                if (decoder != 0L) {
                    int totalSamples = STBVorbis.stb_vorbis_stream_length_in_samples((long)decoder);
                    STBVorbis.stb_vorbis_get_info((long)decoder, (STBVorbisInfo)info);
                    this.info = new GameSound.VorbisInfo(totalSamples, info.channels(), info.sample_rate());
                    STBVorbis.stb_vorbis_close((long)decoder);
                }
                System.err.println("Error creating decoder for " + path + ": " + errorBuffer.get(0));
            }
        } else {
            byte[] finalInputBytes = data;
            Callable<GameSound.VorbisSamples> completeSamplesLoading = () -> LWJGLGameSound.decodeVorbisSamples(this, finalInputBytes);
            if (loadingExecutor != null) {
                this.getSamplesLoading = loadingExecutor.submit(completeSamplesLoading);
            } else {
                FutureTask<GameSound.VorbisSamples> task;
                this.getSamplesLoading = task = new FutureTask<GameSound.VorbisSamples>(completeSamplesLoading);
                task.run();
                this.waitForDoneLoading();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static GameSound.VorbisSamples decodeVorbisSamples(LWJGLGameSound sound, byte[] inputBytes) {
        ByteBuffer inputData;
        IntBuffer errorBuffer;
        STBVorbisInfo info;
        long decoder;
        block9: {
            GameSound.VorbisSamples vorbisSamples;
            decoder = 0L;
            info = null;
            errorBuffer = null;
            inputData = null;
            try {
                errorBuffer = MemoryUtil.memAllocInt((int)1);
                info = STBVorbisInfo.malloc();
                inputData = MemoryUtil.memAlloc((int)inputBytes.length);
                inputData.put(inputBytes);
                inputData.position(0);
                decoder = STBVorbis.stb_vorbis_open_memory((ByteBuffer)inputData, (IntBuffer)errorBuffer, null);
                if (decoder != 0L) break block9;
                System.err.println("Error creating decoder for " + sound.path + ": " + errorBuffer.get(0));
                vorbisSamples = new GameSound.VorbisSamples(MemoryUtil.memAllocShort((int)0), new GameSound.VorbisInfo(0, 1, 0));
            }
            catch (Throwable throwable) {
                MemoryUtil.memFree((Buffer)errorBuffer);
                if (info != null) {
                    info.free();
                }
                MemoryUtil.memFree(inputData);
                if (decoder != 0L) {
                    STBVorbis.stb_vorbis_close((long)decoder);
                }
                throw throwable;
            }
            MemoryUtil.memFree((Buffer)errorBuffer);
            if (info != null) {
                info.free();
            }
            MemoryUtil.memFree((Buffer)inputData);
            if (decoder != 0L) {
                STBVorbis.stb_vorbis_close((long)decoder);
            }
            return vorbisSamples;
        }
        STBVorbis.stb_vorbis_get_info((long)decoder, (STBVorbisInfo)info);
        int channels = info.channels();
        int sampleRate = info.sample_rate();
        int sampleLength = STBVorbis.stb_vorbis_stream_length_in_samples((long)decoder);
        ShortBuffer audioBuffer = MemoryUtil.memAllocShort((int)(sampleLength * channels));
        int samplesLoaded = STBVorbis.stb_vorbis_get_samples_short_interleaved((long)decoder, (int)channels, (ShortBuffer)audioBuffer);
        audioBuffer.position(0);
        GameSound.VorbisSamples vorbisSamples = new GameSound.VorbisSamples(audioBuffer, new GameSound.VorbisInfo(samplesLoaded, channels, sampleRate));
        MemoryUtil.memFree((Buffer)errorBuffer);
        if (info != null) {
            info.free();
        }
        MemoryUtil.memFree((Buffer)inputData);
        if (decoder != 0L) {
            STBVorbis.stb_vorbis_close((long)decoder);
        }
        return vorbisSamples;
    }

    public static float getBufferSeconds(int buffer) {
        int sampleRate = AL10.alGetBufferi((int)buffer, (int)8193);
        int channels = AL10.alGetBufferi((int)buffer, (int)8195);
        int bytes = AL10.alGetBufferi((int)buffer, (int)8196);
        int bits = AL10.alGetBufferi((int)buffer, (int)8194);
        int samples = bytes / (bits / 8) / channels;
        return (float)samples / (float)sampleRate;
    }

    private static void queryAndPrintALError(String prefix) {
        String error = LWJGLGameSound.queryALError();
        if (error != null) {
            System.err.println(prefix + "AL error: " + error);
        }
    }

    private static String queryALError() {
        int error = AL10.alGetError();
        if (error == 0) {
            return null;
        }
        return AL10.alGetString((int)error);
    }
}

