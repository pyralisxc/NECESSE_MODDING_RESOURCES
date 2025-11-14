/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.networkField;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.entity.mobs.networkField.NetworkField;

public class LongNetworkField
extends NetworkField<Long> {
    public LongNetworkField(long startValue) {
        super(startValue);
    }

    @Override
    public void writePacket(Long value, PacketWriter writer) {
        writer.putNextLong(value);
    }

    @Override
    public Long readPacket(PacketReader reader) {
        return reader.getNextLong();
    }
}

