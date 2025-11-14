/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.regionSystem;

import necesse.level.maps.regionSystem.Region;

public class RegionUnloadBuffer {
    public final int regionX;
    public final int regionY;
    protected int buffer = 0;

    public RegionUnloadBuffer(int regionX, int regionY) {
        this.regionX = regionX;
        this.regionY = regionY;
    }

    public void gameTick(Region region) {
        if (region.regionX == this.regionX && region.regionY == this.regionY) {
            ++this.buffer;
        }
    }

    public void keepLoaded() {
        this.buffer = 0;
    }

    public void keepLoaded(int minSeconds) {
        this.buffer = Math.min(this.buffer, minSeconds * 20);
    }

    public boolean shouldUnload(int seconds) {
        return this.buffer > seconds * 20;
    }

    public float getUnloadProgress(int seconds) {
        return Math.min(1.0f, (float)this.buffer / (float)(seconds * 20));
    }

    public int getBuffer() {
        return this.buffer;
    }
}

