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
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.level.maps.Level;

public class PacketMobBuffRemove
extends Packet {
    public final int mobUniqueID;
    public final int buffID;

    public PacketMobBuffRemove(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.mobUniqueID = reader.getNextInt();
        this.buffID = reader.getNextShortUnsigned();
    }

    public PacketMobBuffRemove(int mobUniqueID, int buffID) {
        this.mobUniqueID = mobUniqueID;
        this.buffID = buffID;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(mobUniqueID);
        writer.putNextShortUnsigned(buffID);
    }

    public void applyPacket(Level level) {
        if (level == null) {
            return;
        }
        this.applyPacket(GameUtils.getLevelMob(this.mobUniqueID, level));
    }

    public void applyPacket(Mob mob) {
        if (mob == null) {
            return;
        }
        mob.buffManager.removeBuff(BuffRegistry.getBuffStringID(this.buffID), false);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        if (this.mobUniqueID == client.slot) {
            ActiveBuff ab = client.playerMob.buffManager.getBuff(BuffRegistry.getBuffStringID(this.buffID));
            if (ab != null) {
                if (ab.canCancel()) {
                    this.applyPacket(client.playerMob);
                    server.network.sendToClientsWithEntityExcept(this, client.playerMob, client);
                } else if (client.getPermissionLevel().getLevel() >= PermissionLevel.ADMIN.getLevel()) {
                    if (server.world.settings.cheatsAllowedOrHidden()) {
                        this.applyPacket(client.playerMob);
                        server.network.sendToClientsWithEntityExcept(this, client.playerMob, client);
                    } else {
                        System.out.println(client.getName() + " tried to remove invalid own buff, but cheats aren't allowed");
                    }
                } else {
                    System.out.println(client.getName() + " tried to remove invalid own buff, but isn't admin");
                }
            }
        } else if (client.getPermissionLevel().getLevel() >= PermissionLevel.ADMIN.getLevel()) {
            if (server.world.settings.cheatsAllowedOrHidden()) {
                this.applyPacket(server.world.getLevel(client));
                server.network.sendToClientsWithEntity(this, client.playerMob);
            } else {
                System.out.println(client.getName() + " tried to remove mob buff, but cheats aren't allowed");
            }
        } else {
            System.out.println(client.getName() + " tried to remove mob buff, but isn't admin");
        }
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (this.mobUniqueID >= 0 && this.mobUniqueID < client.getSlots()) {
            ClientClient other = client.getClient(this.mobUniqueID);
            if (other != null && other.playerMob != null) {
                this.applyPacket(other.playerMob);
            }
        } else {
            this.applyPacket(client.getLevel());
        }
    }
}

