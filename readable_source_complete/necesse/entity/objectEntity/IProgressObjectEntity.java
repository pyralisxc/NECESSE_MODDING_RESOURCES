/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;

public interface IProgressObjectEntity {
    public void setupProgressPacket(PacketWriter var1);

    public void applyProgressPacket(PacketReader var1);
}

