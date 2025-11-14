/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketPlayerGeneral;
import necesse.engine.network.packet.PacketRemoveMob;
import necesse.engine.network.packet.PacketSpawnMob;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.Mob;

public class PacketRequestMobData
extends Packet {
    public final int uniqueID;

    public PacketRequestMobData(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.uniqueID = reader.getNextInt();
    }

    public PacketRequestMobData(int uniqueID) {
        this.uniqueID = uniqueID;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(uniqueID);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        if (this.uniqueID >= 0 && this.uniqueID < server.getSlots()) {
            ServerClient other = server.getClient(this.uniqueID);
            if (other != null) {
                server.network.sendPacket((Packet)new PacketPlayerGeneral(other), client);
            }
        } else {
            Mob mob = server.world.getLevel((ServerClient)client).entityManager.mobs.get(this.uniqueID, false);
            if (mob != null) {
                if (mob.shouldSendSpawnPacket()) {
                    server.network.sendPacket((Packet)new PacketSpawnMob(mob), client);
                } else {
                    Mob master = mob.getSpawnPacketMaster();
                    if (master != null) {
                        server.network.sendPacket((Packet)new PacketSpawnMob(master), client);
                    }
                }
            } else {
                server.network.sendPacket((Packet)new PacketRemoveMob(this.uniqueID), client);
            }
        }
    }
}

