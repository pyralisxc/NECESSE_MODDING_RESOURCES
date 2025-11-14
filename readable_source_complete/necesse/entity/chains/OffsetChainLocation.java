/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.chains;

import necesse.entity.chains.ChainLocation;

public class OffsetChainLocation
implements ChainLocation {
    public ChainLocation location;
    public int xOffset;
    public int yOffset;

    public OffsetChainLocation(ChainLocation location, int xOffset, int yOffset) {
        this.location = location;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    @Override
    public int getX() {
        return this.location.getX() + this.xOffset;
    }

    @Override
    public int getY() {
        return this.location.getY() + this.yOffset;
    }

    @Override
    public boolean removed() {
        return this.location.removed();
    }
}

