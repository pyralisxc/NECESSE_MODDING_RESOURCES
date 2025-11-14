/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEUsers;

public class PacketOEUseUpdateFull
extends Packet {
    public final int tileX;
    public final int tileY;
    public final Packet content;

    public PacketOEUseUpdateFull(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.tileX = reader.getNextInt();
        this.tileY = reader.getNextInt();
        this.content = reader.getNextContentPacket();
    }

    public PacketOEUseUpdateFull(OEUsers users) {
        ObjectEntity oe = (ObjectEntity)((Object)users);
        this.tileX = oe.tileX;
        this.tileY = oe.tileY;
        this.content = new Packet();
        users.getUsersObject().writeUsersSpawnPacket(new PacketWriter(this.content));
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.tileX);
        writer.putNextInt(this.tileY);
        writer.putNextContentPacket(this.content);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.getLevel() == null) {
            return;
        }
        ObjectEntity oe = client.getLevel().entityManager.getObjectEntity(this.tileX, this.tileY);
        if (oe instanceof OEUsers) {
            ((OEUsers)((Object)oe)).submitUpdatePacket(oe, this);
        }
    }
}

