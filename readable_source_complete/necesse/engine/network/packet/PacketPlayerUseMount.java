/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.GameLog;
import necesse.engine.Settings;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketPlayerMovement;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventorySlot;
import necesse.inventory.item.Item;
import necesse.inventory.item.mountItem.MountItem;

public class PacketPlayerUseMount
extends Packet {
    public final int slot;
    public final int itemID;
    public final float playerX;
    public final float playerY;

    public PacketPlayerUseMount(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.slot = reader.getNextByteUnsigned();
        this.itemID = reader.getNextShortUnsigned();
        this.playerX = reader.getNextFloat();
        this.playerY = reader.getNextFloat();
    }

    public PacketPlayerUseMount(int slot, PlayerMob player, Item item) {
        this.slot = slot;
        this.itemID = item.getID();
        this.playerX = player.x;
        this.playerY = player.y;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextByteUnsigned(slot);
        writer.putNextShortUnsigned(this.itemID);
        writer.putNextFloat(this.playerX);
        writer.putNextFloat(this.playerY);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        if (this.slot != client.slot) {
            return;
        }
        if (!client.checkHasRequestedSelf() || client.isDead()) {
            return;
        }
        client.checkSpawned();
        InventorySlot slot = client.playerMob.getInv().equipment.getSelectedEquipmentSlot(0);
        InventoryItem item = slot.getItem();
        if (item != null && item.item.getID() == this.itemID && item.item.isMountItem()) {
            MountItem mountItem = (MountItem)item.item;
            if (mountItem.canUseMount(item, client.playerMob, client.getLevel()) == null || !Settings.strictServerAuthority) {
                double allowed = client.playerMob.allowServerMovement(server, client, this.playerX, this.playerY);
                if (allowed <= 0.0) {
                    client.playerMob.setPos(this.playerX, this.playerY, false);
                } else {
                    GameLog.warn.println(client.getName() + " attempted to use mount from wrong position, snapping back " + allowed);
                    server.network.sendToClientsWithEntity(new PacketPlayerMovement(client, false), client.playerMob);
                }
                slot.setItem(mountItem.useMount(client, this.playerX, this.playerY, item, client.getLevel()));
            }
        } else {
            slot.markDirty();
        }
    }
}

