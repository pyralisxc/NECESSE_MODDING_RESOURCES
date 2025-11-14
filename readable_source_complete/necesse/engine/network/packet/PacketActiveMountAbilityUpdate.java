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
import necesse.engine.network.packet.PacketRequestActiveMountAbility;
import necesse.engine.network.packet.PacketRequestPlayerData;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class PacketActiveMountAbilityUpdate
extends Packet {
    public final int slot;
    public final int uniqueID;
    public final Packet content;

    public PacketActiveMountAbilityUpdate(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.slot = reader.getNextByteUnsigned();
        this.uniqueID = reader.getNextInt();
        this.content = reader.getNextContentPacket();
    }

    public PacketActiveMountAbilityUpdate(int slot, int uniqueID, Packet content) {
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
            if (!target.playerMob.runActiveMountAbilityUpdate(this.uniqueID, this.content)) {
                client.network.sendPacket(new PacketRequestActiveMountAbility(this.slot));
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
            if (client.playerMob.runActiveMountAbilityUpdate(this.uniqueID, this.content)) {
                server.network.sendToClientsWithEntityExcept(new PacketActiveMountAbilityUpdate(this.slot, this.uniqueID, this.content), client.playerMob, client);
            } else {
                client.playerMob.sendActiveMountAbilityState(server, client);
            }
        } else {
            GameLog.warn.println(client.getName() + " tried to run active mount ability update from wrong slot, kicking him for desync");
            server.disconnectClient(client, PacketDisconnect.Code.STATE_DESYNC);
        }
    }
}

