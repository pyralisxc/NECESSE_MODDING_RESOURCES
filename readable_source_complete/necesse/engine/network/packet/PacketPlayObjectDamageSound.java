/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.registries.ObjectRegistry;

public class PacketPlayObjectDamageSound
extends Packet {
    public final int tileX;
    public final int tileY;
    public final int objectID;

    public PacketPlayObjectDamageSound(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.tileX = reader.getNextInt();
        this.tileY = reader.getNextInt();
        this.objectID = reader.getNextShortUnsigned();
    }

    public PacketPlayObjectDamageSound(int tileX, int tileY, int objectID) {
        this.tileX = tileX;
        this.tileY = tileY;
        this.objectID = objectID;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(tileX);
        writer.putNextInt(tileY);
        writer.putNextShortUnsigned(objectID);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.getLevel() == null) {
            return;
        }
        ObjectRegistry.getObject(this.objectID).playDamageSound(client.getLevel(), this.tileX, this.tileY, true);
    }
}

