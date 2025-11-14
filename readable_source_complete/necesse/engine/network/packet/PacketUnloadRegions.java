/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import java.awt.Point;
import java.util.HashSet;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientLevelLoading;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.level.maps.Level;

public class PacketUnloadRegions
extends Packet {
    public final int levelIdentifierHashCode;
    public final HashSet<Point> regionPositions;

    public PacketUnloadRegions(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.levelIdentifierHashCode = reader.getNextInt();
        int size = reader.getNextShortUnsigned();
        this.regionPositions = new HashSet();
        for (int i = 0; i < size; ++i) {
            int regionX = reader.getNextInt();
            int regionY = reader.getNextInt();
            this.regionPositions.add(new Point(regionX, regionY));
        }
    }

    public PacketUnloadRegions(Level level, HashSet<Point> regionPositions) {
        this.levelIdentifierHashCode = level.getIdentifierHashCode();
        this.regionPositions = regionPositions;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.levelIdentifierHashCode);
        writer.putNextShortUnsigned(regionPositions.size());
        for (Point pos : regionPositions) {
            writer.putNextInt(pos.x);
            writer.putNextInt(pos.y);
        }
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        Level level = client.getLevel();
        if (level != null && level.getIdentifierHashCode() == this.levelIdentifierHashCode) {
            ClientLevelLoading loading = client.levelManager.loading();
            loading.unloadRegions(this.regionPositions, false);
        }
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        Level level = server.world.getLevel(client);
        if (level != null && level.getIdentifierHashCode() == this.levelIdentifierHashCode) {
            client.removeLoadedRegions(level, this.regionPositions, false, false);
        }
    }
}

