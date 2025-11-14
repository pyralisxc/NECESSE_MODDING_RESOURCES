/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.GameLog;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.level.maps.Level;

public class PacketObjectEntity
extends Packet {
    public final int levelIdentifierHashCode;
    public final int tileX;
    public final int tileY;
    public final int objectID;
    public final Packet content;

    public PacketObjectEntity(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.levelIdentifierHashCode = reader.getNextInt();
        this.tileX = reader.getNextInt();
        this.tileY = reader.getNextInt();
        this.objectID = reader.getNextShortUnsigned();
        this.content = reader.getNextContentPacket();
    }

    public PacketObjectEntity(ObjectEntity objectEntity) {
        this.levelIdentifierHashCode = objectEntity.getLevel().getIdentifierHashCode();
        this.tileX = objectEntity.tileX;
        this.tileY = objectEntity.tileY;
        this.objectID = objectEntity.getObject().getID();
        this.content = new Packet();
        objectEntity.setupContentPacket(new PacketWriter(this.content));
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.levelIdentifierHashCode);
        writer.putNextInt(this.tileX);
        writer.putNextInt(this.tileY);
        writer.putNextShortUnsigned(this.objectID);
        writer.putNextContentPacket(this.content);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        if (client.getPermissionLevel().getLevel() >= PermissionLevel.ADMIN.getLevel()) {
            if (server.world.settings.cheatsAllowedOrHidden()) {
                Level level = server.world.getLevel(client);
                if (level.getIdentifierHashCode() == this.levelIdentifierHashCode) {
                    if (level.getObjectID(this.tileX, this.tileY) == this.objectID) {
                        ObjectEntity ent = level.entityManager.getObjectEntity(this.tileX, this.tileY);
                        if (ent != null) {
                            ent.applyContentPacket(new PacketReader(this.content));
                            server.network.sendToClientsWithEntityExcept(this, ent, client);
                        } else {
                            GameLog.warn.println(client.getName() + " wrongfully attempted to update unknown object entity at (" + this.tileX + ", " + this.tileY + ").");
                        }
                    } else {
                        GameLog.warn.println(client.getName() + " wrongfully attempted to update wrong object entity id at (" + this.tileX + ", " + this.tileY + ").");
                    }
                } else {
                    GameLog.warn.println(client.getName() + " tried to update object entity on wrong level");
                }
            } else {
                GameLog.warn.println(client.getName() + " tried to update object entity, but cheats aren't allowed");
            }
        } else {
            GameLog.warn.println(client.getName() + " tried to update object entity, but isn't admin");
        }
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (!client.levelManager.checkIfLoadedRegionAtTile(this.levelIdentifierHashCode, this.tileX, this.tileY, true)) {
            return;
        }
        client.loading.objectEntities.submitObjectEntityPacket(this);
    }
}

