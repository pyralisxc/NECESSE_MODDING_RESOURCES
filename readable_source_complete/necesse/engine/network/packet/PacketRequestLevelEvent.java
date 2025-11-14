/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketLevelEvent;
import necesse.engine.network.packet.PacketLevelEventOver;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.levelEvent.LevelEvent;
import necesse.level.maps.Level;

public class PacketRequestLevelEvent
extends Packet {
    public final int eventUniqueID;

    public PacketRequestLevelEvent(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.eventUniqueID = reader.getNextInt();
    }

    public PacketRequestLevelEvent(int uniqueID) {
        this.eventUniqueID = uniqueID;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.eventUniqueID);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        Level level = server.world.getLevel(client);
        LevelEvent event = level.entityManager.events.get(this.eventUniqueID, false);
        if (event != null) {
            client.sendPacket(new PacketLevelEvent(event));
        } else {
            client.sendPacket(new PacketLevelEventOver(this.eventUniqueID));
        }
    }
}

