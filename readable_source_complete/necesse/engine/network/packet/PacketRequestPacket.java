/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import java.awt.Point;
import java.util.Collection;
import java.util.Collections;
import java.util.function.BiFunction;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketLevelData;
import necesse.engine.network.packet.PacketPlayerLevelChange;
import necesse.engine.network.packet.PacketPlayerStats;
import necesse.engine.network.packet.PacketWorldData;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class PacketRequestPacket
extends Packet {
    public final RequestType request;

    public PacketRequestPacket(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        RequestType[] values = RequestType.values();
        int ID = reader.getNextByteUnsigned();
        this.request = ID < 0 || ID >= values.length ? RequestType.WORLD_DATA : values[ID];
    }

    public PacketRequestPacket(RequestType request) {
        this.request = request;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextByteUnsigned(request.ordinal());
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        Packet reply = this.request.packetProducer.apply(server, client);
        if (reply != null) {
            server.network.sendPacket(reply, client);
        }
    }

    public static enum RequestType {
        WORLD_DATA((s, c) -> new PacketWorldData(s.world.worldEntity)),
        PLAYER_STATS((s, c) -> new PacketPlayerStats(c.characterStats(), false)),
        LEVEL_DATA((s, c) -> new PacketLevelData(s.world.getLevel((ServerClient)c), (ServerClient)c, (Collection<Point>)Collections.emptyList())),
        LEVEL_CHANGE((s, c) -> new PacketPlayerLevelChange(c.slot, c.getLevelIdentifier(), true)),
        LOADED_REGIONS((s, c) -> c.getLoadedRegionsPacket(c.getLevelIdentifier()));

        public final BiFunction<Server, ServerClient, Packet> packetProducer;

        private RequestType(BiFunction<Server, ServerClient, Packet> packetProducer) {
            this.packetProducer = packetProducer;
        }
    }
}

