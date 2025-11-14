/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.achievements.AchievementManager;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.playerStats.EmptyStats;
import necesse.engine.playerStats.PlayerStats;

public class PacketClientStats
extends Packet {
    public final PlayerStats stats;
    public final AchievementManager achievements;

    public PacketClientStats(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.stats = new PlayerStats(false, EmptyStats.Mode.READ_AND_WRITE);
        this.stats.applyContentPacket(reader);
        this.achievements = new AchievementManager(this.stats);
        this.achievements.applyContentPacket(reader);
    }

    public PacketClientStats(PlayerStats stats, AchievementManager achievements) {
        this.stats = stats;
        this.achievements = achievements;
        PacketWriter writer = new PacketWriter(this);
        stats.setupContentPacket(writer);
        achievements.setupContentPacket(writer);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        client.applyClientStatsPacket(this);
    }
}

