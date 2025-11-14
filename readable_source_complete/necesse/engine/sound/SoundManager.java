/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.sound;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import necesse.engine.AbstractMusicList;
import necesse.engine.GlobalData;
import necesse.engine.MusicList;
import necesse.engine.MusicOptions;
import necesse.engine.PlayingMusicManager;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.sound.GameMusic;
import necesse.engine.sound.PositionSoundEffect;
import necesse.engine.sound.PrimitiveSoundEmitter;
import necesse.engine.sound.RainSoundEffect;
import necesse.engine.sound.SoundCooldown;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundEmitter;
import necesse.engine.sound.SoundPlayer;
import necesse.engine.sound.SoundSettings;
import necesse.engine.sound.SoundTime;
import necesse.engine.sound.gameSound.GameSound;
import necesse.engine.sound.gameSound.ResourceGameSoundStreamer;
import necesse.engine.sound.gameSound.SampleGameSoundStreamer;
import necesse.engine.state.State;
import necesse.engine.util.ComparableSequence;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.window.WindowManager;

public abstract class SoundManager {
    protected static final Object lock = new Object();
    private static final Object soundSynchronized = new Object();
    public static ExecutorService musicStreamer;
    public static SoundEmitter listener;
    protected static SoundPlayer weather;
    protected static SoundPlayer fadingWeather;
    protected static ArrayList<SoundPlayer> playingSounds;
    protected static LinkedList<SoundTime> recentSounds;
    protected static PlayingMusicManager musicManager;
    private static SoundManager currentSoundManager;
    private static boolean wasWindowFocused;

    public static void forceChangeMusic() {
        musicManager.forceChangeMusic();
    }

    public static void setMusic(AbstractMusicList list, MusicPriority priority) {
        SoundManager.setMusic(list, priority.priority);
    }

    public static void setMusic(AbstractMusicList list, int priority) {
        SoundManager.setMusic(list, new ComparableSequence<Integer>(priority));
    }

    public static void setMusic(AbstractMusicList list, ComparableSequence<Integer> priority) {
        musicManager.setNextMusic(list, priority);
    }

    public static void setMusic(MusicOptions music, MusicPriority priority) {
        SoundManager.setMusic(music, priority.priority);
    }

    public static void setMusic(MusicOptions music, int priority) {
        SoundManager.setMusic(music, new ComparableSequence<Integer>(priority));
    }

    public static void setMusic(MusicOptions music, ComparableSequence<Integer> priority) {
        SoundManager.setMusic((AbstractMusicList)new MusicList().addMusic(music), priority);
    }

    public static void setMusic(GameMusic music, MusicPriority priority, float volume) {
        SoundManager.setMusic(music, priority.priority, volume);
    }

    public static void setMusic(GameMusic music, int priority, float volume) {
        SoundManager.setMusic(music, new ComparableSequence<Integer>(priority), volume);
    }

    public static void setMusic(GameMusic music, ComparableSequence<Integer> priority, float volume) {
        SoundManager.setMusic((AbstractMusicList)new MusicList().addMusic(music, volume), priority);
    }

    public static void setMusic(GameMusic music, MusicPriority priority) {
        SoundManager.setMusic(music, priority.priority);
    }

    public static void setMusic(GameMusic music, int priority) {
        SoundManager.setMusic(music, new ComparableSequence<Integer>(priority));
    }

    public static void setMusic(GameMusic music, ComparableSequence<Integer> priority) {
        SoundManager.setMusic((AbstractMusicList)new MusicList().addMusic(music), priority);
    }

    public static GameMusic getCurrentMusic() {
        return musicManager.getCurrentMusic();
    }

    public static SoundPlayer playSound(GameSound gameSound, SoundEffect effect) {
        if (gameSound == null) {
            return null;
        }
        return SoundManager.playSound(gameSound, effect, gameSound.cooldown);
    }

    public static SoundPlayer playSound(GameSound gameSound, SoundEffect effect, Consumer<SoundPlayer> onCreated) {
        if (gameSound == null) {
            return null;
        }
        return SoundManager.playSound(gameSound, effect, gameSound.cooldown, onCreated);
    }

    public static SoundPlayer playSoundAtPosition(GameSound gameSound, float position, SoundEffect effect, Consumer<SoundPlayer> onCreated) {
        if (gameSound == null) {
            return null;
        }
        return SoundManager.playSoundAtPosition(gameSound, position, effect, gameSound.cooldown, onCreated);
    }

    public static SoundPlayer playSound(GameSound gameSound, SoundEffect effect, SoundCooldown cooldown) {
        return SoundManager.playSound(gameSound, effect, cooldown, null);
    }

    public static SoundPlayer playSound(GameSound gameSound, SoundEffect effect, SoundCooldown cooldown, Consumer<SoundPlayer> onCreated) {
        return SoundManager.playSoundAtPosition(gameSound, 0.0f, effect, cooldown, onCreated);
    }

    public static SoundPlayer playSoundAtPosition(GameSound gameSound, float position, SoundEffect effect, SoundCooldown cooldown, Consumer<SoundPlayer> onCreated) {
        if (gameSound == null || effect == null) {
            return null;
        }
        if (!currentSoundManager.audioDeviceReady()) {
            return null;
        }
        SoundPlayer player = currentSoundManager.createSoundPlayer(gameSound, effect);
        if (onCreated != null) {
            onCreated.accept(player);
        }
        SoundManager.addQueuedSound(player, position, cooldown);
        return player;
    }

    public static SoundPlayer playSound(SoundSettings soundSettings, float x, float y) {
        return SoundManager.playSound(soundSettings, SoundEffect.effect(x, y));
    }

    public static SoundPlayer playSound(SoundSettings soundSettings, PrimitiveSoundEmitter emitter) {
        return SoundManager.playSound(soundSettings, SoundEffect.effect(emitter));
    }

    public static SoundPlayer playSound(SoundSettings soundSettings, PositionSoundEffect effect) {
        return SoundManager.playSound(soundSettings, effect, null);
    }

    public static SoundPlayer playSound(SoundSettings soundSettings, PositionSoundEffect effect, Consumer<SoundPlayer> onCreated) {
        if (soundSettings == null || soundSettings.sounds == null) {
            return null;
        }
        GameSound sound = soundSettings.sounds[GameRandom.globalRandom.getIntBetween(0, soundSettings.sounds.length - 1)];
        if (sound == null) {
            return null;
        }
        SoundCooldown cooldown = soundSettings.getCooldown();
        return SoundManager.playSound(sound, soundSettings.applySettings(effect), cooldown != null ? cooldown : sound.cooldown, onCreated);
    }

    public abstract boolean audioDeviceReady();

    public abstract SoundPlayer createSoundPlayer(GameSound var1, SoundEffect var2);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void addQueuedSound(SoundPlayer sound, float position, SoundCooldown cooldown) {
        Object object = soundSynchronized;
        synchronized (object) {
            if (cooldown != null) {
                cooldown = cooldown.initializeForSound(sound);
            }
            if (cooldown != null) {
                for (SoundTime recentSound : recentSounds) {
                    if (!cooldown.isBlockedBy(sound, recentSound)) continue;
                    sound.dispose();
                    return;
                }
            }
            sound.playSound(position);
            playingSounds.add(sound);
            if (cooldown != null && cooldown.getTimeToLive() > 0) {
                Object object2 = lock;
                synchronized (object2) {
                    recentSounds.add(new SoundTime(sound, cooldown));
                }
            }
        }
    }

    public static int getPlayingSoundsCount() {
        int count = 0;
        if (playingSounds != null) {
            count += playingSounds.size();
        }
        if (weather != null) {
            ++count;
        }
        if (fadingWeather != null) {
            ++count;
        }
        return count += musicManager.getPlayingCount();
    }

    public static void setWeatherSound(GameSound sound, float dx, float dy, float distance, float intensity) {
        if (!currentSoundManager.audioDeviceReady()) {
            return;
        }
        if (weather != null && weather.getSecondsLeft() < 0.7f) {
            fadingWeather = weather;
            SoundManager.updateWeatherVol(fadingWeather);
            weather = null;
        }
        Point2D.Float dir = GameMath.normalize(dx, dy);
        RainSoundEffect effect = new RainSoundEffect(() -> Float.valueOf(Settings.masterVolume * Settings.weatherVolume), dir.x, dir.y, distance, intensity);
        if (weather == null) {
            weather = currentSoundManager.createSoundPlayer(sound, effect);
            weather.playSound();
        } else {
            SoundManager.weather.effect = effect;
        }
        SoundManager.updateWeatherVol(weather);
    }

    private static void updateWeatherVol(SoundPlayer sound) {
        if (sound.getSecondsLeft() < 0.5f) {
            float vol = sound.getSecondsLeft() / 0.5f;
            sound.effect.volume(vol);
        } else if (sound.getPositionSeconds() < 0.5f) {
            float vol = sound.getPositionSeconds() / 0.5f;
            sound.effect.volume(vol);
        } else {
            sound.effect.volume(1.0f);
        }
        sound.update();
    }

    public boolean initialize() {
        musicStreamer = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(), r -> new Thread(null, r, "music-streamer"));
        listener = SoundManager.getALListener();
        currentSoundManager = this.getCurrentSoundManager();
        return true;
    }

    public static SoundEmitter getALListener() {
        State state = GlobalData.getCurrentState();
        if (state == null) {
            return new SoundEmitter(){

                @Override
                public float getSoundPositionX() {
                    return 0.0f;
                }

                @Override
                public float getSoundPositionY() {
                    return 0.0f;
                }
            };
        }
        return state.getALListener();
    }

    protected abstract SoundManager getCurrentSoundManager();

    public void dispose() {
        GameSound.deleteSounds();
        playingSounds.forEach(SoundPlayer::dispose);
        musicManager.dispose();
        try {
            if (musicStreamer != null) {
                musicStreamer.shutdownNow();
                musicStreamer.awaitTermination(2L, TimeUnit.SECONDS);
            }
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
        if (weather != null) {
            weather.dispose();
        }
        if (fadingWeather != null) {
            fadingWeather.dispose();
        }
    }

    public void preGameTick(TickManager tickManager) {
        if (tickManager.isGameTick()) {
            musicManager.clearNextMusic();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void postGameTick(TickManager tickManager) {
        boolean focus;
        if (tickManager.isGameTick() && this.audioDeviceReady()) {
            Object object = soundSynchronized;
            synchronized (object) {
                musicManager.tick();
                if (fadingWeather != null) {
                    if (fadingWeather.isDone()) {
                        fadingWeather.dispose();
                        fadingWeather = null;
                    } else {
                        SoundManager.updateWeatherVol(fadingWeather);
                    }
                }
                if (weather != null) {
                    if (weather.isDone()) {
                        weather.dispose();
                        weather = null;
                    } else {
                        SoundManager.updateWeatherVol(weather);
                    }
                }
                listener = SoundManager.getALListener();
                Performance.record((PerformanceTimerManager)tickManager, "recentSounds", () -> {
                    Object object = lock;
                    synchronized (object) {
                        recentSounds.removeIf(t -> t.getAge() > (long)t.cooldown.getTimeToLive());
                    }
                });
                Performance.record((PerformanceTimerManager)tickManager, "playingSounds", () -> {
                    for (int i = 0; i < playingSounds.size(); ++i) {
                        SoundPlayer s = playingSounds.get(i);
                        s.update();
                        if (!s.isDone()) continue;
                        s.dispose();
                        playingSounds.remove(i);
                        --i;
                    }
                });
            }
        }
        if ((focus = WindowManager.getWindow().isFocused()) != wasWindowFocused) {
            wasWindowFocused = focus;
            SoundManager.updateVolume();
            if (focus) {
                this.checkForNewDefaultAudioDevice();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void updateVolume() {
        Object object = soundSynchronized;
        synchronized (object) {
            if (playingSounds != null) {
                playingSounds.forEach(SoundPlayer::update);
            }
            if (weather != null) {
                weather.update();
            }
            if (fadingWeather != null) {
                fadingWeather.update();
            }
            musicManager.updateVolume();
        }
    }

    public abstract void checkForNewDefaultAudioDevice();

    public abstract List<String> getOutputDevices();

    public abstract String getCurrentAudioDeviceName();

    public abstract void setAudioDeviceFromSettings();

    public abstract GameSound createGameSound(String var1, SoundCooldown var2, boolean var3) throws IOException;

    public abstract SampleGameSoundStreamer createSampleGameSoundStreamer(GameSound.VorbisSamples var1);

    public abstract ResourceGameSoundStreamer createResourceGameSoundStreamer(GameSound var1, ByteBuffer var2);

    static {
        playingSounds = new ArrayList();
        recentSounds = new LinkedList();
        musicManager = new PlayingMusicManager();
        wasWindowFocused = false;
    }

    public static enum MusicPriority {
        BIOME(0),
        EVENT(100),
        MUSIC_PLAYER(1000),
        PORTABLE_MUSIC_PLAYER(2000);

        public final int priority;

        private MusicPriority(int priority) {
            this.priority = priority;
        }

        public ComparableSequence<Integer> thenBy(int priority) {
            return new ComparableSequence<Integer>(this.priority).thenBy(priority);
        }
    }
}

