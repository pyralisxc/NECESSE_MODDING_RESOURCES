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
import necesse.engine.registries.MobRegistry;
import necesse.entity.mobs.Mob;
import necesse.level.maps.Level;

public class PacketSpawnMob
extends Packet {
    public final int levelIdentifierHashCode;
    public final int mobID;
    public final Packet spawnContent;

    public PacketSpawnMob(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.levelIdentifierHashCode = reader.getNextInt();
        this.mobID = reader.getNextShortUnsigned();
        this.spawnContent = reader.getNextContentPacket();
    }

    public PacketSpawnMob(Mob mob) {
        this.levelIdentifierHashCode = mob.getLevel().getIdentifierHashCode();
        this.mobID = mob.getID();
        this.spawnContent = new Packet();
        mob.setupSpawnPacket(new PacketWriter(this.spawnContent));
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.levelIdentifierHashCode);
        writer.putNextShortUnsigned(this.mobID);
        writer.putNextContentPacket(this.spawnContent);
    }

    public Mob getMob(Level level) {
        Mob out = MobRegistry.getMob(this.mobID, level);
        if (out != null) {
            out.applySpawnPacket(new PacketReader(this.spawnContent));
        }
        return out;
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        if (client.getPermissionLevel().getLevel() >= PermissionLevel.ADMIN.getLevel()) {
            if (server.world.settings.cheatsAllowedOrHidden()) {
                Level level = server.world.getLevel(client);
                if (level.getIdentifierHashCode() == this.levelIdentifierHashCode) {
                    level.entityManager.mobs.add(this.getMob(level));
                } else {
                    System.out.println(client.getName() + " tried to spawn a mob on wrong level");
                }
            } else {
                System.out.println(client.getName() + " tried to spawn a mob, but cheats aren't allowed");
            }
        } else {
            System.out.println(client.getName() + " tried to spawn a mob, but isn't admin");
        }
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (!client.levelManager.isLevelLoaded(this.levelIdentifierHashCode)) {
            return;
        }
        Mob mob = this.getMob(client.getLevel());
        if (!client.levelManager.checkIfLoadedRegionAtTile(this.levelIdentifierHashCode, mob, true)) {
            Mob foundMob = client.getLevel().entityManager.mobs.get(mob.getUniqueID(), false);
            if (foundMob != null) {
                foundMob.remove();
            }
            return;
        }
        client.getLevel().entityManager.mobs.add(mob);
    }
}

