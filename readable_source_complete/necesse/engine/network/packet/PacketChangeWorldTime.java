/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;

public class PacketChangeWorldTime
extends Packet {
    public final long worldTime;
    public final boolean isSleeping;

    public PacketChangeWorldTime(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.worldTime = reader.getNextLong();
        this.isSleeping = reader.getNextBoolean();
    }

    public PacketChangeWorldTime(Server server) {
        this.worldTime = server.world.worldEntity.getWorldTime();
        this.isSleeping = server.world.worldEntity.isSleeping();
        PacketWriter writer = new PacketWriter(this);
        writer.putNextLong(this.worldTime);
        writer.putNextBoolean(this.isSleeping);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.worldEntity == null) {
            return;
        }
        client.worldEntity.applyChangeWorldTimePacket(this);
    }
}

