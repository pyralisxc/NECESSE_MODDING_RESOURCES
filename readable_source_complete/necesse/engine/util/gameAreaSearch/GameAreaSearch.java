/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util.gameAreaSearch;

import necesse.engine.util.gameAreaSearch.GameAreaPipeline;
import necesse.engine.util.gameAreaSearch.GameAreaStream;

public abstract class GameAreaSearch<T> {
    public final int startX;
    public final int startY;
    protected int maxDistance;
    protected int minX;
    protected int minY;
    protected int maxX;
    protected int maxY;
    protected boolean isDone;
    protected int currentTile;
    protected int currentDistance;
    protected int currentDir;
    protected int dirsHandled;

    public GameAreaSearch(int startX, int startY, int minX, int minY, int maxX, int maxY, int maxDistance) {
        this.startX = startX;
        this.startY = startY;
        this.setLimit(minX, minY, maxX, maxY);
        this.setMaxDistance(maxDistance);
    }

    public GameAreaSearch(int startX, int startY, int maxDistance) {
        this(startX, startY, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, maxDistance);
    }

    public int getMaxDistance() {
        return this.maxDistance;
    }

    public void setMaxDistance(int maxDistance) {
        this.maxDistance = maxDistance;
        if (this.currentDistance > maxDistance) {
            this.isDone = true;
        }
    }

    public void setLimit(int minX, int minY, int maxX, int maxY) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    public void shrinkLimit(int minX, int minY, int maxX, int maxY) {
        this.minX = Math.max(minX, this.minX);
        this.minY = Math.max(minY, this.minY);
        this.maxX = Math.min(maxX, this.maxX);
        this.maxY = Math.min(maxY, this.maxY);
    }

    private static int getOffset(int currentTile) {
        if (currentTile % 2 == 0) {
            return currentTile / 2;
        }
        return -(currentTile + 1) / 2;
    }

    protected FoundElement<T> next() {
        if (this.isDone) {
            return null;
        }
        FoundElement<T> out = null;
        int tiles = this.currentDistance * 2 + 1;
        if (this.currentDistance == 0) {
            out = new FoundElement<T>(this.currentDistance, this.currentTile, this.get(this.startX, this.startY));
        } else {
            if (this.currentDir >= 4) {
                if (this.dirsHandled == 0) {
                    this.isDone = true;
                    return null;
                }
                this.dirsHandled = 0;
                this.currentDir = 0;
            }
            ++this.currentDir;
            switch (this.currentDir) {
                case 1: {
                    int y = this.startY - this.currentDistance;
                    if (y < this.minY) {
                        return null;
                    }
                    int x = this.startX + GameAreaSearch.getOffset(this.currentTile);
                    if (x < this.minX || x > this.maxX) {
                        return null;
                    }
                    ++this.dirsHandled;
                    return new FoundElement<T>(this.currentDistance, this.currentTile, this.get(x, y));
                }
                case 2: {
                    int y = this.startY + this.currentDistance;
                    if (y > this.maxY) {
                        return null;
                    }
                    int x = this.startX - GameAreaSearch.getOffset(this.currentTile);
                    if (x < this.minX || x > this.maxX) {
                        return null;
                    }
                    ++this.dirsHandled;
                    return new FoundElement<T>(this.currentDistance, this.currentTile, this.get(x, y));
                }
                case 3: {
                    int x = this.startX - this.currentDistance;
                    if (x < this.minX) {
                        return null;
                    }
                    int y = this.startY - GameAreaSearch.getOffset(this.currentTile);
                    if (y < this.minY || y > this.maxY) {
                        return null;
                    }
                    ++this.dirsHandled;
                    return new FoundElement<T>(this.currentDistance, this.currentTile, this.get(x, y));
                }
                case 4: {
                    int y;
                    int x = this.startX + this.currentDistance;
                    if (x > this.maxX || (y = this.startY + GameAreaSearch.getOffset(this.currentTile)) < this.minY || y > this.maxY) break;
                    ++this.dirsHandled;
                    out = new FoundElement<T>(this.currentDistance, this.currentTile, this.get(x, y));
                    break;
                }
            }
        }
        ++this.currentTile;
        if (this.currentTile >= tiles - 1) {
            ++this.currentDistance;
            this.currentTile = 0;
            if (this.currentDistance > this.maxDistance) {
                this.isDone = true;
            }
        }
        return out;
    }

    protected abstract T get(int var1, int var2);

    public int getCurrentDistance() {
        return this.currentDistance;
    }

    public boolean isDone() {
        return this.isDone;
    }

    public GameAreaStream<T> stream() {
        return new GameAreaPipeline(this);
    }

    public static class FoundElement<T> {
        public final int distance;
        public final int tile;
        public final T element;

        public FoundElement(int distance, int tile, T element) {
            this.distance = distance;
            this.tile = tile;
            this.element = element;
        }
    }
}

