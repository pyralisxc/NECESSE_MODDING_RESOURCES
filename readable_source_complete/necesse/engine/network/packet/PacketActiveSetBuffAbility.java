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
import necesse.entity.mobs.buffs.ActiveBuffAbility;
import necesse.entity.mobs.buffs.staticBuffs.Buff;

public class PacketActiveSetBuffAbility
extends Packet {
    public final int slot;
    public final int buffID;
    public final int uniqueID;
    public final Packet content;

    public PacketActiveSetBuffAbility(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.slot = reader.getNextByteUnsigned();
        this.buffID = reader.getNextShortUnsigned();
        this.uniqueID = reader.getNextInt();
        this.content = reader.getNextContentPacket();
    }

    public PacketActiveSetBuffAbility(int slot, Buff buff, int uniqueID, Packet content) {
        this.slot = slot;
        this.buffID = buff.getID();
        this.content = content;
        this.uniqueID = uniqueID;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextByteUnsigned(slot);
        writer.putNextShortUnsigned(this.buffID);
        writer.putNextInt(uniqueID);
        writer.putNextContentPacket(content);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.getLevel() == null) {
            return;
        }
        ClientClient target = client.getClient(this.slot);
        if (target != null && target.playerMob.getLevel() != null) {
            target.playerMob.runActiveSetBuffAbility(this.buffID, this.uniqueID, this.content);
        } else {
            client.network.sendPacket(new PacketRequestPlayerData(this.slot));
        }
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        if (client.slot == this.slot) {
            ActiveBuffAbility buffAbility;
            if (!client.checkHasRequestedSelf() || client.isDead()) {
                return;
            }
            ActiveBuff buff = client.playerMob.buffManager.getBuff(this.buffID);
            if (buff != null && buff.buff instanceof ActiveBuffAbility && (buffAbility = (ActiveBuffAbility)((Object)buff.buff)).canRunAbility(client.playerMob, buff, this.content)) {
                client.playerMob.runActiveSetBuffAbility(this.buffID, this.uniqueID, this.content);
                server.network.sendToClientsWithEntityExcept(new PacketActiveSetBuffAbility(this.slot, buff.buff, this.uniqueID, this.content), client.playerMob, client);
            }
        } else {
            GameLog.warn.println(client.getName() + " tried to run active set buff ability from wrong slot, kicking him for desync");
            server.disconnectClient(client, PacketDisconnect.Code.STATE_DESYNC);
        }
    }
}

