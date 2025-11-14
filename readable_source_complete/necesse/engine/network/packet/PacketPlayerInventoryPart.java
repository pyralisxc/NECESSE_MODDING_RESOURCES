/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketRequestPlayerData;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.PlayerInventory;

public class PacketPlayerInventoryPart
extends Packet {
    public final int slot;
    public final int inventoryID;
    public final Packet inventoryContent;

    public PacketPlayerInventoryPart(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.slot = reader.getNextByteUnsigned();
        this.inventoryID = reader.getNextShortUnsigned();
        this.inventoryContent = reader.getNextContentPacket();
    }

    public PacketPlayerInventoryPart(ServerClient client, PlayerInventory inventory) {
        if (inventory.player != client.playerMob) {
            throw new NullPointerException("Invalid inventory and client match");
        }
        this.slot = client.slot;
        this.inventoryID = inventory.getInventoryID();
        this.inventoryContent = inventory.getContentPacket();
        PacketWriter writer = new PacketWriter(this);
        writer.putNextByteUnsigned(this.slot);
        writer.putNextShortUnsigned(this.inventoryID);
        writer.putNextContentPacket(this.inventoryContent);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.getClient(this.slot) == null) {
            client.network.sendPacket(new PacketRequestPlayerData(this.slot));
        } else {
            client.getClient(this.slot).applyInventoryPartPacket(this);
        }
    }
}

