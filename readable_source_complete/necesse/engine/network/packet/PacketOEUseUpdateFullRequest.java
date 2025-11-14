/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketChangeObject;
import necesse.engine.network.packet.PacketOEUseUpdateFull;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEUsers;
import necesse.level.maps.Level;

public class PacketOEUseUpdateFullRequest
extends Packet {
    public final int tileX;
    public final int tileY;

    public PacketOEUseUpdateFullRequest(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.tileX = reader.getNextInt();
        this.tileY = reader.getNextInt();
    }

    public PacketOEUseUpdateFullRequest(OEUsers users) {
        ObjectEntity oe = (ObjectEntity)((Object)users);
        this.tileX = oe.tileX;
        this.tileY = oe.tileY;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.tileX);
        writer.putNextInt(this.tileY);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        Level level = client.getLevel();
        ObjectEntity oe = level.entityManager.getObjectEntity(this.tileX, this.tileY);
        if (oe instanceof OEUsers) {
            client.sendPacket(new PacketOEUseUpdateFull((OEUsers)((Object)oe)));
        } else {
            client.sendPacket(new PacketChangeObject(level, 0, this.tileX, this.tileY, level.getObjectID(this.tileX, this.tileY), level.getObjectRotation(this.tileX, this.tileY)));
        }
    }
}

