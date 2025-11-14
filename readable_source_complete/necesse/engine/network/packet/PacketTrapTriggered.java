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
import necesse.entity.objectEntity.TrapObjectEntity;
import necesse.level.maps.Level;

public class PacketTrapTriggered
extends Packet {
    public final int tileX;
    public final int tileY;

    public PacketTrapTriggered(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.tileX = reader.getNextInt();
        this.tileY = reader.getNextInt();
    }

    public PacketTrapTriggered(int tileX, int tileY) {
        this.tileX = tileX;
        this.tileY = tileY;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(tileX);
        writer.putNextInt(tileY);
    }

    public ObjectEntity getObjectEntity(Level level) {
        return level.entityManager.getObjectEntity(this.tileX, this.tileY);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.getLevel() == null) {
            return;
        }
        ObjectEntity objectEntity = client.getLevel().entityManager.getObjectEntity(this.tileX, this.tileY);
        if (objectEntity instanceof TrapObjectEntity) {
            ((TrapObjectEntity)objectEntity).onClientTrigger();
        }
    }
}

