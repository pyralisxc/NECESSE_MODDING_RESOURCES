/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.entity.DamagedObjectEntity;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;

public class PacketObjectDamage
extends Packet {
    public final int levelIdentifierHashCode;
    public final int objectLayerID;
    public final int tileX;
    public final int tileY;
    public final int expectedObjectID;
    public final int totalDamage;
    public final int addedDamage;
    public final boolean destroyed;
    public final boolean showEffect;
    public final int mouseX;
    public final int mouseY;

    public PacketObjectDamage(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.levelIdentifierHashCode = reader.getNextInt();
        this.objectLayerID = reader.getNextByteUnsigned();
        this.tileX = reader.getNextInt();
        this.tileY = reader.getNextInt();
        this.expectedObjectID = reader.getNextInt();
        this.totalDamage = reader.getNextInt();
        this.addedDamage = reader.getNextInt();
        this.destroyed = reader.getNextBoolean();
        this.showEffect = reader.getNextBoolean();
        this.mouseX = reader.getNextInt();
        this.mouseY = reader.getNextInt();
    }

    public PacketObjectDamage(Level level, int objectLayerID, int tileX, int tileY, int objectID, int totalDamage, int addedDamage, boolean destroyed, boolean showEffect, int mouseX, int mouseY) {
        this.levelIdentifierHashCode = level.getIdentifierHashCode();
        this.objectLayerID = objectLayerID;
        this.tileX = tileX;
        this.tileY = tileY;
        this.expectedObjectID = objectID;
        this.totalDamage = totalDamage;
        this.addedDamage = addedDamage;
        this.destroyed = destroyed;
        this.showEffect = showEffect;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.levelIdentifierHashCode);
        writer.putNextByteUnsigned(objectLayerID);
        writer.putNextInt(tileX);
        writer.putNextInt(tileY);
        writer.putNextInt(this.expectedObjectID);
        writer.putNextInt(totalDamage);
        writer.putNextInt(addedDamage);
        writer.putNextBoolean(destroyed);
        writer.putNextBoolean(showEffect);
        writer.putNextInt(mouseX);
        writer.putNextInt(mouseY);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (!client.levelManager.checkIfLoadedRegionAtTile(this.levelIdentifierHashCode, this.tileX, this.tileY, true)) {
            return;
        }
        Level level = client.getLevel();
        GameObject object = level.getObject(this.objectLayerID, this.tileX, this.tileY);
        if (this.expectedObjectID != -1 && object.getID() != this.expectedObjectID) {
            return;
        }
        DamagedObjectEntity damagedEntity = level.entityManager.getOrCreateDamagedObjectEntity(this.tileX, this.tileY);
        damagedEntity.updateObjectDamage(this.objectLayerID, this.totalDamage, this.destroyed);
        object.onDamaged(level, this.objectLayerID, this.tileX, this.tileY, this.addedDamage, null, null, this.showEffect, this.mouseX, this.mouseY);
    }
}

