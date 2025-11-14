/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine;

import necesse.engine.MusicOptions;
import necesse.engine.MusicOptionsOffset;

public abstract class AbstractMusicList {
    public abstract MusicOptions getNextMusic(MusicOptions var1);

    public abstract MusicOptionsOffset getExpectedMusicPlaying();

    public abstract boolean shouldChangeTrack(MusicOptions var1);

    public abstract Iterable<MusicOptions> getMusicInList();
}

