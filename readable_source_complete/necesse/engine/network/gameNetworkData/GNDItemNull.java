/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.gameNetworkData;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

public class GNDItemNull
extends GNDItem {
    public GNDItemNull() {
    }

    public GNDItemNull(PacketReader reader) {
        this.readPacket(reader);
    }

    public GNDItemNull(LoadData data) {
    }

    @Override
    public String toString() {
        return "null";
    }

    @Override
    public boolean isDefault() {
        return true;
    }

    @Override
    public boolean equals(GNDItem item) {
        return item == null || item instanceof GNDItemNull;
    }

    @Override
    public GNDItemNull copy() {
        return new GNDItemNull();
    }

    @Override
    public void addSaveData(SaveData data) {
    }

    @Override
    public void writePacket(PacketWriter writer) {
    }

    @Override
    public void readPacket(PacketReader reader) {
    }
}

