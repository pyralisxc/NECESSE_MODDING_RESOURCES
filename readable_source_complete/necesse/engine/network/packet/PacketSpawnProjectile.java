/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.commands.PermissionLevel;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ProjectileRegistry;
import necesse.entity.projectile.Projectile;
import necesse.level.maps.Level;

public class PacketSpawnProjectile
extends Packet {
    public final int levelIdentifierHashCode;
    public final int projectileID;
    public final Packet spawnContent;

    public PacketSpawnProjectile(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.levelIdentifierHashCode = reader.getNextInt();
        this.projectileID = reader.getNextShortUnsigned();
        this.spawnContent = reader.getNextContentPacket();
    }

    public PacketSpawnProjectile(Projectile projectile) {
        this.levelIdentifierHashCode = projectile.getLevel().getIdentifierHashCode();
        this.projectileID = projectile.getID();
        this.spawnContent = new Packet();
        projectile.setupSpawnPacket(new PacketWriter(this.spawnContent));
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.levelIdentifierHashCode);
        writer.putNextShortUnsigned(this.projectileID);
        writer.putNextContentPacket(this.spawnContent);
    }

    public Projectile getProjectile(Level level) {
        Projectile out = ProjectileRegistry.getProjectile(this.projectileID, level);
        out.applySpawnPacket(new PacketReader(this.spawnContent));
        return out;
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        if (client.getPermissionLevel().getLevel() >= PermissionLevel.ADMIN.getLevel()) {
            if (server.world.settings.cheatsAllowedOrHidden()) {
                Level level = server.world.getLevel(client);
                if (level.getIdentifierHashCode() == this.levelIdentifierHashCode) {
                    Projectile projectile = this.getProjectile(level);
                    projectile.setOwner(client.playerMob);
                    level.entityManager.projectiles.addHidden(projectile);
                    server.network.sendToClientsWithEntityExcept(new PacketSpawnProjectile(projectile), projectile, client);
                } else {
                    System.out.println(client.getName() + " tried to spawn a projectile on wrong level");
                }
            } else {
                System.out.println(client.getName() + " tried to spawn a projectile, but cheats aren't allowed");
            }
        } else {
            System.out.println(client.getName() + " tried to spawn a projectile, but isn't admin");
        }
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (!client.levelManager.isLevelLoaded(this.levelIdentifierHashCode)) {
            return;
        }
        Projectile projectile = this.getProjectile(client.getLevel());
        if (!client.levelManager.checkIfLoadedRegionAtTile(this.levelIdentifierHashCode, projectile, true)) {
            Projectile foundProjectile = client.getLevel().entityManager.projectiles.get(projectile.getUniqueID(), false);
            if (foundProjectile != null) {
                foundProjectile.remove();
            }
            return;
        }
        client.getLevel().entityManager.projectiles.addHidden(projectile);
    }
}

