/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;

public class PacketOEInventoryUpdate
extends Packet {
    public final int tileX;
    public final int tileY;
    public final int inventorySlot;
    public final Packet itemContent;

    public PacketOEInventoryUpdate(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.tileX = reader.getNextInt();
        this.tileY = reader.getNextInt();
        this.inventorySlot = reader.getNextShortUnsigned();
        this.itemContent = reader.getNextContentPacket();
    }

    public PacketOEInventoryUpdate(OEInventory oeInventory, int inventorySlot) {
        this.tileX = ((ObjectEntity)((Object)oeInventory)).tileX;
        this.tileY = ((ObjectEntity)((Object)oeInventory)).tileY;
        this.inventorySlot = inventorySlot;
        this.itemContent = InventoryItem.getContentPacket(oeInventory.getInventory().getItem(inventorySlot));
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.tileX);
        writer.putNextInt(this.tileY);
        writer.putNextShortUnsigned(inventorySlot);
        writer.putNextContentPacket(this.itemContent);
    }

    public ObjectEntity getObjectEntity(Level level) {
        return level.entityManager.getObjectEntity(this.tileX, this.tileY);
    }

    public OEInventory getOEInventory(Level level) {
        ObjectEntity oe = this.getObjectEntity(level);
        if (oe instanceof OEInventory) {
            return (OEInventory)((Object)oe);
        }
        return null;
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.getLevel() == null) {
            return;
        }
        OEInventory oeInventory = this.getOEInventory(client.getLevel());
        if (oeInventory != null) {
            oeInventory.getInventory().setItem(this.inventorySlot, InventoryItem.fromContentPacket(this.itemContent));
        }
    }
}

