/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.entity.projectile.Projectile;

public class PacketRemoveProjectile
extends Packet {
    public final int projectileUniqueID;

    public PacketRemoveProjectile(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.projectileUniqueID = reader.getNextInt();
    }

    public PacketRemoveProjectile(int uniqueID) {
        this.projectileUniqueID = uniqueID;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.projectileUniqueID);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        Projectile projectile;
        if (client.getLevel() != null && (projectile = client.getLevel().entityManager.projectiles.get(this.projectileUniqueID, false)) != null) {
            projectile.remove();
        }
    }
}

