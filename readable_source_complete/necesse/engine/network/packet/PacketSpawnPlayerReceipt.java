/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;

public class PacketSpawnPlayerReceipt
extends Packet {
    public final boolean isRequest;

    public PacketSpawnPlayerReceipt(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.isRequest = reader.getNextBoolean();
    }

    public PacketSpawnPlayerReceipt(boolean isRequest) {
        this.isRequest = isRequest;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextBoolean(isRequest);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.getSlot() == -1) {
            return;
        }
        if (this.isRequest) {
            client.spawnPacketSentTime = Integer.MIN_VALUE;
        } else {
            client.spawnPacketSentTime = 0L;
            ClientClient me = client.getClient();
            if (me != null && me.loadedPlayer) {
                me.playerMob.refreshSpawnTime();
                me.applySpawned(me.playerMob.getRemainingSpawnInvincibilityTime());
            }
        }
    }
}

