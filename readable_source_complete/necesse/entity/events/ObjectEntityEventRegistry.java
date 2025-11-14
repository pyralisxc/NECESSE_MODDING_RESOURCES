/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.events;

import necesse.engine.network.Packet;
import necesse.engine.network.packet.PacketObjectEntityEvent;
import necesse.engine.network.server.Server;
import necesse.entity.events.EntityEvent;
import necesse.entity.events.EntityEventRegistry;
import necesse.entity.objectEntity.ObjectEntity;

public class ObjectEntityEventRegistry
extends EntityEventRegistry<ObjectEntity> {
    public ObjectEntityEventRegistry(ObjectEntity objectEntity) {
        super(objectEntity);
    }

    @Override
    public String getDebugIdentifierString() {
        return null;
    }

    @Override
    public void sendEventPacket(Server server, EntityEvent event, Packet content) {
        server.network.sendToClientsWithTile(new PacketObjectEntityEvent((ObjectEntity)this.entity, event.getID(), content), ((ObjectEntity)this.entity).getLevel(), ((ObjectEntity)this.entity).tileX, ((ObjectEntity)this.entity).tileY);
    }
}

