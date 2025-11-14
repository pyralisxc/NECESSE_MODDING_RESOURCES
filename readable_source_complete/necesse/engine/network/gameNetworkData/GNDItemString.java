/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.gameNetworkData;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameUtils;

public class GNDItemString
extends GNDItem {
    private String data;

    public GNDItemString(String data) {
        this.data = data;
    }

    public GNDItemString(PacketReader reader) {
        this.readPacket(reader);
    }

    public GNDItemString(LoadData data) {
        this.data = data.getSafeString("value");
    }

    @Override
    public String toString() {
        return this.data == null ? "" : this.data;
    }

    @Override
    public boolean isDefault() {
        return this.data == null || this.data.length() == 0;
    }

    @Override
    public boolean equals(GNDItem item) {
        if (item instanceof GNDItemString) {
            return this.toString().equals(item.toString());
        }
        if (item instanceof GNDItem.GNDPrimitive) {
            double d = ((GNDItem.GNDPrimitive)item).getDouble();
            return GameUtils.formatNumber(d).equals(this.data);
        }
        return false;
    }

    @Override
    public GNDItemString copy() {
        return new GNDItemString(this.data);
    }

    @Override
    public void addSaveData(SaveData data) {
        data.addSafeString("value", this.data);
    }

    @Override
    public void writePacket(PacketWriter writer) {
        writer.putNextString(this.data);
    }

    @Override
    public void readPacket(PacketReader reader) {
        this.data = reader.getNextString();
    }
}

