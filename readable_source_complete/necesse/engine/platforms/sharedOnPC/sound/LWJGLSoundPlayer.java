/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.openal.AL10
 */
package necesse.engine.platforms.sharedOnPC.sound;

import java.nio.FloatBuffer;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundPlayer;
import necesse.engine.sound.gameSound.GameSound;
import org.lwjgl.openal.AL10;

public class LWJGLSoundPlayer
extends SoundPlayer {
    public LWJGLSoundPlayer(GameSound gameSound, SoundEffect effect) {
        super(gameSound, effect);
    }

    public static String getError() {
        int error = AL10.alGetError();
        switch (error) {
            case 40961: {
                return "AL_INVALID_NAME";
            }
            case 40962: {
                return "AL_INVALID_ENUM";
            }
            case 40963: {
                return "AL_INVALID_VALUE";
            }
            case 40964: {
                return "AL_INVALID_OPERATION";
            }
            case 40965: {
                return "AL_OUT_OF_MEMORY";
            }
        }
        return null;
    }

    @Override
    protected void alStopAndDeleteSource() {
        if (this.source != 0) {
            AL10.alSourceStop((int)this.source);
            AL10.alDeleteSources((int)this.source);
            this.source = 0;
        }
    }

    @Override
    protected void alSetupSource() {
        this.source = AL10.alGenSources();
        AL10.alSourcei((int)this.source, (int)514, (int)1);
        AL10.alSourcef((int)this.source, (int)4131, (float)0.0f);
    }

    @Override
    protected void alPlaySource() {
        AL10.alSourcePlay((int)this.source);
    }

    @Override
    protected void alQueueBuffers(int buffer) {
        AL10.alSourceQueueBuffers((int)this.source, (int)buffer);
    }

    @Override
    protected boolean alIsSourceStopped() {
        return AL10.alGetSourcei((int)this.source, (int)4112) == 4116;
    }

    @Override
    protected void alSetSourceLooping(boolean loops) {
        AL10.alSourcei((int)this.source, (int)4103, (int)(loops ? 1 : 0));
    }

    @Override
    protected void alPauseSource() {
        AL10.alSourcePause((int)this.source);
    }

    @Override
    public void alSetPitch(float pitch) {
        AL10.alSourcef((int)this.source, (int)4099, (float)pitch);
    }

    @Override
    public void alSetGain(float volume) {
        AL10.alSourcef((int)this.source, (int)4106, (float)volume);
    }

    @Override
    public void alSetPosition(FloatBuffer position) {
        AL10.alSourcefv((int)this.source, (int)4100, (FloatBuffer)position);
    }

    private String state() {
        int state = AL10.alGetSourcei((int)this.source, (int)4112);
        switch (state) {
            case 4113: {
                return "AL_INITIAL";
            }
            case 4114: {
                return "AL_PLAYING";
            }
            case 4115: {
                return "AL_PAUSED";
            }
            case 4116: {
                return "AL_STOPPED";
            }
        }
        return "N/A";
    }
}

