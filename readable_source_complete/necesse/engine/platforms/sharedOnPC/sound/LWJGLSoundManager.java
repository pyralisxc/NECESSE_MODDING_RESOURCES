/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.openal.AL
 *  org.lwjgl.openal.AL10
 *  org.lwjgl.openal.ALC
 *  org.lwjgl.openal.ALC10
 *  org.lwjgl.openal.ALCCapabilities
 *  org.lwjgl.openal.ALCapabilities
 *  org.lwjgl.openal.ALUtil
 */
package necesse.engine.platforms.sharedOnPC.sound;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Objects;
import necesse.engine.GameLog;
import necesse.engine.Settings;
import necesse.engine.platforms.sharedOnPC.sound.LWJGLGameSound;
import necesse.engine.platforms.sharedOnPC.sound.LWJGLResourceGameSoundStreamer;
import necesse.engine.platforms.sharedOnPC.sound.LWJGLSampleGameSoundStreamer;
import necesse.engine.platforms.sharedOnPC.sound.LWJGLSoundPlayer;
import necesse.engine.sound.SoundCooldown;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundPlayer;
import necesse.engine.sound.gameSound.GameSound;
import necesse.engine.sound.gameSound.ResourceGameSoundStreamer;
import necesse.engine.sound.gameSound.SampleGameSoundStreamer;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.openal.ALUtil;

public class LWJGLSoundManager
extends SoundManager {
    private String alDeviceName;
    private long alContext;
    private long alDevice;

    @Override
    public boolean initialize() {
        try {
            Thread.sleep(100L);
            this.initALDevice();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        return super.initialize();
    }

    @Override
    protected SoundManager getCurrentSoundManager() {
        return this;
    }

    @Override
    public void dispose() {
        super.dispose();
        if (this.alContext != 0L) {
            ALC10.alcDestroyContext((long)this.alContext);
        }
        if (this.alDevice != 0L) {
            ALC10.alcCloseDevice((long)this.alDevice);
        }
    }

    @Override
    public List<String> getOutputDevices() {
        return ALUtil.getStringList((long)0L, (int)4115);
    }

    @Override
    public boolean audioDeviceReady() {
        return this.alDevice != 0L && this.alContext != 0L;
    }

    @Override
    public void checkForNewDefaultAudioDevice() {
        if (Settings.outputDevice == null) {
            ALC10.alcGetString((long)0L, (int)4115);
            String defaultDeviceName = ALC10.alcGetString((long)0L, (int)4114);
            if (!Objects.equals(this.alDeviceName, defaultDeviceName)) {
                this.initALDevice();
            }
        }
    }

    @Override
    public String getCurrentAudioDeviceName() {
        return this.alDeviceName;
    }

    @Override
    public void setAudioDeviceFromSettings() {
        this.initALDevice();
    }

    @Override
    public GameSound createGameSound(String path, SoundCooldown cooldown, boolean isMusic) throws IOException {
        return new LWJGLGameSound(path, cooldown, isMusic);
    }

    @Override
    public SampleGameSoundStreamer createSampleGameSoundStreamer(GameSound.VorbisSamples data) {
        return new LWJGLSampleGameSoundStreamer(data);
    }

    @Override
    public ResourceGameSoundStreamer createResourceGameSoundStreamer(GameSound sound, ByteBuffer inputBytes) {
        return new LWJGLResourceGameSoundStreamer(sound, inputBytes);
    }

    @Override
    public SoundPlayer createSoundPlayer(GameSound gameSound, SoundEffect effect) {
        return new LWJGLSoundPlayer(gameSound, effect);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void initALDevice() {
        if (Settings.outputDevice != null && Settings.outputDevice.equals(this.alDeviceName)) {
            return;
        }
        if (weather != null) {
            weather.dispose();
            weather = null;
        }
        if (fadingWeather != null) {
            fadingWeather.dispose();
            fadingWeather = null;
        }
        musicManager.dispose();
        Object object = lock;
        synchronized (object) {
            for (SoundPlayer playingSound : playingSounds) {
                playingSound.dispose();
            }
            playingSounds.clear();
        }
        ALC10.alcMakeContextCurrent((long)0L);
        if (this.alDevice != 0L) {
            ALC10.alcCloseDevice((long)this.alDevice);
        }
        if (this.alContext != 0L) {
            ALC10.alcDestroyContext((long)this.alContext);
        }
        this.alDevice = 0L;
        this.alContext = 0L;
        this.alDeviceName = "";
        String deviceName = "";
        long device = 0L;
        if (Settings.outputDevice != null) {
            device = ALC10.alcOpenDevice((CharSequence)Settings.outputDevice);
            deviceName = Settings.outputDevice;
        }
        if (device == 0L) {
            String defaultDeviceName = ALC10.alcGetString((long)0L, (int)4114);
            device = ALC10.alcOpenDevice((CharSequence)defaultDeviceName);
            deviceName = defaultDeviceName;
            if (device == 0L) {
                System.err.println("Could initialize OpenAL: No device was found");
                return;
            }
        }
        this.alDeviceName = deviceName;
        this.alDevice = device;
        int[] attributes = new int[]{0};
        this.alContext = ALC10.alcCreateContext((long)this.alDevice, (int[])attributes);
        ALC10.alcMakeContextCurrent((long)this.alContext);
        if (this.alContext == 0L) {
            System.err.println("Could initialize OpenAL: Could not create device context for " + deviceName);
            return;
        }
        ALCCapabilities alcCapabilities = ALC.createCapabilities((long)this.alDevice);
        ALCapabilities alCapabilities = AL.createCapabilities((ALCCapabilities)alcCapabilities);
        if (!alCapabilities.OpenAL11) {
            GameLog.warn.println("OpenAL11 is not supported on this device");
        } else if (!alCapabilities.OpenAL10) {
            GameLog.warn.println("OpenAL10 is not supported on this device");
        }
        AL10.alListenerfv((int)4100, (float[])new float[]{0.0f, 0.0f, 0.0f});
        AL10.alListenerfv((int)4102, (float[])new float[]{0.0f, 0.0f, 0.0f});
        AL10.alListenerfv((int)4111, (float[])new float[]{0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f});
    }
}

