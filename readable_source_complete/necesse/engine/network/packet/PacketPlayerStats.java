/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.playerStats.EmptyStats;
import necesse.engine.playerStats.PlayerStats;

public class PacketPlayerStats
extends Packet {
    public final PlayerStats stats;
    public final boolean forceUpdate;

    public PacketPlayerStats(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.stats = new PlayerStats(false, EmptyStats.Mode.READ_ONLY);
        this.stats.applyContentPacket(reader);
        this.forceUpdate = reader.getNextBoolean();
    }

    public PacketPlayerStats(PlayerStats stats, boolean forceUpdate) {
        this.stats = stats;
        this.forceUpdate = forceUpdate;
        PacketWriter writer = new PacketWriter(this);
        stats.setupContentPacket(writer);
        writer.putNextBoolean(forceUpdate);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.characterStats == null || this.forceUpdate) {
            client.characterStats = this.stats;
        }
        client.loading.statsPhase.submitStatsPacket(this);
    }
}

