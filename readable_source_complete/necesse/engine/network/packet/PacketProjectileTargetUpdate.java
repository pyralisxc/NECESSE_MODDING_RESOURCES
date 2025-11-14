/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketRequestProjectile;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.followingProjectile.FollowingProjectile;
import necesse.level.maps.Level;

public class PacketProjectileTargetUpdate
extends Packet {
    public final int projectileUniqueID;
    public final float x;
    public final float y;
    public final float dx;
    public final float dy;
    public final float travelledDistance;
    public final int distance;
    public final Packet content;

    public PacketProjectileTargetUpdate(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.projectileUniqueID = reader.getNextInt();
        this.x = reader.getNextFloat();
        this.y = reader.getNextFloat();
        this.dx = reader.getNextFloat();
        this.dy = reader.getNextFloat();
        this.travelledDistance = reader.getNextFloat();
        this.distance = reader.getNextInt();
        this.content = reader.getNextContentPacket();
    }

    public PacketProjectileTargetUpdate(FollowingProjectile projectile) {
        this.projectileUniqueID = projectile.getUniqueID();
        this.x = projectile.x;
        this.y = projectile.y;
        this.dx = projectile.dx;
        this.dy = projectile.dy;
        this.travelledDistance = projectile.traveledDistance;
        this.distance = projectile.distance;
        this.content = new Packet();
        projectile.addTargetData(new PacketWriter(this.content));
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.projectileUniqueID);
        writer.putNextFloat(this.x);
        writer.putNextFloat(this.y);
        writer.putNextFloat(this.dx);
        writer.putNextFloat(this.dy);
        writer.putNextFloat(this.travelledDistance);
        writer.putNextInt(this.distance);
        writer.putNextContentPacket(this.content);
    }

    public Projectile getProjectile(Level level) {
        if (level == null) {
            return null;
        }
        return level.entityManager.projectiles.get(this.projectileUniqueID, false);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        Projectile p = this.getProjectile(server.world.getLevel(client));
        if (p instanceof FollowingProjectile && p.handlingClient == client) {
            p.changePosition(this.x, this.y);
            p.dx = this.dx;
            p.dy = this.dy;
            p.traveledDistance = this.travelledDistance;
            p.setDistance(this.distance);
            p.updateAngle();
            ((FollowingProjectile)p).applyTargetData(new PacketReader(this.content));
            server.network.sendToClientsWithEntityExcept(this, p, client);
        }
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        Projectile p = this.getProjectile(client.getLevel());
        if (p instanceof FollowingProjectile) {
            p.changePosition(this.x, this.y);
            p.dx = this.dx;
            p.dy = this.dy;
            p.traveledDistance = this.travelledDistance;
            p.setDistance(this.distance);
            p.updateAngle();
            ((FollowingProjectile)p).applyTargetData(new PacketReader(this.content));
        } else {
            client.network.sendPacket(new PacketRequestProjectile(this.projectileUniqueID));
        }
    }
}

