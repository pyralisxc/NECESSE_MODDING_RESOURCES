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
import necesse.engine.registries.ObjectRegistry;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.SwitchObject;
import necesse.level.maps.Level;

public class PacketObjectSwitched
extends Packet {
    public final int levelIdentifierHashCode;
    public final int tileX;
    public final int tileY;
    public final int objectID;

    public PacketObjectSwitched(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.levelIdentifierHashCode = reader.getNextInt();
        this.tileX = reader.getNextInt();
        this.tileY = reader.getNextInt();
        this.objectID = reader.getNextShortUnsigned();
    }

    public PacketObjectSwitched(Level level, int tileX, int tileY, int objectID) {
        this.levelIdentifierHashCode = level.getIdentifierHashCode();
        this.tileX = tileX;
        this.tileY = tileY;
        this.objectID = objectID;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.levelIdentifierHashCode);
        writer.putNextInt(tileX);
        writer.putNextInt(tileY);
        writer.putNextShortUnsigned(objectID);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (!client.levelManager.checkIfLoadedRegionAtTile(this.levelIdentifierHashCode, this.tileX, this.tileY, true)) {
            return;
        }
        if (client.getLevel().getObjectID(this.tileX, this.tileY) == this.objectID) {
            GameObject object = ObjectRegistry.getObject(this.objectID);
            if (object instanceof SwitchObject) {
                SwitchObject switchObject = (SwitchObject)object;
                switchObject.onSwitched(client.getLevel(), this.tileX, this.tileY);
            } else {
                client.network.sendPacket(new PacketRequestObjectChange(this.tileX, this.tileY));
            }
        } else {
            client.network.sendPacket(new PacketRequestObjectChange(this.tileX, this.tileY));
        }
    }
}

