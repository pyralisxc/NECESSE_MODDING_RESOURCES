/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.chains;

import necesse.entity.chains.ChainLocation;

public class StaticChainLocation
implements ChainLocation {
    public int x;
    public int y;

    public StaticChainLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int getX() {
        return this.x;
    }

    @Override
    public int getY() {
        return this.y;
    }

    @Override
    public boolean removed() {
        return false;
    }
}

