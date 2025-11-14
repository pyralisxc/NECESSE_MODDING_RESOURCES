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
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;

public class PacketMobBuff
extends Packet {
    public final int mobUniqueID;
    public final Packet buffContent;
    public final boolean forceUpdateBuffs;

    public PacketMobBuff(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.mobUniqueID = reader.getNextInt();
        this.buffContent = reader.getNextContentPacket();
        this.forceUpdateBuffs = reader.getNextBoolean();
    }

    public PacketMobBuff(int mobUniqueID, ActiveBuff ab, boolean forceUpdateBuffs) {
        this.mobUniqueID = mobUniqueID;
        this.buffContent = ab.getContentPacket();
        this.forceUpdateBuffs = forceUpdateBuffs;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.mobUniqueID);
        writer.putNextContentPacket(this.buffContent);
        writer.putNextBoolean(forceUpdateBuffs);
    }

    public ActiveBuff getBuff(Mob owner) {
        return ActiveBuff.fromContentPacket(this.buffContent, owner);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        if (client.getPermissionLevel().getLevel() >= PermissionLevel.ADMIN.getLevel()) {
            if (server.world.settings.cheatsAllowedOrHidden()) {
                if (!client.checkHasRequestedSelf()) {
                    return;
                }
                Mob mob = GameUtils.getLevelMob(this.mobUniqueID, server.world.getLevel(client));
                if (mob != null) {
                    mob.buffManager.addBuff(this.getBuff(mob), true, true, this.forceUpdateBuffs);
                }
            } else {
                System.out.println(client.getName() + " tried to set mob buff, but cheats aren't allowed");
            }
        } else {
            System.out.println(client.getName() + " tried to set mob buff, but isn't admin");
        }
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        Mob mob;
        if (client.getLevel() != null && (mob = GameUtils.getLevelMob(this.mobUniqueID, client.getLevel())) != null) {
            mob.buffManager.addBuff(this.getBuff(mob), false, true, this.forceUpdateBuffs);
        }
    }
}

