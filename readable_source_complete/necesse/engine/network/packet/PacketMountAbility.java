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
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MountAbility;

public class PacketMountAbility
extends Packet {
    public final int slot;
    public final int mountUniqueID;
    public final Packet content;

    public PacketMountAbility(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.slot = reader.getNextByteUnsigned();
        this.mountUniqueID = reader.getNextInt();
        this.content = reader.getNextContentPacket();
    }

    public PacketMountAbility(int slot, Mob mount, Packet content) {
        this.slot = slot;
        this.mountUniqueID = mount.getUniqueID();
        this.content = content;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextByteUnsigned(slot);
        writer.putNextInt(this.mountUniqueID);
        writer.putNextContentPacket(content);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.getLevel() == null) {
            return;
        }
        ClientClient target = client.getClient(this.slot);
        if (target != null && target.playerMob.getLevel() != null) {
            Mob mount = target.playerMob.getMount();
            if (mount instanceof MountAbility) {
                ((MountAbility)((Object)mount)).runMountAbility(target.playerMob, this.content);
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
            Mob mount = client.playerMob.getMount();
            if (mount instanceof MountAbility && ((MountAbility)((Object)mount)).canRunMountAbility(client.playerMob, this.content)) {
                ((MountAbility)((Object)mount)).runMountAbility(client.playerMob, this.content);
                server.network.sendToClientsWithEntityExcept(new PacketMountAbility(this.slot, mount, this.content), client.playerMob, client);
            }
        } else {
            GameLog.warn.println(client.getName() + " tried to run mount from wrong slot, kicking him for desync");
            server.disconnectClient(client, PacketDisconnect.Code.STATE_DESYNC);
        }
    }
}

