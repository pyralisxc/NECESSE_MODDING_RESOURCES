/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketChangeTile;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.level.maps.Level;

public class PacketRequestTileChange
extends Packet {
    public final int tileX;
    public final int tileY;

    public PacketRequestTileChange(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.tileX = reader.getNextInt();
        this.tileY = reader.getNextInt();
    }

    public PacketRequestTileChange(int tileX, int tileY) {
        this.tileX = tileX;
        this.tileY = tileY;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(tileX);
        writer.putNextInt(tileY);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        Level level = client.getLevel();
        if (level == null) {
            return;
        }
        client.sendPacket(new PacketChangeTile(level, this.tileX, this.tileY, level.getTileID(this.tileX, this.tileY)));
    }
}

