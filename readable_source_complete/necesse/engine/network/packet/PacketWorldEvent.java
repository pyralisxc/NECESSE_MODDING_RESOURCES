/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.registries.WorldEventRegistry;
import necesse.engine.world.WorldEntity;
import necesse.engine.world.worldEvent.WorldEvent;

public class PacketWorldEvent
extends Packet {
    public final int eventID;
    public final Packet spawnContent;

    public PacketWorldEvent(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.eventID = reader.getNextShortUnsigned();
        this.spawnContent = reader.getNextContentPacket();
    }

    public PacketWorldEvent(WorldEvent event) {
        this.eventID = event.getID();
        this.spawnContent = new Packet();
        event.setupSpawnPacket(new PacketWriter(this.spawnContent));
        PacketWriter writer = new PacketWriter(this);
        writer.putNextShortUnsigned(this.eventID);
        writer.putNextContentPacket(this.spawnContent);
    }

    public WorldEvent getEvent(WorldEntity worldEntity) {
        WorldEvent event = WorldEventRegistry.getEvent(this.eventID);
        if (event != null) {
            event.world = worldEntity;
            event.applySpawnPacket(new PacketReader(this.spawnContent));
        }
        return event;
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.worldEntity == null) {
            return;
        }
        client.worldEntity.addWorldEvent(this.getEvent(client.worldEntity));
    }
}

