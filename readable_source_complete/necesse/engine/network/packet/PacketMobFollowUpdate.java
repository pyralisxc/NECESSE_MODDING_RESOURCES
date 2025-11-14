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
import necesse.level.maps.Level;

public class PacketMobFollowUpdate
extends Packet {
    public final int mobUniqueID;
    public final int followingUniqueID;

    public PacketMobFollowUpdate(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.mobUniqueID = reader.getNextInt();
        this.followingUniqueID = reader.getNextInt();
    }

    public PacketMobFollowUpdate(int mobUniqueID, int followingUniqueID) {
        this.mobUniqueID = mobUniqueID;
        this.followingUniqueID = followingUniqueID;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(mobUniqueID);
        writer.putNextInt(followingUniqueID);
    }

    public Mob getMob(Level level) {
        return level.entityManager.mobs.get(this.mobUniqueID, false);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.getLevel() == null) {
            return;
        }
        Mob mob = this.getMob(client.getLevel());
        if (mob != null) {
            mob.applyFollowUpdatePacket(this);
        } else {
            client.network.sendPacket(new PacketRequestMobData(this.mobUniqueID));
        }
    }
}

