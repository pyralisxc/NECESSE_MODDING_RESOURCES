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
import necesse.engine.registries.ObjectLayerRegistry;
import necesse.level.maps.Level;

public class PacketChangeObjects
extends Packet {
    public final int levelIdentifierHashCode;
    public final int tileX;
    public final int tileY;
    public final int[] objectIDs;
    public final byte[] rotations;
    public final boolean[] isPlayerPlaced;

    public PacketChangeObjects(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.levelIdentifierHashCode = reader.getNextInt();
        this.tileX = reader.getNextInt();
        this.tileY = reader.getNextInt();
        int layers = ObjectLayerRegistry.getTotalLayers();
        this.objectIDs = reader.getNextShortsUnsigned(layers);
        this.rotations = reader.getNextBytes(layers);
        this.isPlayerPlaced = reader.getNextBooleans(layers);
    }

    public PacketChangeObjects(Level level, int tileX, int tileY) {
        this.levelIdentifierHashCode = level.getIdentifierHashCode();
        this.tileX = tileX;
        this.tileY = tileY;
        int layers = ObjectLayerRegistry.getTotalLayers();
        this.objectIDs = new int[layers];
        this.rotations = new byte[layers];
        this.isPlayerPlaced = new boolean[layers];
        for (int layerID = 0; layerID < layers; ++layerID) {
            this.objectIDs[layerID] = level.objectLayer.getObjectID(layerID, tileX, tileY);
            this.rotations[layerID] = level.objectLayer.getObjectRotation(layerID, tileX, tileY);
            this.isPlayerPlaced[layerID] = level.objectLayer.isPlayerPlaced(layerID, tileX, tileY);
        }
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.levelIdentifierHashCode);
        writer.putNextInt(tileX);
        writer.putNextInt(tileY);
        writer.putNextShortsUnsigned(this.objectIDs);
        writer.putNextBytes(this.rotations);
        writer.putNextBooleans(this.isPlayerPlaced);
    }

    public void updateLevel(Level level) {
        int layers = ObjectLayerRegistry.getTotalLayers();
        for (int layerID = 0; layerID < layers; ++layerID) {
            level.objectLayer.setObject(layerID, this.tileX, this.tileY, this.objectIDs[layerID]);
            level.objectLayer.setObjectRotation(layerID, this.tileX, this.tileY, this.rotations[layerID]);
            level.objectLayer.setIsPlayerPlaced(layerID, this.tileX, this.tileY, this.isPlayerPlaced[layerID]);
        }
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

