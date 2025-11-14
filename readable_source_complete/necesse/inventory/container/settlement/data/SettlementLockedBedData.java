/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.data;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;

public class SettlementLockedBedData {
    public final int tileX;
    public final int tileY;

    public SettlementLockedBedData(int tileX, int tileY) {
        this.tileX = tileX;
        this.tileY = tileY;
    }

    public SettlementLockedBedData(PacketReader reader) {
        this.tileX = reader.getNextInt();
        this.tileY = reader.getNextInt();
    }

    public void writeContentPacket(PacketWriter writer) {
        writer.putNextInt(this.tileX);
        writer.putNextInt(this.tileY);
    }
}

