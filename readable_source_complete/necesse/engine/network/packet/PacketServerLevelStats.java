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
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.playerStats.EmptyStats;
import necesse.engine.playerStats.PlayerStats;
import necesse.engine.state.MainGame;

public class PacketServerLevelStats
extends Packet {
    public final PlayerStats stats;

    public PacketServerLevelStats(byte[] data) {
        super(data);
        this.stats = new PlayerStats(false, EmptyStats.Mode.READ_ONLY);
        this.stats.applyContentPacket(new PacketReader(this));
    }

    public PacketServerLevelStats(PlayerStats stats) {
        this.stats = stats;
        stats.setupContentPacket(new PacketWriter(this));
    }

    public PacketServerLevelStats() {
        this.stats = null;
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        client.sendPacket(new PacketServerLevelStats(client.getLevel().levelStats));
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (GlobalData.getCurrentState() instanceof MainGame) {
            ((MainGame)GlobalData.getCurrentState()).formManager.pauseMenu.applyServerLevelStatsPacket(this);
        }
    }
}

