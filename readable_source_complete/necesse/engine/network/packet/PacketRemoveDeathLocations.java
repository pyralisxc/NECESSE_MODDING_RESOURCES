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

public class PacketRemoveDeathLocations
extends Packet {
    public final int islandX;
    public final int islandY;

    public PacketRemoveDeathLocations(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.islandX = reader.getNextInt();
        this.islandY = reader.getNextInt();
    }

    public PacketRemoveDeathLocations(int islandX, int islandY) {
        this.islandX = islandX;
        this.islandY = islandY;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(islandX);
        writer.putNextInt(islandY);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        client.removeDeathLocations(this.islandX, this.islandY);
    }
}

