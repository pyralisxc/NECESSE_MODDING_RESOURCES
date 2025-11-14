/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class PacketPlayerDropItem
extends Packet {
    public final int inventoryID;
    public final int inventorySlot;
    public final int amount;

    public PacketPlayerDropItem(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.inventoryID = reader.getNextShortUnsigned();
        this.inventorySlot = reader.getNextShortUnsigned();
        this.amount = reader.getNextShortUnsigned();
    }

    public PacketPlayerDropItem(int inventoryID, int inventorySlot, int amount) {
        this.inventoryID = inventoryID;
        this.inventorySlot = inventorySlot;
        this.amount = amount;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextShortUnsigned(inventoryID);
        writer.putNextShortUnsigned(inventorySlot);
        writer.putNextShortUnsigned(amount);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        client.playerMob.getInv().dropItem(this.inventoryID, this.inventorySlot, this.amount);
    }
}

