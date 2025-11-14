/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.world.WorldEntity;

public class PacketWorldData
extends Packet {
    public final long time;
    public final long worldTime;
    public final boolean isSleeping;

    public PacketWorldData(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.time = reader.getNextLong();
        this.worldTime = reader.getNextLong();
        this.isSleeping = reader.getNextBoolean();
    }

    public PacketWorldData(WorldEntity worldEntity) {
        this.time = worldEntity.getTime();
        this.worldTime = worldEntity.getWorldTime();
        this.isSleeping = worldEntity.isSleeping();
        PacketWriter writer = new PacketWriter(this);
        writer.putNextLong(this.time);
        writer.putNextLong(this.worldTime);
        writer.putNextBoolean(this.isSleeping);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.worldEntity == null) {
            client.worldEntity = WorldEntity.getClientWorldEntity(client);
        }
        client.worldEntity.applyWorldPacket(this);
        client.loading.worldPhase.submitWorldDataPacket(this);
    }
}

