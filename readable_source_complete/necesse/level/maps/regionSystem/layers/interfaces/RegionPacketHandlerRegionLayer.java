/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.regionSystem.layers.interfaces;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;

public interface RegionPacketHandlerRegionLayer {
    public void writeLayerPacket(PacketWriter var1);

    public boolean applyLayerPacket(PacketReader var1);
}

