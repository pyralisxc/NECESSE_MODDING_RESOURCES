/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.entity.mobs.PlayerMob;

public class PacketPlayerStopAttack
extends Packet {
    public int slot;

    public PacketPlayerStopAttack(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.slot = reader.getNextByteUnsigned();
    }

    public PacketPlayerStopAttack(int slot) {
        this.slot = slot;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextByteUnsigned(slot);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        PlayerMob player = client.getPlayer(this.slot);
        if (player != null && player.getLevel() != null) {
            player.forceEndAttack();
        }
    }
}

