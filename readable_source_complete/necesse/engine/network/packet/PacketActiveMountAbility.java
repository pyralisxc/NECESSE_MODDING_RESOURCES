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
import necesse.entity.mobs.ActiveMountAbility;
import necesse.entity.mobs.Mob;

public class PacketActiveMountAbility
extends Packet {
    public final int slot;
    public final int mountUniqueID;
    public final int uniqueID;
    public final Packet content;

    public PacketActiveMountAbility(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.slot = reader.getNextByteUnsigned();
        this.mountUniqueID = reader.getNextInt();
        this.uniqueID = reader.getNextInt();
        this.content = reader.getNextContentPacket();
    }

    public PacketActiveMountAbility(int slot, Mob mount, int uniqueID, Packet content) {
        this.slot = slot;
        this.mountUniqueID = mount.getUniqueID();
        this.content = content;
        this.uniqueID = uniqueID;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextByteUnsigned(slot);
        writer.putNextInt(this.mountUniqueID);
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
            target.playerMob.runActiveMountAbility(this.mountUniqueID, this.uniqueID, this.content);
        } else {
            client.network.sendPacket(new PacketRequestPlayerData(this.slot));
        }
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        if (client.slot == this.slot) {
            ActiveMountAbility mountAbility;
            if (!client.checkHasRequestedSelf() || client.isDead()) {
                return;
            }
            Mob mount = client.playerMob.getMount();
            if (mount instanceof ActiveMountAbility && (mountAbility = (ActiveMountAbility)((Object)mount)).canRunMountAbility(client.playerMob, this.content)) {
                client.playerMob.runActiveMountAbility(this.mountUniqueID, this.uniqueID, this.content);
                server.network.sendToClientsWithEntityExcept(new PacketActiveMountAbility(this.slot, mount, this.uniqueID, this.content), client.playerMob, client);
            }
        } else {
            GameLog.warn.println(client.getName() + " tried to run active mount ability from wrong slot, kicking him for desync");
            server.disconnectClient(client, PacketDisconnect.Code.STATE_DESYNC);
        }
    }
}

