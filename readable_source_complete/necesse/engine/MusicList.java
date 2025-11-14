/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine;

import java.util.ArrayList;
import necesse.engine.AbstractMusicList;
import necesse.engine.MusicOptions;
import necesse.engine.MusicOptionsOffset;
import necesse.engine.sound.GameMusic;
import necesse.engine.util.GameRandom;

public class MusicList
extends AbstractMusicList {
    private ArrayList<MusicOptions> musicList = new ArrayList();
    private long totalMusicListMillis = 0L;
    private long timeOffset;

    public MusicList(long timeOffset) {
        this.timeOffset = timeOffset;
    }

    public MusicList() {
        this(-1L);
    }

    public MusicList(long timeOffset, GameMusic ... musics) {
        this(timeOffset);
        this.addMusic(musics);
    }

    public MusicList(GameMusic ... musics) {
        this();
        this.addMusic(musics);
    }

    public MusicList(long timeOffset, MusicOptions ... options) {
        this(timeOffset);
        this.addMusic(options);
    }

    public MusicList(MusicOptions ... options) {
        this();
        this.addMusic(options);
    }

    @Override
    public MusicOptions getNextMusic(MusicOptions currentMusic) {
        MusicOptions[] newMusic = (MusicOptions[])this.musicList.stream().filter(m -> currentMusic == null || m.music != currentMusic.music).toArray(MusicOptions[]::new);
        if (newMusic.length == 0) {
            return GameRandom.globalRandom.getOneOf(this.musicList);
        }
        return GameRandom.globalRandom.getOneOf(newMusic);
    }

    @Override
    public MusicOptionsOffset getExpectedMusicPlaying() {
        if (this.musicList.isEmpty()) {
            return null;
        }
        if (this.timeOffset != -1L) {
            long currentOffset = Math.floorMod(this.timeOffset, this.totalMusicListMillis);
            for (int i = 0; i < this.musicList.size(); ++i) {
                MusicOptions options = this.musicList.get(i);
                long currentLength = options.getMusicListMilliseconds();
                if (currentOffset < currentLength) {
                    return new MusicOptionsOffset(options, i, currentOffset);
                }
                currentOffset -= currentLength;
            }
        }
        return null;
    }

    @Override
    public boolean shouldChangeTrack(MusicOptions currentMusic) {
        if (currentMusic == null && !this.musicList.isEmpty()) {
            return true;
        }
        return this.musicList.stream().noneMatch(o -> o.music == currentMusic.music);
    }

    @Override
    public Iterable<MusicOptions> getMusicInList() {
        return () -> this.musicList.iterator();
    }

    public MusicList setTimeOffset(long timeOffset) {
        this.timeOffset = timeOffset;
        return this;
    }

    public MusicList addMusic(MusicOptions options) {
        this.musicList.add(options);
        if (options.music != null && options.music.sound != null) {
            this.totalMusicListMillis += options.getMusicListMilliseconds();
        }
        return this;
    }

    public MusicList addMusic(MusicOptions ... options) {
        for (MusicOptions option : options) {
            this.addMusic(option);
        }
        return this;
    }

    public MusicList addMusic(GameMusic music) {
        return this.addMusic(new MusicOptions(music));
    }

    public MusicList addMusic(GameMusic ... musics) {
        for (GameMusic music : musics) {
            this.addMusic(music);
        }
        return this;
    }

    public MusicList addMusic(GameMusic music, float volume) {
        return this.addMusic(new MusicOptions(music).volume(volume));
    }

    public MusicOptions addMusicConfig(GameMusic music) {
        MusicOptions options = new MusicOptions(music);
        this.addMusic(options);
        return options;
    }
}

