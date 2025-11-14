/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.GameLog;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.packet.PacketDisconnect;
import necesse.engine.network.packet.PacketRequestPlayerData;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffAbility;
import necesse.entity.mobs.buffs.staticBuffs.Buff;

public class PacketBuffAbility
extends Packet {
    public final int slot;
    public final int buffID;
    public final Packet content;

    public PacketBuffAbility(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.slot = reader.getNextByteUnsigned();
        this.buffID = reader.getNextShortUnsigned();
        this.content = reader.getNextContentPacket();
    }

    public PacketBuffAbility(int slot, Buff buff, Packet content) {
        this.slot = slot;
        this.buffID = buff.getID();
        this.content = content;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextByteUnsigned(slot);
        writer.putNextShortUnsigned(this.buffID);
        writer.putNextContentPacket(content);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.getLevel() == null) {
            return;
        }
        ClientClient target = client.getClient(this.slot);
        if (target != null && target.playerMob.getLevel() != null) {
            ActiveBuff buff = target.playerMob.buffManager.getBuff(this.buffID);
            if (buff != null && buff.buff instanceof BuffAbility) {
                ((BuffAbility)((Object)buff.buff)).runAbility(target.playerMob, buff, this.content);
            }
        } else {
            client.network.sendPacket(new PacketRequestPlayerData(this.slot));
        }
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        if (client.slot == this.slot) {
            if (!client.checkHasRequestedSelf() || client.isDead()) {
                return;
            }
            ActiveBuff buff = client.playerMob.buffManager.getBuff(this.buffID);
            if (buff != null && buff.buff instanceof BuffAbility && ((BuffAbility)((Object)buff.buff)).canRunAbility(client.playerMob, buff, this.content)) {
                ((BuffAbility)((Object)buff.buff)).runAbility(client.playerMob, buff, this.content);
                server.network.sendToClientsWithEntityExcept(new PacketBuffAbility(this.slot, buff.buff, this.content), client.playerMob, client);
            }
        } else {
            GameLog.warn.println(client.getName() + " tried to run buff ability from wrong slot, kicking him for desync");
            server.disconnectClient(client, PacketDisconnect.Code.STATE_DESYNC);
        }
    }
}

