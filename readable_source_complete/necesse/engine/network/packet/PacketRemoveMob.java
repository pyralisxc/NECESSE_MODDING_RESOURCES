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
import necesse.entity.mobs.Mob;
import necesse.level.maps.Level;

public class PacketRemoveMob
extends Packet {
    public final int mobUniqueID;

    public PacketRemoveMob(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.mobUniqueID = reader.getNextInt();
    }

    public PacketRemoveMob(int uniqueID) {
        this.mobUniqueID = uniqueID;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.mobUniqueID);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        if (client.getPermissionLevel().getLevel() >= PermissionLevel.ADMIN.getLevel()) {
            if (server.world.settings.cheatsAllowedOrHidden()) {
                Level level = server.world.getLevel(client);
                Mob mob = level.entityManager.mobs.get(this.mobUniqueID, false);
                if (mob != null) {
                    mob.remove();
                }
            } else {
                System.out.println(client.getName() + " tried to remove a mob, but cheats aren't allowed");
            }
        } else {
            System.out.println(client.getName() + " tried to remove a mob, but isn't admin");
        }
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        Mob mob;
        if (client.getLevel() != null && (mob = client.getLevel().entityManager.mobs.get(this.mobUniqueID, false)) != null) {
            mob.remove();
        }
    }
}

