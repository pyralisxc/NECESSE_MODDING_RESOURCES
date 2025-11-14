/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketRequestMobData;
import necesse.entity.mobs.Mob;

public class PacketMobNetworkFields
extends Packet {
    public final int levelIdentifierHashCode;
    public final int mobUniqueID;
    public final Packet content;

    public PacketMobNetworkFields(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.levelIdentifierHashCode = reader.getNextInt();
        this.mobUniqueID = reader.getNextInt();
        this.content = reader.getNextContentPacket();
    }

    public PacketMobNetworkFields(Mob mob, Packet content) {
        this.levelIdentifierHashCode = mob.getLevel().getIdentifierHashCode();
        this.mobUniqueID = mob.getUniqueID();
        this.content = content;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.levelIdentifierHashCode);
        writer.putNextInt(this.mobUniqueID);
        writer.putNextContentPacket(content);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (!client.levelManager.isLevelLoaded(this.levelIdentifierHashCode)) {
            return;
        }
        Mob mob = client.getLevel().entityManager.mobs.get(this.mobUniqueID, false);
        if (mob != null) {
            mob.runNetworkFieldUpdate(new PacketReader(this.content));
            mob.refreshClientUpdateTime();
        } else {
            client.network.sendPacket(new PacketRequestMobData(this.mobUniqueID));
        }
    }
}

