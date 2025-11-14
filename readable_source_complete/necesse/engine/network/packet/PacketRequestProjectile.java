/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketRemoveProjectile;
import necesse.engine.network.packet.PacketSpawnProjectile;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.projectile.Projectile;

public class PacketRequestProjectile
extends Packet {
    public final int projectileUniqueID;

    public PacketRequestProjectile(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.projectileUniqueID = reader.getNextInt();
    }

    public PacketRequestProjectile(int uniqueID) {
        this.projectileUniqueID = uniqueID;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.projectileUniqueID);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        Projectile p = server.world.getLevel((ServerClient)client).entityManager.projectiles.get(this.projectileUniqueID, false);
        if (p != null) {
            server.network.sendPacket((Packet)new PacketSpawnProjectile(p), client);
        } else {
            server.network.sendPacket((Packet)new PacketRemoveProjectile(this.projectileUniqueID), client);
        }
    }
}

