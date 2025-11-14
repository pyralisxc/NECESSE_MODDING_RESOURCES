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
import necesse.engine.network.packet.PacketRequestPlayerData;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;

public class PacketPlayerBuff
extends Packet {
    public final int slot;
    public final Packet buffContent;

    public PacketPlayerBuff(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.slot = reader.getNextByteUnsigned();
        this.buffContent = reader.getNextContentPacket();
    }

    public PacketPlayerBuff(int slot, ActiveBuff ab) {
        this.slot = slot;
        this.buffContent = ab.getContentPacket();
        PacketWriter writer = new PacketWriter(this);
        writer.putNextByteUnsigned(slot);
        writer.putNextContentPacket(this.buffContent);
    }

    public ActiveBuff getBuff(Mob owner) {
        return ActiveBuff.fromContentPacket(this.buffContent, owner);
    }

    public void applyBuff(PlayerMob player) {
        player.addBuff(this.getBuff(player), false);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        if (client.getPermissionLevel().getLevel() >= PermissionLevel.ADMIN.getLevel()) {
            if (this.slot != client.slot) {
                return;
            }
            if (server.world.settings.cheatsAllowedOrHidden()) {
                this.applyBuff(client.playerMob);
                server.network.sendToClientsWithEntity(this, client.playerMob);
            } else {
                System.out.println(client.getName() + " tried to set own buff, but cheats aren't allowed");
            }
        } else {
            System.out.println(client.getName() + " tried to set own buff, but isn't admin");
        }
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.getClient(this.slot) == null) {
            client.network.sendPacket(new PacketRequestPlayerData(this.slot));
        } else {
            this.applyBuff(client.getClient((int)this.slot).playerMob);
        }
    }
}

