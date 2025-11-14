/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network;

import necesse.engine.network.PacketManager;

public class SizePacket {
    public final int type;
    public final int byteSize;
    private long getTime;

    public SizePacket(int type, int byteSize) {
        this.type = type;
        this.byteSize = byteSize;
        this.getTime = System.currentTimeMillis();
    }

    public boolean isExpired() {
        return this.getTime + (long)(PacketManager.networkTrackingTime * 1000) <= System.currentTimeMillis();
    }
}

