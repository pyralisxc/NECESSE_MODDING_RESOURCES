/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.entity.objectEntity.IProgressObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.level.maps.Level;

public class PacketOEProgressUpdate
extends Packet {
    public final int tileX;
    public final int tileY;
    public final Packet content;

    public PacketOEProgressUpdate(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.tileX = reader.getNextInt();
        this.tileY = reader.getNextInt();
        this.content = reader.getNextContentPacket();
    }

    public PacketOEProgressUpdate(IProgressObjectEntity fueledObjectEntity) {
        ObjectEntity objectEntity = (ObjectEntity)((Object)fueledObjectEntity);
        this.tileX = objectEntity.tileX;
        this.tileY = objectEntity.tileY;
        this.content = new Packet();
        fueledObjectEntity.setupProgressPacket(new PacketWriter(this.content));
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.tileX);
        writer.putNextInt(this.tileY);
        writer.putNextContentPacket(this.content);
    }

    public ObjectEntity getObjectEntity(Level level) {
        return level.entityManager.getObjectEntity(this.tileX, this.tileY);
    }

    public IProgressObjectEntity getProcessingObjectEntity(Level level) {
        ObjectEntity oe = this.getObjectEntity(level);
        if (oe instanceof IProgressObjectEntity) {
            return (IProgressObjectEntity)((Object)oe);
        }
        return null;
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.getLevel() == null) {
            return;
        }
        IProgressObjectEntity fueledObjectEntity = this.getProcessingObjectEntity(client.getLevel());
        if (fueledObjectEntity != null) {
            fueledObjectEntity.applyProgressPacket(new PacketReader(this.content));
        }
    }
}

