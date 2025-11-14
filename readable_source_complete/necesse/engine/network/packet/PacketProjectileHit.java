/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketRemoveMob;
import necesse.engine.network.packet.PacketRemoveProjectile;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.projectile.Projectile;
import necesse.level.maps.Level;

public class PacketProjectileHit
extends Packet {
    public final int projectileUniqueID;
    public final int mobUniqueID;
    public final float fromX;
    public final float fromY;

    public PacketProjectileHit(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.projectileUniqueID = reader.getNextInt();
        this.mobUniqueID = reader.getNextInt();
        this.fromX = reader.getNextFloat();
        this.fromY = reader.getNextFloat();
    }

    public PacketProjectileHit(Projectile projectile, float fromX, float fromY, Mob mob) {
        this.projectileUniqueID = projectile.getUniqueID();
        this.mobUniqueID = mob.getUniqueID();
        this.fromX = fromX;
        this.fromY = fromY;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.projectileUniqueID);
        writer.putNextInt(this.mobUniqueID);
        writer.putNextFloat(fromX);
        writer.putNextFloat(fromY);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        Projectile projectile;
        if (client.getLevel() != null && (projectile = client.getLevel().entityManager.projectiles.get(this.projectileUniqueID, true)) != null) {
            if (this.mobUniqueID == -1) {
                projectile.onHit(null, null, this.fromX, this.fromY, true, null);
            } else {
                Mob mob = GameUtils.getLevelMob(this.mobUniqueID, client.getLevel());
                if (mob != null) {
                    projectile.onHit(mob, null, this.fromX, this.fromY, true, null);
                }
            }
        }
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        Level level = client.getLevel();
        Projectile projectile = level.entityManager.projectiles.get(this.projectileUniqueID, true);
        Mob target = GameUtils.getLevelMob(this.mobUniqueID, level);
        if (projectile != null && target != null) {
            this.serverHit(client, projectile, target);
        } else {
            level.entityManager.submittedHits.submitProjectileHit(client, this.projectileUniqueID, this.mobUniqueID, this::serverHit, (foundClient, attackerUniqueID, foundProjectile, targetUniqueID, foundTarget) -> {
                if (foundProjectile == null) {
                    foundClient.sendPacket(new PacketRemoveProjectile(attackerUniqueID));
                }
                if (foundTarget == null) {
                    foundClient.sendPacket(new PacketRemoveMob(targetUniqueID));
                }
            });
        }
    }

    private void serverHit(ServerClient client, Projectile projectile, Mob target) {
        if (target == client.playerMob || projectile.handlingClient == client && !target.isPlayer) {
            projectile.onHit(target, null, this.fromX, this.fromY, true, client);
        }
    }
}

