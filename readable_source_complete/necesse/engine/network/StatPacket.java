/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network;

import necesse.engine.util.GameUtils;

public class StatPacket {
    public final int type;
    public int amount;
    public int bytes;

    public StatPacket(int type) {
        this.type = type;
        this.amount = 0;
        this.bytes = 0;
    }

    public String getBytes() {
        return GameUtils.getByteString(this.bytes);
    }
}

