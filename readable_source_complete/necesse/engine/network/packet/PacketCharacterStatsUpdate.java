/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.playerStats.PlayerStats;

public class PacketCharacterStatsUpdate
extends Packet {
    public PacketCharacterStatsUpdate(byte[] data) {
        super(data);
    }

    public PacketCharacterStatsUpdate(PlayerStats dirtyStats) {
        dirtyStats.setupDirtyPacket(new PacketWriter(this));
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.characterStats == null) {
            return;
        }
        client.characterStats.applyDirtyPacket(new PacketReader(this));
    }
}

