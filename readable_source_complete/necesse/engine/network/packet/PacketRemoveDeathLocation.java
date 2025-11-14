/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.LevelIdentifier;

public class PacketRemoveDeathLocation
extends Packet {
    public final LevelIdentifier levelIdentifier;
    public final int x;
    public final int y;

    public PacketRemoveDeathLocation(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.levelIdentifier = new LevelIdentifier(reader);
        this.x = reader.getNextInt();
        this.y = reader.getNextInt();
    }

    public PacketRemoveDeathLocation(LevelIdentifier levelIdentifier, int x, int y) {
        this.levelIdentifier = levelIdentifier;
        this.x = x;
        this.y = y;
        PacketWriter writer = new PacketWriter(this);
        levelIdentifier.writePacket(writer);
        writer.putNextInt(x);
        writer.putNextInt(y);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        client.removeDeathLocation(this.levelIdentifier, this.x, this.y);
    }
}

