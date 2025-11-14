/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.commands.PermissionLevel;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.packet.PacketRequestPlayerData;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;

public class PacketPlayerInventorySlot
extends Packet {
    public final int slot;
    public final PlayerInventorySlot inventorySlot;
    public final Packet itemContent;

    public PacketPlayerInventorySlot(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.slot = reader.getNextByteUnsigned();
        int inventoryID = reader.getNextShortUnsigned();
        int inventorySlot = reader.getNextShortUnsigned();
        this.inventorySlot = new PlayerInventorySlot(inventoryID, inventorySlot);
        this.itemContent = reader.getNextContentPacket();
    }

    public PacketPlayerInventorySlot(ServerClient client, PlayerInventorySlot invSlot) {
        this(client.slot, client.playerMob, invSlot);
    }

    public PacketPlayerInventorySlot(int slot, PlayerMob player, PlayerInventorySlot invSlot) {
        this.slot = slot;
        this.inventorySlot = invSlot;
        this.itemContent = InventoryItem.getContentPacket(player.getInv().getItem(invSlot));
        PacketWriter writer = new PacketWriter(this);
        writer.putNextByteUnsigned(slot);
        writer.putNextShortUnsigned(this.inventorySlot.inventoryID);
        writer.putNextShortUnsigned(this.inventorySlot.slot);
        writer.putNextContentPacket(this.itemContent);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        if (client.getPermissionLevel().getLevel() < PermissionLevel.ADMIN.getLevel()) {
            return;
        }
        if (!server.world.settings.cheatsAllowedOrHidden()) {
            return;
        }
        if (this.slot != client.slot || !client.checkHasRequestedSelf()) {
            return;
        }
        client.playerMob.getInv().setItem(this.inventorySlot, InventoryItem.fromContentPacket(this.itemContent));
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        ClientClient target = client.getClient(this.slot);
        if (target == null) {
            client.network.sendPacket(new PacketRequestPlayerData(this.slot));
        } else {
            target.playerMob.getInv().setItem(this.inventorySlot, InventoryItem.fromContentPacket(this.itemContent), false);
        }
    }
}

