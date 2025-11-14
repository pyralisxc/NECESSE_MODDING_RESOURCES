/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.system.MemoryUtil
 */
package necesse.engine.sound.gameSound;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import necesse.engine.GameLoadingScreen;
import necesse.engine.GameLog;
import necesse.engine.GlobalData;
import necesse.engine.platforms.Platform;
import necesse.engine.sound.SameNearRandomSoundCooldown;
import necesse.engine.sound.SameNearSoundCooldown;
import necesse.engine.sound.SoundCooldown;
import necesse.engine.sound.gameSound.GameSoundStreamer;
import necesse.engine.util.GameUtils;
import necesse.gfx.res.ResourceEncoder;
import org.lwjgl.system.MemoryUtil;

public abstract class GameSound {
    private static final int LOADING_THREADS = 10;
    protected static ThreadPoolExecutor loadingExecutor;
    private static final HashMap<String, GameSound> loadedSounds;
    public final String path;
    public final SoundCooldown cooldown;
    protected Future<VorbisSamples> getSamplesLoading;
    protected ByteBuffer inputBytes;
    protected VorbisInfo info;
    protected VorbisSamples samples;
    private final boolean isMusic;
    private boolean foundInFile;
    private float volumeModifier = 1.0f;

    protected GameSound(String path, SoundCooldown cooldown, boolean isMusic) throws IOException {
        this.path = path;
        this.cooldown = cooldown;
        this.isMusic = isMusic;
    }

    public static void startLoaderThreads() {
        if (loadingExecutor != null) {
            return;
        }
        loadingExecutor = new ThreadPoolExecutor(10, 10, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<Runnable>(), GameSound.defaultThreadFactory());
    }

    private static ThreadFactory defaultThreadFactory() {
        AtomicInteger threadNum = new AtomicInteger(0);
        return r -> {
            Thread thread = new Thread(null, r, "sound-loader-" + threadNum.incrementAndGet());
            thread.setDaemon(true);
            return thread;
        };
    }

    public static void endLoaderThreads() {
        for (GameSound sound : loadedSounds.values()) {
            sound.waitForDoneLoading();
        }
        loadingExecutor.shutdown();
        loadingExecutor = null;
    }

    public void waitForDoneLoading() {
        if (this.getSamplesLoading != null) {
            GameLoadingScreen.drawLoadingSub("sound/" + this.path);
            try {
                this.samples = this.getSamplesLoading.get();
            }
            catch (InterruptedException e) {
                this.samples = new VorbisSamples(MemoryUtil.memAllocShort((int)0), new VorbisInfo(0, 1, 0));
            }
            catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
            if (this.samples.info.channels > 1 && !this.isMusic) {
                GameLog.warn.println(this.path + " audio file is not mono, so positional audio will not work.");
            }
            this.info = this.samples.info;
            if (!this.foundInFile && !GlobalData.isDevMode()) {
                GameLog.warn.println("sound/" + this.path + " was not found in resource file.");
            }
        }
        this.getSamplesLoading = null;
    }

    public static void listUnloadedSounds(List<String> excludes, List<String> excludeDirs) {
        if (excludes == null) {
            excludes = new ArrayList<String>();
        }
        if (excludeDirs == null) {
            excludeDirs = new ArrayList<String>();
        }
        GameSound.listUnloadedSounds("", excludes, excludeDirs);
    }

    private static void listUnloadedSounds(String dir, List<String> excludes, List<String> excludeDirs) {
        File[] files;
        String subDir;
        excludes.replaceAll(s -> GameUtils.formatFileExtension(s, "ogg"));
        File fDir = new File(GlobalData.rootPath() + "res/sound/" + dir);
        String string = subDir = dir.isEmpty() ? "" : dir + "/";
        if (fDir.exists() && fDir.isDirectory() && (files = fDir.listFiles()) != null) {
            for (File f : files) {
                GameSound loadedTexture;
                if (f.isDirectory()) {
                    if (excludeDirs.contains(subDir + f.getName())) continue;
                    GameSound.listUnloadedSounds(subDir + f.getName(), excludes, excludeDirs);
                    continue;
                }
                String name = f.getName();
                String key = subDir + name;
                if (!name.endsWith(".ogg") || (loadedTexture = loadedSounds.get(key)) != null || excludes.contains(key)) continue;
                GameLog.warn.println("Sound " + key + " is never loaded");
            }
        }
    }

    public static GameSound fromFile(String filePath) {
        return GameSound.fromFile(filePath, null);
    }

    public static GameSound fromFile(String filePath, int cooldown) {
        return GameSound.fromFile(filePath, cooldown > 0 ? new SameNearSoundCooldown(cooldown, 50) : null, false);
    }

    public static GameSound fromFile(String filePath, int minRandomCooldown, int maxRandomCooldown) {
        return GameSound.fromFile(filePath, new SameNearRandomSoundCooldown(minRandomCooldown, maxRandomCooldown, 50), false);
    }

    public static GameSound fromFile(String filePath, SoundCooldown cooldown) {
        return GameSound.fromFile(filePath, cooldown, false);
    }

    public static GameSound fromFile(String filePath, SoundCooldown cooldown, boolean isMusic) {
        Objects.requireNonNull(filePath);
        try {
            return GameSound.fromFileRaw(filePath, cooldown, isMusic);
        }
        catch (IOException e) {
            System.err.println("Could not find sound file " + filePath);
            e.printStackTrace();
            return null;
        }
    }

    public static GameSound fromFileRaw(String filePath, SoundCooldown cooldown, boolean isMusic) throws IOException {
        GameSound gameSound = loadedSounds.get(filePath = GameUtils.formatFileExtension(filePath, "ogg"));
        if (gameSound == null) {
            gameSound = Platform.getSoundManager().createGameSound(filePath, cooldown, isMusic);
            loadedSounds.put(filePath, gameSound);
        }
        return gameSound;
    }

    public static GameSound fromFileMusic(String filePath) {
        return GameSound.fromFile(filePath, null, true);
    }

    public static void deleteSounds() {
        if (loadingExecutor != null) {
            loadingExecutor.shutdownNow();
        }
        for (GameSound sound : loadedSounds.values()) {
            sound.delete();
        }
        loadedSounds.clear();
    }

    public void delete() {
        if (this.samples != null) {
            MemoryUtil.memFree((Buffer)this.samples.samples);
        }
        if (this.inputBytes != null) {
            MemoryUtil.memFree((Buffer)this.inputBytes);
        }
    }

    protected byte[] loadBytes() throws IOException {
        File outsideFile = new File(GlobalData.rootPath() + "res/sound/" + this.path);
        if (outsideFile.exists()) {
            this.foundInFile = true;
            return GameUtils.loadByteFile(outsideFile);
        }
        try {
            this.foundInFile = true;
            return ResourceEncoder.getResourceBytes("sound/" + this.path);
        }
        catch (FileNotFoundException e) {
            return GameUtils.loadByteFile(outsideFile);
        }
    }

    public float getVolumeModifier() {
        return this.volumeModifier;
    }

    public GameSound setVolumeModifier(float modifier) {
        this.volumeModifier = modifier;
        return this;
    }

    public long getLengthInMillis() {
        if (this.info != null) {
            return this.info.lengthMillis;
        }
        return -1L;
    }

    public float getLengthInSeconds() {
        if (this.info != null) {
            return this.info.lengthSeconds;
        }
        return -1.0f;
    }

    public boolean isMusic() {
        return this.isMusic;
    }

    public GameSoundStreamer getStreamer() {
        if (this.samples != null) {
            return Platform.getSoundManager().createSampleGameSoundStreamer(this.samples);
        }
        return Platform.getSoundManager().createResourceGameSoundStreamer(this, this.inputBytes);
    }

    static {
        loadedSounds = new HashMap();
    }

    public static class VorbisSamples {
        public final ShortBuffer samples;
        public final VorbisInfo info;

        public VorbisSamples(ShortBuffer samples, VorbisInfo info) {
            this.samples = samples;
            this.info = info;
        }
    }

    public static class VorbisInfo {
        public final int totalSamples;
        public final int channels;
        public final int sampleRate;
        public final long lengthMillis;
        public final float lengthSeconds;

        public VorbisInfo(int totalSamples, int channels, int sampleRate) {
            this.totalSamples = totalSamples;
            this.channels = channels;
            this.sampleRate = sampleRate;
            if (sampleRate != 0) {
                this.lengthMillis = (long)((double)totalSamples / (double)sampleRate * 1000.0);
                this.lengthSeconds = (float)((double)totalSamples / (double)sampleRate);
            } else {
                this.lengthMillis = 0L;
                this.lengthSeconds = 0.0f;
            }
        }
    }
}

