/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.networkField;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.entity.mobs.networkField.NetworkField;

public class FloatNetworkField
extends NetworkField<Float> {
    public FloatNetworkField(float startValue) {
        super(Float.valueOf(startValue));
    }

    @Override
    public void writePacket(Float value, PacketWriter writer) {
        writer.putNextFloat(value.floatValue());
    }

    @Override
    public Float readPacket(PacketReader reader) {
        return Float.valueOf(reader.getNextFloat());
    }
}

