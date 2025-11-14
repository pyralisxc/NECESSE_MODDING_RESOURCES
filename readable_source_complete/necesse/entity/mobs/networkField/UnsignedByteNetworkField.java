/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.networkField;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.entity.mobs.networkField.NetworkField;

public class UnsignedByteNetworkField
extends NetworkField<Integer> {
    public UnsignedByteNetworkField(int startValue) {
        super(startValue);
    }

    @Override
    public void writePacket(Integer value, PacketWriter writer) {
        writer.putNextByteUnsigned(value);
    }

    @Override
    public Integer readPacket(PacketReader reader) {
        return reader.getNextByteUnsigned();
    }
}

