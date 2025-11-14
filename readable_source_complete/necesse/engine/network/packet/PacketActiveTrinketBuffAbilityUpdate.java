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
import necesse.engine.network.packet.PacketRequestActiveTrinketBuffAbility;
import necesse.engine.network.packet.PacketRequestPlayerData;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class PacketActiveTrinketBuffAbilityUpdate
extends Packet {
    public final int slot;
    public final int uniqueID;
    public final Packet content;

    public PacketActiveTrinketBuffAbilityUpdate(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.slot = reader.getNextByteUnsigned();
        this.uniqueID = reader.getNextInt();
        this.content = reader.getNextContentPacket();
    }

    public PacketActiveTrinketBuffAbilityUpdate(int slot, int uniqueID, Packet content) {
        this.slot = slot;
        this.uniqueID = uniqueID;
        this.content = content;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextByteUnsigned(slot);
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
            if (!target.playerMob.runActiveTrinketBuffAbilityUpdate(this.uniqueID, this.content)) {
                client.network.sendPacket(new PacketRequestActiveTrinketBuffAbility(this.slot));
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
            if (client.playerMob.runActiveTrinketBuffAbilityUpdate(this.uniqueID, this.content)) {
                server.network.sendToClientsWithEntityExcept(new PacketActiveTrinketBuffAbilityUpdate(this.slot, this.uniqueID, this.content), client.playerMob, client);
            } else {
                client.playerMob.sendActiveTrinketBuffAbilityState(server, client);
            }
        } else {
            GameLog.warn.println(client.getName() + " tried to run active buff ability update from wrong slot, kicking him for desync");
            server.disconnectClient(client, PacketDisconnect.Code.STATE_DESYNC);
        }
    }
}

