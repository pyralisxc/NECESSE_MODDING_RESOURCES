/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.networkField;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.entity.mobs.networkField.NetworkField;

public class ByteNetworkField
extends NetworkField<Byte> {
    public ByteNetworkField(byte startValue) {
        super(startValue);
    }

    @Override
    public void writePacket(Byte value, PacketWriter writer) {
        writer.putNextByte(value);
    }

    @Override
    public Byte readPacket(PacketReader reader) {
        return reader.getNextByte();
    }
}

