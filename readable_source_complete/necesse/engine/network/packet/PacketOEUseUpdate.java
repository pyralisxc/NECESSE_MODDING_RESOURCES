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

public class PacketOEUseUpdate
extends Packet {
    public final int tileX;
    public final int tileY;
    public final int mobUniqueID;
    public final boolean isUsing;
    public final int totalUsers;

    public PacketOEUseUpdate(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.tileX = reader.getNextInt();
        this.tileY = reader.getNextInt();
        this.mobUniqueID = reader.getNextInt();
        this.isUsing = reader.getNextBoolean();
        this.totalUsers = reader.getNextShortUnsigned();
    }

    public PacketOEUseUpdate(OEUsers users, int mobUniqueID, boolean isUsing) {
        ObjectEntity oe = (ObjectEntity)((Object)users);
        this.tileX = oe.tileX;
        this.tileY = oe.tileY;
        this.mobUniqueID = mobUniqueID;
        this.isUsing = isUsing;
        this.totalUsers = users.getTotalUsers();
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.tileX);
        writer.putNextInt(this.tileY);
        writer.putNextInt(mobUniqueID);
        writer.putNextBoolean(isUsing);
        writer.putNextShortUnsigned(this.totalUsers);
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

