/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.networkField;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.entity.mobs.networkField.NetworkField;

public class ShortNetworkField
extends NetworkField<Short> {
    public ShortNetworkField(short startValue) {
        super(startValue);
    }

    @Override
    public void writePacket(Short value, PacketWriter writer) {
        writer.putNextShort(value);
    }

    @Override
    public Short readPacket(PacketReader reader) {
        return reader.getNextShort();
    }
}

