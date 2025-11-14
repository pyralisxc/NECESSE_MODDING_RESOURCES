/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util.gameAreaSearch;

import necesse.engine.util.gameAreaSearch.GameAreaSearch;
import necesse.level.maps.Level;

public abstract class GameRegionSearch<T>
extends GameAreaSearch<T> {
    public final Level level;

    public GameRegionSearch(Level level, int startX, int startY, int maxDistance) {
        super(startX, startY, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, maxDistance);
        this.level = level;
    }

    @Override
    protected abstract T get(int var1, int var2);
}

