/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketChangeObject;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.level.maps.Level;

public class PacketRequestObjectChange
extends Packet {
    public final int layerID;
    public final int tileX;
    public final int tileY;

    public PacketRequestObjectChange(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.layerID = reader.getNextByteUnsigned();
        this.tileX = reader.getNextInt();
        this.tileY = reader.getNextInt();
    }

    public PacketRequestObjectChange(int tileX, int tileY) {
        this(0, tileX, tileY);
    }

    public PacketRequestObjectChange(int layerID, int tileX, int tileY) {
        this.layerID = layerID;
        this.tileX = tileX;
        this.tileY = tileY;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextByteUnsigned(layerID);
        writer.putNextInt(tileX);
        writer.putNextInt(tileY);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        Level level = client.getLevel();
        if (level == null) {
            return;
        }
        client.sendPacket(new PacketChangeObject(level, this.layerID, this.tileX, this.tileY, level.getObjectID(this.tileX, this.tileY), level.getObjectRotation(this.tileX, this.tileY)));
    }
}

