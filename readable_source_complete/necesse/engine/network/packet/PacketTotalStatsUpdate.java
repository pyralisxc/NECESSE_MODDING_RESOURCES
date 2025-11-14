/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.GlobalData;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.playerStats.PlayerStats;

public class PacketTotalStatsUpdate
extends Packet {
    public PacketTotalStatsUpdate(byte[] data) {
        super(data);
    }

    public PacketTotalStatsUpdate(PlayerStats dirtyStats) {
        dirtyStats.setupDirtyPacket(new PacketWriter(this));
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        GlobalData.stats().applyDirtyPacket(new PacketReader(this));
    }
}

