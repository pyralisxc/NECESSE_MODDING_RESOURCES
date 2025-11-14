/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.packet.PacketRequestPlayerData;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.PlayerEquipmentInventory;
import necesse.inventory.PlayerInventory;

public class PacketPlayerInventorySetProxy
extends Packet {
    public final int playerSlot;
    public final int inventoryID;
    public final int inventorySlot;
    public final int proxySetIndex;

    public PacketPlayerInventorySetProxy(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.playerSlot = reader.getNextByteUnsigned();
        this.inventoryID = reader.getNextShortUnsigned();
        this.inventorySlot = reader.getNextShortUnsigned();
        this.proxySetIndex = reader.getNextShortUnsigned();
    }

    public PacketPlayerInventorySetProxy(ServerClient client, int inventoryID, int inventorySlot, int proxySetIndex) {
        this(client.slot, inventoryID, inventorySlot, proxySetIndex);
    }

    public PacketPlayerInventorySetProxy(Client client, int inventoryID, int inventorySlot, int proxySetIndex) {
        this(client.getSlot(), inventoryID, inventorySlot, proxySetIndex);
    }

    private PacketPlayerInventorySetProxy(int playerSlot, int inventoryID, int inventorySlot, int proxySetIndex) {
        this.playerSlot = playerSlot;
        this.inventoryID = inventoryID;
        this.inventorySlot = inventorySlot;
        this.proxySetIndex = proxySetIndex;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextByteUnsigned(playerSlot);
        writer.putNextShortUnsigned(inventoryID);
        writer.putNextShortUnsigned(inventorySlot);
        writer.putNextShortUnsigned(proxySetIndex);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        if (this.playerSlot != client.slot || !client.checkHasRequestedSelf()) {
            return;
        }
        PlayerInventory inventory = client.playerMob.getInv().getInventoryByID(this.inventoryID);
        if (inventory instanceof PlayerEquipmentInventory) {
            ((PlayerEquipmentInventory)inventory).setProxy(this.inventorySlot, this.proxySetIndex);
        }
        server.network.sendToAllClientsExcept(this, client);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        ClientClient target = client.getClient(this.playerSlot);
        if (target == null) {
            client.network.sendPacket(new PacketRequestPlayerData(this.playerSlot));
        } else {
            PlayerInventory inventory = target.playerMob.getInv().getInventoryByID(this.inventoryID);
            if (inventory instanceof PlayerEquipmentInventory) {
                ((PlayerEquipmentInventory)inventory).setProxy(this.inventorySlot, this.proxySetIndex);
            }
        }
    }
}

