/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.networkField;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.entity.mobs.networkField.NetworkField;

public class UnsignedShortNetworkField
extends NetworkField<Integer> {
    public UnsignedShortNetworkField(int startValue) {
        super(startValue);
    }

    @Override
    public void writePacket(Integer value, PacketWriter writer) {
        writer.putNextShortUnsigned(value);
    }

    @Override
    public Integer readPacket(PacketReader reader) {
        return reader.getNextShortUnsigned();
    }
}

