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
import necesse.level.maps.Level;

public class PacketOEInventoryNameUpdate
extends Packet {
    public final int levelIdentifierHashCode;
    public final int tileX;
    public final int tileY;
    public final String name;

    public PacketOEInventoryNameUpdate(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.levelIdentifierHashCode = reader.getNextInt();
        this.tileX = reader.getNextInt();
        this.tileY = reader.getNextInt();
        this.name = reader.getNextString();
    }

    public PacketOEInventoryNameUpdate(OEInventory oeInventory, String name) {
        this.levelIdentifierHashCode = ((ObjectEntity)((Object)oeInventory)).getLevel().getIdentifierHashCode();
        this.tileX = ((ObjectEntity)((Object)oeInventory)).tileX;
        this.tileY = ((ObjectEntity)((Object)oeInventory)).tileY;
        this.name = name;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.levelIdentifierHashCode);
        writer.putNextInt(this.tileX);
        writer.putNextInt(this.tileY);
        writer.putNextString(name);
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
        if (!client.levelManager.checkIfLoadedRegionAtTile(this.levelIdentifierHashCode, this.tileX, this.tileY, true)) {
            return;
        }
        OEInventory oeInventory = this.getOEInventory(client.getLevel());
        if (oeInventory != null) {
            oeInventory.setInventoryName(this.name);
        }
    }
}

