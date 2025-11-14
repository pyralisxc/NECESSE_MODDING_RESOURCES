/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.openal.AL10
 */
package necesse.engine.platforms.sharedOnPC.sound;

import necesse.engine.sound.gameSound.GameSound;
import necesse.engine.sound.gameSound.SampleGameSoundStreamer;
import org.lwjgl.openal.AL10;

public class LWJGLSampleGameSoundStreamer
extends SampleGameSoundStreamer {
    public LWJGLSampleGameSoundStreamer(GameSound.VorbisSamples data) {
        super(data);
    }

    @Override
    protected void generateBuffers(int[] buffers) {
        AL10.alGenBuffers((int[])buffers);
    }

    @Override
    protected void deleteBuffers(int bufferCount) {
        AL10.alDeleteBuffers((int)bufferCount);
    }

    @Override
    protected void bufferData(int buffer, boolean isStereo, short[] audioData, int sampleRate) {
        AL10.alBufferData((int)buffer, (int)(isStereo ? 4355 : 4353), (short[])audioData, (int)sampleRate);
    }
}

