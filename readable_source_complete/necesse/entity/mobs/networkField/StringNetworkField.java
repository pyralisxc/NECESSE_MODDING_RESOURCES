/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.networkField;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.entity.mobs.networkField.NetworkField;

public class StringNetworkField
extends NetworkField<String> {
    public StringNetworkField(String startValue) {
        super(startValue);
    }

    @Override
    public void writePacket(String value, PacketWriter writer) {
        writer.putNextString(value);
    }

    @Override
    public String readPacket(PacketReader reader) {
        return reader.getNextString();
    }
}

