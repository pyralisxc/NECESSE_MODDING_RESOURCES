/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketRequestPlayerData;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.Mob;

public class PacketPlayerBuffs
extends Packet {
    public final int slot;
    public final Packet content;

    public PacketPlayerBuffs(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.slot = reader.getNextByteUnsigned();
        this.content = reader.getNextContentPacket();
    }

    public PacketPlayerBuffs(ServerClient client) {
        this.slot = client.slot;
        this.content = new Packet();
        client.playerMob.buffManager.setupContentPacket(new PacketWriter(this.content));
        PacketWriter writer = new PacketWriter(this);
        writer.putNextByteUnsigned(this.slot);
        writer.putNextContentPacket(this.content);
    }

    public void apply(Mob mob) {
        mob.buffManager.applyContentPacket(new PacketReader(this.content));
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.getClient(this.slot) == null) {
            client.network.sendPacket(new PacketRequestPlayerData(this.slot));
            return;
        }
        client.getClient(this.slot).applyPacketPlayerBuffs(this);
    }
}

