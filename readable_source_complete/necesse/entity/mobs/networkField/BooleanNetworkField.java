/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.networkField;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.entity.mobs.networkField.NetworkField;

public class BooleanNetworkField
extends NetworkField<Boolean> {
    public BooleanNetworkField(boolean startValue) {
        super(startValue);
    }

    @Override
    public void writePacket(Boolean value, PacketWriter writer) {
        writer.putNextBoolean(value);
    }

    @Override
    public Boolean readPacket(PacketReader reader) {
        return reader.getNextBoolean();
    }
}

