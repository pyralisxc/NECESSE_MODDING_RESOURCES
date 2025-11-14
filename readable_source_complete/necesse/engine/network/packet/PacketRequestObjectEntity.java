/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.GameLog;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketObjectEntity;
import necesse.engine.network.packet.PacketObjectEntityError;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.level.maps.Level;

public class PacketRequestObjectEntity
extends Packet {
    public final int levelIdentifierHashCode;
    public final int tileX;
    public final int tileY;

    public PacketRequestObjectEntity(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.levelIdentifierHashCode = reader.getNextInt();
        this.tileX = reader.getNextInt();
        this.tileY = reader.getNextInt();
    }

    public PacketRequestObjectEntity(Level level, int tileX, int tileY) {
        this.levelIdentifierHashCode = level.getIdentifierHashCode();
        this.tileX = tileX;
        this.tileY = tileY;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.levelIdentifierHashCode);
        writer.putNextInt(tileX);
        writer.putNextInt(tileY);
    }

    public Packet getRequestedPacket(Level level) {
        ObjectEntity ent = level.entityManager.getObjectEntity(this.tileX, this.tileY);
        if (ent != null) {
            return new PacketObjectEntity(ent);
        }
        return new PacketObjectEntityError(this.tileX, this.tileY);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        Level level = server.world.getLevel(client);
        if (level.getIdentifierHashCode() == this.levelIdentifierHashCode) {
            server.network.sendPacket(this.getRequestedPacket(level), client);
        } else {
            GameLog.warn.println(client.getName() + " requested object entity on wrong level");
        }
    }
}

