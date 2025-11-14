/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobInventory;
import necesse.inventory.InventoryItem;

public class PacketMobInventoryUpdate
extends Packet {
    public final int mobUniqueID;
    public final int inventorySlot;
    public final Packet itemContent;

    public PacketMobInventoryUpdate(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.mobUniqueID = reader.getNextInt();
        this.inventorySlot = reader.getNextShortUnsigned();
        this.itemContent = reader.getNextContentPacket();
    }

    public PacketMobInventoryUpdate(MobInventory inventory, int inventorySlot) {
        this.mobUniqueID = ((Mob)((Object)inventory)).getUniqueID();
        this.inventorySlot = inventorySlot;
        this.itemContent = InventoryItem.getContentPacket(inventory.getInventory().getItem(inventorySlot));
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.mobUniqueID);
        writer.putNextShortUnsigned(inventorySlot);
        writer.putNextContentPacket(this.itemContent);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.getLevel() == null) {
            return;
        }
        Mob mob = GameUtils.getLevelMob(this.mobUniqueID, client.getLevel());
        if (mob instanceof MobInventory) {
            ((MobInventory)((Object)mob)).getInventory().setItem(this.inventorySlot, InventoryItem.fromContentPacket(this.itemContent));
        }
    }
}

