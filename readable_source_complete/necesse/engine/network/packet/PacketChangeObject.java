/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.commands.PermissionLevel;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.level.maps.Level;

public class PacketChangeObject
extends Packet {
    public final int levelIdentifierHashCode;
    public final int layerID;
    public final int tileX;
    public final int tileY;
    public final int objectID;
    public final int rotation;
    public final boolean isPlayerPlaced;

    public PacketChangeObject(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.levelIdentifierHashCode = reader.getNextInt();
        this.layerID = reader.getNextByteUnsigned();
        this.tileX = reader.getNextInt();
        this.tileY = reader.getNextInt();
        this.objectID = reader.getNextShortUnsigned();
        this.rotation = reader.getNextByteUnsigned();
        this.isPlayerPlaced = reader.getNextBoolean();
    }

    public PacketChangeObject(Level level, int layerID, int tileX, int tileY) {
        this(level, layerID, tileX, tileY, level.getObjectID(layerID, tileX, tileY));
    }

    public PacketChangeObject(Level level, int layerID, int tileX, int tileY, int objectID) {
        this(level, layerID, tileX, tileY, objectID, level.getObjectRotation(layerID, tileX, tileY));
    }

    public PacketChangeObject(Level level, int layerID, int tileX, int tileY, int objectID, int rotation) {
        this(level, layerID, tileX, tileY, objectID, rotation, level.objectLayer.isPlayerPlaced(layerID, tileX, tileY));
    }

    public PacketChangeObject(Level level, int layerID, int tileX, int tileY, int objectID, int rotation, boolean isPlayerPlaced) {
        this.levelIdentifierHashCode = level.getIdentifierHashCode();
        this.layerID = layerID;
        this.tileX = tileX;
        this.tileY = tileY;
        this.objectID = objectID;
        this.rotation = rotation;
        this.isPlayerPlaced = isPlayerPlaced;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.levelIdentifierHashCode);
        writer.putNextByteUnsigned(layerID);
        writer.putNextInt(tileX);
        writer.putNextInt(tileY);
        writer.putNextShortUnsigned(objectID);
        writer.putNextByteUnsigned(rotation);
        writer.putNextBoolean(isPlayerPlaced);
    }

    public void updateLevel(Level level) {
        level.objectLayer.setObject(this.layerID, this.tileX, this.tileY, this.objectID);
        level.objectLayer.setObjectRotation(this.layerID, this.tileX, this.tileY, this.rotation);
        level.objectLayer.setIsPlayerPlaced(this.layerID, this.tileX, this.tileY, this.isPlayerPlaced);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        if (client.getPermissionLevel().getLevel() >= PermissionLevel.ADMIN.getLevel()) {
            if (server.world.settings.cheatsAllowedOrHidden()) {
                Level level = server.world.getLevel(client);
                if (level.getIdentifierHashCode() == this.levelIdentifierHashCode) {
                    this.updateLevel(level);
                    server.network.sendToClientsWithTile(this, level, this.tileX, this.tileY);
                } else {
                    System.out.println(client.getName() + " tried to change object on wrong level");
                }
            } else {
                System.out.println(client.getName() + " tried to change object, but cheats aren't allowed");
            }
        } else {
            System.out.println(client.getName() + " tried to change object, but isn't admin");
        }
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (!client.levelManager.checkIfLoadedRegionAtTile(this.levelIdentifierHashCode, this.tileX, this.tileY, true)) {
            return;
        }
        this.updateLevel(client.getLevel());
    }
}

