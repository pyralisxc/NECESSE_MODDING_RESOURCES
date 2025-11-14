/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketRequestObjectChange;
import necesse.entity.objectEntity.ObjectEntity;

public class PacketObjectEntityEvent
extends Packet {
    public final int levelIdentifierHashCode;
    public final int tileX;
    public final int tileY;
    public final int objectID;
    public final int eventID;
    public final Packet eventContent;

    public PacketObjectEntityEvent(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.levelIdentifierHashCode = reader.getNextInt();
        this.tileX = reader.getNextInt();
        this.tileY = reader.getNextInt();
        this.objectID = reader.getNextShortUnsigned();
        this.eventID = reader.getNextShort();
        this.eventContent = reader.getNextContentPacket();
    }

    public PacketObjectEntityEvent(ObjectEntity objectEntity, int eventID, Packet content) {
        this.levelIdentifierHashCode = objectEntity.getLevel().getIdentifierHashCode();
        this.tileX = objectEntity.tileX;
        this.tileY = objectEntity.tileY;
        this.objectID = objectEntity.getObject().getID();
        this.eventID = eventID;
        this.eventContent = content;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.levelIdentifierHashCode);
        writer.putNextInt(this.tileX);
        writer.putNextInt(this.tileY);
        writer.putNextShortUnsigned(this.objectID);
        writer.putNextShort((short)eventID);
        writer.putNextContentPacket(this.eventContent);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (!client.levelManager.isLevelLoaded(this.levelIdentifierHashCode)) {
            return;
        }
        ObjectEntity objectEntity = client.getLevel().entityManager.getObjectEntity(this.tileX, this.tileY);
        if (objectEntity != null && objectEntity.getObject().getID() == this.objectID) {
            objectEntity.runEvent(this.eventID, new PacketReader(this.eventContent));
        } else {
            client.network.sendPacket(new PacketRequestObjectChange(this.tileX, this.tileY));
        }
    }
}

