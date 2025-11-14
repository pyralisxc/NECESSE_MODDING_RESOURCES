/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketRequestObjectChange;
import necesse.engine.network.packet.PacketRequestTileChange;
import necesse.entity.DamagedObjectEntity;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.LevelTile;

public class PacketTileDestroyed
extends Packet {
    public final int levelIdentifierHashCode;
    public final int tileX;
    public final int tileY;
    public final int id;
    public final boolean isTile;
    public final int objectLayerID;

    public PacketTileDestroyed(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.levelIdentifierHashCode = reader.getNextInt();
        this.tileX = reader.getNextInt();
        this.tileY = reader.getNextInt();
        this.id = reader.getNextShortUnsigned();
        this.isTile = reader.getNextBoolean();
        this.objectLayerID = !this.isTile ? reader.getNextByteUnsigned() : -1;
    }

    public PacketTileDestroyed(Level level, int tileX, int tileY, int id, boolean isTile, int objectLayerID) {
        this.levelIdentifierHashCode = level.getIdentifierHashCode();
        this.tileX = tileX;
        this.tileY = tileY;
        this.id = id;
        this.isTile = isTile;
        this.objectLayerID = objectLayerID;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.levelIdentifierHashCode);
        writer.putNextInt(tileX);
        writer.putNextInt(tileY);
        writer.putNextShortUnsigned(id);
        writer.putNextBoolean(isTile);
        if (!isTile) {
            writer.putNextByteUnsigned(objectLayerID);
        }
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (!client.levelManager.checkIfLoadedRegionAtTile(this.levelIdentifierHashCode, this.tileX, this.tileY, true)) {
            return;
        }
        Level level = client.getLevel();
        DamagedObjectEntity damagedObjectEntity = level.entityManager.getDamagedObjectEntity(this.tileX, this.tileY);
        if (this.isTile) {
            LevelTile lt = level.getLevelTile(this.tileX, this.tileY);
            if (lt.tile.getID() == this.id) {
                lt.onTileDestroyed(null, null, null);
                lt.level.tileLayer.setIsPlayerPlaced(this.tileX, this.tileY, false);
                if (damagedObjectEntity != null) {
                    damagedObjectEntity.updateTileDamage(0, true);
                }
            } else {
                client.network.sendPacket(new PacketRequestTileChange(this.tileX, this.tileY));
            }
        } else {
            GameObject object = level.getObject(this.objectLayerID, this.tileX, this.tileY);
            if (object.getID() == this.id) {
                object.onDestroyed(level, this.objectLayerID, this.tileX, this.tileY, null, null, null);
                level.objectLayer.setIsPlayerPlaced(this.objectLayerID, this.tileX, this.tileY, false);
                if (damagedObjectEntity != null) {
                    damagedObjectEntity.updateObjectDamage(this.objectLayerID, 0, true);
                }
            } else {
                client.network.sendPacket(new PacketRequestObjectChange(this.tileX, this.tileY));
            }
        }
    }
}

