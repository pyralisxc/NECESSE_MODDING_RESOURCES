/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import java.util.Objects;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class PacketUpdateSession
extends Packet {
    public final long sessionID;

    public PacketUpdateSession(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.sessionID = reader.getNextLong();
    }

    public PacketUpdateSession(long sessionID) {
        this.sessionID = sessionID;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextLong(sessionID);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        ServerClient foundClient = server.streamClients().filter(Objects::nonNull).filter(c -> c.getSessionID() == this.sessionID).findFirst().orElse(null);
        if (foundClient != null && !Objects.equals(foundClient.networkInfo, packet.networkInfo)) {
            System.out.println(foundClient.getName() + " submitted valid session ID. Updating connection to " + (packet.networkInfo == null ? "LOCAL" : packet.networkInfo.getDisplayName()));
            foundClient.networkInfo = packet.networkInfo;
        }
    }
}

