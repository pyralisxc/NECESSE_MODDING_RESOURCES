/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import necesse.engine.network.Packet;
import necesse.engine.network.packet.PacketObjectEntityNetworkFields;
import necesse.entity.mobs.networkField.NetworkFieldRegistry;
import necesse.entity.objectEntity.ObjectEntity;

public class ObjectEntityNetworkFieldRegistry
extends NetworkFieldRegistry {
    private final ObjectEntity objectEntity;

    public ObjectEntityNetworkFieldRegistry(ObjectEntity objectEntity) {
        this.objectEntity = objectEntity;
    }

    @Override
    public void sendUpdatePacket(Packet content) {
        this.objectEntity.getServer().network.sendToClientsWithTile(new PacketObjectEntityNetworkFields(this.objectEntity, content), this.objectEntity.getLevel(), this.objectEntity.tileX, this.objectEntity.tileY);
    }

    @Override
    public String getDebugIdentifierString() {
        return this.objectEntity.toString();
    }
}

